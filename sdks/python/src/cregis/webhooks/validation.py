"""Shared project webhook signature verification."""

from __future__ import annotations

import hmac
import json
import re
from typing import Any, Dict, Union

from cregis.errors import CregisClientError
from cregis.signing import sign_project_parameters

_SIGNATURE_PATTERN = re.compile(r"^[0-9a-f]{32}$", re.IGNORECASE)
RawBody = Union[str, bytes]


def verify_project_webhook(raw_body: RawBody, api_key: str) -> Dict[str, Any]:
    """Verify a Payment/WaaS callback and return its decoded JSON object."""

    if not isinstance(api_key, str) or not api_key.strip():
        raise CregisClientError("API Key is required")
    if not isinstance(raw_body, (str, bytes)) or not raw_body.strip():
        raise CregisClientError("Callback body is required")
    try:
        parsed = json.loads(raw_body)
    except (json.JSONDecodeError, UnicodeDecodeError, TypeError) as exc:
        raise CregisClientError("Failed to parse callback JSON for signature verification") from exc
    if not isinstance(parsed, dict):
        raise CregisClientError("Callback body must be a JSON object")

    incoming_sign = parsed.get("sign")
    if not isinstance(incoming_sign, str) or _SIGNATURE_PATTERN.fullmatch(incoming_sign) is None:
        raise CregisClientError("Callback signature must be a 32-character hexadecimal string")
    for field in ("pid", "timestamp"):
        value = parsed.get(field)
        if isinstance(value, bool) or not isinstance(value, int) or value <= 0:
            raise CregisClientError(f"Callback {field} must be a positive integer")
    nonce = parsed.get("nonce")
    if not isinstance(nonce, str) or not nonce.strip():
        raise CregisClientError("Callback nonce must be a non-empty string")

    unsigned = dict(parsed)
    del unsigned["sign"]
    calculated = sign_project_parameters(unsigned, api_key)
    if not hmac.compare_digest(calculated, incoming_sign.lower()):
        raise CregisClientError("Callback signature verification failed")
    return parsed
