"""WaaS callback verification and contract parsing."""

from __future__ import annotations

from typing import Type, TypeVar

from pydantic import BaseModel, ValidationError

from cregis.errors import CregisClientError, CregisContractError
from cregis.generated.waas.models import (
    AddressDepositCallbackNotification,
    PayoutCallbackNotification,
    PayoutExternalVerificationCallbackNotification,
    WithdrawalCallbackNotification,
)
from cregis.webhooks.validation import RawBody, verify_project_webhook

WebhookModel = TypeVar("WebhookModel", bound=BaseModel)


class CregisWaasCallbackHandler:
    CALLBACK_SUCCESS = "success"
    EXTERNAL_VERIFICATION_APPROVE = "ok"
    EXTERNAL_VERIFICATION_DENY = "deny"

    def __init__(self, api_key: str) -> None:
        if not isinstance(api_key, str) or not api_key.strip():
            raise CregisClientError("API Key is required")
        self._api_key = api_key

    def handle_deposit_callback(self, raw_body: RawBody) -> AddressDepositCallbackNotification:
        return self._verify(raw_body, AddressDepositCallbackNotification, "WaaS deposit callback")

    def handle_payout_callback(self, raw_body: RawBody) -> PayoutCallbackNotification:
        return self._verify(raw_body, PayoutCallbackNotification, "WaaS payout callback")

    def handle_payout_external_verification_callback(
        self, raw_body: RawBody
    ) -> PayoutExternalVerificationCallbackNotification:
        return self._verify(
            raw_body,
            PayoutExternalVerificationCallbackNotification,
            "WaaS payout external verification callback",
        )

    def handle_withdrawal_callback(self, raw_body: RawBody) -> WithdrawalCallbackNotification:
        return self._verify(
            raw_body,
            WithdrawalCallbackNotification,
            "WaaS withdrawal callback",
        )

    def _verify(
        self,
        raw_body: RawBody,
        model: Type[WebhookModel],
        name: str,
    ) -> WebhookModel:
        payload = verify_project_webhook(raw_body, self._api_key)
        try:
            return model.model_validate(payload, strict=True)
        except (ValidationError, TypeError, ValueError) as exc:
            raise CregisContractError(name, exc) from exc
