#!/usr/bin/env python3
"""Check that handwritten TypeScript clients expose every configured operation."""

from __future__ import annotations

import json
import re
import sys
from pathlib import Path
from typing import Any, Dict, List


CLIENT_FILES = {
    "payment": "payment-client.ts",
    "team": "team-client.ts",
    "waas": "waas-client.ts",
}
METHOD = re.compile(
    r"^\s*public\s+(?:async\s+)?(?P<name>[A-Za-z_$][A-Za-z0-9_$]*)\s*\(",
    re.MULTILINE,
)
OPERATION_REFERENCE = re.compile(
    r"\b(?P<api>payment|team|waas)Operations\.(?P<operation>[A-Za-z_$][A-Za-z0-9_$]*)"
)
LITERAL_API_PATH = re.compile(r'["\']/(?:api|openapi)/')


class SurfaceError(RuntimeError):
    pass


def read_json(path: Path) -> Dict[str, Any]:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (FileNotFoundError, json.JSONDecodeError) as exc:
        raise SurfaceError(f"Cannot read {path}: {exc}") from exc
    if not isinstance(value, dict):
        raise SurfaceError(f"JSON root must be an object: {path}")
    return value


def method_slices(source: str) -> Dict[str, List[str]]:
    matches = list(METHOD.finditer(source))
    result: Dict[str, List[str]] = {}
    for index, match in enumerate(matches):
        end = matches[index + 1].start() if index + 1 < len(matches) else len(source)
        result.setdefault(match.group("name"), []).append(source[match.start():end])
    return result


def main() -> int:
    repo_root = Path(__file__).resolve().parents[2]
    config = read_json(repo_root / "codegen/configs/openapi-operations.json")
    overrides = read_json(repo_root / "codegen/configs/typescript-overrides.json")
    apis = config.get("apis")
    override_apis = overrides.get("apis")
    if not isinstance(apis, dict) or set(apis) != set(CLIENT_FILES) \
            or not isinstance(override_apis, dict) or set(override_apis) != set(CLIENT_FILES):
        raise SurfaceError("Operation config and TypeScript client list define different APIs")

    issues: List[str] = []
    operation_count = 0
    clients_root = repo_root / "sdks/typescript/src/clients"
    for api_name, filename in CLIENT_FILES.items():
        source_path = clients_root / filename
        try:
            source = source_path.read_text(encoding="utf-8")
        except FileNotFoundError as exc:
            raise SurfaceError(f"Missing TypeScript client: {source_path}") from exc
        if LITERAL_API_PATH.search(source):
            issues.append(f"{filename}: contains a literal API path instead of generated metadata")

        slices = method_slices(source)
        expected_references = set()
        client_methods = override_apis[api_name].get("clientMethods")
        if not isinstance(client_methods, dict):
            issues.append(f"{api_name}: TypeScript clientMethods must be an object")
            continue
        for operation in apis[api_name].get("operations", []):
            if not isinstance(operation, dict):
                issues.append(f"{api_name}: invalid operation entry")
                continue
            operation_id = operation.get("operationId")
            client_method = client_methods.get(operation_id)
            if not isinstance(operation_id, str) or not isinstance(client_method, str):
                issues.append(f"{api_name}: operation is missing operationId or clientMethod")
                continue
            operation_count += 1
            expected_reference = f"{api_name}Operations.{operation_id}"
            expected_references.add(operation_id)
            candidates = slices.get(client_method, [])
            if len(candidates) != 1:
                issues.append(
                    f"{filename}: expected one public method {client_method}, found {len(candidates)}"
                )
                continue
            if expected_reference not in candidates[0]:
                issues.append(f"{filename}: {client_method} does not use {expected_reference}")

        actual_references = {
            match.group("operation")
            for match in OPERATION_REFERENCE.finditer(source)
            if match.group("api") == api_name
        }
        missing = sorted(expected_references - actual_references)
        extra = sorted(actual_references - expected_references)
        if missing or extra:
            issues.append(f"{filename}: operation references missing={missing}, extra={extra}")

    if issues:
        raise SurfaceError("\n".join(issues))
    print(f"TypeScript client surface is consistent: {operation_count} operations")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except SurfaceError as exc:
        print(f"TypeScript client-surface check failed: {exc}", file=sys.stderr)
        raise SystemExit(1)
