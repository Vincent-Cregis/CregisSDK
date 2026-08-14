#!/usr/bin/env python3
"""Validate the committed TypeScript generated-model boundary."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from typing import Any, Dict, Set


class GeneratedModelError(RuntimeError):
    pass


def read_json(path: Path) -> Dict[str, Any]:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (FileNotFoundError, json.JSONDecodeError) as exc:
        raise GeneratedModelError(f"Cannot read JSON file {path}: {exc}") from exc
    if not isinstance(value, dict):
        raise GeneratedModelError(f"JSON root must be an object: {path}")
    return value


def main() -> int:
    repo_root = Path(__file__).resolve().parents[2]
    operations = read_json(repo_root / "codegen/configs/openapi-operations.json")
    models = read_json(repo_root / "codegen/configs/openapi-models.json")
    lock = read_json(repo_root / "codegen/manifests/typescript-models.lock.json")
    generated_root = repo_root / "sdks/typescript/src/generated"

    if set(operations.get("apis", {})) != set(models.get("apis", {})) \
            or set(operations.get("apis", {})) != set(lock.get("apis", {})):
        raise GeneratedModelError("TypeScript configs and lock manifest define different APIs")

    expected_files: Set[Path] = {Path("index.ts")}
    total_operations = 0
    for api_name, api_lock in lock["apis"].items():
        expected_files.update({
            Path(api_name, "index.ts"),
            Path(api_name, "operations.ts"),
            Path(api_name, "schemas.ts"),
            Path(api_name, "webhooks.ts"),
        })
        model_names = api_lock.get("models")
        if not isinstance(model_names, list) or not all(isinstance(name, str) for name in model_names):
            raise GeneratedModelError(f"Invalid locked model list for {api_name}")
        expected_files.update(Path(api_name, f"{name}.ts") for name in model_names)

        configured = models["apis"][api_name]["operations"]
        locked_operations = api_lock.get("operations")
        if not isinstance(locked_operations, list):
            raise GeneratedModelError(f"Invalid locked operations for {api_name}")
        total_operations += len(locked_operations)
        for operation in locked_operations:
            operation_id = operation["operationId"]
            mapping = configured.get(operation_id)
            if not isinstance(mapping, dict):
                raise GeneratedModelError(f"Missing operation model mapping: {api_name}.{operation_id}")
            for key in ("requestModel", "responseModel", "responseContainer"):
                if operation.get(key) != mapping.get(key):
                    raise GeneratedModelError(f"Stale operation mapping: {api_name}.{operation_id}.{key}")

            request_model = operation.get("requestModel")
            if isinstance(request_model, str):
                source_path = generated_root / api_name / f"{request_model}.ts"
                source = source_path.read_text(encoding="utf-8")
                for field in models["sdkManagedRequestFields"]:
                    if re.search(rf"^\s*{re.escape(field)}\??:\s", source, re.MULTILINE):
                        raise GeneratedModelError(
                            f"SDK-managed field {field!r} leaked into {api_name}.{request_model}"
                        )

        configured_webhooks = models["apis"][api_name].get("webhooks", {})
        locked_webhooks = api_lock.get("webhooks", [])
        if not isinstance(configured_webhooks, dict) or not isinstance(locked_webhooks, list):
            raise GeneratedModelError(f"Invalid webhook configuration for {api_name}")
        locked_webhook_names = {item.get("name") for item in locked_webhooks if isinstance(item, dict)}
        if locked_webhook_names != set(configured_webhooks):
            raise GeneratedModelError(f"Stale webhook mapping for {api_name}")
        for webhook in locked_webhooks:
            if not isinstance(webhook, dict):
                raise GeneratedModelError(f"Invalid locked webhook for {api_name}")
            configured_webhook = configured_webhooks[webhook["name"]]
            if webhook.get("model") != configured_webhook.get("model"):
                raise GeneratedModelError(f"Stale webhook model for {api_name}.{webhook['name']}")

    actual_files = {
        path.relative_to(generated_root)
        for path in generated_root.rglob("*.ts")
    } if generated_root.is_dir() else set()
    if actual_files != expected_files:
        missing = sorted(str(path) for path in expected_files - actual_files)
        extra = sorted(str(path) for path in actual_files - expected_files)
        raise GeneratedModelError(f"Generated TypeScript files are stale; missing={missing}, extra={extra}")

    for path in generated_root.rglob("*.ts"):
        source = path.read_text(encoding="utf-8")
        if "FromJSON" in source or "../runtime" in source:
            raise GeneratedModelError(f"Runtime generator code leaked into type-only model: {path}")

    generator = lock.get("generator")
    script = (repo_root / "codegen/scripts/generate-typescript-models.sh").read_text(encoding="utf-8")
    if not isinstance(generator, dict) or not re.search(
        rf'{re.escape(str(generator.get("image")))}:{re.escape(str(generator.get("version")))}@'
        rf'{re.escape(str(generator.get("digest")))}',
        script,
    ):
        raise GeneratedModelError("TypeScript generation script does not pin the locked generator")

    print(
        f"Committed TypeScript model boundary is consistent: "
        f"{sum(len(api['models']) for api in lock['apis'].values())} models, "
        f"{total_operations} operations"
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except GeneratedModelError as exc:
        print(f"TypeScript generated-model check failed: {exc}", file=sys.stderr)
        raise SystemExit(1)
