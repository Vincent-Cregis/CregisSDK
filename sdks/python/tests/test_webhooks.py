from __future__ import annotations

import json
from typing import Any, Dict

import pytest

from cregis import (
    CregisClientError,
    CregisContractError,
    CregisPaymentCallbackHandler,
    CregisWaasCallbackHandler,
    PaymentRefundedCallbackData,
    sign_project_parameters,
)

API_KEY = "callback-api-key"


def _signed_body(payload: Dict[str, Any], api_key: str = API_KEY) -> str:
    return json.dumps({**payload, "sign": sign_project_parameters(payload, api_key)})


def _common_envelope() -> Dict[str, Any]:
    return {"pid": 1382528827416576, "nonce": "m8jisx", "timestamp": 1687848653294}


def _payment_data(event_type: str) -> Dict[str, Any]:
    common = {
        "cregis_id": "po-1",
        "order_id": "merchant-1",
        "status": "expired"
        if event_type == "expired"
        else "canceled"
        if event_type == "refunded"
        else "paid",
    }
    if event_type == "expired":
        return common
    return {
        **common,
        "payment_address": "T-payment",
        "receive_amount": "10",
        "receive_currency": "USD",
        "pay_amount": "10",
        "pay_currency": "USDT-TRC20",
        "exchange_rate": "1",
        "tx_id": "tx-1",
        "transact_time": 1719994383015,
    }


def _waas_base() -> Dict[str, Any]:
    return {
        **_common_envelope(),
        "cid": 1382813146816512,
        "chain_id": "195",
        "token_id": "195",
        "currency": "195@195",
        "amount": "10.5",
    }


def test_payment_callback_dispatches_all_documented_events() -> None:
    handler = CregisPaymentCallbackHandler(API_KEY)
    for event_type in (
        "paid",
        "paid_partial",
        "paid_over",
        "expired",
        "refunded",
        "paid_remain",
    ):
        result = handler.verify_and_parse(
            _signed_body(
                {
                    **_common_envelope(),
                    "event_name": "order",
                    "event_type": event_type,
                    "data": _payment_data(event_type),
                }
            )
        )
        assert result.event_type == event_type


def test_payment_callback_preserves_refund_wire_types() -> None:
    result = CregisPaymentCallbackHandler(API_KEY).verify_and_parse(
        _signed_body(
            {
                **_common_envelope(),
                "event_name": "order",
                "event_type": "refunded",
                "future_envelope_field": "ignored",
                "data": {
                    **_payment_data("refunded"),
                    "type": 1,
                    "refund_id": "rf-1",
                    "refund_status": 1,
                    "future_data_field": "ignored",
                },
            }
        )
    )
    assert isinstance(result.data.actual_instance, PaymentRefundedCallbackData)
    assert result.data.actual_instance.refund_status == 1
    assert CregisPaymentCallbackHandler.CALLBACK_SUCCESS == "success"


def test_all_four_waas_webhook_contracts_verify_and_parse() -> None:
    handler = CregisWaasCallbackHandler(API_KEY)
    deposit = handler.handle_deposit_callback(
        _signed_body(
            {
                **_waas_base(),
                "address": "deposit-address",
                "status": "1",
                "txid": "tx-1",
                "block_time": "1734328473070",
            }
        )
    )
    assert deposit.status == "1"

    payout = handler.handle_payout_callback(
        _signed_body(
            {
                **_waas_base(),
                "address": "payout-address",
                "third_party_id": "payout-1",
                "status": 6,
                "block_time": 1734328473070,
            }
        )
    )
    assert payout.status == 6

    external_base = _waas_base()
    del external_base["currency"]
    external = handler.handle_payout_external_verification_callback(
        _signed_body(
            {
                **external_base,
                "third_party_id": "external-1",
                "from_address": "from",
                "to_address": "to",
            }
        )
    )
    assert external.third_party_id == "external-1"

    withdrawal = handler.handle_withdrawal_callback(
        _signed_body(
            {
                **_waas_base(),
                "from_address": "from",
                "to_address": "to",
                "third_party_id": "withdrawal-1",
                "status": 6,
            }
        )
    )
    assert withdrawal.to_address == "to"


def test_webhooks_reject_bad_signatures_and_wrong_wire_types() -> None:
    payment = CregisPaymentCallbackHandler(API_KEY)
    waas = CregisWaasCallbackHandler(API_KEY)
    with pytest.raises(CregisClientError):
        payment.verify_and_parse("{}")
    with pytest.raises(CregisContractError):
        payment.verify_and_parse(
            _signed_body(
                {
                    **_common_envelope(),
                    "event_name": "order",
                    "event_type": "future_event",
                    "data": {},
                }
            )
        )
    with pytest.raises(CregisContractError):
        waas.handle_deposit_callback(
            _signed_body(
                {
                    **_waas_base(),
                    "address": "deposit-address",
                    "status": 1,
                    "txid": "tx-1",
                }
            )
        )
