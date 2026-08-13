#!/usr/bin/env bash
set -euo pipefail

GENERATOR_IMAGE="openapitools/openapi-generator-cli:7.19.0@sha256:b9e7ad71a9f9406bd810378a939755fad114747a767e29bbf83ef9364d5f9dc0"

usage() {
  echo "Usage: $0 --spec-dir DIR [--check]" >&2
}

spec_dir=""
check_only="false"
while [[ $# -gt 0 ]]; do
  case "$1" in
    --spec-dir)
      spec_dir="${2:-}"
      shift 2
      ;;
    --check)
      check_only="true"
      shift
      ;;
    *)
      usage
      exit 2
      ;;
  esac
done

if [[ -z "$spec_dir" ]]; then
  usage
  exit 2
fi

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd "${script_dir}/../.." && pwd)"
config_file="${repo_root}/codegen/configs/java-generator.json"
java_overrides_file="${repo_root}/codegen/configs/java-overrides.json"
generated_root="${repo_root}/sdks/java/src/generated/java"
lock_file="${repo_root}/codegen/manifests/java-models.lock.json"

if [[ ! -d "$spec_dir" ]]; then
  echo "Spec directory does not exist: $spec_dir" >&2
  exit 2
fi
spec_dir="$(cd "$spec_dir" && pwd)"

work_dir="$(mktemp -d)"
cleanup() {
  rm -rf "$work_dir"
}
trap cleanup EXIT

prepared_dir="${work_dir}/prepared"
generator_dir="${work_dir}/generator"
candidate_root="${work_dir}/candidate"
mkdir -p "$prepared_dir" "$generator_dir" "$candidate_root"

"${repo_root}/codegen/scripts/prepare-openapi.py" \
  --spec-dir "$spec_dir" \
  --output-dir "$prepared_dir"

python3 - "$prepared_dir/manifest.json" "$java_overrides_file" <<'PY'
import json
import pathlib
import sys

manifest_path = pathlib.Path(sys.argv[1])
manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
overrides = json.loads(pathlib.Path(sys.argv[2]).read_text(encoding="utf-8"))
if set(manifest["apis"]) != set(overrides["apis"]):
    raise SystemExit("Java overrides and shared OpenAPI config define different APIs")
for api_name, api in manifest["apis"].items():
    api["modelPackage"] = overrides["apis"][api_name]["modelPackage"]
manifest_path.write_text(
    json.dumps(manifest, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
    encoding="utf-8",
)
PY

generate_api() {
  local api_name="$1"
  local model_package
  local package_path
  local api_output="${generator_dir}/${api_name}"

  model_package="$(python3 -c 'import json,sys; print(json.load(open(sys.argv[1]))["apis"][sys.argv[2]]["modelPackage"])' "$java_overrides_file" "$api_name")"
  package_path="${model_package//.//}"

  mkdir -p "$api_output"
  docker run --rm \
    --user "$(id -u):$(id -g)" \
    --volume "${prepared_dir}:/prepared:ro" \
    --volume "${config_file}:/config/java-generator.json:ro" \
    --volume "${api_output}:/out" \
    "$GENERATOR_IMAGE" generate \
    --input-spec "/prepared/${api_name}.json" \
    --generator-name java \
    --output /out \
    --config /config/java-generator.json \
    --invoker-package com.cregis.sdk.generated \
    --model-package "$model_package" \
    --global-property models,modelDocs=false,modelTests=false

  if [[ ! -d "${api_output}/src/main/java/${package_path}" ]]; then
    echo "Generator did not create expected model package: ${model_package}" >&2
    exit 1
  fi
  mkdir -p "${candidate_root}/${package_path}"
  cp -R "${api_output}/src/main/java/${package_path}/." "${candidate_root}/${package_path}/"
}

generate_api payment
generate_api waas
generate_api team

python3 - "$candidate_root" <<'PY'
import pathlib
import sys

root = pathlib.Path(sys.argv[1])
for path in sorted(root.rglob("*.java")):
    lines = path.read_text(encoding="utf-8").splitlines()
    while lines and not lines[-1].strip():
        lines.pop()
    path.write_text(
        "\n".join(line.rstrip() for line in lines) + "\n",
        encoding="utf-8",
    )
PY

python3 - "$prepared_dir/manifest.json" "$candidate_root" <<'PY'
import json
import pathlib
import sys

manifest = json.loads(pathlib.Path(sys.argv[1]).read_text(encoding="utf-8"))
root = pathlib.Path(sys.argv[2])
for api_name, api in manifest["apis"].items():
    package = pathlib.Path(*api["modelPackage"].split("."))
    for model in api["models"]:
        expected = root / package / f"{model}.java"
        if not expected.is_file():
            raise SystemExit(f"Missing generated {api_name} model: {model}")
PY

if [[ "$check_only" == "true" ]]; then
  if [[ ! -d "$generated_root" || ! -f "$lock_file" ]]; then
    echo "Committed Java generated models or lock manifest are missing" >&2
    exit 1
  fi
  if ! diff -ru "$generated_root" "$candidate_root"; then
    echo "Committed Java generated models are stale" >&2
    exit 1
  fi
  if ! cmp -s "$prepared_dir/manifest.json" "$lock_file"; then
    echo "Committed Java model lock manifest is stale" >&2
    diff -u "$lock_file" "$prepared_dir/manifest.json" || true
    exit 1
  fi
  "${repo_root}/codegen/scripts/check-java-generated-models.py"
  echo "Committed Java generated models match the canonical OpenAPI specs"
  exit 0
fi

mkdir -p "$generated_root" "$(dirname "$lock_file")"
find "$generated_root" -mindepth 1 -delete
cp -R "${candidate_root}/." "$generated_root/"
cp "$prepared_dir/manifest.json" "$lock_file"

"${repo_root}/codegen/scripts/check-java-generated-models.py"

model_count="$(find "$generated_root" -type f -name '*.java' | wc -l | tr -d ' ')"
echo "Updated ${model_count} committed Java models in: ${generated_root}"
