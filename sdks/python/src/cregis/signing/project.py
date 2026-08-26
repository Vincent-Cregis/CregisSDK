"""Payment Engine and WaaS parameter signing."""

from __future__ import annotations

import hashlib
import math
from typing import Any, Mapping

import rfc8785

from cregis.errors import CregisClientError


def _canonicalize_nested(value: Any) -> str:
    try:
        return rfc8785.dumps(value).decode("utf-8")
    except (TypeError, ValueError, rfc8785.CanonicalizationError) as exc:
        raise CregisClientError("Failed to serialize a nested signature value") from exc


def _stringify_signature_value(value: Any) -> str:
    if isinstance(value, (dict, list)):
        return _canonicalize_nested(value)
    if isinstance(value, bool):
        return "true" if value else "false"
    if isinstance(value, int):
        return str(value)
    if isinstance(value, float):
        if not math.isfinite(value):
            raise CregisClientError("Signature parameters must contain finite numbers")
        try:
            return rfc8785.dumps(value).decode("ascii")
        except (ValueError, rfc8785.CanonicalizationError) as exc:
            raise CregisClientError("Failed to serialize a signature number") from exc
    return str(value)


def sign_project_parameters(parameters: Mapping[str, Any], api_key: str) -> str:
    """Return the Cregis MD5 signature for project-authenticated parameters."""

    if not isinstance(parameters, Mapping):
        raise CregisClientError("Signature parameters must be a JSON object")
    if not isinstance(api_key, str) or not api_key.strip():
        raise CregisClientError("API Key is required")

    signing_text = api_key
    for key in sorted(parameters):
        if not isinstance(key, str):
            raise CregisClientError("Signature parameter names must be strings")
        value = parameters[key]
        if key == "sign" or value is None:
            continue
        string_value = _stringify_signature_value(value)
        if not string_value:
            continue
        signing_text += key + string_value
    return hashlib.md5(signing_text.encode("utf-8")).hexdigest()  # noqa: S324
