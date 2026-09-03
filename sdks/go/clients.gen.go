// Code generated from the canonical Cregis OpenAPI specification. DO NOT EDIT.
package cregis

import "context"

// CreateOrder calls POST /api/v2/checkout.
func (client *PaymentClient) CreateOrder(ctx context.Context, request *CreateOrderRequest) (*CreateOrderResponse, error) {
	return execute[CreateOrderResponse](ctx, client.base, operation{id: "createOrder", method: "POST", path: "/api/v2/checkout"}, request)
}

// QueryOrder calls POST /api/v2/order/info.
func (client *PaymentClient) QueryOrder(ctx context.Context, request *QueryOrderRequest) (*QueryOrderResponse, error) {
	return execute[QueryOrderResponse](ctx, client.base, operation{id: "queryOrder", method: "POST", path: "/api/v2/order/info"}, request)
}

// GenerateAddress calls POST /api/v1/address/create.
func (client *WaaSClient) GenerateAddress(ctx context.Context, request *GenerateAddressRequest) (*GenerateAddressResponse, error) {
	return execute[GenerateAddressResponse](ctx, client.base, operation{id: "createAddress", method: "POST", path: "/api/v1/address/create"}, request)
}

// BatchGenerateAddress calls POST /api/v1/batch/address/create.
func (client *WaaSClient) BatchGenerateAddress(ctx context.Context, request *BatchGenerateAddressRequest) ([]GeneratedAddress, error) {
	response, err := execute[[]GeneratedAddress](ctx, client.base, operation{id: "batchCreateAddress", method: "POST", path: "/api/v1/batch/address/create"}, request)
	if err != nil {
		return nil, err
	}
	return *response, nil
}

// UpdateAddress calls POST /api/v1/address/update.
func (client *WaaSClient) UpdateAddress(ctx context.Context, request *AddressUpdateRequest) error {
	return executeNoResponse(ctx, client.base, operation{id: "updateAddress", method: "POST", path: "/api/v1/address/update"}, request)
}

// ValidateAddress calls POST /api/v1/address/inner.
func (client *WaaSClient) ValidateAddress(ctx context.Context, request *ValidateAddressRequest) (*ValidateAddressResponse, error) {
	return execute[ValidateAddressResponse](ctx, client.base, operation{id: "verifyAddress", method: "POST", path: "/api/v1/address/inner"}, request)
}

// CheckAddressLegality calls POST /api/v1/address/legal.
func (client *WaaSClient) CheckAddressLegality(ctx context.Context, request *CheckAddressLegalityRequest) (*CheckAddressLegalityResponse, error) {
	return execute[CheckAddressLegalityResponse](ctx, client.base, operation{id: "validateAddressFormat", method: "POST", path: "/api/v1/address/legal"}, request)
}

// PayoutV1 calls POST /api/v1/payout.
func (client *WaaSClient) PayoutV1(ctx context.Context, request *PayoutV1Request) (*PayoutResponse, error) {
	return execute[PayoutResponse](ctx, client.base, operation{id: "initiatePayout", method: "POST", path: "/api/v1/payout"}, request)
}

// PayoutV2 calls POST /api/v2/payout.
func (client *WaaSClient) PayoutV2(ctx context.Context, request *PayoutRequest) (*PayoutResponse, error) {
	return execute[PayoutResponse](ctx, client.base, operation{id: "initiatePayoutV2", method: "POST", path: "/api/v2/payout"}, request)
}

// Withdrawal calls POST /api/v1/sub_address_withdrawal.
func (client *WaaSClient) Withdrawal(ctx context.Context, request *WithdrawalRequest) (*WithdrawalResponse, error) {
	return execute[WithdrawalResponse](ctx, client.base, operation{id: "subAddressWithdrawal", method: "POST", path: "/api/v1/sub_address_withdrawal"}, request)
}

// BalanceCollect calls POST /api/v1/collection.
func (client *WaaSClient) BalanceCollect(ctx context.Context, request *BalanceCollectRequest) (*BalanceCollectResponse, error) {
	return execute[BalanceCollectResponse](ctx, client.base, operation{id: "initiateCollection", method: "POST", path: "/api/v1/collection"}, request)
}

// QueryProjectCoins calls POST /api/v1/coins.
func (client *WaaSClient) QueryProjectCoins(ctx context.Context) (*ProjectCoinQueryResponse, error) {
	return execute[ProjectCoinQueryResponse](ctx, client.base, operation{id: "getProjectCoins", method: "POST", path: "/api/v1/coins"}, nil)
}

// QueryTradeRecords calls POST /api/v1/trade/page.
func (client *WaaSClient) QueryTradeRecords(ctx context.Context, request *TradeRecordQueryRequest) (*TradeRecordQueryResponse, error) {
	return execute[TradeRecordQueryResponse](ctx, client.base, operation{id: "tradePage", method: "POST", path: "/api/v1/trade/page"}, request)
}

// QueryPayout calls POST /api/v1/payout/query.
func (client *WaaSClient) QueryPayout(ctx context.Context, request *QueryPayoutRequest) (*QueryPayoutResponse, error) {
	return execute[QueryPayoutResponse](ctx, client.base, operation{id: "queryPayout", method: "POST", path: "/api/v1/payout/query"}, request)
}

// QueryWithdrawal calls POST /api/v1/sub_address_withdrawal/info.
func (client *WaaSClient) QueryWithdrawal(ctx context.Context, request *QueryWithdrawalRequest) (*QueryWithdrawalResponse, error) {
	return execute[QueryWithdrawalResponse](ctx, client.base, operation{id: "querySubAddressWithdrawal", method: "POST", path: "/api/v1/sub_address_withdrawal/info"}, request)
}

// QueryAddressBalance calls POST /api/v1/sub_address_balance.
func (client *WaaSClient) QueryAddressBalance(ctx context.Context, request *AddressBalanceRequest) (*AddressBalanceResponse, error) {
	return execute[AddressBalanceResponse](ctx, client.base, operation{id: "subAddressBalancePage", method: "POST", path: "/api/v1/sub_address_balance"}, request)
}

// QueryAddressBalanceV2 calls POST /api/v2/sub_address_balance.
func (client *WaaSClient) QueryAddressBalanceV2(ctx context.Context, request *AddressBalanceV2Request) (*AddressBalanceV2Response, error) {
	return execute[AddressBalanceV2Response](ctx, client.base, operation{id: "querySubAddressBalanceV2", method: "POST", path: "/api/v2/sub_address_balance"}, request)
}

// ListTeamWallets calls POST /openapi/v1/wallets.
func (client *TeamClient) ListTeamWallets(ctx context.Context, request *ListTeamWalletsRequest) (*ListTeamWalletsResponse, error) {
	return execute[ListTeamWalletsResponse](ctx, client.base, operation{id: "listTeamWallets", method: "POST", path: "/openapi/v1/wallets"}, request)
}

// ListTeamWalletAddresses calls POST /openapi/v1/wallet_address.
func (client *TeamClient) ListTeamWalletAddresses(ctx context.Context, request *ListTeamWalletAddressesRequest) (*ListTeamWalletAddressesResponse, error) {
	return execute[ListTeamWalletAddressesResponse](ctx, client.base, operation{id: "listTeamWalletAddresses", method: "POST", path: "/openapi/v1/wallet_address"}, request)
}

// QueryTeamWalletBalance calls POST /openapi/v1/wallet_balance.
func (client *TeamClient) QueryTeamWalletBalance(ctx context.Context, request *QueryTeamWalletBalanceRequest) (*QueryTeamWalletBalanceResponse, error) {
	return execute[QueryTeamWalletBalanceResponse](ctx, client.base, operation{id: "queryTeamWalletBalance", method: "POST", path: "/openapi/v1/wallet_balance"}, request)
}

// QueryTeamWalletAddressBalance calls POST /openapi/v1/wallet_address_balance.
func (client *TeamClient) QueryTeamWalletAddressBalance(ctx context.Context, request *QueryTeamWalletAddressBalanceRequest) (*QueryTeamWalletAddressBalanceResponse, error) {
	return execute[QueryTeamWalletAddressBalanceResponse](ctx, client.base, operation{id: "queryTeamWalletAddressBalance", method: "POST", path: "/openapi/v1/wallet_address_balance"}, request)
}

// QueryTeamWalletHistoryTransactions calls POST /openapi/v1/wallet_history_transaction_info.
func (client *TeamClient) QueryTeamWalletHistoryTransactions(ctx context.Context, request *QueryTeamWalletHistoryTransactionsRequest) (*QueryTeamWalletHistoryTransactionsResponse, error) {
	return execute[QueryTeamWalletHistoryTransactionsResponse](ctx, client.base, operation{id: "queryTeamWalletHistoryTransactions", method: "POST", path: "/openapi/v1/wallet_history_transaction_info"}, request)
}

// QueryTeamWalletProcessingTransactions calls POST /openapi/v1/wallet_processing_transaction_info.
func (client *TeamClient) QueryTeamWalletProcessingTransactions(ctx context.Context, request *QueryTeamWalletProcessingTransactionsRequest) (*QueryTeamWalletProcessingTransactionsResponse, error) {
	return execute[QueryTeamWalletProcessingTransactionsResponse](ctx, client.base, operation{id: "queryTeamWalletProcessingTransactions", method: "POST", path: "/openapi/v1/wallet_processing_transaction_info"}, request)
}
