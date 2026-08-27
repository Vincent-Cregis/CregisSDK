"""Synchronous WaaS client."""

from __future__ import annotations

import warnings
from typing import List, cast

from cregis.generated.waas.models import (
    AddressBalanceRequest,
    AddressBalanceResponse,
    AddressBalanceV2Request,
    AddressBalanceV2Response,
    AddressUpdateRequest,
    BalanceCollectRequest,
    BalanceCollectResponse,
    BatchGenerateAddressRequest,
    CheckAddressLegalityRequest,
    CheckAddressLegalityResponse,
    GenerateAddressRequest,
    GenerateAddressResponse,
    GeneratedAddress,
    PayoutRequest,
    PayoutResponse,
    PayoutV1Request,
    ProjectCoinQueryResponse,
    QueryPayoutRequest,
    QueryPayoutResponse,
    QueryWithdrawalRequest,
    QueryWithdrawalResponse,
    TradeRecordQueryRequest,
    TradeRecordQueryResponse,
    ValidateAddressRequest,
    ValidateAddressResponse,
    WithdrawalRequest,
    WithdrawalResponse,
)
from cregis.generated.waas.operations import OPERATIONS
from cregis.transport import CregisProjectClientBase


class CregisWaasClient(CregisProjectClientBase):
    def generate_address(self, request: GenerateAddressRequest) -> GenerateAddressResponse:
        return cast(
            GenerateAddressResponse,
            self._post(OPERATIONS["generate_address"], request),
        )

    def batch_generate_address(
        self, request: BatchGenerateAddressRequest
    ) -> List[GeneratedAddress]:
        return cast(
            List[GeneratedAddress],
            self._post(OPERATIONS["batch_generate_address"], request),
        )

    def update_address(self, request: AddressUpdateRequest) -> None:
        self._post(OPERATIONS["update_address"], request)

    def validate_address(self, request: ValidateAddressRequest) -> ValidateAddressResponse:
        return cast(
            ValidateAddressResponse,
            self._post(OPERATIONS["validate_address"], request),
        )

    def check_address_legality(
        self, request: CheckAddressLegalityRequest
    ) -> CheckAddressLegalityResponse:
        return cast(
            CheckAddressLegalityResponse,
            self._post(OPERATIONS["check_address_legality"], request),
        )

    def payout_v1(self, request: PayoutV1Request) -> PayoutResponse:
        return cast(PayoutResponse, self._post(OPERATIONS["payout_v1"], request))

    def payout_v2(self, request: PayoutRequest) -> PayoutResponse:
        return cast(PayoutResponse, self._post(OPERATIONS["payout_v2"], request))

    def payout(self, request: PayoutRequest) -> PayoutResponse:
        warnings.warn("payout() is deprecated; use payout_v2()", DeprecationWarning, stacklevel=2)
        return self.payout_v2(request)

    def withdrawal(self, request: WithdrawalRequest) -> WithdrawalResponse:
        return cast(WithdrawalResponse, self._post(OPERATIONS["withdrawal"], request))

    def balance_collect(self, request: BalanceCollectRequest) -> BalanceCollectResponse:
        return cast(
            BalanceCollectResponse,
            self._post(OPERATIONS["balance_collect"], request),
        )

    def query_project_coins(self) -> ProjectCoinQueryResponse:
        return cast(
            ProjectCoinQueryResponse,
            self._post(OPERATIONS["query_project_coins"]),
        )

    def query_trade_records(self, request: TradeRecordQueryRequest) -> TradeRecordQueryResponse:
        return cast(
            TradeRecordQueryResponse,
            self._post(OPERATIONS["query_trade_records"], request),
        )

    def query_payout(self, request: QueryPayoutRequest) -> QueryPayoutResponse:
        return cast(
            QueryPayoutResponse,
            self._post(OPERATIONS["query_payout"], request),
        )

    def query_withdrawal(self, request: QueryWithdrawalRequest) -> QueryWithdrawalResponse:
        return cast(
            QueryWithdrawalResponse,
            self._post(OPERATIONS["query_withdrawal"], request),
        )

    def query_address_balance(self, request: AddressBalanceRequest) -> AddressBalanceResponse:
        return cast(
            AddressBalanceResponse,
            self._post(OPERATIONS["query_address_balance"], request),
        )

    def query_address_balance_v2(
        self, request: AddressBalanceV2Request
    ) -> AddressBalanceV2Response:
        return cast(
            AddressBalanceV2Response,
            self._post(OPERATIONS["query_address_balance_v2"], request),
        )
