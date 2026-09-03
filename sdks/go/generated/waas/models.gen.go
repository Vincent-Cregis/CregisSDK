// Code generated from the canonical Cregis OpenAPI specification. DO NOT EDIT.
package waas

// AddressBalance is generated from the canonical waas API contract.
type AddressBalance struct {
	Address    *string `json:"address,omitempty"`
	Available  *string `json:"available,omitempty"`
	Currency   *string `json:"currency,omitempty"`
	PID        *int64  `json:"pid,omitempty"`
	Processing *string `json:"processing,omitempty"`
	Total      *string `json:"total,omitempty"`
}

// AddressBalanceRequest is generated from the canonical waas API contract.
type AddressBalanceRequest struct {
	Address        *string `json:"address,omitempty"`
	Currency       string  `json:"currency"`
	MaximumBalance *string `json:"maximum_balance,omitempty"`
	MinimumBalance *string `json:"minimum_balance,omitempty"`
	PageNum        *int32  `json:"page_num,omitempty"`
	PageSize       *int32  `json:"page_size,omitempty"`
}

// AddressBalanceResponse is generated from the canonical waas API contract.
type AddressBalanceResponse struct {
	PageNum  *int32           `json:"pageNum,omitempty"`
	PageSize *int32           `json:"pageSize,omitempty"`
	Rows     []AddressBalance `json:"rows,omitempty"`
	Total    *int64           `json:"total,omitempty"`
}

// AddressBalanceV2 is generated from the canonical waas API contract.
type AddressBalanceV2 struct {
	Address    *string `json:"address,omitempty"`
	Available  *string `json:"available,omitempty"`
	ChainID    *string `json:"chain_id,omitempty"`
	Processing *string `json:"processing,omitempty"`
	TokenID    *string `json:"token_id,omitempty"`
	Total      *string `json:"total,omitempty"`
}

// AddressBalanceV2Request is generated from the canonical waas API contract.
type AddressBalanceV2Request struct {
	Address        string  `json:"address"`
	Currency       *string `json:"currency,omitempty"`
	MaximumBalance *string `json:"maximum_balance,omitempty"`
	MinimumBalance *string `json:"minimum_balance,omitempty"`
	PageNum        *int32  `json:"page_num,omitempty"`
	PageSize       *int32  `json:"page_size,omitempty"`
}

// AddressBalanceV2Response is generated from the canonical waas API contract.
type AddressBalanceV2Response struct {
	PageNum  *int32             `json:"pageNum,omitempty"`
	PageSize *int32             `json:"pageSize,omitempty"`
	Rows     []AddressBalanceV2 `json:"rows,omitempty"`
	Total    *int64             `json:"total,omitempty"`
}

// AddressDepositCallbackNotificationStatus is the documented value set for AddressDepositCallbackNotification.Status.
type AddressDepositCallbackNotificationStatus string

const (
	AddressDepositCallbackNotificationStatus1 AddressDepositCallbackNotificationStatus = "1"
	AddressDepositCallbackNotificationStatus2 AddressDepositCallbackNotificationStatus = "2"
)

// AddressDepositCallbackNotification is generated from the canonical waas API contract.
type AddressDepositCallbackNotification struct {
	Address     string                                   `json:"address"`
	Amount      string                                   `json:"amount"`
	BlockHeight *string                                  `json:"block_height,omitempty"`
	BlockTime   *string                                  `json:"block_time,omitempty"`
	ChainID     string                                   `json:"chain_id"`
	CID         int64                                    `json:"cid"`
	Currency    string                                   `json:"currency"`
	Memo        *string                                  `json:"memo,omitempty"`
	Nonce       string                                   `json:"nonce"`
	PID         int64                                    `json:"pid"`
	Sign        string                                   `json:"sign"`
	Status      AddressDepositCallbackNotificationStatus `json:"status"`
	Timestamp   int64                                    `json:"timestamp"`
	TokenID     string                                   `json:"token_id"`
	TxID        string                                   `json:"txid"`
}

// AddressUpdateRequestStatus is the documented value set for AddressUpdateRequest.Status.
type AddressUpdateRequestStatus string

const (
	AddressUpdateRequestStatus0 AddressUpdateRequestStatus = "0"
	AddressUpdateRequestStatus1 AddressUpdateRequestStatus = "1"
)

// AddressUpdateRequest is generated from the canonical waas API contract.
type AddressUpdateRequest struct {
	Address     string                      `json:"address"`
	Alias       *string                     `json:"alias,omitempty"`
	CallbackURL *string                     `json:"callback_url,omitempty"`
	Status      *AddressUpdateRequestStatus `json:"status,omitempty"`
}

// BalanceCollectRequest is generated from the canonical waas API contract.
type BalanceCollectRequest struct {
	Amount      *string `json:"amount,omitempty"`
	Currency    string  `json:"currency"`
	FromAddress string  `json:"from_address"`
	ToAddress   string  `json:"to_address"`
}

// BalanceCollectResponse is generated from the canonical waas API contract.
type BalanceCollectResponse struct {
	CID *int64 `json:"cid,omitempty"`
}

// BatchGenerateAddressRequest is generated from the canonical waas API contract.
type BatchGenerateAddressRequest struct {
	Alias       *string `json:"alias,omitempty"`
	CallbackURL *string `json:"callback_url,omitempty"`
	ChainID     string  `json:"chain_id"`
	Number      string  `json:"number"`
}

// CheckAddressLegalityRequest is generated from the canonical waas API contract.
type CheckAddressLegalityRequest struct {
	Address string `json:"address"`
	ChainID string `json:"chain_id"`
}

// CheckAddressLegalityResponse is generated from the canonical waas API contract.
type CheckAddressLegalityResponse struct {
	Result *bool `json:"result,omitempty"`
}

// GenerateAddressRequest is generated from the canonical waas API contract.
type GenerateAddressRequest struct {
	Alias       *string `json:"alias,omitempty"`
	CallbackURL *string `json:"callback_url,omitempty"`
	ChainID     string  `json:"chain_id"`
}

// GenerateAddressResponse is generated from the canonical waas API contract.
type GenerateAddressResponse struct {
	Address *string `json:"address,omitempty"`
}

// GeneratedAddress is generated from the canonical waas API contract.
type GeneratedAddress struct {
	Address *string `json:"address,omitempty"`
}

// PayoutCallbackNotificationStatus is the documented value set for PayoutCallbackNotification.Status.
type PayoutCallbackNotificationStatus int32

const (
	PayoutCallbackNotificationStatus2 PayoutCallbackNotificationStatus = 2
	PayoutCallbackNotificationStatus4 PayoutCallbackNotificationStatus = 4
	PayoutCallbackNotificationStatus6 PayoutCallbackNotificationStatus = 6
	PayoutCallbackNotificationStatus7 PayoutCallbackNotificationStatus = 7
)

// PayoutCallbackNotification is generated from the canonical waas API contract.
type PayoutCallbackNotification struct {
	Address      string                           `json:"address"`
	Amount       string                           `json:"amount"`
	BlockHeight  *string                          `json:"block_height,omitempty"`
	BlockTime    *int64                           `json:"block_time,omitempty"`
	ChainID      string                           `json:"chain_id"`
	CID          int64                            `json:"cid"`
	Currency     string                           `json:"currency"`
	Memo         *string                          `json:"memo,omitempty"`
	Nonce        string                           `json:"nonce"`
	PID          int64                            `json:"pid"`
	Remark       *string                          `json:"remark,omitempty"`
	Sign         string                           `json:"sign"`
	Status       PayoutCallbackNotificationStatus `json:"status"`
	ThirdPartyID string                           `json:"third_party_id"`
	Timestamp    int64                            `json:"timestamp"`
	TokenID      string                           `json:"token_id"`
	TxID         *string                          `json:"txid,omitempty"`
}

// PayoutExternalVerificationCallbackNotification is generated from the canonical waas API contract.
type PayoutExternalVerificationCallbackNotification struct {
	Amount       string  `json:"amount"`
	ChainID      string  `json:"chain_id"`
	CID          int64   `json:"cid"`
	FromAddress  string  `json:"from_address"`
	Memo         *string `json:"memo,omitempty"`
	Nonce        string  `json:"nonce"`
	PID          int64   `json:"pid"`
	Remark       *string `json:"remark,omitempty"`
	Sign         string  `json:"sign"`
	ThirdPartyID string  `json:"third_party_id"`
	Timestamp    int64   `json:"timestamp"`
	ToAddress    string  `json:"to_address"`
	TokenID      string  `json:"token_id"`
}

// PayoutRequest is generated from the canonical waas API contract.
type PayoutRequest struct {
	Amount       string  `json:"amount"`
	CallbackURL  *string `json:"callback_url,omitempty"`
	Currency     string  `json:"currency"`
	FromAddress  *string `json:"from_address,omitempty"`
	Memo         *string `json:"memo,omitempty"`
	Remark       *string `json:"remark,omitempty"`
	ThirdPartyID string  `json:"third_party_id"`
	ToAddress    string  `json:"to_address"`
	WalletID     *int64  `json:"wallet_id,omitempty"`
}

// PayoutResponse is generated from the canonical waas API contract.
type PayoutResponse struct {
	CID *int64 `json:"cid,omitempty"`
}

// PayoutV1Request is generated from the canonical waas API contract.
type PayoutV1Request struct {
	Address      string  `json:"address"`
	Amount       string  `json:"amount"`
	CallbackURL  *string `json:"callback_url,omitempty"`
	Currency     string  `json:"currency"`
	Memo         *string `json:"memo,omitempty"`
	Remark       *string `json:"remark,omitempty"`
	ThirdPartyID string  `json:"third_party_id"`
}

// ProjectCoin is generated from the canonical waas API contract.
type ProjectCoin struct {
	ChainID  *string `json:"chain_id,omitempty"`
	CoinName *string `json:"coin_name,omitempty"`
	Decimals *string `json:"decimals,omitempty"`
	TokenID  *string `json:"token_id,omitempty"`
}

// ProjectCoinQueryResponse is generated from the canonical waas API contract.
type ProjectCoinQueryResponse struct {
	AddressCoins []ProjectCoin                             `json:"address_coins,omitempty"`
	OrderCoins   *[]ProjectCoinQueryResponseOrderCoinsItem `json:"order_coins,omitempty"`
	PayoutCoins  []ProjectCoin                             `json:"payout_coins,omitempty"`
}

// ProjectCoinQueryResponseOrderCoinsItem is generated from the canonical waas API contract.
type ProjectCoinQueryResponseOrderCoinsItem struct {
	ChainID  *string `json:"chain_id,omitempty"`
	CoinName *string `json:"coin_name,omitempty"`
	Decimals *string `json:"decimals,omitempty"`
	TokenID  *string `json:"token_id,omitempty"`
}

// QueryPayoutRequest is generated from the canonical waas API contract.
type QueryPayoutRequest struct {
	CID int64 `json:"cid"`
}

// QueryPayoutResponseStatus is the documented value set for QueryPayoutResponse.Status.
type QueryPayoutResponseStatus int

const (
	QueryPayoutResponseStatus0 QueryPayoutResponseStatus = 0
	QueryPayoutResponseStatus1 QueryPayoutResponseStatus = 1
	QueryPayoutResponseStatus2 QueryPayoutResponseStatus = 2
	QueryPayoutResponseStatus3 QueryPayoutResponseStatus = 3
	QueryPayoutResponseStatus4 QueryPayoutResponseStatus = 4
	QueryPayoutResponseStatus5 QueryPayoutResponseStatus = 5
	QueryPayoutResponseStatus6 QueryPayoutResponseStatus = 6
	QueryPayoutResponseStatus7 QueryPayoutResponseStatus = 7
)

// QueryPayoutResponse is generated from the canonical waas API contract.
type QueryPayoutResponse struct {
	Address      *string                    `json:"address,omitempty"`
	Amount       *string                    `json:"amount,omitempty"`
	BlockHeight  *string                    `json:"block_height,omitempty"`
	BlockTime    *int64                     `json:"block_time,omitempty"`
	ChainID      *string                    `json:"chain_id,omitempty"`
	Currency     *string                    `json:"currency,omitempty"`
	FromAddress  *string                    `json:"from_address,omitempty"`
	Memo         *string                    `json:"memo,omitempty"`
	PID          *int64                     `json:"pid,omitempty"`
	Remark       *string                    `json:"remark,omitempty"`
	Status       *QueryPayoutResponseStatus `json:"status,omitempty"`
	ThirdPartyID *string                    `json:"third_party_id,omitempty"`
	TokenID      *string                    `json:"token_id,omitempty"`
	TxID         *string                    `json:"txid,omitempty"`
}

// QueryWithdrawalRequest is generated from the canonical waas API contract.
type QueryWithdrawalRequest struct {
	CID int64 `json:"cid"`
}

// QueryWithdrawalResponseStatus is the documented value set for QueryWithdrawalResponse.Status.
type QueryWithdrawalResponseStatus int

const (
	QueryWithdrawalResponseStatus0 QueryWithdrawalResponseStatus = 0
	QueryWithdrawalResponseStatus1 QueryWithdrawalResponseStatus = 1
	QueryWithdrawalResponseStatus2 QueryWithdrawalResponseStatus = 2
	QueryWithdrawalResponseStatus3 QueryWithdrawalResponseStatus = 3
	QueryWithdrawalResponseStatus4 QueryWithdrawalResponseStatus = 4
	QueryWithdrawalResponseStatus5 QueryWithdrawalResponseStatus = 5
	QueryWithdrawalResponseStatus6 QueryWithdrawalResponseStatus = 6
	QueryWithdrawalResponseStatus7 QueryWithdrawalResponseStatus = 7
)

// QueryWithdrawalResponse is generated from the canonical waas API contract.
type QueryWithdrawalResponse struct {
	Amount       *string                        `json:"amount,omitempty"`
	BlockHeight  *string                        `json:"block_height,omitempty"`
	BlockTime    *string                        `json:"block_time,omitempty"`
	ChainID      *string                        `json:"chain_id,omitempty"`
	Currency     *string                        `json:"currency,omitempty"`
	FromAddress  *string                        `json:"from_address,omitempty"`
	Memo         *string                        `json:"memo,omitempty"`
	PID          *int64                         `json:"pid,omitempty"`
	Remark       *string                        `json:"remark,omitempty"`
	Status       *QueryWithdrawalResponseStatus `json:"status,omitempty"`
	ThirdPartyID *string                        `json:"third_party_id,omitempty"`
	ToAddress    *string                        `json:"to_address,omitempty"`
	TokenID      *string                        `json:"token_id,omitempty"`
	TxID         *string                        `json:"txid,omitempty"`
}

// TradeRecordStatus is the documented value set for TradeRecord.Status.
type TradeRecordStatus int32

const (
	TradeRecordStatus0 TradeRecordStatus = 0
	TradeRecordStatus1 TradeRecordStatus = 1
	TradeRecordStatus2 TradeRecordStatus = 2
)

// TradeRecord is generated from the canonical waas API contract.
type TradeRecord struct {
	Amount       *string            `json:"amount,omitempty"`
	BlockHeight  *string            `json:"block_height,omitempty"`
	BlockTime    *int64             `json:"block_time,omitempty"`
	BusinessType *string            `json:"business_type,omitempty"`
	ChainID      *string            `json:"chain_id,omitempty"`
	CID          *int64             `json:"cid,omitempty"`
	Currency     *string            `json:"currency,omitempty"`
	Fee          *string            `json:"fee,omitempty"`
	FromAddress  *string            `json:"from_address,omitempty"`
	Memo         *string            `json:"memo,omitempty"`
	PID          *int64             `json:"pid,omitempty"`
	Remark       *string            `json:"remark,omitempty"`
	Status       *TradeRecordStatus `json:"status,omitempty"`
	ToAddress    *string            `json:"to_address,omitempty"`
	TokenID      *string            `json:"token_id,omitempty"`
	TradeType    *string            `json:"trade_type,omitempty"`
	TxID         *string            `json:"txid,omitempty"`
}

// TradeRecordQueryRequestBusinessType is the documented value set for TradeRecordQueryRequest.BusinessType.
type TradeRecordQueryRequestBusinessType int32

const (
	TradeRecordQueryRequestBusinessType0 TradeRecordQueryRequestBusinessType = 0
	TradeRecordQueryRequestBusinessType2 TradeRecordQueryRequestBusinessType = 2
	TradeRecordQueryRequestBusinessType3 TradeRecordQueryRequestBusinessType = 3
	TradeRecordQueryRequestBusinessType4 TradeRecordQueryRequestBusinessType = 4
	TradeRecordQueryRequestBusinessType5 TradeRecordQueryRequestBusinessType = 5
)

// TradeRecordQueryRequestStatus is the documented value set for TradeRecordQueryRequest.Status.
type TradeRecordQueryRequestStatus int32

const (
	TradeRecordQueryRequestStatus0 TradeRecordQueryRequestStatus = 0
	TradeRecordQueryRequestStatus1 TradeRecordQueryRequestStatus = 1
	TradeRecordQueryRequestStatus2 TradeRecordQueryRequestStatus = 2
)

// TradeRecordQueryRequestTradeType is the documented value set for TradeRecordQueryRequest.TradeType.
type TradeRecordQueryRequestTradeType int32

const (
	TradeRecordQueryRequestTradeType1 TradeRecordQueryRequestTradeType = 1
	TradeRecordQueryRequestTradeType2 TradeRecordQueryRequestTradeType = 2
)

// TradeRecordQueryRequest is generated from the canonical waas API contract.
type TradeRecordQueryRequest struct {
	BlocktimeEnd   *int64                               `json:"blocktime_end,omitempty"`
	BlocktimeStart *int64                               `json:"blocktime_start,omitempty"`
	BusinessType   *TradeRecordQueryRequestBusinessType `json:"business_type,omitempty"`
	ChainID        *string                              `json:"chain_id,omitempty"`
	CID            *int64                               `json:"cid,omitempty"`
	PageNum        *int32                               `json:"page_num,omitempty"`
	PageSize       *int32                               `json:"page_size,omitempty"`
	Status         *TradeRecordQueryRequestStatus       `json:"status,omitempty"`
	TokenID        *string                              `json:"token_id,omitempty"`
	TradeType      *TradeRecordQueryRequestTradeType    `json:"trade_type,omitempty"`
	TxID           *string                              `json:"tx_id,omitempty"`
}

// TradeRecordQueryResponse is generated from the canonical waas API contract.
type TradeRecordQueryResponse struct {
	PageNum  *int32        `json:"pageNum,omitempty"`
	PageSize *int32        `json:"pageSize,omitempty"`
	Rows     []TradeRecord `json:"rows,omitempty"`
	Total    *int64        `json:"total,omitempty"`
}

// ValidateAddressRequest is generated from the canonical waas API contract.
type ValidateAddressRequest struct {
	Address string `json:"address"`
	ChainID string `json:"chain_id"`
}

// ValidateAddressResponse is generated from the canonical waas API contract.
type ValidateAddressResponse struct {
	Result *bool `json:"result,omitempty"`
}

// WithdrawalCallbackNotificationStatus is the documented value set for WithdrawalCallbackNotification.Status.
type WithdrawalCallbackNotificationStatus int32

const (
	WithdrawalCallbackNotificationStatus2 WithdrawalCallbackNotificationStatus = 2
	WithdrawalCallbackNotificationStatus4 WithdrawalCallbackNotificationStatus = 4
	WithdrawalCallbackNotificationStatus6 WithdrawalCallbackNotificationStatus = 6
	WithdrawalCallbackNotificationStatus7 WithdrawalCallbackNotificationStatus = 7
)

// WithdrawalCallbackNotification is generated from the canonical waas API contract.
type WithdrawalCallbackNotification struct {
	Amount       string                               `json:"amount"`
	BlockHeight  *string                              `json:"block_height,omitempty"`
	BlockTime    *int64                               `json:"block_time,omitempty"`
	ChainID      string                               `json:"chain_id"`
	CID          int64                                `json:"cid"`
	Currency     string                               `json:"currency"`
	FromAddress  string                               `json:"from_address"`
	Memo         *string                              `json:"memo,omitempty"`
	Nonce        string                               `json:"nonce"`
	PID          int64                                `json:"pid"`
	Remark       *string                              `json:"remark,omitempty"`
	Sign         string                               `json:"sign"`
	Status       WithdrawalCallbackNotificationStatus `json:"status"`
	ThirdPartyID string                               `json:"third_party_id"`
	Timestamp    int64                                `json:"timestamp"`
	ToAddress    string                               `json:"to_address"`
	TokenID      string                               `json:"token_id"`
	TxID         *string                              `json:"txid,omitempty"`
}

// WithdrawalRequest is generated from the canonical waas API contract.
type WithdrawalRequest struct {
	Amount       string  `json:"amount"`
	CallbackURL  *string `json:"callback_url,omitempty"`
	Currency     string  `json:"currency"`
	FromAddress  string  `json:"from_address"`
	Memo         *string `json:"memo,omitempty"`
	Remark       *string `json:"remark,omitempty"`
	ThirdPartyID string  `json:"third_party_id"`
	ToAddress    string  `json:"to_address"`
}

// WithdrawalResponse is generated from the canonical waas API contract.
type WithdrawalResponse struct {
	CID *int64 `json:"cid,omitempty"`
}
