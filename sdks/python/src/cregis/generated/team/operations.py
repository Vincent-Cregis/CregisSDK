# Generated from the canonical Cregis OpenAPI specification. Do not edit.
from cregis.operation import GeneratedOperation
from .models import *  # noqa: F403

OPERATIONS = {
    "list_team_wallets": GeneratedOperation(
        operation_id="listTeamWallets",
        method="POST",
        path="/openapi/v1/wallets",
        request_model=ListTeamWalletsRequest,
        response_model=ListTeamWalletsResponse,
        response_container=None,
    ),
    "list_team_wallet_addresses": GeneratedOperation(
        operation_id="listTeamWalletAddresses",
        method="POST",
        path="/openapi/v1/wallet_address",
        request_model=ListTeamWalletAddressesRequest,
        response_model=ListTeamWalletAddressesResponse,
        response_container=None,
    ),
    "query_team_wallet_balance": GeneratedOperation(
        operation_id="queryTeamWalletBalance",
        method="POST",
        path="/openapi/v1/wallet_balance",
        request_model=QueryTeamWalletBalanceRequest,
        response_model=QueryTeamWalletBalanceResponse,
        response_container=None,
    ),
    "query_team_wallet_address_balance": GeneratedOperation(
        operation_id="queryTeamWalletAddressBalance",
        method="POST",
        path="/openapi/v1/wallet_address_balance",
        request_model=QueryTeamWalletAddressBalanceRequest,
        response_model=QueryTeamWalletAddressBalanceResponse,
        response_container=None,
    ),
    "query_team_wallet_history_transactions": GeneratedOperation(
        operation_id="queryTeamWalletHistoryTransactions",
        method="POST",
        path="/openapi/v1/wallet_history_transaction_info",
        request_model=QueryTeamWalletHistoryTransactionsRequest,
        response_model=QueryTeamWalletHistoryTransactionsResponse,
        response_container=None,
    ),
    "query_team_wallet_processing_transactions": GeneratedOperation(
        operation_id="queryTeamWalletProcessingTransactions",
        method="POST",
        path="/openapi/v1/wallet_processing_transaction_info",
        request_model=QueryTeamWalletProcessingTransactionsRequest,
        response_model=QueryTeamWalletProcessingTransactionsResponse,
        response_container=None,
    ),
}
