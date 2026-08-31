from __future__ import annotations

import os
import time
from typing import Dict, Iterable, Set

import httpx
import pytest

from cregis import (
    AddressBalanceRequest,
    AddressBalanceV2Request,
    AddressUpdateRequest,
    BalanceCollectRequest,
    BatchGenerateAddressRequest,
    CheckAddressLegalityRequest,
    CreateOrderRequest,
    CregisPaymentClient,
    CregisTeamClient,
    CregisWaasClient,
    GenerateAddressRequest,
    ListTeamWalletAddressesRequest,
    ListTeamWalletsRequest,
    PayoutRequest,
    PayoutV1Request,
    QueryOrderRequest,
    QueryPayoutRequest,
    QueryTeamWalletAddressBalanceRequest,
    QueryTeamWalletBalanceRequest,
    QueryTeamWalletHistoryTransactionsRequest,
    QueryTeamWalletProcessingTransactionsRequest,
    QueryWithdrawalRequest,
    TradeRecordQueryRequest,
    ValidateAddressRequest,
    WithdrawalRequest,
)
from cregis.generated.payment.operations import OPERATIONS as PAYMENT_OPERATIONS
from cregis.generated.team.operations import OPERATIONS as TEAM_OPERATIONS
from cregis.generated.waas.operations import OPERATIONS as WAAS_OPERATIONS


def _present(name: str) -> str:
    return os.environ[name].strip()


def _enabled(*names: str) -> bool:
    return os.environ.get("CREGIS_RUN_SANDBOX_TESTS") == "true" and all(
        os.environ.get(name, "").strip() for name in names
    )


def _readonly_enabled(*names: str) -> bool:
    return os.environ.get("CREGIS_SANDBOX_SUITE") != "all" and _enabled(*names)


def _all_enabled(*names: str) -> bool:
    return (
        os.environ.get("CREGIS_SANDBOX_SUITE") == "all"
        and os.environ.get("CREGIS_ALLOW_MUTATING_TESTS") == "true"
        and _enabled(*names)
    )


def _unique_id(prefix: str) -> str:
    return f"{prefix}-{time.time_ns() // 1_000_000}"


def _short_alias(prefix: str) -> str:
    return f"{prefix}-{str(time.time_ns() // 1_000_000)[-8:]}"


def _contract_client(operations: Iterable[object], covered: Set[str]) -> httpx.Client:
    def record(request: httpx.Request) -> None:
        covered.add(f"{request.method} {request.url.path}")

    return httpx.Client(event_hooks={"request": [record]})


def _assert_covered(operations: Dict[str, object], covered: Set[str]) -> None:
    expected = {
        f"{operation.method} {operation.path}"  # type: ignore[attr-defined]
        for operation in operations.values()
    }
    assert expected == covered, f"Sandbox did not call: {sorted(expected - covered)}"


@pytest.mark.skipif(
    not _readonly_enabled("WAAS_PID", "WAAS_API_KEY", "WAAS_ENDPOINT"),
    reason="read-only WaaS Sandbox credentials are not enabled",
)
def test_sandbox_waas_read_only_operations() -> None:
    with CregisWaasClient(
        base_url=_present("WAAS_ENDPOINT"),
        pid=_present("WAAS_PID"),
        api_key=_present("WAAS_API_KEY"),
    ) as client:
        coins = client.query_project_coins()
        assert coins.address_coins
        assert isinstance(coins.address_coins[0].decimals, str)
        trades = client.query_trade_records(TradeRecordQueryRequest(page_num=1, page_size=10))
        assert isinstance(trades.page_num, int)
        assert isinstance(trades.rows, list)


@pytest.mark.skipif(
    not _readonly_enabled(
        "PAYMENT_PID",
        "PAYMENT_API_KEY",
        "PAYMENT_ENDPOINT",
        "PAYMENT_CREGIS_ID",
    ),
    reason="read-only Payment Sandbox credentials are not enabled",
)
def test_sandbox_payment_read_only_query() -> None:
    cregis_id = _present("PAYMENT_CREGIS_ID")
    with CregisPaymentClient(
        base_url=_present("PAYMENT_ENDPOINT"),
        pid=_present("PAYMENT_PID"),
        api_key=_present("PAYMENT_API_KEY"),
    ) as client:
        order = client.query_order(QueryOrderRequest(cregis_id=cregis_id))
        assert order.cregis_id == cregis_id


@pytest.mark.skipif(
    not _all_enabled("PAYMENT_PID", "PAYMENT_API_KEY", "PAYMENT_ENDPOINT"),
    reason="full Payment Sandbox suite is not enabled",
)
def test_sandbox_all_payment_operations() -> None:
    covered: Set[str] = set()
    with _contract_client(PAYMENT_OPERATIONS.values(), covered) as http_client:
        client = CregisPaymentClient(
            base_url=_present("PAYMENT_ENDPOINT"),
            pid=_present("PAYMENT_PID"),
            api_key=_present("PAYMENT_API_KEY"),
            client=http_client,
        )
        created = client.create_order(
            CreateOrderRequest(
                order_id=_unique_id("python-sdk-order"),
                order_amount="1.0",
                order_currency="USDT",
                payer_id="python-sdk-sandbox",
                payer_name="Python SDK Sandbox",
                callback_url="https://webhook.site/test",
                success_url="https://example.com/success",
                cancel_url="https://example.com/cancel",
                remark="Python SDK Sandbox test",
                valid_time=60,
                language="sc",
                underpaid_tolerance=0.1,
                overpaid_tolerance=0.1,
            )
        )
        assert created.cregis_id
        assert created.checkout_url
        queried = client.query_order(QueryOrderRequest(cregis_id=created.cregis_id))
        assert queried.cregis_id == created.cregis_id
    _assert_covered(PAYMENT_OPERATIONS, covered)


@pytest.mark.skipif(
    not _all_enabled("WAAS_PID", "WAAS_API_KEY", "WAAS_ENDPOINT", "WITHDRAW_ADDRESS"),
    reason="full WaaS Sandbox suite is not enabled",
)
def test_sandbox_all_waas_operations() -> None:
    covered: Set[str] = set()
    with _contract_client(WAAS_OPERATIONS.values(), covered) as http_client:
        client = CregisWaasClient(
            base_url=_present("WAAS_ENDPOINT"),
            pid=_present("WAAS_PID"),
            api_key=_present("WAAS_API_KEY"),
            client=http_client,
        )
        source_address = _present("WITHDRAW_ADDRESS")
        amount = os.environ.get("WAAS_TEST_AMOUNT", "0.001")
        preferred_chain_id = os.environ.get("WAAS_CHAIN_ID", "198")

        coins = client.query_project_coins()
        address_coins = coins.address_coins or []
        payout_coins = coins.payout_coins or []
        address_chain_ids = {coin.chain_id for coin in address_coins}
        selected = next(
            (
                coin
                for coin in payout_coins
                if coin.chain_id == preferred_chain_id and coin.chain_id in address_chain_ids
            ),
            None,
        ) or next(
            (coin for coin in payout_coins if coin.chain_id in address_chain_ids),
            None,
        )
        assert selected is not None and selected.chain_id and selected.token_id
        chain_id = selected.chain_id
        currency = f"{chain_id}@{selected.token_id}"

        generated = client.generate_address(
            GenerateAddressRequest(chain_id=chain_id, alias=_short_alias("py-sdk"))
        )
        assert generated.address
        generated_address = generated.address
        batch = client.batch_generate_address(
            BatchGenerateAddressRequest(
                chain_id=chain_id,
                number="2",
                alias=_short_alias("py-batch"),
            )
        )
        assert batch and batch[0].address
        client.update_address(
            AddressUpdateRequest(address=generated_address, alias=_short_alias("py-updated"))
        )
        assert (
            client.validate_address(
                ValidateAddressRequest(chain_id=chain_id, address=generated_address)
            ).result
            is True
        )
        assert (
            client.check_address_legality(
                CheckAddressLegalityRequest(chain_id=chain_id, address=generated_address)
            ).result
            is True
        )
        assert isinstance(
            client.query_address_balance(
                AddressBalanceRequest(
                    currency=currency,
                    address=source_address,
                    page_num=1,
                    page_size=10,
                )
            ).rows,
            list,
        )
        assert isinstance(
            client.query_address_balance_v2(
                AddressBalanceV2Request(
                    address=source_address,
                    currency=currency,
                    page_num=1,
                    page_size=10,
                )
            ).rows,
            list,
        )
        assert isinstance(
            client.query_trade_records(TradeRecordQueryRequest(page_num=1, page_size=10)).rows,
            list,
        )

        destination = os.environ.get("WAAS_PAYOUT_TO_ADDRESS") or generated_address
        payout_v1 = client.payout_v1(
            PayoutV1Request(
                currency=currency,
                address=destination,
                amount=amount,
                third_party_id=_unique_id("py-sdk-p1"),
                remark="Python SDK Sandbox test",
            )
        )
        assert payout_v1.cid is not None
        payout = client.query_payout(QueryPayoutRequest(cid=payout_v1.cid))

        wallet_id_value = os.environ.get("WAAS_WALLET_ID", "").strip()
        wallet_id = int(wallet_id_value) if wallet_id_value else None
        payout_v2_request = {
            "currency": currency,
            "to_address": destination,
            "amount": amount,
            "third_party_id": _unique_id("py-sdk-p2"),
            "remark": "Python SDK Sandbox test",
        }
        if wallet_id is not None:
            payout_v2_request["wallet_id"] = wallet_id
        assert client.payout_v2(PayoutRequest(**payout_v2_request)).cid is not None

        withdrawal = client.withdrawal(
            WithdrawalRequest(
                currency=currency,
                from_address=source_address,
                to_address=os.environ.get("WITHDRAW_TO_ADDRESS") or generated_address,
                amount=amount,
                third_party_id=_unique_id("py-sdk-wd"),
                remark="Python SDK Sandbox test",
            )
        )
        assert withdrawal.cid is not None
        client.query_withdrawal(QueryWithdrawalRequest(cid=withdrawal.cid))

        collection_destination = os.environ.get("WAAS_COLLECTION_TO_ADDRESS") or payout.from_address
        assert collection_destination
        assert (
            client.balance_collect(
                BalanceCollectRequest(
                    currency=currency,
                    from_address=source_address,
                    to_address=collection_destination,
                    amount=amount,
                )
            ).cid
            is not None
        )
    _assert_covered(WAAS_OPERATIONS, covered)


@pytest.mark.skipif(
    not _enabled("TEAM_ACCESS_KEY", "TEAM_ACCESS_SECRET", "TEAM_ENDPOINT"),
    reason="Team Sandbox credentials are not enabled",
)
def test_sandbox_all_team_operations() -> None:
    covered: Set[str] = set()
    with _contract_client(TEAM_OPERATIONS.values(), covered) as http_client:
        client = CregisTeamClient(
            base_url=_present("TEAM_ENDPOINT"),
            access_key=_present("TEAM_ACCESS_KEY"),
            access_secret=_present("TEAM_ACCESS_SECRET"),
            client=http_client,
        )
        wallets = client.list_team_wallets(ListTeamWalletsRequest(page_num=1, page_size=10))
        assert wallets.rows
        wallet = wallets.rows[0]
        assert wallet.wallet_id is not None and wallet.tokens
        token = wallet.tokens[0]
        assert token.chain_id and token.token_id

        addresses = client.list_team_wallet_addresses(
            ListTeamWalletAddressesRequest(
                wallet_id=wallet.wallet_id,
                chain_id=token.chain_id,
                page_num=1,
                page_size=10,
            )
        )
        assert isinstance(addresses.rows, list)
        assert isinstance(
            client.query_team_wallet_balance(
                QueryTeamWalletBalanceRequest(
                    wallet_id=wallet.wallet_id,
                    chain_id=token.chain_id,
                    token_id=token.token_id,
                    page_num=1,
                    page_size=10,
                )
            ).rows,
            list,
        )
        address = addresses.rows[0].address if addresses.rows else None
        address_balance_values = {
            "wallet_id": wallet.wallet_id,
            "chain_id": token.chain_id,
            "token_id": token.token_id,
            "page_num": 1,
            "page_size": 10,
        }
        if address is not None:
            address_balance_values["address"] = address
        assert isinstance(
            client.query_team_wallet_address_balance(
                QueryTeamWalletAddressBalanceRequest(**address_balance_values)
            ).rows,
            list,
        )
        assert isinstance(
            client.query_team_wallet_history_transactions(
                QueryTeamWalletHistoryTransactionsRequest(
                    wallet_id=wallet.wallet_id,
                    chain_id=token.chain_id,
                    token_id=token.token_id,
                    page_num=1,
                    page_size=10,
                )
            ).rows,
            list,
        )
        assert isinstance(
            client.query_team_wallet_processing_transactions(
                QueryTeamWalletProcessingTransactionsRequest(
                    wallet_id=wallet.wallet_id,
                    chain_id=token.chain_id,
                    token_id=token.token_id,
                    page_num=1,
                    page_size=10,
                )
            ).rows,
            list,
        )
    _assert_covered(TEAM_OPERATIONS, covered)
