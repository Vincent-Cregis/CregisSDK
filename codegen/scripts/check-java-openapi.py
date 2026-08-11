#!/usr/bin/env python3
"""Compare local Cregis OpenAPI operations with the Java SDK surface."""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import sys
from pathlib import Path
from typing import Any, Dict, Iterable, List, Tuple


HTTP_METHODS = {"delete", "get", "head", "options", "patch", "post", "put", "trace"}
METHOD_DECLARATION = re.compile(
    r"^\s+public\s+(?!static\s+)(?:[^\n]+?\s+)(?P<name>[A-Za-z_$][A-Za-z0-9_$]*)\s*\(",
    re.MULTILINE,
)
POST_PATH = re.compile(r'\bpost\s*\(\s*"(?P<path>/[^"\\]*)"')


def parse_args(argv: Iterable[str]) -> argparse.Namespace:
    script = Path(__file__).resolve()
    repo_root = script.parents[2]
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--spec-dir", required=True, type=Path, help="Directory containing the three canonical specs")
    parser.add_argument(
        "--manifest",
        type=Path,
        default=repo_root / "codegen" / "configs" / "java-operations.json",
    )
    parser.add_argument(
        "--java-source-root",
        type=Path,
        default=repo_root / "sdks" / "java" / "src" / "main" / "java",
    )
    parser.add_argument("--json-output", type=Path, help="Optional machine-readable report path")
    return parser.parse_args(list(argv))


def read_json(path: Path) -> Dict[str, Any]:
    try:
        with path.open("r", encoding="utf-8") as handle:
            value = json.load(handle)
    except FileNotFoundError as error:
        raise ValueError(f"Missing file: {path}") from error
    except json.JSONDecodeError as error:
        raise ValueError(f"Invalid JSON in {path}: {error}") from error
    if not isinstance(value, dict):
        raise ValueError(f"Expected a JSON object in {path}")
    return value


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(65536), b""):
            digest.update(chunk)
    return digest.hexdigest()


def collect_spec_operations(api_name: str, spec: Dict[str, Any]) -> Dict[str, Dict[str, str]]:
    paths = spec.get("paths")
    if not isinstance(paths, dict):
        raise ValueError(f"{api_name}: OpenAPI paths must be an object")

    operations: Dict[str, Dict[str, str]] = {}
    for path, path_item in paths.items():
        if not isinstance(path_item, dict):
            continue
        for method, operation in path_item.items():
            normalized_method = method.lower()
            if normalized_method not in HTTP_METHODS or not isinstance(operation, dict):
                continue
            operation_id = operation.get("operationId")
            if not isinstance(operation_id, str) or not operation_id:
                raise ValueError(f"{api_name}: {normalized_method.upper()} {path} has no operationId")
            if operation_id in operations:
                raise ValueError(f"{api_name}: duplicate operationId {operation_id}")
            operations[operation_id] = {
                "api": api_name,
                "operationId": operation_id,
                "method": normalized_method,
                "path": path,
            }
    return operations


def json_type_name(value: Any) -> str:
    if value is None:
        return "null"
    if isinstance(value, bool):
        return "boolean"
    if isinstance(value, str):
        return "string"
    if isinstance(value, int):
        return "integer"
    if isinstance(value, float):
        return "number"
    if isinstance(value, list):
        return "array"
    if isinstance(value, dict):
        return "object"
    return type(value).__name__


def resolve_local_ref(spec: Dict[str, Any], reference: str) -> Any:
    if not reference.startswith("#/"):
        raise ValueError(f"Only local OpenAPI references are supported: {reference}")
    current: Any = spec
    for raw_part in reference[2:].split("/"):
        part = raw_part.replace("~1", "/").replace("~0", "~")
        if not isinstance(current, dict) or part not in current:
            raise ValueError(f"Unresolvable OpenAPI reference: {reference}")
        current = current[part]
    return current


def example_matches_schema(
    spec: Dict[str, Any],
    schema: Dict[str, Any],
    value: Any,
    location: str,
) -> List[str]:
    """Return type/enum issues for one explicit example without exposing its value."""
    if value is None:
        if schema.get("nullable") is True or not any(
            key in schema for key in ("type", "$ref", "allOf", "oneOf", "anyOf", "properties", "items")
        ):
            return []
        return [f"{location} is null but the schema is not nullable"]

    reference = schema.get("$ref")
    if isinstance(reference, str):
        resolved = resolve_local_ref(spec, reference)
        if not isinstance(resolved, dict):
            return [f"{location} references a non-object schema"]
        problems = example_matches_schema(spec, resolved, value, location)
        sibling_schema = {key: item for key, item in schema.items() if key != "$ref"}
        if sibling_schema:
            problems.extend(example_matches_schema(spec, sibling_schema, value, location))
        return problems

    for keyword in ("allOf",):
        alternatives = schema.get(keyword)
        if isinstance(alternatives, list):
            problems: List[str] = []
            for item in alternatives:
                if isinstance(item, dict):
                    problems.extend(example_matches_schema(spec, item, value, location))
            return problems

    for keyword in ("oneOf", "anyOf"):
        alternatives = schema.get(keyword)
        if isinstance(alternatives, list):
            attempted = [
                example_matches_schema(spec, item, value, location)
                for item in alternatives
                if isinstance(item, dict)
            ]
            if any(not problems for problems in attempted):
                return []
            return attempted[0] if attempted else []

    expected = schema.get("type")
    if not isinstance(expected, str):
        if isinstance(schema.get("properties"), dict):
            expected = "object"
        elif isinstance(schema.get("items"), dict):
            expected = "array"

    actual = json_type_name(value)
    type_matches = {
        "string": isinstance(value, str),
        "integer": isinstance(value, int) and not isinstance(value, bool),
        "number": isinstance(value, (int, float)) and not isinstance(value, bool),
        "boolean": isinstance(value, bool),
        "array": isinstance(value, list),
        "object": isinstance(value, dict),
    }
    if expected in type_matches and not type_matches[expected]:
        return [f"{location} has JSON type {actual}, expected {expected}"]

    enum_values = schema.get("enum")
    if isinstance(enum_values, list) and value not in enum_values:
        return [f"{location} is outside the documented enum"]

    if expected == "integer":
        if schema.get("format") == "int32" and not -(2**31) <= value < 2**31:
            return [f"{location} is outside the int32 range"]
        if schema.get("format") == "int64" and not -(2**63) <= value < 2**63:
            return [f"{location} is outside the int64 range"]

    if expected == "array" and isinstance(value, list):
        item_schema = schema.get("items")
        if isinstance(item_schema, dict):
            problems = []
            for index, item in enumerate(value):
                problems.extend(example_matches_schema(spec, item_schema, item, f"{location}[{index}]"))
            return problems

    if expected == "object" and isinstance(value, dict):
        properties = schema.get("properties")
        if isinstance(properties, dict):
            problems = []
            for name, property_schema in properties.items():
                if name in value and isinstance(property_schema, dict):
                    problems.extend(
                        example_matches_schema(spec, property_schema, value[name], f"{location}.{name}")
                    )
            return problems

    return []


def collect_example_issues(api_name: str, spec: Dict[str, Any]) -> List[str]:
    issues: List[str] = []

    def walk(value: Any, path: str) -> None:
        if isinstance(value, dict):
            if "example" in value:
                schema: Any
                if isinstance(value.get("schema"), dict):
                    schema = value["schema"]
                elif any(
                    key in value
                    for key in ("type", "$ref", "allOf", "oneOf", "anyOf", "properties", "items", "enum")
                ):
                    schema = value
                else:
                    schema = None
                if isinstance(schema, dict):
                    for problem in example_matches_schema(spec, schema, value["example"], f"{path}.example"):
                        issues.append(f"{api_name}: {problem}")
            for key, item in value.items():
                if key != "example":
                    walk(item, f"{path}.{key}")
        elif isinstance(value, list):
            for index, item in enumerate(value):
                walk(item, f"{path}[{index}]")

    walk(spec, "$")
    return issues


def method_slices(source: str) -> Dict[str, List[str]]:
    matches = list(METHOD_DECLARATION.finditer(source))
    result: Dict[str, List[str]] = {}
    for index, match in enumerate(matches):
        end = matches[index + 1].start() if index + 1 < len(matches) else len(source)
        result.setdefault(match.group("name"), []).append(source[match.start():end])
    return result


def compare_source(
    api_name: str,
    api_config: Dict[str, Any],
    java_source_root: Path,
    issues: List[str],
) -> None:
    source_name = api_config.get("clientSource")
    if not isinstance(source_name, str) or not source_name:
        issues.append(f"{api_name}: manifest is missing clientSource")
        return

    source_path = java_source_root / source_name
    try:
        source = source_path.read_text(encoding="utf-8")
    except FileNotFoundError:
        issues.append(f"{api_name}: missing Java client source {source_path}")
        return

    slices = method_slices(source)
    expected_paths = set()
    operations = api_config.get("operations", [])
    for operation in operations:
        client_method = operation.get("clientMethod")
        path = operation.get("path")
        if not isinstance(client_method, str) or not isinstance(path, str):
            issues.append(f"{api_name}: operation has invalid clientMethod or path")
            continue
        expected_paths.add(path)
        candidates = slices.get(client_method, [])
        if len(candidates) != 1:
            issues.append(
                f"{api_name}: expected one public Java method {client_method}, found {len(candidates)}"
            )
            continue
        actual_paths = set(POST_PATH.findall(candidates[0]))
        if path not in actual_paths:
            rendered = ", ".join(sorted(actual_paths)) if actual_paths else "no POST path"
            issues.append(f"{api_name}: Java method {client_method} uses {rendered}, expected {path}")

    actual_paths = set(POST_PATH.findall(source))
    for path in sorted(actual_paths - expected_paths):
        issues.append(f"{api_name}: Java client has an untracked POST path {path}")


def compare(args: argparse.Namespace) -> Dict[str, Any]:
    manifest = read_json(args.manifest)
    if manifest.get("version") != 1:
        raise ValueError("Unsupported java-operations manifest version")
    api_configs = manifest.get("apis")
    if not isinstance(api_configs, dict) or not api_configs:
        raise ValueError("Manifest must contain a non-empty apis object")

    issues: List[str] = []
    report_apis: Dict[str, Any] = {}
    total_operations = 0

    for api_name, api_config in api_configs.items():
        if not isinstance(api_config, dict):
            issues.append(f"{api_name}: manifest API configuration must be an object")
            continue
        spec_file = api_config.get("specFile")
        if not isinstance(spec_file, str) or not spec_file:
            issues.append(f"{api_name}: manifest is missing specFile")
            continue

        spec_path = args.spec_dir / spec_file
        spec = read_json(spec_path)
        issues.extend(collect_example_issues(api_name, spec))
        actual = collect_spec_operations(api_name, spec)
        configured_operations = api_config.get("operations")
        if not isinstance(configured_operations, list):
            issues.append(f"{api_name}: manifest operations must be an array")
            continue

        expected: Dict[str, Dict[str, str]] = {}
        for operation in configured_operations:
            if not isinstance(operation, dict):
                issues.append(f"{api_name}: manifest operation must be an object")
                continue
            operation_id = operation.get("operationId")
            if not isinstance(operation_id, str) or not operation_id:
                issues.append(f"{api_name}: manifest operation is missing operationId")
                continue
            if operation_id in expected:
                issues.append(f"{api_name}: manifest has duplicate operationId {operation_id}")
                continue
            expected[operation_id] = operation

        for operation_id in sorted(actual.keys() - expected.keys()):
            item = actual[operation_id]
            issues.append(
                f"{api_name}: OpenAPI added {item['method'].upper()} {item['path']} ({operation_id})"
            )
        for operation_id in sorted(expected.keys() - actual.keys()):
            item = expected[operation_id]
            issues.append(
                f"{api_name}: OpenAPI removed {str(item.get('method', '')).upper()} "
                f"{item.get('path')} ({operation_id})"
            )
        for operation_id in sorted(actual.keys() & expected.keys()):
            actual_item = actual[operation_id]
            expected_item = expected[operation_id]
            expected_method = str(expected_item.get("method", "")).lower()
            expected_path = expected_item.get("path")
            if actual_item["method"] != expected_method or actual_item["path"] != expected_path:
                issues.append(
                    f"{api_name}: {operation_id} changed from "
                    f"{expected_method.upper()} {expected_path} to "
                    f"{actual_item['method'].upper()} {actual_item['path']}"
                )

        compare_source(api_name, api_config, args.java_source_root, issues)
        count = len(actual)
        total_operations += count
        report_apis[api_name] = {
            "specFile": spec_file,
            "sha256": sha256(spec_path),
            "operationCount": count,
        }

    return {
        "status": "passed" if not issues else "failed",
        "operationCount": total_operations,
        "apis": report_apis,
        "issues": issues,
    }


def main(argv: Iterable[str] = ()) -> int:
    args = parse_args(argv)
    try:
        report = compare(args)
    except ValueError as error:
        print(f"OpenAPI drift check could not run: {error}", file=sys.stderr)
        return 2

    if args.json_output:
        args.json_output.parent.mkdir(parents=True, exist_ok=True)
        args.json_output.write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")

    if report["issues"]:
        print("Java/OpenAPI drift check failed:", file=sys.stderr)
        for issue in report["issues"]:
            print(f"- {issue}", file=sys.stderr)
        return 1

    counts = ", ".join(
        f"{name} {details['operationCount']}"
        for name, details in report["apis"].items()
    )
    print(f"Java/OpenAPI drift check passed: {counts}; total {report['operationCount']}")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
