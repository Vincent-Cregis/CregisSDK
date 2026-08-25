# Generated from the canonical Cregis OpenAPI specification. Do not edit.
from cregis.operation import GeneratedWebhook
from .models import *  # noqa: F403

WEBHOOKS = {
    "orderCallback": GeneratedWebhook(
        operation_id="orderCallback",
        model=PaymentCallbackEnvelope,
    ),
}
