#!/usr/bin/env python3
"""Render the small, typed Go surface from prepared Cregis OpenAPI documents."""

from __future__ import annotations

import argparse
import hashlib
import json
import pathlib
import re
from typing import Any, Iterable, Mapping


REFERENCE_PREFIX = "#/components/schemas/"
ACRONYMS = {
    "api": "API",
    "cid": "CID",
    "http": "HTTP",
    "https": "HTTPS",
    "id": "ID",
    "ip": "IP",
    "pid": "PID",
    "qr": "QR",
    "qrcode": "QRCode",
    "qrcodes": "QRCodes",
    "txid": "TxID",
    "uri": "URI",
    "url": "URL",
    "uuid": "UUID",
}


class RenderError(RuntimeError):
    """Raised when a prepared schema cannot be represented safely in Go."""


def read_json(path: pathlib.Path) -> dict[str, Any]:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        raise RenderError(f"Cannot read {path}: {exc}") from exc
    if not isinstance(value, dict):
        raise RenderError(f"JSON root must be an object: {path}")
    return value


def sha256_file(path: pathlib.Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def go_name(value: str) -> str:
    parts = re.findall(r"[A-Z]+(?=[A-Z][a-z]|\d|$)|[A-Z]?[a-z]+|\d+", value.replace("-", "_"))
    if "_" in value:
        parts = [part for segment in value.split("_") for part in re.findall(
            r"[A-Z]+(?=[A-Z][a-z]|\d|$)|[A-Z]?[a-z]+|\d+", segment
        )]
    if not parts:
        raise RenderError(f"Cannot derive Go name from {value!r}")
    rendered = []
    for part in parts:
        lower = part.lower()
        rendered.append(ACRONYMS.get(lower, lower[:1].upper() + lower[1:]))
    return "".join(rendered)


def quoted(value: str) -> str:
    return json.dumps(value, ensure_ascii=False)


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


def reference_closure(roots: Iterable[str], schemas: Mapping[str, Any]) -> set[str]:
    found: set[str] = set()
    pending = list(roots)
    while pending:
        model = pending.pop()
        if model in found:
            continue
        schema = schemas.get(model)
        if not isinstance(schema, dict):
            raise RenderError(f"Unknown prepared model: {model}")
        found.add(model)
        pending.extend(local_references(schema))
    return found


def classify_models(document: Mapping[str, Any]) -> dict[str, str]:
    schemas = document["components"]["schemas"]
    operations = document.get("x-cregis-operations", [])
    webhooks = document.get("x-cregis-webhooks", [])
    request_roots = {
        item["requestModel"] for item in operations if item.get("requestModel") is not None
    }
    output_roots = {
        item["responseModel"] for item in operations if item.get("responseModel") is not None
    } | {item["model"] for item in webhooks}
    request_models = reference_closure(request_roots, schemas)
    output_models = reference_closure(output_roots, schemas)
    overlap = request_models & output_models
    if overlap:
        raise RenderError(f"Go models have conflicting directions: {sorted(overlap)}")
    unclassified = set(schemas) - request_models - output_models
    if unclassified:
        raise RenderError(f"Go models have no direction: {sorted(unclassified)}")
    return {
        model: "request" if model in request_models else "output"
        for model in sorted(schemas)
    }


def base_go_type(schema: Mapping[str, Any]) -> str:
    reference = schema.get("$ref")
    if isinstance(reference, str) and reference.startswith(REFERENCE_PREFIX):
        return reference[len(REFERENCE_PREFIX) :]
    if isinstance(schema.get("oneOf"), list) or isinstance(schema.get("anyOf"), list):
        return "json.RawMessage"
    schema_type = schema.get("type")
    schema_format = schema.get("format")
    if schema_type == "string":
        return "string"
    if schema_type == "boolean":
        return "bool"
    if schema_type == "integer":
        if schema_format == "int32":
            return "int32"
        if schema_format == "int64":
            return "int64"
        return "int"
    if schema_type == "number":
        return "float32" if schema_format == "float" else "float64"
    if schema_type == "array":
        items = schema.get("items")
        if not isinstance(items, dict):
            raise RenderError("Array property must define an item schema")
        return "[]" + base_go_type(items)
    raise RenderError(f"Unsupported prepared Go schema: {schema}")


def enum_type_name(model_name: str, wire_name: str) -> str:
    return model_name + go_name(wire_name)


def enum_member_suffix(value: Any) -> str:
    if isinstance(value, str):
        return "Empty" if value == "" else go_name(value)
    if isinstance(value, bool):
        return "True" if value else "False"
    if isinstance(value, (int, float)):
        rendered = str(value).replace("-", "Negative").replace(".", "Point")
        return rendered[:1].upper() + rendered[1:]
    raise RenderError(f"Unsupported Go enum value: {value!r}")


def field_go_type(
    model_name: str,
    wire_name: str,
    schema: Mapping[str, Any],
    required: bool,
) -> str:
    enum = schema.get("enum")
    base = enum_type_name(model_name, wire_name) if isinstance(enum, list) and enum else base_go_type(schema)
    if schema.get("nullable") is True:
        return "*" + base
    if required or base.startswith("[]"):
        return base
    return "*" + base


def contains_one_of(value: Any) -> bool:
    if isinstance(value, dict):
        return "oneOf" in value or "anyOf" in value or any(contains_one_of(v) for v in value.values())
    if isinstance(value, list):
        return any(contains_one_of(item) for item in value)
    return False


def render_models(api_name: str, document: Mapping[str, Any]) -> tuple[str, str, dict[str, str]]:
    schemas: dict[str, Any] = document["components"]["schemas"]
    directions = classify_models(document)
    validation_models = models_requiring_validation(schemas)
    model_lines = [
        "// Code generated from the canonical Cregis OpenAPI specification. DO NOT EDIT.",
        f"package {api_name}",
        "",
    ]
    if contains_one_of(schemas):
        model_lines.extend(['import "encoding/json"', ""])
    for model_name in sorted(schemas):
        schema = schemas[model_name]
        if schema.get("type") != "object" or not isinstance(schema.get("properties", {}), dict):
            raise RenderError(f"Prepared Go model must be an object: {api_name}.{model_name}")
        required = set(schema.get("required", []))
        for wire_name, property_schema in schema.get("properties", {}).items():
            if not isinstance(property_schema, dict):
                raise RenderError(f"Invalid property: {api_name}.{model_name}.{wire_name}")
            enum = property_schema.get("enum")
            if not isinstance(enum, list) or not enum:
                continue
            enum_type = enum_type_name(model_name, wire_name)
            underlying = base_go_type(property_schema)
            if underlying.startswith("[]") or underlying == "json.RawMessage":
                raise RenderError(f"Unsupported enum type: {api_name}.{model_name}.{wire_name}")
            model_lines.extend([
                f"// {enum_type} is the documented value set for {model_name}.{go_name(wire_name)}.",
                f"type {enum_type} {underlying}",
                "",
                "const (",
            ])
            members: set[str] = set()
            for value in enum:
                member = enum_type + enum_member_suffix(value)
                if member in members:
                    raise RenderError(f"Colliding Go enum member: {api_name}.{member}")
                members.add(member)
                model_lines.append(f"\t{member} {enum_type} = {go_literal(value)}")
            model_lines.extend([")", ""])
        model_lines.extend([
            f"// {model_name} is generated from the canonical {api_name} API contract.",
            f"type {model_name} struct {{",
        ])
        for wire_name, property_schema in schema.get("properties", {}).items():
            if not isinstance(property_schema, dict):
                raise RenderError(f"Invalid property: {api_name}.{model_name}.{wire_name}")
            field_name = go_name(wire_name)
            field_type = field_go_type(model_name, wire_name, property_schema, wire_name in required)
            omit = "" if wire_name in required else ",omitempty"
            model_lines.append(f"\t{field_name} {field_type} `json:\"{wire_name}{omit}\"`")
        model_lines.extend(["}", ""])

    validation_lines = [
        "// Code generated from the canonical Cregis OpenAPI specification. DO NOT EDIT.",
        f"package {api_name}",
        "",
        "import (",
        '\t"bytes"',
        '\t"encoding/json"',
        '\t"fmt"',
    ]
    validation_schemas = [schemas[name] for name in validation_models]
    needs_regexp = any(
        "pattern" in prop
        for schema in validation_schemas
        for prop in schema.get("properties", {}).values()
        if isinstance(prop, dict)
    )
    needs_utf8 = any(
        "minLength" in prop or "maxLength" in prop
        for schema in validation_schemas
        for prop in schema.get("properties", {}).values()
        if isinstance(prop, dict)
    )
    if needs_regexp:
        validation_lines.append('\t"regexp"')
    if needs_utf8:
        validation_lines.append('\t"unicode/utf8"')
    validation_lines.extend([
        ")",
        "",
        "func unmarshalModel(data []byte, destination any, required []string, nullable map[string]struct{}, allowed map[string]struct{}, rejectUnknown bool) error {",
        "\tvar fields map[string]json.RawMessage",
        "\tif err := json.Unmarshal(data, &fields); err != nil {",
        "\t\treturn err",
        "\t}",
        "\tif fields == nil {",
        "\t\treturn fmt.Errorf(\"model must be a JSON object\")",
        "\t}",
        "\tif rejectUnknown {",
        "\t\tfor field := range fields {",
        "\t\t\tif _, ok := allowed[field]; !ok {",
        "\t\t\t\treturn fmt.Errorf(\"unknown field %q\", field)",
        "\t\t\t}",
        "\t\t}",
        "\t}",
        "\tfor _, field := range required {",
        "\t\traw, ok := fields[field]",
        "\t\tif !ok {",
        "\t\t\treturn fmt.Errorf(\"required field %q is missing\", field)",
        "\t\t}",
        "\t\tif bytes.Equal(bytes.TrimSpace(raw), []byte(\"null\")) {",
        "\t\t\tif _, allowed := nullable[field]; !allowed {",
        "\t\t\t\treturn fmt.Errorf(\"required field %q must not be null\", field)",
        "\t\t\t}",
        "\t\t}",
        "\t}",
        "\treturn json.Unmarshal(data, destination)",
        "}",
        "",
    ])
    for model_name in sorted(schemas):
        schema = schemas[model_name]
        required = list(schema.get("required", []))
        reject_unknown = directions[model_name] == "request"
        validates = model_name in validation_models
        if required or reject_unknown or validates:
            nullable = [
                field for field in required
                if isinstance(schema["properties"].get(field), dict)
                and schema["properties"][field].get("nullable") is True
            ]
            required_go = "[]string{" + ", ".join(quoted(field) for field in required) + "}"
            nullable_go = "nil"
            if nullable:
                nullable_go = "map[string]struct{}{\n" + "".join(
                    f"\t\t{quoted(field)}: {{}},\n" for field in nullable
                ) + "\t}"
            allowed_go = "nil"
            if reject_unknown:
                allowed_go = "map[string]struct{}{\n" + "".join(
                    f"\t\t{quoted(field)}: {{}},\n" for field in schema["properties"]
                ) + "\t}"
            validation_lines.extend([
                f"// UnmarshalJSON validates the declared {model_name} wire contract.",
                f"func (model *{model_name}) UnmarshalJSON(data []byte) error {{",
                f"\ttype plain {model_name}",
                "\tvar value plain",
                f"\tif err := unmarshalModel(data, &value, {required_go}, {nullable_go}, {allowed_go}, {str(reject_unknown).lower()}); err != nil {{",
                f"\t\treturn fmt.Errorf(\"decode {model_name}: %w\", err)",
                "\t}",
                f"\tdecoded := {model_name}(value)",
            ])
            if validates:
                validation_lines.extend([
                    "\tif err := decoded.Validate(); err != nil {",
                    f"\t\treturn fmt.Errorf(\"decode {model_name}: %w\", err)",
                    "\t}",
                ])
            validation_lines.extend([
                "\t*model = decoded",
                "\treturn nil",
                "}",
                "",
            ])
        if validates:
            validation_lines.extend(render_validate_method(model_name, schema, validation_models))
    return "\n".join(model_lines), "\n".join(validation_lines), directions


def model_needs_validation(schema: Mapping[str, Any]) -> bool:
    if schema.get("x-cregis-runtime-anyOf"):
        return True
    constraint_keys = {
        "enum", "minimum", "maximum", "exclusiveMinimum", "exclusiveMaximum",
        "minLength", "maxLength", "pattern", "minItems", "maxItems",
    }
    return any(
        constraint_keys & set(prop)
        for prop in schema.get("properties", {}).values()
        if isinstance(prop, dict)
    )


def direct_model_reference(schema: Mapping[str, Any]) -> tuple[str | None, bool]:
    reference = schema.get("$ref")
    if isinstance(reference, str) and reference.startswith(REFERENCE_PREFIX):
        return reference[len(REFERENCE_PREFIX):], False
    if schema.get("type") == "array" and isinstance(schema.get("items"), dict):
        reference = schema["items"].get("$ref")
        if isinstance(reference, str) and reference.startswith(REFERENCE_PREFIX):
            return reference[len(REFERENCE_PREFIX):], True
    return None, False


def models_requiring_validation(schemas: Mapping[str, Any]) -> set[str]:
    found = {
        name for name, schema in schemas.items()
        if isinstance(schema, dict) and model_needs_validation(schema)
    }
    changed = True
    while changed:
        changed = False
        for name, schema in schemas.items():
            if name in found or not isinstance(schema, dict):
                continue
            for prop in schema.get("properties", {}).values():
                if not isinstance(prop, dict):
                    continue
                reference, _ = direct_model_reference(prop)
                if reference in found:
                    found.add(name)
                    changed = True
                    break
    return found


def go_literal(value: Any) -> str:
    if isinstance(value, str):
        return quoted(value)
    if value is True:
        return "true"
    if value is False:
        return "false"
    if isinstance(value, (int, float)) and not isinstance(value, bool):
        return repr(value)
    raise RenderError(f"Unsupported Go constraint literal: {value!r}")


def property_expression(field_name: str, field_type: str) -> tuple[str | None, str]:
    if field_type.startswith("*"):
        return f"model.{field_name} != nil", f"*model.{field_name}"
    return None, f"model.{field_name}"


def append_guarded(lines: list[str], guard: str | None, statements: list[str]) -> None:
    if guard is None:
        lines.extend(f"\t{line}" for line in statements)
        return
    lines.append(f"\tif {guard} {{")
    lines.extend(f"\t\t{line}" for line in statements)
    lines.append("\t}")


def render_validate_method(
    model_name: str,
    schema: Mapping[str, Any],
    validation_models: set[str],
) -> list[str]:
    required = set(schema.get("required", []))
    lines = [
        f"// Validate checks OpenAPI constraints for {model_name}.",
        f"func (model {model_name}) Validate() error {{",
    ]
    properties = schema.get("properties", {})
    for wire_name, property_schema in properties.items():
        if not isinstance(property_schema, dict):
            continue
        field_name = go_name(wire_name)
        field_type = field_go_type(model_name, wire_name, property_schema, wire_name in required)
        guard, expression = property_expression(field_name, field_type)
        statements: list[str] = []
        enum = property_schema.get("enum")
        if isinstance(enum, list) and enum:
            cases = ", ".join(go_literal(item) for item in enum)
            statements.extend([
                f"switch {expression} {{",
                f"case {cases}:",
                "default:",
                f"\treturn fmt.Errorf(\"{wire_name} must be one of the documented values\")",
                "}",
            ])
        for key, operator in (
            ("minimum", "<"), ("maximum", ">"),
            ("exclusiveMinimum", "<="), ("exclusiveMaximum", ">="),
        ):
            if key in property_schema:
                statements.extend([
                    f"if {expression} {operator} {go_literal(property_schema[key])} {{",
                    f"\treturn fmt.Errorf(\"{wire_name} violates OpenAPI {key}\")",
                    "}",
                ])
        if "minLength" in property_schema:
            statements.extend([
                f"if utf8.RuneCountInString({expression}) < {property_schema['minLength']} {{",
                f"\treturn fmt.Errorf(\"{wire_name} is shorter than OpenAPI minLength\")",
                "}",
            ])
        if "maxLength" in property_schema:
            statements.extend([
                f"if utf8.RuneCountInString({expression}) > {property_schema['maxLength']} {{",
                f"\treturn fmt.Errorf(\"{wire_name} is longer than OpenAPI maxLength\")",
                "}",
            ])
        if "pattern" in property_schema:
            statements.extend([
                f"if matched, err := regexp.MatchString({quoted(property_schema['pattern'])}, {expression}); err != nil || !matched {{",
                f"\treturn fmt.Errorf(\"{wire_name} does not match its OpenAPI pattern\")",
                "}",
            ])
        if "minItems" in property_schema:
            statements.extend([
                f"if len({expression}) < {property_schema['minItems']} {{",
                f"\treturn fmt.Errorf(\"{wire_name} has fewer than OpenAPI minItems\")",
                "}",
            ])
        if "maxItems" in property_schema:
            statements.extend([
                f"if len({expression}) > {property_schema['maxItems']} {{",
                f"\treturn fmt.Errorf(\"{wire_name} has more than OpenAPI maxItems\")",
                "}",
            ])
        if statements:
            append_guarded(lines, guard, statements)
        reference, is_array = direct_model_reference(property_schema)
        if reference not in validation_models:
            continue
        if is_array:
            nested = [
                f"for index := range {expression} {{",
                f"\tif err := ({expression})[index].Validate(); err != nil {{",
                f"\t\treturn fmt.Errorf(\"{wire_name}[%d]: %w\", index, err)",
                "\t}",
                "}",
            ]
        else:
            nested = [
                f"if err := model.{field_name}.Validate(); err != nil {{",
                f"\treturn fmt.Errorf(\"{wire_name}: %w\", err)",
                "}",
            ]
        append_guarded(lines, guard, nested)
    alternatives = schema.get("x-cregis-runtime-anyOf")
    if isinstance(alternatives, list) and alternatives:
        groups = []
        for alternative in alternatives:
            if not isinstance(alternative, dict) or set(alternative) != {"required"}:
                raise RenderError(f"Unsupported runtime anyOf in {model_name}")
            expressions = []
            for field in alternative["required"]:
                property_schema = properties[field]
                field_type = field_go_type(model_name, field, property_schema, field in required)
                guard, _ = property_expression(go_name(field), field_type)
                expressions.append(guard or "true")
            groups.append("(" + " && ".join(expressions) + ")")
        lines.extend([
            f"\tif !({' || '.join(groups)}) {{",
            "\t\treturn fmt.Errorf(\"at least one OpenAPI anyOf required-field group must be present\")",
            "\t}",
        ])
    lines.extend(["\treturn nil", "}", ""])
    return lines


def model_enum_definitions(document: Mapping[str, Any]) -> list[tuple[str, list[tuple[str, Any]]]]:
    definitions: list[tuple[str, list[tuple[str, Any]]]] = []
    for model_name in sorted(document["components"]["schemas"]):
        schema = document["components"]["schemas"][model_name]
        for wire_name, property_schema in schema.get("properties", {}).items():
            if not isinstance(property_schema, dict):
                continue
            enum = property_schema.get("enum")
            if not isinstance(enum, list) or not enum:
                continue
            enum_type = enum_type_name(model_name, wire_name)
            definitions.append((enum_type, [
                (enum_type + enum_member_suffix(value), value) for value in enum
            ]))
    return definitions


def render_aliases(
    module: str,
    api_models: Mapping[str, list[str]],
    documents: Mapping[str, Mapping[str, Any]],
) -> str:
    lines = [
        "// Code generated from the canonical Cregis OpenAPI specification. DO NOT EDIT.",
        "package cregis",
        "",
        "import (",
    ]
    for api_name in sorted(api_models):
        lines.append(f'\t{api_name}models "{module}/generated/{api_name}"')
    lines.extend([")", ""])
    for api_name in sorted(api_models):
        for model in api_models[api_name]:
            lines.extend([
                f"// {model} is the generated {api_name} API contract model.",
                f"type {model} = {api_name}models.{model}",
                "",
            ])
        for enum_type, members in model_enum_definitions(documents[api_name]):
            lines.extend([
                f"// {enum_type} is the generated {api_name} API enum type.",
                f"type {enum_type} = {api_name}models.{enum_type}",
                "",
                "const (",
            ])
            for member, _ in members:
                lines.append(f"\t{member} = {api_name}models.{member}")
            lines.extend([")", ""])
    return "\n".join(lines)


def render_clients(documents: Mapping[str, Mapping[str, Any]], overrides: Mapping[str, Any]) -> str:
    client_types = {"payment": "PaymentClient", "waas": "WaaSClient", "team": "TeamClient"}
    lines = [
        "// Code generated from the canonical Cregis OpenAPI specification. DO NOT EDIT.",
        "package cregis",
        "",
        'import "context"',
        "",
    ]
    for api_name in ("payment", "waas", "team"):
        methods = overrides["apis"][api_name]["clientMethods"]
        for operation in documents[api_name]["x-cregis-operations"]:
            operation_id = operation["operationId"]
            method_name = methods.get(operation_id)
            if not isinstance(method_name, str):
                raise RenderError(f"Missing Go client method: {api_name}.{operation_id}")
            client_type = client_types[api_name]
            request_model = operation.get("requestModel")
            response_model = operation.get("responseModel")
            container = operation.get("responseContainer")
            parameters = "ctx context.Context"
            request_value = "nil"
            if request_model is not None:
                parameters += f", request *{request_model}"
                request_value = "request"
            operation_value = (
                f'operation{{id: {quoted(operation_id)}, method: "POST", path: '
                f'{quoted(operation["path"])}}}'
            )
            if response_model is None:
                lines.extend([
                    f"// {method_name} calls {operation['method'].upper()} {operation['path']}.",
                    f"func (client *{client_type}) {method_name}({parameters}) error {{",
                    f"\treturn executeNoResponse(ctx, client.base, {operation_value}, {request_value})",
                    "}",
                    "",
                ])
            elif container == "array":
                lines.extend([
                    f"// {method_name} calls {operation['method'].upper()} {operation['path']}.",
                    f"func (client *{client_type}) {method_name}({parameters}) ([]%s, error) {{" % response_model,
                    f"\tresponse, err := execute[[]{response_model}](ctx, client.base, {operation_value}, {request_value})",
                    "\tif err != nil {",
                    "\t\treturn nil, err",
                    "\t}",
                    "\treturn *response, nil",
                    "}",
                    "",
                ])
            else:
                lines.extend([
                    f"// {method_name} calls {operation['method'].upper()} {operation['path']}.",
                    f"func (client *{client_type}) {method_name}({parameters}) (*{response_model}, error) {{",
                    f"\treturn execute[{response_model}](ctx, client.base, {operation_value}, {request_value})",
                    "}",
                    "",
                ])
    return "\n".join(lines)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--prepared-dir", required=True, type=pathlib.Path)
    parser.add_argument("--output-dir", required=True, type=pathlib.Path)
    parser.add_argument("--overrides", required=True, type=pathlib.Path)
    parser.add_argument("--config", required=True, type=pathlib.Path)
    parser.add_argument("--manifest", required=True, type=pathlib.Path)
    args = parser.parse_args()

    config = read_json(args.config)
    overrides = read_json(args.overrides)
    if config.get("generatorVersion") != 1 or overrides.get("version") != 1:
        raise RenderError("Unsupported Go generator configuration version")
    documents: dict[str, dict[str, Any]] = {}
    directions: dict[str, dict[str, str]] = {}
    api_models: dict[str, list[str]] = {}
    args.output_dir.mkdir(parents=True, exist_ok=True)
    for api_name in ("payment", "waas", "team"):
        document = read_json(args.prepared_dir / f"{api_name}.json")
        documents[api_name] = document
        models_source, validation_source, model_directions = render_models(api_name, document)
        output = args.output_dir / "generated" / api_name
        output.mkdir(parents=True, exist_ok=True)
        (output / "models.gen.go").write_text(models_source, encoding="utf-8")
        (output / "validation.gen.go").write_text(validation_source, encoding="utf-8")
        directions[api_name] = model_directions
        api_models[api_name] = sorted(document["components"]["schemas"])

    all_models = [model for models in api_models.values() for model in models]
    if len(all_models) != len(set(all_models)):
        raise RenderError("Generated Go model names collide across APIs")
    module = config.get("module")
    if not isinstance(module, str) or not module:
        raise RenderError("Go module path is required")
    (args.output_dir / "models_aliases.gen.go").write_text(
        render_aliases(module, api_models, documents), encoding="utf-8"
    )
    (args.output_dir / "clients.gen.go").write_text(
        render_clients(documents, overrides), encoding="utf-8"
    )

    source_manifest = read_json(args.prepared_dir / "manifest.json")
    for api_name, api in source_manifest["apis"].items():
        api["generatedDirectory"] = f"sdks/go/generated/{api_name}"
        api["modelPolicy"] = directions[api_name]
        methods = overrides["apis"][api_name]["clientMethods"]
        for operation in api["operations"]:
            operation["clientMethod"] = methods[operation["operationId"]]
    source_manifest["generator"] = {
        "configSha256": sha256_file(args.config),
        "minimumGoVersion": config["minimumGoVersion"],
        "module": module,
        "name": "cregis-go-model-generator",
        "overridesSha256": sha256_file(args.overrides),
        "rendererSha256": sha256_file(pathlib.Path(__file__)),
        "version": config["generatorVersion"],
    }
    args.manifest.parent.mkdir(parents=True, exist_ok=True)
    args.manifest.write_text(
        json.dumps(source_manifest, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except RenderError as exc:
        print(f"Go model rendering failed: {exc}")
        raise SystemExit(1)
