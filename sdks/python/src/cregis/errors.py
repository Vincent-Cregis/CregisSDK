"""Public Cregis SDK exception hierarchy."""

from __future__ import annotations

from typing import Optional


class CregisError(Exception):
    """Base class for all supported SDK errors."""


class CregisClientError(CregisError):
    """Invalid configuration, serialization, timeout, or network failure."""


class CregisContractError(CregisClientError):
    """Request, response, or webhook data does not satisfy the OpenAPI contract."""

    def __init__(self, context: str, cause: Exception) -> None:
        super().__init__(f"Cregis contract validation failed for {context}: {cause}")
        self.context = context
        self.cause = cause


class CregisHttpError(CregisError):
    """The server returned a non-success HTTP status."""

    def __init__(self, status_code: int, status_text: str, response_body: str) -> None:
        super().__init__(f"Cregis HTTP error: [{status_code}] {status_text}".rstrip())
        self.status_code = status_code
        self.status_text = status_text
        self.response_body = response_body


class CregisApiError(CregisError):
    """The server returned a non-success Cregis business code."""

    def __init__(self, code: str, api_message: Optional[str] = None) -> None:
        message = f"Cregis API error: [{code}] {api_message or ''}".rstrip()
        super().__init__(message)
        self.code = code
        self.api_message = api_message
