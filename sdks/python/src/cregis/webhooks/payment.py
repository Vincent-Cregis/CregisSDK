"""Payment Engine callback verification and contract parsing."""

from __future__ import annotations

from typing import Dict, Type

from pydantic import BaseModel, ValidationError

from cregis.errors import CregisClientError, CregisContractError
from cregis.generated.payment.models import (
    PaymentCallbackEnvelope,
    PaymentCallbackEnvelopeData,
    PaymentCompletedCallbackData,
    PaymentExpiredCallbackData,
    PaymentRefundedCallbackData,
    PaymentRemainingCallbackData,
)
from cregis.webhooks.validation import RawBody, verify_project_webhook

_EVENT_MODELS: Dict[str, Type[BaseModel]] = {
    "paid": PaymentCompletedCallbackData,
    "paid_partial": PaymentCompletedCallbackData,
    "paid_over": PaymentCompletedCallbackData,
    "expired": PaymentExpiredCallbackData,
    "refunded": PaymentRefundedCallbackData,
    "paid_remain": PaymentRemainingCallbackData,
}


class CregisPaymentCallbackHandler:
    CALLBACK_SUCCESS = "success"

    def __init__(self, api_key: str) -> None:
        if not isinstance(api_key, str) or not api_key.strip():
            raise CregisClientError("API Key is required")
        self._api_key = api_key

    def verify_and_parse(self, raw_body: RawBody) -> PaymentCallbackEnvelope:
        payload = verify_project_webhook(raw_body, self._api_key)
        event_type = payload.get("event_type")
        model = _EVENT_MODELS.get(event_type) if isinstance(event_type, str) else None
        if model is None:
            cause = ValueError(f"unsupported payment event_type: {event_type!r}")
            raise CregisContractError("Payment callback", cause)
        try:
            data = model.model_validate(payload.get("data"), strict=True)
            one_of_data = PaymentCallbackEnvelopeData(actual_instance=data)
            envelope = dict(payload)
            envelope["data"] = one_of_data
            return PaymentCallbackEnvelope.model_validate(envelope, strict=True)
        except (ValidationError, TypeError, ValueError) as exc:
            raise CregisContractError("Payment callback", exc) from exc
