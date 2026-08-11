#!/usr/bin/env python3
"""Validate the committed Java generated-model boundary without OpenAPI files."""

from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path
from typing import Any, Dict, List, Mapping, Optional, Set


class GeneratedModelError(RuntimeError):
    """Raised when committed generated Java models are inconsistent."""


def read_json(path: Path) -> Dict[str, Any]:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except FileNotFoundError as exc:
        raise GeneratedModelError(f"Missing file: {path}") from exc
    except json.JSONDecodeError as exc:
        raise GeneratedModelError(f"Invalid JSON in {path}: {exc}") from exc
    if not isinstance(value, dict):
        raise GeneratedModelError(f"JSON root must be an object: {path}")
    return value


def operation_records(
    operation_api: Mapping[str, Any],
    model_api: Mapping[str, Any],
) -> List[Dict[str, Any]]:
    inventory = operation_api.get("operations")
    model_operations = model_api.get("operations")
    if not isinstance(inventory, list) or not isinstance(model_operations, dict):
        raise GeneratedModelError("Invalid Java operation or model configuration")

    records: List[Dict[str, Any]] = []
    for operation in inventory:
        if not isinstance(operation, dict):
            raise GeneratedModelError("Invalid Java operation entry")
        operation_id = operation.get("operationId")
        model = model_operations.get(operation_id)
        if not isinstance(operation_id, str) or not isinstance(model, dict):
            raise GeneratedModelError(f"Missing model mapping for operation: {operation_id}")
        records.append({
            "method": str(operation.get("method", "")).lower(),
            "operationId": operation_id,
            "path": operation.get("path"),
            "requestModel": model.get("requestModel"),
            "responseContainer": model.get("responseContainer"),
            "responseModel": model.get("responseModel"),
        })

    configured_ids = set(model_operations)
    inventory_ids = {record["operationId"] for record in records}
    if configured_ids != inventory_ids:
        raise GeneratedModelError(
            "Java model mappings do not match the operation inventory; "
            f"missing={sorted(inventory_ids - configured_ids)}, "
            f"extra={sorted(configured_ids - inventory_ids)}"
        )
    return records


def check(repo_root: Path) -> int:
    operation_config = read_json(repo_root / "codegen/configs/java-operations.json")
    model_config = read_json(repo_root / "codegen/configs/java-models.json")
    lock = read_json(repo_root / "codegen/manifests/java-models.lock.json")

    operation_apis = operation_config.get("apis")
    model_apis = model_config.get("apis")
    lock_apis = lock.get("apis")
    if not all(isinstance(value, dict) for value in (operation_apis, model_apis, lock_apis)):
        raise GeneratedModelError("Java configs and lock manifest must define APIs")
    if set(operation_apis) != set(model_apis) or set(operation_apis) != set(lock_apis):
        raise GeneratedModelError("Java configs and lock manifest define different APIs")

    managed_fields = model_config.get("sdkManagedRequestFields")
    if not isinstance(managed_fields, list) or not all(isinstance(item, str) for item in managed_fields):
        raise GeneratedModelError("sdkManagedRequestFields must be a string array")

    generated_root = repo_root / "sdks/java/src/generated/java"
    expected_files: Set[Path] = set()
    expected_model_names: Set[str] = set()
    total_operations = 0

    for api_name in sorted(operation_apis):
        operation_api = operation_apis[api_name]
        model_api = model_apis[api_name]
        locked_api = lock_apis[api_name]
        if not all(isinstance(value, dict) for value in (operation_api, model_api, locked_api)):
            raise GeneratedModelError(f"Invalid API configuration: {api_name}")

        package = model_api.get("modelPackage")
        models = locked_api.get("models")
        if not isinstance(package, str) or not isinstance(models, list) or not all(
            isinstance(model, str) for model in models
        ):
            raise GeneratedModelError(f"Invalid package or model lock for API: {api_name}")
        if locked_api.get("modelPackage") != package:
            raise GeneratedModelError(f"Locked model package is stale for API: {api_name}")
        if locked_api.get("specFile") != operation_api.get("specFile"):
            raise GeneratedModelError(f"Locked spec file is stale for API: {api_name}")

        expected_operations = operation_records(operation_api, model_api)
        if locked_api.get("operations") != expected_operations:
            raise GeneratedModelError(f"Locked operations are stale for API: {api_name}")
        total_operations += len(expected_operations)

        package_path = Path(*package.split("."))
        for model in models:
            expected_files.add(package_path / f"{model}.java")
            expected_model_names.add(model)

        request_models = {
            operation["requestModel"]
            for operation in expected_operations
            if isinstance(operation.get("requestModel"), str)
        }
        for request_model in sorted(request_models):
            request_file = generated_root / package_path / f"{request_model}.java"
            if not request_file.is_file():
                raise GeneratedModelError(
                    f"Missing generated request model for {api_name}: {request_model}"
                )
            source = request_file.read_text(encoding="utf-8")
            for field in managed_fields:
                declaration = re.compile(
                    rf'JSON_PROPERTY_[A-Z0-9_]+\s*=\s*"{re.escape(field)}"'
                )
                if declaration.search(source):
                    raise GeneratedModelError(
                        f"SDK-managed field {field!r} leaked into request model "
                        f"{api_name}.{request_model}"
                    )

        client_source = operation_api.get("clientSource")
        if not isinstance(client_source, str):
            raise GeneratedModelError(f"Missing clientSource for API: {api_name}")
        client_path = repo_root / "sdks/java/src/main/java" / client_source
        try:
            client = client_path.read_text(encoding="utf-8")
        except FileNotFoundError as exc:
            raise GeneratedModelError(f"Missing Java client: {client_path}") from exc
        public_models = {
            value
            for operation in expected_operations
            for value in (operation.get("requestModel"), operation.get("responseModel"))
            if isinstance(value, str)
        }
        for model in sorted(public_models):
            if f"import {package}.{model};" not in client:
                raise GeneratedModelError(
                    f"Java client {client_path.name} does not import generated model "
                    f"{package}.{model}"
                )

    actual_files = {
        path.relative_to(generated_root)
        for path in generated_root.rglob("*.java")
    } if generated_root.is_dir() else set()
    missing = sorted(str(path) for path in expected_files - actual_files)
    extra = sorted(str(path) for path in actual_files - expected_files)
    if missing or extra:
        raise GeneratedModelError(
            f"Committed generated model files are stale; missing={missing}, extra={extra}"
        )

    handwritten_root = repo_root / "sdks/java/src/main/java"
    handwritten_duplicates = sorted(
        str(path.relative_to(repo_root))
        for path in handwritten_root.rglob("*.java")
        if path.stem in expected_model_names
    ) if handwritten_root.is_dir() else []
    if handwritten_duplicates:
        raise GeneratedModelError(
            "Handwritten operation models duplicate generated models: "
            + ", ".join(handwritten_duplicates)
        )

    generator = lock.get("generator")
    if not isinstance(generator, dict):
        raise GeneratedModelError("Lock manifest has no generator identity")
    image = generator.get("image")
    version = generator.get("version")
    script_path = repo_root / "codegen/scripts/generate-java-models.sh"
    try:
        generation_script = script_path.read_text(encoding="utf-8")
    except FileNotFoundError as exc:
        raise GeneratedModelError(f"Missing generation script: {script_path}") from exc
    if not isinstance(image, str) or not isinstance(version, str) or not re.search(
        rf'{re.escape(image)}:{re.escape(version)}@sha256:[0-9a-f]{{64}}',
        generation_script,
    ):
        raise GeneratedModelError("Generation script does not pin the locked generator by digest")

    print(
        f"Committed Java model boundary is consistent: "
        f"{len(expected_files)} models, {total_operations} operations"
    )
    return 0


def parse_args(argv: Optional[List[str]] = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--repo-root", type=Path)
    return parser.parse_args(argv)


def main(argv: Optional[List[str]] = None) -> int:
    args = parse_args(argv)
    repo_root = args.repo_root or Path(__file__).resolve().parents[2]
    try:
        return check(repo_root.resolve())
    except GeneratedModelError as exc:
        print(f"Java generated-model check failed: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
