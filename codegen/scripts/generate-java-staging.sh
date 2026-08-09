#!/usr/bin/env bash
set -euo pipefail

GENERATOR_IMAGE="openapitools/openapi-generator-cli:7.19.0@sha256:b9e7ad71a9f9406bd810378a939755fad114747a767e29bbf83ef9364d5f9dc0"

usage() {
  echo "Usage: $0 --spec-dir DIR --output-dir EMPTY_DIR" >&2
}

spec_dir=""
output_dir=""
while [[ $# -gt 0 ]]; do
  case "$1" in
    --spec-dir)
      spec_dir="${2:-}"
      shift 2
      ;;
    --output-dir)
      output_dir="${2:-}"
      shift 2
      ;;
    *)
      usage
      exit 2
      ;;
  esac
done

if [[ -z "$spec_dir" || -z "$output_dir" ]]; then
  usage
  exit 2
fi

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd "${script_dir}/../.." && pwd)"
config_file="${repo_root}/codegen/configs/java-generator.json"

if [[ ! -d "$spec_dir" ]]; then
  echo "Spec directory does not exist: $spec_dir" >&2
  exit 2
fi
spec_dir="$(cd "$spec_dir" && pwd)"

for spec_file in payment-engine-api.json waas-api.json team-api.json; do
  if [[ ! -f "${spec_dir}/${spec_file}" ]]; then
    echo "Missing canonical spec: ${spec_dir}/${spec_file}" >&2
    exit 2
  fi
done

mkdir -p "$output_dir"
output_dir="$(cd "$output_dir" && pwd)"
if find "$output_dir" -mindepth 1 -maxdepth 1 -print -quit | grep -q .; then
  echo "Output directory must be empty: $output_dir" >&2
  exit 2
fi

generate_models() {
  local api_name="$1"
  local spec_file="$2"
  local model_package="com.cregis.sdk.generated.${api_name}.model"

  mkdir -p "${output_dir}/${api_name}"
  docker run --rm \
    --user "$(id -u):$(id -g)" \
    --volume "${spec_dir}:/specs:ro" \
    --volume "${config_file}:/config/java-generator.json:ro" \
    --volume "${output_dir}:/out" \
    "$GENERATOR_IMAGE" generate \
    --input-spec "/specs/${spec_file}" \
    --generator-name java \
    --output "/out/${api_name}" \
    --config /config/java-generator.json \
    --model-package "$model_package" \
    --global-property models,modelDocs=false,modelTests=false
}

generate_models payment payment-engine-api.json
generate_models waas waas-api.json
generate_models team team-api.json

echo "Generated Java model staging output in: $output_dir"
