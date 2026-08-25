#!/usr/bin/env python3
"""Validate the committed Python generated-model boundary."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from typing import Any


class GeneratedModelError(RuntimeError):
    pass


def read_json(path: Path) -> dict[str, Any]:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (FileNotFoundError, json.JSONDecodeError) as exc:
        raise GeneratedModelError(f"Cannot read JSON file {path}: {exc}") from exc
    if not isinstance(value, dict):
        raise GeneratedModelError(f"JSON root must be an object: {path}")
    return value


def snake_case(name: str) -> str:
    first = re.sub(r"(.)([A-Z][a-z]+)", r"\1_\2", name)
    return re.sub(r"([a-z0-9])([A-Z])", r"\1_\2", first).lower()


def main() -> int:
    repo_root = Path(__file__).resolve().parents[2]
    operations = read_json(repo_root / "codegen/configs/openapi-operations.json")
    models = read_json(repo_root / "codegen/configs/openapi-models.json")
    overrides = read_json(repo_root / "codegen/configs/python-overrides.json")
    lock = read_json(repo_root / "codegen/manifests/python-models.lock.json")
    generated_root = repo_root / "sdks/python/src/cregis/generated"

    api_names = set(operations.get("apis", {}))
    if api_names != set(models.get("apis", {})) \
            or api_names != set(overrides.get("apis", {})) \
            or api_names != set(lock.get("apis", {})):
        raise GeneratedModelError("Python configs and lock manifest define different APIs")

    expected_files: set[Path] = {Path("__init__.py")}
    total_models = 0
    total_operations = 0
    for api_name, api_lock in lock["apis"].items():
        expected_files.update({
            Path(api_name, "__init__.py"),
            Path(api_name, "operations.py"),
            Path(api_name, "webhooks.py"),
            Path(api_name, "models", "__init__.py"),
        })
        model_names = api_lock.get("models")
        if not isinstance(model_names, list) or not all(isinstance(name, str) for name in model_names):
            raise GeneratedModelError(f"Invalid locked model list for {api_name}")
        model_policy = api_lock.get("modelPolicy")
        if not isinstance(model_policy, dict) or set(model_policy) != set(model_names):
            raise GeneratedModelError(f"Invalid locked model policy for {api_name}")
        for model_name, policy in model_policy.items():
            if not isinstance(policy, dict) or policy.get("direction") not in {"request", "output"}:
                raise GeneratedModelError(f"Invalid model direction for {api_name}.{model_name}")
            expected_extra = "forbid" if policy["direction"] == "request" else "ignore"
            if policy.get("extra") != expected_extra:
                raise GeneratedModelError(f"Invalid extra-field policy for {api_name}.{model_name}")
        total_models += len(model_names)
        expected_files.update(
            Path(api_name, "models", f"{snake_case(name)}.py")
            for name in model_names
        )

        runtime_any_of = api_lock.get("runtimeAnyOf", {})
        if not isinstance(runtime_any_of, dict):
            raise GeneratedModelError(f"Invalid runtime anyOf lock data for {api_name}")
        for model_name, required_groups in runtime_any_of.items():
            if model_name not in model_names or not isinstance(required_groups, list):
                raise GeneratedModelError(f"Invalid runtime anyOf model for {api_name}.{model_name}")
            source_path = generated_root / api_name / "models" / f"{snake_case(model_name)}.py"
            source = source_path.read_text(encoding="utf-8")
            if "_validate_cregis_runtime_any_of" not in source:
                raise GeneratedModelError(
                    f"Missing runtime anyOf validator for {api_name}.{model_name}"
                )
            for group in required_groups:
                if not isinstance(group, list) or not all(isinstance(field, str) for field in group):
                    raise GeneratedModelError(
                        f"Invalid runtime anyOf fields for {api_name}.{model_name}"
                    )
                for field in group:
                    if repr(field) not in source:
                        raise GeneratedModelError(
                            f"Missing runtime anyOf field {field!r} for {api_name}.{model_name}"
                        )

        locked_operations = api_lock.get("operations")
        if not isinstance(locked_operations, list):
            raise GeneratedModelError(f"Invalid locked operations for {api_name}")
        total_operations += len(locked_operations)
        configured_operations = models["apis"][api_name]["operations"]
        methods = overrides["apis"][api_name].get("clientMethods")
        if not isinstance(methods, dict):
            raise GeneratedModelError(f"Missing Python client method mapping for {api_name}")
        for operation in locked_operations:
            operation_id = operation["operationId"]
            mapping = configured_operations.get(operation_id)
            if not isinstance(mapping, dict) or operation_id not in methods:
                raise GeneratedModelError(f"Missing Python operation mapping: {api_name}.{operation_id}")
            for key in ("requestModel", "responseModel", "responseContainer"):
                if operation.get(key) != mapping.get(key):
                    raise GeneratedModelError(
                        f"Stale Python operation mapping: {api_name}.{operation_id}.{key}"
                    )

            request_model = operation.get("requestModel")
            if isinstance(request_model, str):
                source_path = generated_root / api_name / "models" / f"{snake_case(request_model)}.py"
                source = source_path.read_text(encoding="utf-8")
                for field in models["sdkManagedRequestFields"]:
                    if re.search(rf"^\s*{re.escape(field)}:\s", source, re.MULTILINE):
                        raise GeneratedModelError(
                            f"SDK-managed field {field!r} leaked into {api_name}.{request_model}"
                        )

    actual_files = {
        path.relative_to(generated_root)
        for path in generated_root.rglob("*.py")
    } if generated_root.is_dir() else set()
    if actual_files != expected_files:
        missing = sorted(str(path) for path in expected_files - actual_files)
        extra = sorted(str(path) for path in actual_files - expected_files)
        raise GeneratedModelError(f"Generated Python files are stale; missing={missing}, extra={extra}")

    for api_name, api_lock in lock["apis"].items():
        for model_name, policy in api_lock["modelPolicy"].items():
            path = generated_root / api_name / "models" / f"{snake_case(model_name)}.py"
            source = path.read_text(encoding="utf-8")
            markers = (
                "strict=True",
                f'extra="{policy["extra"]}"',
                'revalidate_instances="always"',
            )
            for marker in markers:
                if marker not in source:
                    raise GeneratedModelError(
                        f"Generated model does not enforce {marker} ({api_name}.{model_name})"
                    )
            if policy["direction"] == "output" \
                    and "# raise errors for additional fields in the input" in source:
                raise GeneratedModelError(
                    f"Generated output model rejects forward-compatible fields: "
                    f"{api_name}.{model_name}"
                )

    generator = lock.get("generator")
    script = (repo_root / "codegen/scripts/generate-python-models.sh").read_text(encoding="utf-8")
    if not isinstance(generator, dict) or not re.search(
        rf'{re.escape(str(generator.get("image")))}:{re.escape(str(generator.get("version")))}@'
        rf'{re.escape(str(generator.get("digest")))}',
        script,
    ):
        raise GeneratedModelError("Python generation script does not pin the locked generator")

    print(
        f"Committed Python model boundary is consistent: "
        f"{total_models} models, {total_operations} operations"
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except GeneratedModelError as exc:
        print(f"Python generated-model check failed: {exc}", file=sys.stderr)
        raise SystemExit(1)
