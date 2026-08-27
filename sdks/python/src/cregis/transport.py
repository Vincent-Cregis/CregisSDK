"""Synchronous HTTP transport shared by all Cregis API clients."""

from __future__ import annotations

import json
import secrets
import time
import uuid
from typing import Any, Dict, Mapping, Optional, Union
from urllib.parse import urlsplit, urlunsplit

import httpx
from pydantic import BaseModel, ValidationError
from typing_extensions import Self

from cregis.errors import (
    CregisApiError,
    CregisClientError,
    CregisContractError,
    CregisHttpError,
)
from cregis.operation import GeneratedOperation
from cregis.signing import canonicalize_json, sign_project_parameters, sign_team_request

_MAX_SAFE_INTEGER = 9_007_199_254_740_991


def _require_non_blank(value: str, name: str) -> str:
    if not isinstance(value, str) or not value.strip():
        raise CregisClientError(f"{name} is required")
    return value


def _normalize_pid(value: Union[int, str]) -> int:
    if isinstance(value, bool):
        raise CregisClientError("PID must be a positive JavaScript-safe int64 value")
    if isinstance(value, str):
        stripped = value.strip()
        parsed = int(stripped) if stripped.isdigit() else 0
    elif isinstance(value, int):
        parsed = value
    else:
        parsed = 0
    if parsed <= 0 or parsed > _MAX_SAFE_INTEGER:
        raise CregisClientError("PID must be a positive JavaScript-safe int64 value")
    return parsed


def _normalize_base_url(value: str) -> str:
    _require_non_blank(value, "Base URL")
    try:
        parsed = urlsplit(value.strip())
        hostname = parsed.hostname
    except ValueError as exc:
        raise CregisClientError("Base URL must be a valid HTTP(S) URL") from exc
    if not parsed.scheme or not parsed.netloc or hostname is None:
        raise CregisClientError("Base URL must be a valid HTTP(S) URL")
    if parsed.username is not None or parsed.password is not None:
        raise CregisClientError("Base URL must not contain user information")
    if parsed.query or parsed.fragment:
        raise CregisClientError("Base URL must not contain a query or fragment")
    loopback = hostname in {"localhost", "127.0.0.1", "::1"}
    if parsed.scheme != "https" and not (parsed.scheme == "http" and loopback):
        raise CregisClientError("Base URL must use HTTPS")
    path = parsed.path.rstrip("/")
    return urlunsplit((parsed.scheme, parsed.netloc, path, "", ""))


class _CregisBaseClient:
    def __init__(
        self,
        *,
        base_url: str,
        timeout: float = 30.0,
        client: Optional[httpx.Client] = None,
    ) -> None:
        self._base_url = _normalize_base_url(base_url)
        if isinstance(timeout, bool) or not isinstance(timeout, (int, float)) or timeout <= 0:
            raise CregisClientError("timeout must be greater than zero")
        self._timeout = float(timeout)
        self._owns_client = client is None
        self._client = client or httpx.Client()

    def _authenticate(self, path: str, payload: Dict[str, Any]) -> tuple[bytes, Mapping[str, str]]:
        raise NotImplementedError

    def _post(self, operation: GeneratedOperation, request: Optional[BaseModel] = None) -> Any:
        if operation.method != "POST":
            raise CregisClientError(f"Unsupported HTTP method: {operation.method}")
        payload = self._serialize_request(operation, request)
        body, auth_headers = self._authenticate(operation.path, payload)
        headers = {
            "Accept": "application/json",
            "Content-Type": "application/json; charset=utf-8",
            **auth_headers,
        }
        try:
            response = self._client.post(
                f"{self._base_url}{operation.path}",
                content=body,
                headers=headers,
                timeout=self._timeout,
                follow_redirects=False,
            )
        except httpx.TimeoutException as exc:
            raise CregisClientError(
                f"Cregis request timed out after {self._timeout:g} seconds"
            ) from exc
        except httpx.RequestError as exc:
            raise CregisClientError(f"Network error executing POST {operation.path}") from exc

        if not 200 <= response.status_code < 300:
            raise CregisHttpError(
                response.status_code,
                response.reason_phrase,
                response.text,
            )
        envelope = self._parse_envelope(response, operation)
        if envelope["code"] != "00000":
            raise CregisApiError(envelope["code"], envelope["msg"])
        return self._validate_response(operation, envelope["data"])

    @staticmethod
    def _serialize_request(
        operation: GeneratedOperation, request: Optional[BaseModel]
    ) -> Dict[str, Any]:
        expected = operation.request_model
        if expected is None:
            if request is not None:
                cause = TypeError(f"{operation.operation_id} does not accept a request model")
                raise CregisContractError(f"{operation.operation_id} request", cause)
            return {}
        if not isinstance(request, expected):
            cause = TypeError(f"expected {expected.__name__}, got {type(request).__name__}")
            raise CregisContractError(f"{operation.operation_id} request", cause)
        try:
            payload = request.model_dump(mode="json", by_alias=True, exclude_none=True)
        except Exception as exc:
            raise CregisContractError(f"{operation.operation_id} request", exc) from exc
        if not isinstance(payload, dict):
            cause = TypeError("serialized request is not a JSON object")
            raise CregisContractError(f"{operation.operation_id} request", cause)
        return payload

    @staticmethod
    def _parse_envelope(response: httpx.Response, operation: GeneratedOperation) -> Dict[str, Any]:
        try:
            value = response.json()
        except (json.JSONDecodeError, ValueError) as exc:
            raise CregisClientError(
                f"Failed to parse Cregis response for POST {operation.path}"
            ) from exc
        context = f"{operation.operation_id} response envelope"
        if not isinstance(value, dict):
            cause = TypeError("Cregis response must be a JSON object")
            raise CregisContractError(context, cause)
        code = value.get("code")
        if not isinstance(code, str) or not code.strip():
            cause = TypeError("Cregis response is missing required string field: code")
            raise CregisContractError(context, cause)
        msg = value.get("msg")
        if not isinstance(msg, str):
            cause = TypeError("Cregis response is missing required string field: msg")
            raise CregisContractError(context, cause)
        if "data" not in value:
            cause = TypeError("Cregis response is missing required field: data")
            raise CregisContractError(context, cause)
        return {"code": code, "msg": msg, "data": value["data"]}

    @staticmethod
    def _validate_response(operation: GeneratedOperation, data: Any) -> Any:
        model = operation.response_model
        if model is None:
            return None
        try:
            if operation.response_container == "array":
                if not isinstance(data, list):
                    raise TypeError("response data must be an array")
                return [model.model_validate(item, strict=True) for item in data]
            return model.model_validate(data, strict=True)
        except (ValidationError, TypeError, ValueError) as exc:
            raise CregisContractError(f"{operation.operation_id} response", exc) from exc

    def close(self) -> None:
        if self._owns_client:
            self._client.close()

    def __enter__(self) -> Self:
        return self

    def __exit__(self, exc_type: Any, exc: Any, traceback: Any) -> None:
        self.close()


class CregisProjectClientBase(_CregisBaseClient):
    def __init__(
        self,
        *,
        pid: Union[int, str],
        api_key: str,
        base_url: str,
        timeout: float = 30.0,
        client: Optional[httpx.Client] = None,
    ) -> None:
        self._pid = _normalize_pid(pid)
        self._api_key = _require_non_blank(api_key, "API Key")
        super().__init__(base_url=base_url, timeout=timeout, client=client)

    def _authenticate(self, path: str, payload: Dict[str, Any]) -> tuple[bytes, Mapping[str, str]]:
        parameters = {
            **payload,
            "pid": self._pid,
            "nonce": secrets.token_hex(3),
            "timestamp": time.time_ns() // 1_000_000,
        }
        parameters["sign"] = sign_project_parameters(parameters, self._api_key)
        try:
            return (
                json.dumps(parameters, ensure_ascii=False, separators=(",", ":")).encode("utf-8"),
                {},
            )
        except (TypeError, ValueError) as exc:
            raise CregisClientError("Failed to serialize request payload") from exc


class CregisTeamClientBase(_CregisBaseClient):
    def __init__(
        self,
        *,
        access_key: str,
        access_secret: str,
        base_url: str,
        timeout: float = 30.0,
        client: Optional[httpx.Client] = None,
    ) -> None:
        self._access_key = _require_non_blank(access_key, "Access Key")
        self._access_secret = _require_non_blank(access_secret, "Access Secret")
        super().__init__(base_url=base_url, timeout=timeout, client=client)

    def _authenticate(self, path: str, payload: Dict[str, Any]) -> tuple[bytes, Mapping[str, str]]:
        body = canonicalize_json(payload)
        timestamp = time.time_ns() // 1_000_000
        nonce = uuid.uuid4().hex
        signature = sign_team_request(
            path,
            timestamp,
            nonce,
            body,
            self._access_secret,
        )
        return body.encode("utf-8"), {
            "Access-Key": self._access_key,
            "Access-Timestamp": str(timestamp),
            "Access-Nonce": nonce,
            "Access-Signature": signature,
        }
