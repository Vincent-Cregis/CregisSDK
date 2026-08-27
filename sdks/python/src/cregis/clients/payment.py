"""Synchronous Payment Engine client."""

from typing import cast

from cregis.generated.payment.models import (
    CreateOrderRequest,
    CreateOrderResponse,
    QueryOrderRequest,
    QueryOrderResponse,
)
from cregis.generated.payment.operations import OPERATIONS
from cregis.transport import CregisProjectClientBase


class CregisPaymentClient(CregisProjectClientBase):
    def create_order(self, request: CreateOrderRequest) -> CreateOrderResponse:
        return cast(CreateOrderResponse, self._post(OPERATIONS["create_order"], request))

    def query_order(self, request: QueryOrderRequest) -> QueryOrderResponse:
        return cast(QueryOrderResponse, self._post(OPERATIONS["query_order"], request))
