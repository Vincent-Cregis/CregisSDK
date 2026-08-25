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
config_file="${repo_root}/codegen/configs/python-generator.json"
operations_file="${repo_root}/codegen/configs/openapi-operations.json"
overrides_file="${repo_root}/codegen/configs/python-overrides.json"
generated_root="${repo_root}/sdks/python/src/cregis/generated"
lock_file="${repo_root}/codegen/manifests/python-models.lock.json"

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
policy_file="${work_dir}/python-model-policy.json"
mkdir -p "$prepared_dir" "$generator_dir" "$candidate_root"

"${repo_root}/codegen/scripts/prepare-openapi.py" \
  --spec-dir "$spec_dir" \
  --output-dir "$prepared_dir" \
  --language Python \
  --include-webhooks

"${repo_root}/codegen/scripts/prepare-python-openapi.py" \
  --prepared-dir "$prepared_dir" \
  --policy-file "$policy_file"

generate_api() {
  local api_name="$1"
  local api_output="${generator_dir}/${api_name}"
  local package_name="cregis.generated.${api_name}"

  mkdir -p "$api_output"
  docker run --rm \
    --user "$(id -u):$(id -g)" \
    --volume "${prepared_dir}:/prepared:ro" \
    --volume "${config_file}:/config/python-generator.json:ro" \
    --volume "${api_output}:/out" \
    "$GENERATOR_IMAGE" generate \
    --input-spec "/prepared/${api_name}.json" \
    --generator-name python \
    --output /out \
    --config /config/python-generator.json \
    --additional-properties "packageName=${package_name}" \
    --global-property models,modelDocs=false,modelTests=false

  local model_output="${api_output}/cregis/generated/${api_name}/models"
  if [[ ! -d "$model_output" ]]; then
    echo "Generator did not create Python models for: ${api_name}" >&2
    exit 1
  fi
  mkdir -p "${candidate_root}/${api_name}/models"
  cp -R "${model_output}/." "${candidate_root}/${api_name}/models/"
}

generate_api payment
generate_api waas
generate_api team

python3 - \
  "$prepared_dir" \
  "$operations_file" \
  "$overrides_file" \
  "$policy_file" \
  "$candidate_root" <<'PY'
import json
import pathlib
import re
import sys

prepared_root = pathlib.Path(sys.argv[1])
operations_config = json.loads(pathlib.Path(sys.argv[2]).read_text(encoding="utf-8"))
overrides = json.loads(pathlib.Path(sys.argv[3]).read_text(encoding="utf-8"))
policy = json.loads(pathlib.Path(sys.argv[4]).read_text(encoding="utf-8"))
candidate_root = pathlib.Path(sys.argv[5])


def snake_case(name):
    first = re.sub(r"(.)([A-Z][a-z]+)", r"\1_\2", name)
    return re.sub(r"([a-z0-9])([A-Z])", r"\1_\2", first).lower()


all_models = set()
generated_models = {}
runtime_any_of_constraints = {}
for api_name in sorted(operations_config["apis"]):
    api_root = candidate_root / api_name
    models_root = api_root / "models"
    prepared = json.loads((prepared_root / f"{api_name}.json").read_text(encoding="utf-8"))
    expected_model_names = set(prepared["components"]["schemas"])
    model_names = []
    model_sources = {}
    for generated_path in sorted(models_root.glob("*.py")):
        if generated_path.name == "__init__.py":
            continue
        generated_source = generated_path.read_text(encoding="utf-8")
        class_match = re.search(
            r"^class\s+([A-Za-z_][A-Za-z0-9_]*)\b",
            generated_source,
            re.MULTILINE,
        )
        if class_match is None:
            raise SystemExit(f"Cannot discover generated model class: {generated_path}")
        class_name = class_match.group(1)
        model_names.append(class_name)
        model_sources[class_name] = generated_source
    model_names = sorted(set(model_names))
    missing_models = expected_model_names.difference(model_names)
    if missing_models:
        raise SystemExit(
            f"Generator omitted prepared Python models for {api_name}: {sorted(missing_models)}"
        )
    generated_models[api_name] = model_names
    runtime_any_of_constraints[api_name] = {}
    model_policies = dict(policy["apis"][api_name]["models"])
    dependencies = {
        model_name: set(re.findall(
            rf"^from cregis\.generated\.{re.escape(api_name)}\.models\.[^\s]+ "
            r"import ([A-Za-z_][A-Za-z0-9_]*)",
            source,
            re.MULTILINE,
        ))
        for model_name, source in model_sources.items()
    }
    changed = True
    while changed:
        changed = False
        for parent, children in dependencies.items():
            parent_policy = model_policies.get(parent)
            if parent_policy is None:
                continue
            for child in children.intersection(model_names):
                child_policy = model_policies.get(child)
                if child_policy is not None and child_policy != parent_policy:
                    raise SystemExit(
                        f"Generated Python model directions conflict: {api_name}.{parent}->{child}"
                    )
                if child_policy is None:
                    model_policies[child] = dict(parent_policy)
                    changed = True
    unclassified_models = set(model_names).difference(model_policies)
    if unclassified_models:
        raise SystemExit(
            f"Generated Python models have no runtime policy for {api_name}: "
            f"{sorted(unclassified_models)}"
        )
    policy["apis"][api_name]["models"] = dict(sorted(model_policies.items()))
    overlap = all_models.intersection(model_names)
    if overlap:
        raise SystemExit(f"Generated model names collide across APIs: {sorted(overlap)}")
    all_models.update(model_names)

    for model_name in model_names:
        model_path = models_root / f"{snake_case(model_name)}.py"
        if not model_path.is_file():
            raise SystemExit(f"Missing generated Python model: {api_name}.{model_name}")
        source = model_path.read_text(encoding="utf-8")
        model_schema = prepared["components"]["schemas"].get(model_name, {})
        runtime_any_of = model_schema.get("x-cregis-runtime-anyOf", [])
        if runtime_any_of:
            required_groups = []
            for alternative in runtime_any_of:
                if set(alternative) != {"required"} or not isinstance(
                    alternative["required"], list
                ):
                    raise SystemExit(
                        f"Unsupported runtime anyOf alternative: {api_name}.{model_name}"
                    )
                required = alternative["required"]
                if not required or not all(isinstance(field, str) for field in required):
                    raise SystemExit(
                        f"Invalid runtime anyOf required fields: {api_name}.{model_name}"
                    )
                required_groups.append(required)
            runtime_any_of_constraints[api_name][model_name] = required_groups
            source = source.replace(
                "from pydantic import ",
                "from pydantic import model_validator, ",
                1,
            )
            config_marker = "    model_config = ConfigDict(\n"
            validator = (
                "    @model_validator(mode=\"after\")\n"
                "    def _validate_cregis_runtime_any_of(self):\n"
                f"        required_groups = {required_groups!r}\n"
                "        if not any(\n"
                "            all(getattr(self, field) is not None for field in group)\n"
                "            for group in required_groups\n"
                "        ):\n"
                "            raise ValueError(\n"
                "                \"at least one OpenAPI anyOf required-field group must be present\"\n"
                "            )\n"
                "        return self\n\n"
            )
            if config_marker not in source:
                raise SystemExit(f"Cannot add runtime anyOf validator: {model_path}")
            source = source.replace(config_marker, validator + config_marker, 1)
        marker = "    model_config = ConfigDict(\n"
        extra_mode = model_policies[model_name]["extra"]
        if extra_mode not in {"forbid", "ignore"}:
            raise SystemExit(f"Unsupported Python extra-field policy: {api_name}.{model_name}")
        replacement = (
            "    model_config = ConfigDict(\n"
            "        strict=True,\n"
            f"        extra=\"{extra_mode}\",\n"
            "        revalidate_instances=\"always\",\n"
        )
        if marker not in source:
            raise SystemExit(f"Cannot enable strict Pydantic config: {model_path}")
        source = source.replace(marker, replacement)
        if extra_mode == "ignore":
            source = re.sub(
                r"\n        # raise errors for additional fields in the input\n"
                r"        for _key in obj\.keys\(\):\n"
                r"            if _key not in cls\.__properties:\n"
                r"                raise ValueError\([^\n]+\)\n",
                "\n",
                source,
            )
        source = "# Generated from the canonical Cregis OpenAPI specification. Do not edit.\n" + source
        model_path.write_text(source, encoding="utf-8")

    model_index = ["# Generated file. Do not edit."]
    model_index.extend(
        f"from .{snake_case(name)} import {name}"
        for name in model_names
    )
    model_index.extend(["", "__all__ = ["])
    model_index.extend(f'    "{name}",' for name in model_names)
    model_index.extend(["]", ""])
    (models_root / "__init__.py").write_text("\n".join(model_index), encoding="utf-8")

    operation_lines = [
        "# Generated from the canonical Cregis OpenAPI specification. Do not edit.",
        "from cregis.operation import GeneratedOperation",
        "from .models import *  # noqa: F403",
        "",
        "OPERATIONS = {",
    ]
    client_methods = overrides["apis"][api_name]["clientMethods"]
    for operation in prepared["x-cregis-operations"]:
        operation_id = operation["operationId"]
        request_model = operation.get("requestModel")
        response_model = operation.get("responseModel")
        response_container = operation.get("responseContainer")
        operation_lines.extend([
            f'    "{client_methods[operation_id]}": GeneratedOperation(',
            f'        operation_id="{operation_id}",',
            f'        method="{operation["method"].upper()}",',
            f'        path="{operation["path"]}",',
            f'        request_model={request_model or "None"},',
            f'        response_model={response_model or "None"},',
            f'        response_container={response_container!r},',
            "    ),",
        ])
    operation_lines.extend(["}", ""])
    (api_root / "operations.py").write_text("\n".join(operation_lines), encoding="utf-8")

    webhook_lines = [
        "# Generated from the canonical Cregis OpenAPI specification. Do not edit.",
        "from cregis.operation import GeneratedWebhook",
        "from .models import *  # noqa: F403",
        "",
        "WEBHOOKS = {",
    ]
    for webhook in prepared.get("x-cregis-webhooks", []):
        webhook_lines.extend([
            f'    "{webhook["name"]}": GeneratedWebhook(',
            f'        operation_id="{webhook["operationId"]}",',
            f'        model={webhook["model"]},',
            "    ),",
        ])
    webhook_lines.extend(["}", ""])
    (api_root / "webhooks.py").write_text("\n".join(webhook_lines), encoding="utf-8")

    api_index = [
        "# Generated file. Do not edit.",
        "from .models import *  # noqa: F403",
        "from .models import __all__ as _model_exports",
        "from .operations import OPERATIONS",
        "from .webhooks import WEBHOOKS",
        "",
        "__all__ = [*_model_exports, \"OPERATIONS\", \"WEBHOOKS\"]",
        "",
    ]
    (api_root / "__init__.py").write_text("\n".join(api_index), encoding="utf-8")
root_index = ["# Generated file. Do not edit."]
for api_name in sorted(operations_config["apis"]):
    root_index.extend([
        f"from .{api_name}.models import *  # noqa: F403",
        f"from .{api_name}.models import __all__ as _{api_name}_exports",
    ])
root_index.extend([
    "",
    "__all__ = [*_payment_exports, *_team_exports, *_waas_exports]",
    "",
])
(candidate_root / "__init__.py").write_text("\n".join(root_index), encoding="utf-8")

manifest_path = prepared_root / "manifest.json"
manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
for api_name, model_names in generated_models.items():
    manifest["apis"][api_name]["models"] = model_names
    manifest["apis"][api_name]["modelPolicy"] = policy["apis"][api_name]["models"]
    manifest["apis"][api_name]["runtimeAnyOf"] = runtime_any_of_constraints[api_name]
manifest_path.write_text(
    json.dumps(manifest, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
    encoding="utf-8",
)

for path in sorted(candidate_root.rglob("*.py")):
    lines = path.read_text(encoding="utf-8").splitlines()
    while lines and not lines[-1].strip():
        lines.pop()
    path.write_text("\n".join(line.rstrip() for line in lines) + "\n", encoding="utf-8")
PY

python3 - "$prepared_dir/manifest.json" "$candidate_root" "$work_dir/python-lock.json" <<'PY'
import json
import pathlib
import sys

source = json.loads(pathlib.Path(sys.argv[1]).read_text(encoding="utf-8"))
candidate_root = pathlib.Path(sys.argv[2])
output = pathlib.Path(sys.argv[3])

for api_name, api in source["apis"].items():
    api.pop("modelPackage", None)
    api["generatedDirectory"] = f"sdks/python/src/cregis/generated/{api_name}"

source["generator"].update({
    "digest": "sha256:b9e7ad71a9f9406bd810378a939755fad114747a767e29bbf83ef9364d5f9dc0",
    "name": "python",
})
output.write_text(
    json.dumps(source, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
    encoding="utf-8",
)
PY

if [[ "$check_only" == "true" ]]; then
  if [[ ! -d "$generated_root" || ! -f "$lock_file" ]]; then
    echo "Committed Python generated models or lock manifest are missing" >&2
    exit 1
  fi
  if ! diff -ru -x '__pycache__' -x '*.pyc' "$generated_root" "$candidate_root"; then
    echo "Committed Python generated models are stale" >&2
    exit 1
  fi
  if ! cmp -s "$work_dir/python-lock.json" "$lock_file"; then
    echo "Committed Python model lock manifest is stale" >&2
    diff -u "$lock_file" "$work_dir/python-lock.json" || true
    exit 1
  fi
  "${repo_root}/codegen/scripts/check-python-generated-models.py"
  echo "Committed Python generated models match the canonical OpenAPI specs"
  exit 0
fi

mkdir -p "$generated_root" "$(dirname "$lock_file")"
find "$generated_root" -mindepth 1 -delete
cp -R "${candidate_root}/." "$generated_root/"
cp "$work_dir/python-lock.json" "$lock_file"

"${repo_root}/codegen/scripts/check-python-generated-models.py"
echo "Generated Python models from canonical OpenAPI specs"
