"""Metadata generated from the canonical OpenAPI specifications."""

from __future__ import annotations

from dataclasses import dataclass
from typing import Optional, Type

from pydantic import BaseModel


@dataclass(frozen=True)
class GeneratedOperation:
    operation_id: str
    method: str
    path: str
    request_model: Optional[Type[BaseModel]]
    response_model: Optional[Type[BaseModel]]
    response_container: Optional[str]


@dataclass(frozen=True)
class GeneratedWebhook:
    operation_id: str
    model: Type[BaseModel]
