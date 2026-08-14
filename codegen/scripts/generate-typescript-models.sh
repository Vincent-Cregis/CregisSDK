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
config_file="${repo_root}/codegen/configs/typescript-generator.json"
operations_file="${repo_root}/codegen/configs/openapi-operations.json"
template_dir="${repo_root}/codegen/templates/typescript-fetch"
generated_root="${repo_root}/sdks/typescript/src/generated"
lock_file="${repo_root}/codegen/manifests/typescript-models.lock.json"

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
generator_dir="${work_dir}/generator"
candidate_root="${work_dir}/candidate"
mkdir -p "$prepared_dir" "$generator_dir" "$candidate_root"

"${repo_root}/codegen/scripts/prepare-openapi.py" \
  --spec-dir "$spec_dir" \
  --output-dir "$prepared_dir" \
  --language TypeScript \
  --include-webhooks

generate_api() {
  local api_name="$1"
  local api_output="${generator_dir}/${api_name}"

  mkdir -p "$api_output"
  docker run --rm \
    --user "$(id -u):$(id -g)" \
    --volume "${prepared_dir}:/prepared:ro" \
    --volume "${config_file}:/config/typescript-generator.json:ro" \
    --volume "${template_dir}:/templates:ro" \
    --volume "${api_output}:/out" \
    "$GENERATOR_IMAGE" generate \
    --input-spec "/prepared/${api_name}.json" \
    --generator-name typescript-fetch \
    --output /out \
    --config /config/typescript-generator.json \
    --template-dir /templates \
    --type-mappings Date=string \
    --global-property models,modelDocs=false,modelTests=false

  if [[ ! -d "${api_output}/models" ]]; then
    echo "Generator did not create TypeScript models for: ${api_name}" >&2
    exit 1
  fi
  mkdir -p "${candidate_root}/${api_name}"
  cp -R "${api_output}/models/." "${candidate_root}/${api_name}/"
}

generate_api payment
generate_api waas
generate_api team

python3 - "$prepared_dir" "$operations_file" "$candidate_root" <<'PY'
import json
import pathlib
import re
import sys

prepared_root = pathlib.Path(sys.argv[1])
operations_config = json.loads(pathlib.Path(sys.argv[2]).read_text(encoding="utf-8"))
candidate_root = pathlib.Path(sys.argv[3])

all_models = set()
for api_name in sorted(operations_config["apis"]):
    api_root = candidate_root / api_name
    prepared = json.loads((prepared_root / f"{api_name}.json").read_text(encoding="utf-8"))
    for model_path in sorted(api_root.glob("*.ts")):
        source = model_path.read_text(encoding="utf-8")
        if "FromJSON" not in source:
            continue
        imports = re.findall(r"^import type [^;]+;$", source, re.MULTILINE)
        type_match = re.search(r"^export type [^;]+;$", source, re.MULTILINE)
        if type_match is None:
            raise SystemExit(f"Cannot reduce generated runtime model to a type alias: {model_path}")
        reduced = [
            "// Generated from the canonical Cregis OpenAPI specification. Do not edit.",
            *imports,
            "" if imports else None,
            type_match.group(0),
            "",
        ]
        model_path.write_text(
            "\n".join(line for line in reduced if line is not None),
            encoding="utf-8",
        )

    allowed_schema_keys = {
        "$ref", "additionalProperties", "allOf", "anyOf", "const", "enum", "format",
        "items", "maxItems", "maxLength", "maximum", "minItems", "minLength", "minimum",
        "nullable", "oneOf", "pattern", "properties", "required", "type",
        "x-cregis-event-payload", "x-cregis-runtime-anyOf",
    }

    def runtime_schema(value):
        if isinstance(value, dict):
            result = {}
            for key, item in value.items():
                if key not in allowed_schema_keys:
                    continue
                if key == "properties" and isinstance(item, dict):
                    result[key] = {
                        property_name: runtime_schema(property_schema)
                        for property_name, property_schema in item.items()
                    }
                elif key == "x-cregis-event-payload" and isinstance(item, dict):
                    result[key] = {
                        "eventProperty": item.get("eventProperty"),
                        "payloadProperty": item.get("payloadProperty"),
                        "mapping": dict(item.get("mapping", {})),
                    }
                elif key == "x-cregis-runtime-anyOf":
                    result["anyOf"] = runtime_schema(item)
                else:
                    result[key] = runtime_schema(item)
            return result
        if isinstance(value, list):
            return [runtime_schema(item) for item in value]
        return value

    schema_constant = f"{api_name}Schemas"
    schemas = {
        name: runtime_schema(schema)
        for name, schema in prepared["components"]["schemas"].items()
    }
    schema_lines = [
        "// Generated from the canonical Cregis OpenAPI specification. Do not edit.",
        f"export const {schema_constant} = "
        + json.dumps(schemas, ensure_ascii=False, indent=2, separators=(",", ": "))
        + " as const;",
        "",
    ]
    (api_root / "schemas.ts").write_text("\n".join(schema_lines), encoding="utf-8")

    operation_lines = [
        "// Generated from the canonical Cregis OpenAPI specification. Do not edit.",
        'import type { GeneratedOperation } from "../../core/types.js";',
        f'import {{ {schema_constant} }} from "./schemas.js";',
        "",
        f"export const {api_name}Operations: Readonly<Record<"
        + " | ".join(json.dumps(item["operationId"]) for item in prepared["x-cregis-operations"])
        + ", GeneratedOperation>> = {",
    ]
    for operation in prepared["x-cregis-operations"]:
        operation_id = operation["operationId"]
        request_model = operation.get("requestModel")
        required = []
        if request_model is not None:
            required = prepared["components"]["schemas"][request_model].get("required", [])
        rendered_required = json.dumps(required, ensure_ascii=False, separators=(",", ":"))
        request_schema = (
            {"$ref": f"#/components/schemas/{request_model}"}
            if request_model is not None else None
        )
        response_model = operation.get("responseModel")
        response_schema = None
        if response_model is not None:
            response_schema = {"$ref": f"#/components/schemas/{response_model}"}
            if operation.get("responseContainer") == "array":
                response_schema = {"type": "array", "items": response_schema}
        operation_lines.extend([
            f"  {operation_id}: {{",
            f'    method: "{operation["method"].upper()}",',
            f'    operationId: "{operation_id}",',
            f'    path: "{operation["path"]}",',
            f"    requiredRequestFields: {rendered_required},",
            f"    requestSchema: {json.dumps(request_schema, separators=(',', ':'))},",
            f"    responseSchema: {json.dumps(response_schema, separators=(',', ':'))},",
            f"    schemas: {schema_constant},",
            "  },",
        ])
    operation_lines.extend(["} as const;", ""])
    (api_root / "operations.ts").write_text("\n".join(operation_lines), encoding="utf-8")

    webhook_lines = [
        "// Generated from the canonical Cregis OpenAPI specification. Do not edit.",
        'import type { GeneratedWebhook } from "../../core/types.js";',
        f'import {{ {schema_constant} }} from "./schemas.js";',
        "",
        f"export const {api_name}Webhooks: Readonly<Record<"
        + (" | ".join(json.dumps(item["name"]) for item in prepared.get("x-cregis-webhooks", [])) or "never")
        + ", GeneratedWebhook>> = {",
    ]
    for webhook in prepared.get("x-cregis-webhooks", []):
        webhook_lines.extend([
            f'  {webhook["name"]}: {{',
            f'    operationId: "{webhook["operationId"]}",',
            f'    schema: {{"$ref":"#/components/schemas/{webhook["model"]}"}},',
            f"    schemas: {schema_constant},",
            "  },",
        ])
    webhook_lines.extend(["} as const;", ""])
    (api_root / "webhooks.ts").write_text("\n".join(webhook_lines), encoding="utf-8")

    generated_support_files = {"operations.ts", "schemas.ts", "webhooks.ts"}
    model_names = sorted(
        path.stem for path in api_root.glob("*.ts") if path.name not in generated_support_files
    )
    overlap = all_models.intersection(model_names)
    if overlap:
        raise SystemExit(f"Generated model names collide across APIs: {sorted(overlap)}")
    all_models.update(model_names)

    index_lines = ["// Generated file. Do not edit."]
    index_lines.extend(f'export * from "./{name}.js";' for name in model_names)
    index_lines.append("")
    (api_root / "index.ts").write_text("\n".join(index_lines), encoding="utf-8")

root_index = ["// Generated file. Do not edit."]
root_index.extend(f'export * from "./{api_name}/index.js";' for api_name in sorted(operations_config["apis"]))
root_index.append("")
(candidate_root / "index.ts").write_text("\n".join(root_index), encoding="utf-8")

for path in sorted(candidate_root.rglob("*.ts")):
    lines = path.read_text(encoding="utf-8").splitlines()
    while lines and not lines[-1].strip():
        lines.pop()
    path.write_text("\n".join(line.rstrip() for line in lines) + "\n", encoding="utf-8")
PY

python3 - "$prepared_dir/manifest.json" "$candidate_root" "$work_dir/typescript-lock.json" <<'PY'
import json
import pathlib
import sys

source = json.loads(pathlib.Path(sys.argv[1]).read_text(encoding="utf-8"))
candidate_root = pathlib.Path(sys.argv[2])
output = pathlib.Path(sys.argv[3])

for api_name, api in source["apis"].items():
    api.pop("modelPackage", None)
    api["generatedDirectory"] = f"sdks/typescript/src/generated/{api_name}"
    for model in api["models"]:
        if not (candidate_root / api_name / f"{model}.ts").is_file():
            raise SystemExit(f"Missing generated {api_name} model: {model}")
    support_files = {"index.ts", "operations.ts", "schemas.ts", "webhooks.ts"}
    api["models"] = sorted(
        path.stem
        for path in (candidate_root / api_name).glob("*.ts")
        if path.name not in support_files
    )

source["generator"].update({
    "digest": "sha256:b9e7ad71a9f9406bd810378a939755fad114747a767e29bbf83ef9364d5f9dc0",
    "name": "typescript-fetch",
})
output.write_text(json.dumps(source, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8")
PY

if [[ "$check_only" == "true" ]]; then
  if [[ ! -d "$generated_root" || ! -f "$lock_file" ]]; then
    echo "Committed TypeScript generated models or lock manifest are missing" >&2
    exit 1
  fi
  if ! diff -ru "$generated_root" "$candidate_root"; then
    echo "Committed TypeScript generated models are stale" >&2
    exit 1
  fi
  if ! cmp -s "$work_dir/typescript-lock.json" "$lock_file"; then
    echo "Committed TypeScript model lock manifest is stale" >&2
    diff -u "$lock_file" "$work_dir/typescript-lock.json" || true
    exit 1
  fi
  "${repo_root}/codegen/scripts/check-typescript-generated-models.py"
  echo "Committed TypeScript generated models match the canonical OpenAPI specs"
  exit 0
fi

mkdir -p "$generated_root" "$(dirname "$lock_file")"
find "$generated_root" -mindepth 1 -delete
cp -R "${candidate_root}/." "$generated_root/"
cp "$work_dir/typescript-lock.json" "$lock_file"

"${repo_root}/codegen/scripts/check-typescript-generated-models.py"

model_count="$(find "$generated_root" -type f -name '*.ts' \
  ! -name 'index.ts' ! -name 'operations.ts' ! -name 'schemas.ts' ! -name 'webhooks.ts' \
  | wc -l | tr -d ' ')"
echo "Updated ${model_count} committed TypeScript models in: ${generated_root}"
