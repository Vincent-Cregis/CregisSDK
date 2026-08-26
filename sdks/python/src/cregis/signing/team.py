"""Team API RFC 8785 canonicalization and HMAC signing."""

from __future__ import annotations

import hashlib
import hmac
import json
from typing import Any

import rfc8785

from cregis.errors import CregisClientError


def canonicalize_json(value: Any) -> str:
    """Return RFC 8785 canonical JSON for a Python value or JSON string."""

    try:
        json_value = json.loads(value) if isinstance(value, str) else value
        return rfc8785.dumps(json_value).decode("utf-8")
    except (json.JSONDecodeError, TypeError, ValueError, rfc8785.CanonicalizationError) as exc:
        raise CregisClientError("JSON value cannot be canonicalized") from exc


def sign_team_request(
    path: str,
    timestamp: int,
    nonce: str,
    canonical_body: str,
    access_secret: str,
) -> str:
    """Return the Team API HMAC-SHA256 request signature."""

    if not isinstance(path, str) or not path.startswith("/"):
        raise CregisClientError("Team API signing path must start with '/'")
    if isinstance(timestamp, bool) or not isinstance(timestamp, int) or timestamp <= 0:
        raise CregisClientError("Team API timestamp must be a positive integer")
    if not isinstance(nonce, str) or not 16 <= len(nonce) <= 64:
        raise CregisClientError("Team API nonce must contain 16 to 64 characters")
    if not isinstance(canonical_body, str):
        raise CregisClientError("Team API canonical body must be a string")
    if not isinstance(access_secret, str) or not access_secret.strip():
        raise CregisClientError("Access Secret is required")

    signing_text = f"{path}\n{timestamp}\n{nonce}\n{canonical_body}"
    return hmac.new(
        access_secret.encode("utf-8"),
        signing_text.encode("utf-8"),
        hashlib.sha256,
    ).hexdigest()
