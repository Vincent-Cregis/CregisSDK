#!/usr/bin/env python3
"""Fast structural check for committed generated Go models and clients."""

from __future__ import annotations

import json
import pathlib
import re
import sys


def fail(message: str) -> None:
    raise SystemExit(message)


def main() -> int:
    repo_root = pathlib.Path(__file__).resolve().parents[2]
    manifest_path = repo_root / "codegen/manifests/go-models.lock.json"
    try:
        manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        fail(f"Cannot read Go model manifest: {exc}")

    aliases = (repo_root / "sdks/go/models_aliases.gen.go").read_text(encoding="utf-8")
    clients = (repo_root / "sdks/go/clients.gen.go").read_text(encoding="utf-8")
    total_models = 0
    total_operations = 0
    for api_name, api in manifest.get("apis", {}).items():
        source_path = repo_root / "sdks/go/generated" / api_name / "models.gen.go"
        validation_path = repo_root / "sdks/go/generated" / api_name / "validation.gen.go"
        if not source_path.is_file() or not validation_path.is_file():
            fail(f"Missing generated Go package: {api_name}")
        source = source_path.read_text(encoding="utf-8")
        generated = set(re.findall(r"^type ([A-Za-z][A-Za-z0-9]*) struct \{$", source, re.MULTILINE))
        expected = set(api.get("models", []))
        if generated != expected:
            fail(
                f"Generated Go model mismatch for {api_name}: "
                f"missing={sorted(expected - generated)}, extra={sorted(generated - expected)}"
            )
        for model in expected:
            if not re.search(rf"^type {re.escape(model)} = ", aliases, re.MULTILINE):
                fail(f"Missing public Go model alias: {model}")
        enum_types = set(re.findall(
            r"^type ([A-Za-z][A-Za-z0-9]*) (?:string|bool|int|int32|int64|float32|float64)$",
            source,
            re.MULTILINE,
        ))
        for enum_type in enum_types:
            if not re.search(rf"^type {re.escape(enum_type)} = ", aliases, re.MULTILINE):
                fail(f"Missing public Go enum alias: {enum_type}")
        for operation in api.get("operations", []):
            method = operation.get("clientMethod")
            if not isinstance(method, str) or not re.search(
                rf"^func \(client \*[A-Za-z]+Client\) {re.escape(method)}\(", clients, re.MULTILINE
            ):
                fail(f"Missing generated Go client method: {api_name}.{method}")
        total_models += len(expected)
        total_operations += len(api.get("operations", []))

    if total_operations != 23:
        fail(f"Expected 23 generated Go operations, found {total_operations}")
    managed = {"pid", "nonce", "timestamp", "sign"}
    for api_name in ("payment", "waas", "team"):
        source = (repo_root / "sdks/go/generated" / api_name / "models.gen.go").read_text(
            encoding="utf-8"
        )
        validation = (
            repo_root / "sdks/go/generated" / api_name / "validation.gen.go"
        ).read_text(encoding="utf-8")
        policies = manifest["apis"][api_name].get("modelPolicy", {})
        for model, direction in policies.items():
            if direction != "request":
                continue
            if not re.search(
                rf"^func \(model \*{re.escape(model)}\) UnmarshalJSON\(",
                validation,
                re.MULTILINE,
            ):
                fail(f"Missing strict Go request decoder: {api_name}.{model}")
            if api_name == "team":
                continue
            match = re.search(
                rf"^type {re.escape(model)} struct \{{\n(.*?)^\}}$",
                source,
                re.MULTILINE | re.DOTALL,
            )
            if match is None:
                fail(f"Cannot inspect generated Go request model: {api_name}.{model}")
            for wire_name in managed:
                if f'json:"{wire_name}' in match.group(1):
                    fail(
                        f"SDK-managed field leaked into generated Go request model: "
                        f"{api_name}.{model}.{wire_name}"
                    )
    print(
        f"Committed Go model boundary is consistent: {total_models} models, "
        f"{total_operations} operations"
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
