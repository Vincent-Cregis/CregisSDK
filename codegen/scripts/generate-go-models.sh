#!/usr/bin/env bash
set -euo pipefail

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
config_file="${repo_root}/codegen/configs/go-generator.json"
overrides_file="${repo_root}/codegen/configs/go-overrides.json"
generated_root="${repo_root}/sdks/go/generated"
alias_file="${repo_root}/sdks/go/models_aliases.gen.go"
clients_file="${repo_root}/sdks/go/clients.gen.go"
lock_file="${repo_root}/codegen/manifests/go-models.lock.json"

if [[ ! -d "$spec_dir" ]]; then
  echo "Spec directory does not exist: $spec_dir" >&2
  exit 2
fi
spec_dir="$(cd "$spec_dir" && pwd)"

"${repo_root}/codegen/scripts/check-java-openapi.py" \
  --spec-dir "$spec_dir" \
  --skip-source-check

work_dir="$(mktemp -d)"
cleanup() {
  rm -rf "$work_dir"
}
trap cleanup EXIT

prepared_dir="${work_dir}/prepared"
candidate_root="${work_dir}/candidate"
candidate_lock="${work_dir}/go-models.lock.json"
mkdir -p "$prepared_dir" "$candidate_root"

"${repo_root}/codegen/scripts/prepare-openapi.py" \
  --spec-dir "$spec_dir" \
  --output-dir "$prepared_dir" \
  --language Go \
  --include-webhooks

"${repo_root}/codegen/scripts/render-go-models.py" \
  --prepared-dir "$prepared_dir" \
  --output-dir "$candidate_root" \
  --overrides "$overrides_file" \
  --config "$config_file" \
  --manifest "$candidate_lock"

gofmt -w "$candidate_root"

if [[ "$check_only" == "true" ]]; then
  if [[ ! -d "$generated_root" || ! -f "$alias_file" || ! -f "$clients_file" || ! -f "$lock_file" ]]; then
    echo "Committed Go generated files or lock manifest are missing" >&2
    exit 1
  fi
  diff -ru "$generated_root" "$candidate_root/generated"
  cmp "$alias_file" "$candidate_root/models_aliases.gen.go"
  cmp "$clients_file" "$candidate_root/clients.gen.go"
  cmp "$lock_file" "$candidate_lock"
  "${repo_root}/codegen/scripts/check-go-generated-models.py"
  echo "Committed Go models match the canonical OpenAPI specs"
  exit 0
fi

mkdir -p "$generated_root" "$(dirname "$lock_file")"
find "$generated_root" -mindepth 1 -delete
cp -R "$candidate_root/generated/." "$generated_root/"
cp "$candidate_root/models_aliases.gen.go" "$alias_file"
cp "$candidate_root/clients.gen.go" "$clients_file"
cp "$candidate_lock" "$lock_file"

"${repo_root}/codegen/scripts/check-go-generated-models.py"
echo "Generated Go models from canonical OpenAPI specs"

