#!/usr/bin/env python3
"""Prepare canonical Cregis OpenAPI documents for Java model generation."""

from __future__ import annotations

import argparse
import copy
import hashlib
import json
import re
import sys
from pathlib import Path
from typing import Any, Dict, Iterable, List, Mapping, MutableMapping, Optional, Set, Tuple


HTTP_METHODS = {"delete", "get", "head", "options", "patch", "post", "put", "trace"}
DOC_ONLY_KEYS = {"description", "example", "examples", "externalDocs", "title"}


class PreparationError(RuntimeError):
    """Raised when a spec or generation manifest is inconsistent."""


def read_json(path: Path) -> Dict[str, Any]:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except FileNotFoundError as exc:
        raise PreparationError(f"Missing file: {path}") from exc
    except json.JSONDecodeError as exc:
        raise PreparationError(f"Invalid JSON in {path}: {exc}") from exc
    if not isinstance(value, dict):
        raise PreparationError(f"JSON root must be an object: {path}")
    return value


def write_json(path: Path, value: Mapping[str, Any]) -> None:
    path.write_text(
        json.dumps(value, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def pascal_case(value: str) -> str:
    parts = re.findall(r"[A-Z]+(?=[A-Z][a-z]|\d|$)|[A-Z]?[a-z]+|\d+", value)
    if not parts:
        raise PreparationError(f"Cannot derive a Java model name from: {value!r}")
    return "".join(part[:1].upper() + part[1:] for part in parts)


def local_ref_name(schema: Mapping[str, Any]) -> Optional[str]:
    ref = schema.get("$ref")
    prefix = "#/components/schemas/"
    if isinstance(ref, str) and ref.startswith(prefix):
        return ref[len(prefix):]
    return None


def schema_signature(value: Any) -> Any:
    """Return the wire-relevant portion used to compare reused model names."""
    if isinstance(value, dict):
        return {
            key: schema_signature(item)
            for key, item in sorted(value.items())
            if key not in DOC_ONLY_KEYS and not key.startswith("x-")
        }
    if isinstance(value, list):
        return [schema_signature(item) for item in value]
    return value


def merge_object_schemas(parts: Iterable[Mapping[str, Any]]) -> Dict[str, Any]:
    merged: Dict[str, Any] = {"type": "object", "properties": {}}
    required: List[str] = []
    for part in parts:
        for key, value in part.items():
            if key in {"allOf", "$ref", "properties", "required", "type"}:
                continue
            if key not in merged:
                merged[key] = copy.deepcopy(value)
        properties = part.get("properties")
        if isinstance(properties, dict):
            merged["properties"].update(copy.deepcopy(properties))
        for field in part.get("required", []):
            if field not in required:
                required.append(field)
    if required:
        merged["required"] = required
    return merged


class SchemaMaterializer:
    def __init__(
        self,
        source_components: Mapping[str, Any],
        component_names: Mapping[str, str],
        schema_names: Mapping[str, str],
        managed_request_fields: Set[str],
    ) -> None:
        self.source_components = source_components
        self.component_names = component_names
        self.schema_names = schema_names
        self.managed_request_fields = managed_request_fields
        self.components: Dict[str, Dict[str, Any]] = {}
        self._building: Set[str] = set()

    def materialize_root(
        self,
        schema: Mapping[str, Any],
        model_name: str,
        context: str,
        strip_managed_fields: bool,
    ) -> None:
        expanded = self._expand_root(schema, set())
        if strip_managed_fields:
            expanded = self._strip_managed_request_fields(expanded)
        self._register(model_name, expanded, context)

    def materialize_array_item(
        self,
        schema: Mapping[str, Any],
        model_name: str,
        context: str,
    ) -> None:
        expanded = self._expand_root(schema, set())
        if expanded.get("type") != "array" or not isinstance(expanded.get("items"), dict):
            raise PreparationError(f"Expected array response for {context}")
        self._register(model_name, expanded["items"], context + ".items")

    def _expand_root(self, schema: Mapping[str, Any], seen_refs: Set[str]) -> Dict[str, Any]:
        ref_name = local_ref_name(schema)
        if ref_name is not None:
            if ref_name in seen_refs:
                raise PreparationError(f"Cyclic root schema reference: {ref_name}")
            target = self.source_components.get(ref_name)
            if not isinstance(target, dict):
                raise PreparationError(f"Missing component schema: {ref_name}")
            expanded = self._expand_root(target, seen_refs | {ref_name})
            siblings = {key: value for key, value in schema.items() if key != "$ref"}
            if siblings:
                expanded = merge_object_schemas([expanded, siblings])
            return expanded

        all_of = schema.get("allOf")
        if isinstance(all_of, list):
            parts = [self._expand_root(part, seen_refs) for part in all_of if isinstance(part, dict)]
            siblings = {key: value for key, value in schema.items() if key != "allOf"}
            if siblings:
                parts.append(siblings)
            return merge_object_schemas(parts)
        return copy.deepcopy(dict(schema))

    def _strip_managed_request_fields(self, schema: Dict[str, Any]) -> Dict[str, Any]:
        properties = schema.get("properties")
        if isinstance(properties, dict):
            for field in self.managed_request_fields:
                properties.pop(field, None)
        required = schema.get("required")
        if isinstance(required, list):
            schema["required"] = [field for field in required if field not in self.managed_request_fields]
            if not schema["required"]:
                schema.pop("required")
        if not schema.get("properties"):
            schema["properties"] = {}
            schema["additionalProperties"] = False
        return schema

    def _register(self, model_name: str, schema: Mapping[str, Any], context: str) -> None:
        if not re.fullmatch(r"[A-Z][A-Za-z0-9]*", model_name):
            raise PreparationError(f"Invalid Java model name {model_name!r} for {context}")
        if model_name in self._building:
            return

        normalized = self._expand_root(schema, set())
        self._building.add(model_name)
        processed = self._process_object(normalized, model_name, context)
        self._building.remove(model_name)

        existing = self.components.get(model_name)
        if existing is not None:
            if schema_signature(existing) != schema_signature(processed):
                raise PreparationError(
                    f"Model name {model_name} resolves to incompatible schemas; context: {context}"
                )
            return
        self.components[model_name] = processed

    def _process_object(
        self,
        schema: Mapping[str, Any],
        model_name: str,
        context: str,
    ) -> Dict[str, Any]:
        processed = copy.deepcopy(dict(schema))
        properties = processed.get("properties")
        if properties is not None and not isinstance(properties, dict):
            raise PreparationError(f"Schema properties must be an object: {context}")
        if isinstance(properties, dict):
            processed["type"] = "object"
            processed["properties"] = {
                prop_name: self._process_property(
                    prop_schema,
                    model_name,
                    prop_name,
                    context + "." + prop_name,
                )
                for prop_name, prop_schema in properties.items()
                if isinstance(prop_schema, dict)
            }
        return processed

    def _process_property(
        self,
        schema: Mapping[str, Any],
        parent_model: str,
        property_name: str,
        context: str,
    ) -> Dict[str, Any]:
        ref_name = local_ref_name(schema)
        if ref_name is not None:
            target = self.source_components.get(ref_name)
            if not isinstance(target, dict):
                raise PreparationError(f"Missing component schema: {ref_name}")
            child_name = self.schema_names.get(
                context,
                self.component_names.get(ref_name, pascal_case(ref_name)),
            )
            self._register(child_name, target, context)
            result: Dict[str, Any] = {"$ref": f"#/components/schemas/{child_name}"}
            for key, value in schema.items():
                if key != "$ref":
                    result[key] = copy.deepcopy(value)
            return result

        if isinstance(schema.get("allOf"), list) or schema.get("type") == "object" or "properties" in schema:
            child_name = self.schema_names.get(context, parent_model + pascal_case(property_name))
            self._register(child_name, schema, context)
            return {"$ref": f"#/components/schemas/{child_name}"}

        if schema.get("type") == "array" and isinstance(schema.get("items"), dict):
            result = copy.deepcopy(dict(schema))
            item_schema = schema["items"]
            item_context = context + ".items"
            if (
                local_ref_name(item_schema) is not None
                or isinstance(item_schema.get("allOf"), list)
                or item_schema.get("type") == "object"
                or "properties" in item_schema
            ):
                ref_item_name = local_ref_name(item_schema)
                default_name = (
                    self.component_names.get(ref_item_name, pascal_case(ref_item_name))
                    if ref_item_name is not None
                    else parent_model + pascal_case(property_name) + "Item"
                )
                child_name = self.schema_names.get(item_context, default_name)
                target = self.source_components.get(ref_item_name) if ref_item_name is not None else item_schema
                if not isinstance(target, dict):
                    raise PreparationError(f"Missing component schema: {ref_item_name}")
                self._register(child_name, target, item_context)
                result["items"] = {"$ref": f"#/components/schemas/{child_name}"}
            else:
                result["items"] = self._process_property(
                    item_schema,
                    parent_model,
                    property_name + "Item",
                    item_context,
                )
            return result

        result = copy.deepcopy(dict(schema))
        content_schema = result.get("contentSchema")
        if isinstance(content_schema, dict):
            result["contentSchema"] = self._process_property(
                content_schema,
                parent_model,
                property_name + "Content",
                context + ".contentSchema",
            )
        return result


def json_schema(content: Mapping[str, Any]) -> Optional[Dict[str, Any]]:
    if not isinstance(content, dict):
        return None
    media = content.get("application/json")
    if not isinstance(media, dict):
        for media_type, candidate in content.items():
            if isinstance(media_type, str) and media_type.startswith("application/json"):
                media = candidate
                break
    if isinstance(media, dict) and isinstance(media.get("schema"), dict):
        return media["schema"]
    return None


def operation_at(spec: Mapping[str, Any], method: str, path: str, operation_id: str) -> Dict[str, Any]:
    path_item = spec.get("paths", {}).get(path)
    operation = path_item.get(method.lower()) if isinstance(path_item, dict) else None
    if not isinstance(operation, dict):
        raise PreparationError(f"Missing operation {method.upper()} {path} ({operation_id})")
    if operation.get("operationId") != operation_id:
        raise PreparationError(
            f"Operation ID mismatch for {method.upper()} {path}: "
            f"expected {operation_id}, got {operation.get('operationId')}"
        )
    return operation


def request_schema(operation: Mapping[str, Any]) -> Optional[Dict[str, Any]]:
    request_body = operation.get("requestBody")
    return json_schema(request_body.get("content", {})) if isinstance(request_body, dict) else None


def successful_response_schema(operation: Mapping[str, Any]) -> Optional[Dict[str, Any]]:
    responses = operation.get("responses")
    if not isinstance(responses, dict):
        return None
    response = responses.get("200")
    return json_schema(response.get("content", {})) if isinstance(response, dict) else None


def extract_data_schema(
    schema: Optional[Mapping[str, Any]],
    materializer: SchemaMaterializer,
) -> Optional[Dict[str, Any]]:
    if schema is None:
        return None
    envelope = materializer._expand_root(schema, set())
    properties = envelope.get("properties")
    data = properties.get("data") if isinstance(properties, dict) else None
    if not isinstance(data, dict):
        return None
    structural_keys = {
        key for key in data
        if key not in DOC_ONLY_KEYS and not key.startswith("x-")
    }
    return copy.deepcopy(data) if structural_keys else None


def prepare_api(
    api_name: str,
    spec_path: Path,
    spec: Mapping[str, Any],
    operation_api_config: Mapping[str, Any],
    model_api_config: Mapping[str, Any],
    managed_request_fields: Set[str],
) -> Tuple[Dict[str, Any], Dict[str, Any]]:
    configured_operations = model_api_config.get("operations")
    operation_entries = operation_api_config.get("operations")
    if not isinstance(configured_operations, dict) or not isinstance(operation_entries, list):
        raise PreparationError(f"Invalid operation configuration for API: {api_name}")

    inventory_ids = {entry.get("operationId") for entry in operation_entries if isinstance(entry, dict)}
    configured_ids = set(configured_operations)
    if inventory_ids != configured_ids:
        missing = sorted(inventory_ids - configured_ids)
        extra = sorted(configured_ids - inventory_ids)
        raise PreparationError(
            f"Java model configuration mismatch for {api_name}; missing={missing}, extra={extra}"
        )

    source_components = spec.get("components", {}).get("schemas", {})
    if not isinstance(source_components, dict):
        raise PreparationError(f"Spec has no component schemas: {spec_path}")
    materializer = SchemaMaterializer(
        source_components=source_components,
        component_names=model_api_config.get("componentNames", {}),
        schema_names=model_api_config.get("schemaNames", {}),
        managed_request_fields=managed_request_fields,
    )

    prepared_operations: List[Dict[str, Any]] = []
    for entry in operation_entries:
        if not isinstance(entry, dict):
            raise PreparationError(f"Invalid operation entry for API: {api_name}")
        operation_id = entry["operationId"]
        model_config = configured_operations[operation_id]
        operation = operation_at(spec, entry["method"], entry["path"], operation_id)

        raw_request = request_schema(operation)
        request_model = model_config.get("requestModel")
        if not isinstance(raw_request, dict):
            raise PreparationError(f"Operation {operation_id} must define a JSON request body")
        if request_model is None:
            empty_request = materializer._expand_root(raw_request, set())
            if api_name in {"payment", "waas"}:
                empty_request = materializer._strip_managed_request_fields(empty_request)
            if empty_request.get("properties") or empty_request.get("required"):
                raise PreparationError(
                    f"Operation {operation_id} omits requestModel but has business request fields"
                )
        elif isinstance(request_model, str):
            materializer.materialize_root(
                raw_request,
                request_model,
                operation_id + ".request",
                strip_managed_fields=api_name in {"payment", "waas"},
            )
        else:
            raise PreparationError(f"Operation {operation_id} requestModel must be a string or null")

        raw_response = extract_data_schema(successful_response_schema(operation), materializer)
        response_model = model_config.get("responseModel")
        response_container = model_config.get("responseContainer")
        if response_model is None:
            if raw_response is not None:
                raise PreparationError(f"Operation {operation_id} has response data but no responseModel")
        elif not isinstance(response_model, str) or raw_response is None:
            raise PreparationError(f"Operation {operation_id} response model does not match its OpenAPI data")
        elif response_container == "array":
            materializer.materialize_array_item(
                raw_response,
                response_model,
                operation_id + ".response",
            )
        elif response_container is not None:
            raise PreparationError(f"Unsupported responseContainer for {operation_id}: {response_container}")
        else:
            materializer.materialize_root(
                raw_response,
                response_model,
                operation_id + ".response",
                strip_managed_fields=False,
            )

        prepared_operations.append(
            {
                "method": entry["method"].lower(),
                "operationId": operation_id,
                "path": entry["path"],
                "requestModel": request_model,
                "responseContainer": response_container,
                "responseModel": response_model,
            }
        )

    original_info = spec.get("info") if isinstance(spec.get("info"), dict) else {}
    prepared = {
        "openapi": spec.get("openapi", "3.1.0"),
        "info": {
            "title": f"Cregis {api_name} Java models",
            "version": original_info.get("version", "0.0.0"),
        },
        "paths": {},
        "components": {"schemas": dict(sorted(materializer.components.items()))},
        "x-cregis-api": api_name,
        "x-cregis-operations": prepared_operations,
        "x-cregis-source-sha256": sha256_file(spec_path),
    }
    lock = {
        "inputSha256": sha256_file(spec_path),
        "modelPackage": model_api_config["modelPackage"],
        "models": sorted(materializer.components),
        "operations": prepared_operations,
        "specFile": spec_path.name,
    }
    return prepared, lock


def prepare(spec_dir: Path, output_dir: Path, repo_root: Path) -> Dict[str, Any]:
    if output_dir.exists() and any(output_dir.iterdir()):
        raise PreparationError(f"Output directory must be empty: {output_dir}")

    operations_config = read_json(repo_root / "codegen/configs/java-operations.json")
    models_config = read_json(repo_root / "codegen/configs/java-models.json")
    if models_config.get("version") != 1:
        raise PreparationError("Unsupported java-models.json version")

    operation_apis = operations_config.get("apis")
    model_apis = models_config.get("apis")
    if not isinstance(operation_apis, dict) or not isinstance(model_apis, dict):
        raise PreparationError("Both Java configuration files must define APIs")
    if set(operation_apis) != set(model_apis):
        raise PreparationError("Java operation and model configurations define different APIs")

    managed_fields = models_config.get("sdkManagedRequestFields")
    if not isinstance(managed_fields, list) or not all(isinstance(item, str) for item in managed_fields):
        raise PreparationError("sdkManagedRequestFields must be a string array")

    output_dir.mkdir(parents=True, exist_ok=True)

    lock_apis: Dict[str, Any] = {}
    for api_name in sorted(operation_apis):
        operation_api_config = operation_apis[api_name]
        model_api_config = model_apis[api_name]
        spec_file = operation_api_config.get("specFile")
        if not isinstance(spec_file, str):
            raise PreparationError(f"Missing specFile for API: {api_name}")
        spec_path = spec_dir / spec_file
        spec = read_json(spec_path)
        prepared, lock = prepare_api(
            api_name,
            spec_path,
            spec,
            operation_api_config,
            model_api_config,
            set(managed_fields),
        )
        write_json(output_dir / f"{api_name}.json", prepared)
        lock_apis[api_name] = lock

    lock_manifest = {
        "apis": lock_apis,
        "generator": {
            "image": "openapitools/openapi-generator-cli",
            "version": "7.19.0",
        },
        "version": 1,
    }
    write_json(output_dir / "manifest.json", lock_manifest)
    return lock_manifest


def parse_args(argv: Optional[List[str]] = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--spec-dir", required=True, type=Path)
    parser.add_argument("--output-dir", required=True, type=Path)
    parser.add_argument("--repo-root", type=Path)
    return parser.parse_args(argv)


def main(argv: Optional[List[str]] = None) -> int:
    args = parse_args(argv)
    repo_root = args.repo_root
    if repo_root is None:
        repo_root = Path(__file__).resolve().parents[2]
    try:
        manifest = prepare(args.spec_dir.resolve(), args.output_dir.resolve(), repo_root.resolve())
    except PreparationError as exc:
        print(f"Java OpenAPI preparation failed: {exc}", file=sys.stderr)
        return 1

    total_models = sum(len(api["models"]) for api in manifest["apis"].values())
    total_operations = sum(len(api["operations"]) for api in manifest["apis"].values())
    print(
        f"Prepared Java model specs: {total_operations} operations, "
        f"{total_models} models -> {args.output_dir.resolve()}"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
