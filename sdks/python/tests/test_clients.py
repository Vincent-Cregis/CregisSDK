from __future__ import annotations

import json
from typing import Any, Dict, List

import httpx
import pytest
from pydantic import ValidationError

from cregis import (
    AddressBalanceRequest,
    AddressBalanceV2Request,
    AddressUpdateRequest,
    BalanceCollectRequest,
    BatchGenerateAddressRequest,
    CheckAddressLegalityRequest,
    CreateOrderRequest,
    CregisApiError,
    CregisClientError,
    CregisContractError,
    CregisHttpError,
    CregisPaymentClient,
    CregisTeamClient,
    CregisWaasClient,
    GenerateAddressRequest,
    ListTeamWalletAddressesRequest,
    ListTeamWalletsRequest,
    PayoutRequest,
    PayoutV1Request,
    QueryOrderRequest,
    QueryOrderResponse,
    QueryPayoutRequest,
    QueryTeamWalletAddressBalanceRequest,
    QueryTeamWalletBalanceRequest,
    QueryTeamWalletHistoryTransactionsRequest,
    QueryTeamWalletProcessingTransactionsRequest,
    QueryWithdrawalRequest,
    TradeRecordQueryRequest,
    ValidateAddressRequest,
    WithdrawalRequest,
    canonicalize_json,
    sign_project_parameters,
    sign_team_request,
)
from cregis.generated.payment.operations import OPERATIONS as PAYMENT_OPERATIONS
from cregis.generated.team.operations import OPERATIONS as TEAM_OPERATIONS
from cregis.generated.waas.operations import OPERATIONS as WAAS_OPERATIONS

PID = 1382528827416576
API_KEY = "test-api-key"
ACCESS_KEY = "test-access-key"
ACCESS_SECRET = "test-access-secret"

ALL_OPERATIONS = {
    operation.path: operation
    for operation in [
        *PAYMENT_OPERATIONS.values(),
        *WAAS_OPERATIONS.values(),
        *TEAM_OPERATIONS.values(),
    ]
}


def _response_data(path: str) -> Any:
    operation = ALL_OPERATIONS[path]
    if operation.response_model is None:
        return None
    if operation.response_container == "array":
        return []
    if path.startswith("/openapi/"):
        return {"pageNum": 1, "pageSize": 10, "rows": [], "total": 0}
    return {}


def _successful_transport(requests: List[httpx.Request]) -> httpx.MockTransport:
    def handler(request: httpx.Request) -> httpx.Response:
        requests.append(request)
        return httpx.Response(
            200,
            json={"code": "00000", "msg": "ok", "data": _response_data(request.url.path)},
        )

    return httpx.MockTransport(handler)


def _project_clients(
    transport: httpx.BaseTransport,
) -> tuple[CregisPaymentClient, CregisWaasClient, httpx.Client]:
    http_client = httpx.Client(transport=transport)
    common = {
        "base_url": "https://sandbox.example",
        "pid": PID,
        "api_key": API_KEY,
        "client": http_client,
    }
    return CregisPaymentClient(**common), CregisWaasClient(**common), http_client


def test_all_23_client_operations_validate_paths_and_signatures() -> None:
    requests: List[httpx.Request] = []
    payment, waas, http_client = _project_clients(_successful_transport(requests))
    team = CregisTeamClient(
        base_url="https://sandbox.example",
        access_key=ACCESS_KEY,
        access_secret=ACCESS_SECRET,
        client=http_client,
    )

    payment.create_order(
        CreateOrderRequest(
            order_id="order-1",
            order_amount="1",
            order_currency="USD",
            payer_id="payer-1",
            success_url="https://merchant.example/success",
            cancel_url="https://merchant.example/cancel",
        )
    )
    payment.query_order(QueryOrderRequest(cregis_id="po-1"))
    waas.generate_address(GenerateAddressRequest(chain_id="195"))
    waas.batch_generate_address(BatchGenerateAddressRequest(chain_id="195", number="2"))
    waas.update_address(AddressUpdateRequest(address="T-test", alias="updated"))
    waas.validate_address(ValidateAddressRequest(chain_id="195", address="T-test"))
    waas.check_address_legality(CheckAddressLegalityRequest(chain_id="195", address="T-test"))
    waas.payout_v1(
        PayoutV1Request(
            currency="195@195", address="T-to", amount="1", third_party_id="payout-v1-1"
        )
    )
    waas.payout_v2(
        PayoutRequest(
            currency="195@195",
            to_address="T-to",
            amount="1",
            third_party_id="payout-v2-1",
        )
    )
    waas.withdrawal(
        WithdrawalRequest(
            currency="195@195",
            from_address="T-from",
            to_address="T-to",
            amount="1",
            third_party_id="withdrawal-1",
        )
    )
    waas.balance_collect(
        BalanceCollectRequest(currency="195@195", from_address="T-from", to_address="T-to")
    )
    waas.query_project_coins()
    waas.query_trade_records(TradeRecordQueryRequest())
    waas.query_payout(QueryPayoutRequest(cid=1))
    waas.query_withdrawal(QueryWithdrawalRequest(cid=1))
    waas.query_address_balance(AddressBalanceRequest(currency="195@195"))
    waas.query_address_balance_v2(AddressBalanceV2Request(address="T-test"))

    team.list_team_wallets(
        ListTeamWalletsRequest(page_size=10, page_num=1, wallet_type="single_sign")
    )
    team.list_team_wallet_addresses(ListTeamWalletAddressesRequest(wallet_id=1, chain_id="195"))
    team.query_team_wallet_balance(QueryTeamWalletBalanceRequest(wallet_id=1))
    team.query_team_wallet_address_balance(QueryTeamWalletAddressBalanceRequest(wallet_id=1))
    team.query_team_wallet_history_transactions(
        QueryTeamWalletHistoryTransactionsRequest(wallet_id=1)
    )
    team.query_team_wallet_processing_transactions(
        QueryTeamWalletProcessingTransactionsRequest(wallet_id=1)
    )

    assert [request.url.path for request in requests] == [
        operation.path
        for operation in [
            *PAYMENT_OPERATIONS.values(),
            *WAAS_OPERATIONS.values(),
            *TEAM_OPERATIONS.values(),
        ]
    ]
    assert len(requests) == 23

    for request in requests[:17]:
        body: Dict[str, Any] = json.loads(request.content)
        assert body["pid"] == PID
        assert len(body["nonce"]) == 6
        assert isinstance(body["timestamp"], int)
        signature = body.pop("sign")
        assert signature == sign_project_parameters(body, API_KEY)

    for request in requests[17:]:
        body = request.content.decode("utf-8")
        assert body == canonicalize_json(json.loads(body))
        timestamp = int(request.headers["Access-Timestamp"])
        nonce = request.headers["Access-Nonce"]
        assert request.headers["Access-Key"] == ACCESS_KEY
        assert request.headers["Access-Signature"] == sign_team_request(
            request.url.path, timestamp, nonce, body, ACCESS_SECRET
        )
    http_client.close()


def test_strict_request_and_response_types_are_rejected() -> None:
    with pytest.raises(ValidationError):
        CreateOrderRequest(
            order_id="order-1",
            order_amount=1,
            order_currency="USD",
            payer_id="payer-1",
            success_url="https://merchant.example/success",
            cancel_url="https://merchant.example/cancel",
        )
    with pytest.raises(ValidationError):
        AddressUpdateRequest(address="T-test")

    def handler(request: httpx.Request) -> httpx.Response:
        return httpx.Response(
            200,
            json={"code": "00000", "msg": "ok", "data": {"cregis_id": 123}},
        )

    payment, _, http_client = _project_clients(httpx.MockTransport(handler))
    with pytest.raises(CregisContractError):
        payment.query_order(QueryOrderRequest(cregis_id="po-1"))
    http_client.close()


def test_int64_boundaries_and_directional_extra_field_policy() -> None:
    for value in (-(2**63), 2**63 - 1):
        assert QueryPayoutRequest(cid=value).cid == value
        assert ListTeamWalletAddressesRequest(wallet_id=value, chain_id="195").wallet_id == value

    for value in (-(2**63) - 1, 2**63):
        with pytest.raises(ValidationError):
            QueryPayoutRequest(cid=value)
        with pytest.raises(ValidationError):
            ListTeamWalletAddressesRequest(wallet_id=value, chain_id="195")

    with pytest.raises(ValidationError):
        QueryPayoutRequest(cid=1, future_field="typo")

    response = QueryOrderResponse.model_validate(
        {"cregis_id": "po-1", "future_field": "ignored"},
        strict=True,
    )
    assert response.cregis_id == "po-1"
    assert not hasattr(response, "future_field")
    from_dict = QueryOrderResponse.from_dict(
        {"cregis_id": "po-1", "future_field": "ignored"}
    )
    assert from_dict is not None and from_dict.cregis_id == "po-1"


def test_http_api_and_envelope_errors_keep_their_category() -> None:
    responses = iter(
        [
            httpx.Response(429, text="rate limited"),
            httpx.Response(200, json={"code": "B0001", "msg": "Signature Error", "data": None}),
            httpx.Response(200, json={"code": "00000", "msg": "ok"}),
            httpx.Response(200, text="not-json"),
        ]
    )

    def handler(request: httpx.Request) -> httpx.Response:
        return next(responses)

    _, waas, http_client = _project_clients(httpx.MockTransport(handler))
    with pytest.raises(CregisHttpError) as http_error:
        waas.query_project_coins()
    assert http_error.value.status_code == 429
    assert http_error.value.response_body == "rate limited"

    with pytest.raises(CregisApiError) as api_error:
        waas.query_project_coins()
    assert api_error.value.code == "B0001"

    with pytest.raises(CregisContractError, match="response envelope"):
        waas.query_project_coins()

    with pytest.raises(Exception, match="Failed to parse Cregis response"):
        waas.query_project_coins()
    http_client.close()


def test_injected_client_is_not_closed_by_sdk() -> None:
    requests: List[httpx.Request] = []
    http_client = httpx.Client(transport=_successful_transport(requests))
    client = CregisWaasClient(
        base_url="https://sandbox.example",
        pid=PID,
        api_key=API_KEY,
        client=http_client,
    )
    client.close()
    assert not http_client.is_closed
    http_client.close()


def test_redirects_network_failures_and_unsafe_base_urls_are_rejected() -> None:
    calls = 0

    def redirect(request: httpx.Request) -> httpx.Response:
        nonlocal calls
        calls += 1
        return httpx.Response(302, headers={"Location": "https://other.example/redirect"})

    _, waas, http_client = _project_clients(httpx.MockTransport(redirect))
    with pytest.raises(CregisHttpError) as redirect_error:
        waas.query_project_coins()
    assert redirect_error.value.status_code == 302
    assert calls == 1
    http_client.close()

    def network_failure(request: httpx.Request) -> httpx.Response:
        raise httpx.ConnectError("connection failed", request=request)

    _, waas, http_client = _project_clients(httpx.MockTransport(network_failure))
    with pytest.raises(CregisClientError, match="Network error"):
        waas.query_project_coins()
    http_client.close()

    with pytest.raises(CregisClientError, match="Base URL must use HTTPS"):
        CregisWaasClient(base_url="http://api.example.com", pid=PID, api_key=API_KEY)
