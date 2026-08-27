"""Synchronous Team API client."""

from typing import cast

from cregis.generated.team.models import (
    ListTeamWalletAddressesRequest,
    ListTeamWalletAddressesResponse,
    ListTeamWalletsRequest,
    ListTeamWalletsResponse,
    QueryTeamWalletAddressBalanceRequest,
    QueryTeamWalletAddressBalanceResponse,
    QueryTeamWalletBalanceRequest,
    QueryTeamWalletBalanceResponse,
    QueryTeamWalletHistoryTransactionsRequest,
    QueryTeamWalletHistoryTransactionsResponse,
    QueryTeamWalletProcessingTransactionsRequest,
    QueryTeamWalletProcessingTransactionsResponse,
)
from cregis.generated.team.operations import OPERATIONS
from cregis.transport import CregisTeamClientBase


class CregisTeamClient(CregisTeamClientBase):
    def list_team_wallets(self, request: ListTeamWalletsRequest) -> ListTeamWalletsResponse:
        return cast(
            ListTeamWalletsResponse,
            self._post(OPERATIONS["list_team_wallets"], request),
        )

    def list_team_wallet_addresses(
        self, request: ListTeamWalletAddressesRequest
    ) -> ListTeamWalletAddressesResponse:
        return cast(
            ListTeamWalletAddressesResponse,
            self._post(OPERATIONS["list_team_wallet_addresses"], request),
        )

    def query_team_wallet_balance(
        self, request: QueryTeamWalletBalanceRequest
    ) -> QueryTeamWalletBalanceResponse:
        return cast(
            QueryTeamWalletBalanceResponse,
            self._post(OPERATIONS["query_team_wallet_balance"], request),
        )

    def query_team_wallet_address_balance(
        self, request: QueryTeamWalletAddressBalanceRequest
    ) -> QueryTeamWalletAddressBalanceResponse:
        return cast(
            QueryTeamWalletAddressBalanceResponse,
            self._post(OPERATIONS["query_team_wallet_address_balance"], request),
        )

    def query_team_wallet_history_transactions(
        self, request: QueryTeamWalletHistoryTransactionsRequest
    ) -> QueryTeamWalletHistoryTransactionsResponse:
        return cast(
            QueryTeamWalletHistoryTransactionsResponse,
            self._post(OPERATIONS["query_team_wallet_history_transactions"], request),
        )

    def query_team_wallet_processing_transactions(
        self, request: QueryTeamWalletProcessingTransactionsRequest
    ) -> QueryTeamWalletProcessingTransactionsResponse:
        return cast(
            QueryTeamWalletProcessingTransactionsResponse,
            self._post(OPERATIONS["query_team_wallet_processing_transactions"], request),
        )
