#!/usr/bin/env python3
"""Apply Python runtime policies to prepared Cregis OpenAPI model documents."""

from __future__ import annotations

import argparse
import json
import sys
from collections.abc import Iterable, Mapping, MutableMapping
from pathlib import Path
from typing import Any

INT64_MIN = -(2**63)
INT64_MAX = 2**63 - 1
REFERENCE_PREFIX = "#/components/schemas/"


class PythonPreparationError(RuntimeError):
    """Raised when a prepared model cannot use the supported Python policy."""


def read_json(path: Path) -> dict[str, Any]:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (FileNotFoundError, json.JSONDecodeError) as exc:
        raise PythonPreparationError(f"Cannot read prepared OpenAPI file {path}: {exc}") from exc
    if not isinstance(value, dict):
        raise PythonPreparationError(f"Prepared OpenAPI root must be an object: {path}")
    return value


def write_json(path: Path, value: Mapping[str, Any]) -> None:
    path.write_text(
        json.dumps(value, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )


def local_references(value: Any) -> Iterable[str]:
    if isinstance(value, dict):
        reference = value.get("$ref")
        if isinstance(reference, str) and reference.startswith(REFERENCE_PREFIX):
            yield reference[len(REFERENCE_PREFIX) :]
        for key, child in value.items():
            if key != "$ref":
                yield from local_references(child)
    elif isinstance(value, list):
        for child in value:
            yield from local_references(child)


def referenced_closure(
    roots: Iterable[str], components: Mapping[str, Mapping[str, Any]], context: str
) -> set[str]:
    discovered: set[str] = set()
    pending = list(roots)
    while pending:
        model_name = pending.pop()
        if model_name in discovered:
            continue
        schema = components.get(model_name)
        if not isinstance(schema, dict):
            raise PythonPreparationError(f"Unknown {context} model: {model_name}")
        discovered.add(model_name)
        pending.extend(local_references(schema))
    return discovered


def add_int64_bounds(value: Any) -> None:
    if isinstance(value, dict):
        if value.get("type") == "integer" and value.get("format") == "int64":
            minimum = value.get("minimum")
            maximum = value.get("maximum")
            if minimum is not None and (isinstance(minimum, bool) or not isinstance(minimum, int)):
                raise PythonPreparationError("OpenAPI int64 minimum must be an integer")
            if maximum is not None and (isinstance(maximum, bool) or not isinstance(maximum, int)):
                raise PythonPreparationError("OpenAPI int64 maximum must be an integer")
            value["minimum"] = max(minimum, INT64_MIN) if minimum is not None else INT64_MIN
            value["maximum"] = min(maximum, INT64_MAX) if maximum is not None else INT64_MAX
            if value["minimum"] > value["maximum"]:
                raise PythonPreparationError("OpenAPI int64 bounds do not overlap signed int64")
        for child in value.values():
            add_int64_bounds(child)
    elif isinstance(value, list):
        for child in value:
            add_int64_bounds(child)


def root_models(entries: Any, field: str, context: str) -> set[str]:
    if not isinstance(entries, list):
        raise PythonPreparationError(f"Prepared {context} metadata must be an array")
    roots: set[str] = set()
    for entry in entries:
        if not isinstance(entry, dict):
            raise PythonPreparationError(f"Prepared {context} entry must be an object")
        model_name = entry.get(field)
        if model_name is not None:
            if not isinstance(model_name, str):
                raise PythonPreparationError(f"Prepared {context} {field} must be a string or null")
            roots.add(model_name)
    return roots


def apply_policy(document: MutableMapping[str, Any], api_name: str) -> dict[str, Any]:
    components_value = document.get("components", {}).get("schemas", {})
    if not isinstance(components_value, dict):
        raise PythonPreparationError(f"Prepared API has no component schemas: {api_name}")
    components: dict[str, Mapping[str, Any]] = components_value

    operations = document.get("x-cregis-operations", [])
    request_roots = root_models(operations, "requestModel", f"{api_name} operations")
    response_roots = root_models(operations, "responseModel", f"{api_name} operations")
    webhook_roots = root_models(
        document.get("x-cregis-webhooks", []), "model", f"{api_name} webhooks"
    )

    request_models = referenced_closure(request_roots, components, f"{api_name} request")
    output_models = referenced_closure(
        response_roots | webhook_roots, components, f"{api_name} output"
    )
    overlap = request_models & output_models
    if overlap:
        raise PythonPreparationError(
            f"Python models cannot be both request and output models: {api_name}.{sorted(overlap)}"
        )
    unclassified = set(components) - request_models - output_models
    if unclassified:
        raise PythonPreparationError(
            f"Prepared Python models have no runtime direction: {api_name}.{sorted(unclassified)}"
        )

    policies: dict[str, Any] = {}
    for model_name, schema_value in components_value.items():
        if not isinstance(schema_value, dict):
            raise PythonPreparationError(f"Python component must be an object: {api_name}.{model_name}")
        add_int64_bounds(schema_value)
        additional = schema_value.get("additionalProperties")
        if isinstance(additional, dict) or additional is True:
            raise PythonPreparationError(
                f"Dynamic additional properties need an explicit Python mapping: "
                f"{api_name}.{model_name}"
            )
        if model_name in request_models:
            # With the pinned generator's historical compatibility mode, an
            # omitted keyword produces the strict request-model template while
            # an explicit false produces an unwanted additional-properties bag.
            schema_value.pop("additionalProperties", None)
            policies[model_name] = {"direction": "request", "extra": "forbid"}
        else:
            schema_value.pop("additionalProperties", None)
            policies[model_name] = {"direction": "output", "extra": "ignore"}

    return {"models": dict(sorted(policies.items()))}


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--prepared-dir", required=True, type=Path)
    parser.add_argument("--policy-file", required=True, type=Path)
    args = parser.parse_args()

    policies: dict[str, Any] = {"apis": {}, "version": 1}
    for path in sorted(args.prepared_dir.glob("*.json")):
        if path.name == "manifest.json":
            continue
        document = read_json(path)
        api_name = document.get("x-cregis-api")
        if not isinstance(api_name, str):
            raise PythonPreparationError(f"Prepared OpenAPI file has no API name: {path}")
        policies["apis"][api_name] = apply_policy(document, api_name)
        write_json(path, document)

    if not policies["apis"]:
        raise PythonPreparationError("No prepared Python OpenAPI files were found")
    write_json(args.policy_file, policies)
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except PythonPreparationError as exc:
        print(f"Python OpenAPI policy failed: {exc}", file=sys.stderr)
        raise SystemExit(1)
