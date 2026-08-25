# Generated from the canonical Cregis OpenAPI specification. Do not edit.
from cregis.operation import GeneratedWebhook
from .models import *  # noqa: F403

WEBHOOKS = {
    "depositCallback": GeneratedWebhook(
        operation_id="depositCallback",
        model=AddressDepositCallbackNotification,
    ),
    "payoutCallback": GeneratedWebhook(
        operation_id="payoutCallback",
        model=PayoutCallbackNotification,
    ),
    "payoutExternalVerificationCallback": GeneratedWebhook(
        operation_id="payoutExternalVerificationCallback",
        model=PayoutExternalVerificationCallbackNotification,
    ),
    "withdrawalCallback": GeneratedWebhook(
        operation_id="withdrawalCallback",
        model=WithdrawalCallbackNotification,
    ),
}
