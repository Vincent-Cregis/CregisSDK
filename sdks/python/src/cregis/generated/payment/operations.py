# Generated from the canonical Cregis OpenAPI specification. Do not edit.
from cregis.operation import GeneratedOperation
from .models import *  # noqa: F403

OPERATIONS = {
    "create_order": GeneratedOperation(
        operation_id="createOrder",
        method="POST",
        path="/api/v2/checkout",
        request_model=CreateOrderRequest,
        response_model=CreateOrderResponse,
        response_container=None,
    ),
    "query_order": GeneratedOperation(
        operation_id="queryOrder",
        method="POST",
        path="/api/v2/order/info",
        request_model=QueryOrderRequest,
        response_model=QueryOrderResponse,
        response_container=None,
    ),
}
