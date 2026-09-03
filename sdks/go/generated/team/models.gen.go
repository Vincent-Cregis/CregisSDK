// Code generated from the canonical Cregis OpenAPI specification. DO NOT EDIT.
package team

// ListTeamWalletAddressesRequest is generated from the canonical team API contract.
type ListTeamWalletAddressesRequest struct {
	ChainID  string `json:"chain_id"`
	PageNum  *int32 `json:"page_num,omitempty"`
	PageSize *int32 `json:"page_size,omitempty"`
	WalletID int64  `json:"wallet_id"`
}

// ListTeamWalletAddressesResponse is generated from the canonical team API contract.
type ListTeamWalletAddressesResponse struct {
	PageNum  int32               `json:"pageNum"`
	PageSize int32               `json:"pageSize"`
	Rows     []TeamWalletAddress `json:"rows"`
	Total    int64               `json:"total"`
}

// ListTeamWalletsRequestWalletType is the documented value set for ListTeamWalletsRequest.WalletType.
type ListTeamWalletsRequestWalletType string

const (
	ListTeamWalletsRequestWalletTypeSingleSign ListTeamWalletsRequestWalletType = "single_sign"
	ListTeamWalletsRequestWalletTypeMultiSign  ListTeamWalletsRequestWalletType = "multi_sign"
)

// ListTeamWalletsRequest is generated from the canonical team API contract.
type ListTeamWalletsRequest struct {
	PageNum    *int32                            `json:"page_num,omitempty"`
	PageSize   *int32                            `json:"page_size,omitempty"`
	WalletType *ListTeamWalletsRequestWalletType `json:"wallet_type,omitempty"`
}

// ListTeamWalletsResponse is generated from the canonical team API contract.
type ListTeamWalletsResponse struct {
	PageNum  int32        `json:"pageNum"`
	PageSize int32        `json:"pageSize"`
	Rows     []TeamWallet `json:"rows"`
	Total    int64        `json:"total"`
}

// QueryTeamWalletAddressBalanceRequest is generated from the canonical team API contract.
type QueryTeamWalletAddressBalanceRequest struct {
	Address        *string `json:"address,omitempty"`
	ChainID        *string `json:"chain_id,omitempty"`
	MaximumBalance *string `json:"maximum_balance,omitempty"`
	MinimumBalance *string `json:"minimum_balance,omitempty"`
	PageNum        *int32  `json:"page_num,omitempty"`
	PageSize       *int32  `json:"page_size,omitempty"`
	TokenID        *string `json:"token_id,omitempty"`
	WalletID       int64   `json:"wallet_id"`
}

// QueryTeamWalletAddressBalanceResponse is generated from the canonical team API contract.
type QueryTeamWalletAddressBalanceResponse struct {
	PageNum  int32                      `json:"pageNum"`
	PageSize int32                      `json:"pageSize"`
	Rows     []TeamWalletAddressBalance `json:"rows"`
	Total    int64                      `json:"total"`
}

// QueryTeamWalletBalanceRequest is generated from the canonical team API contract.
type QueryTeamWalletBalanceRequest struct {
	ChainID  *string `json:"chain_id,omitempty"`
	PageNum  *int32  `json:"page_num,omitempty"`
	PageSize *int32  `json:"page_size,omitempty"`
	TokenID  *string `json:"token_id,omitempty"`
	WalletID int64   `json:"wallet_id"`
}

// QueryTeamWalletBalanceResponse is generated from the canonical team API contract.
type QueryTeamWalletBalanceResponse struct {
	PageNum  int32               `json:"pageNum"`
	PageSize int32               `json:"pageSize"`
	Rows     []TeamWalletBalance `json:"rows"`
	Total    int64               `json:"total"`
}

// QueryTeamWalletHistoryTransactionsRequestTransactionStatus is the documented value set for QueryTeamWalletHistoryTransactionsRequest.TransactionStatus.
type QueryTeamWalletHistoryTransactionsRequestTransactionStatus int32

const (
	QueryTeamWalletHistoryTransactionsRequestTransactionStatus1 QueryTeamWalletHistoryTransactionsRequestTransactionStatus = 1
	QueryTeamWalletHistoryTransactionsRequestTransactionStatus2 QueryTeamWalletHistoryTransactionsRequestTransactionStatus = 2
)

// QueryTeamWalletHistoryTransactionsRequestTransactionType is the documented value set for QueryTeamWalletHistoryTransactionsRequest.TransactionType.
type QueryTeamWalletHistoryTransactionsRequestTransactionType int32

const (
	QueryTeamWalletHistoryTransactionsRequestTransactionType1 QueryTeamWalletHistoryTransactionsRequestTransactionType = 1
	QueryTeamWalletHistoryTransactionsRequestTransactionType2 QueryTeamWalletHistoryTransactionsRequestTransactionType = 2
)

// QueryTeamWalletHistoryTransactionsRequest is generated from the canonical team API contract.
type QueryTeamWalletHistoryTransactionsRequest struct {
	BlocktimeEnd      *int64                                                      `json:"blocktime_end,omitempty"`
	BlocktimeStart    *int64                                                      `json:"blocktime_start,omitempty"`
	ChainID           *string                                                     `json:"chain_id,omitempty"`
	PageNum           *int32                                                      `json:"page_num,omitempty"`
	PageSize          *int32                                                      `json:"page_size,omitempty"`
	TokenID           *string                                                     `json:"token_id,omitempty"`
	TransactionStatus *QueryTeamWalletHistoryTransactionsRequestTransactionStatus `json:"transaction_status,omitempty"`
	TransactionType   *QueryTeamWalletHistoryTransactionsRequestTransactionType   `json:"transaction_type,omitempty"`
	TxID              *string                                                     `json:"txid,omitempty"`
	WalletID          int64                                                       `json:"wallet_id"`
}

// QueryTeamWalletHistoryTransactionsResponse is generated from the canonical team API contract.
type QueryTeamWalletHistoryTransactionsResponse struct {
	PageNum  int32                   `json:"pageNum"`
	PageSize int32                   `json:"pageSize"`
	Rows     []TeamWalletTransaction `json:"rows"`
	Total    int64                   `json:"total"`
}

// QueryTeamWalletProcessingTransactionsRequest is generated from the canonical team API contract.
type QueryTeamWalletProcessingTransactionsRequest struct {
	ChainID  *string `json:"chain_id,omitempty"`
	PageNum  *int32  `json:"page_num,omitempty"`
	PageSize *int32  `json:"page_size,omitempty"`
	TokenID  *string `json:"token_id,omitempty"`
	TxID     *string `json:"txid,omitempty"`
	WalletID int64   `json:"wallet_id"`
}

// QueryTeamWalletProcessingTransactionsResponse is generated from the canonical team API contract.
type QueryTeamWalletProcessingTransactionsResponse struct {
	PageNum  int32                             `json:"pageNum"`
	PageSize int32                             `json:"pageSize"`
	Rows     []TeamWalletProcessingTransaction `json:"rows"`
	Total    int64                             `json:"total"`
}

// TeamWalletWalletType is the documented value set for TeamWallet.WalletType.
type TeamWalletWalletType string

const (
	TeamWalletWalletTypeSingleSign TeamWalletWalletType = "single_sign"
	TeamWalletWalletTypeMultiSign  TeamWalletWalletType = "multi_sign"
)

// TeamWalletWalletStatus is the documented value set for TeamWallet.WalletStatus.
type TeamWalletWalletStatus string

const (
	TeamWalletWalletStatusNormal TeamWalletWalletStatus = "normal"
)

// TeamWallet is generated from the canonical team API contract.
type TeamWallet struct {
	Alias        *string                 `json:"alias,omitempty"`
	CreateTime   *int64                  `json:"create_time,omitempty"`
	Tokens       []TeamWalletToken       `json:"tokens,omitempty"`
	WalletType   TeamWalletWalletType    `json:"walletType"`
	WalletID     int64                   `json:"wallet_id"`
	WalletStatus *TeamWalletWalletStatus `json:"wallet_status,omitempty"`
}

// TeamWalletAddressAddressStatus is the documented value set for TeamWalletAddress.AddressStatus.
type TeamWalletAddressAddressStatus string

const (
	TeamWalletAddressAddressStatusEnable  TeamWalletAddressAddressStatus = "enable"
	TeamWalletAddressAddressStatusDisable TeamWalletAddressAddressStatus = "disable"
)

// TeamWalletAddress is generated from the canonical team API contract.
type TeamWalletAddress struct {
	Address       string                          `json:"address"`
	AddressStatus *TeamWalletAddressAddressStatus `json:"address_status,omitempty"`
	Alias         *string                         `json:"alias,omitempty"`
	CreateTime    *int64                          `json:"create_time,omitempty"`
}

// TeamWalletAddressBalance is generated from the canonical team API contract.
type TeamWalletAddressBalance struct {
	Address    string `json:"address"`
	Available  string `json:"available"`
	ChainID    string `json:"chain_id"`
	Processing string `json:"processing"`
	TokenID    string `json:"token_id"`
	Total      string `json:"total"`
}

// TeamWalletBalance is generated from the canonical team API contract.
type TeamWalletBalance struct {
	Available  string `json:"available"`
	ChainID    string `json:"chain_id"`
	Processing string `json:"processing"`
	TokenID    string `json:"token_id"`
	Total      string `json:"total"`
}

// TeamWalletProcessingTransactionStatus is the documented value set for TeamWalletProcessingTransaction.Status.
type TeamWalletProcessingTransactionStatus int32

const (
	TeamWalletProcessingTransactionStatus0 TeamWalletProcessingTransactionStatus = 0
)

// TeamWalletProcessingTransactionTransactionType is the documented value set for TeamWalletProcessingTransaction.TransactionType.
type TeamWalletProcessingTransactionTransactionType int32

const (
	TeamWalletProcessingTransactionTransactionType1 TeamWalletProcessingTransactionTransactionType = 1
)

// TeamWalletProcessingTransaction is generated from the canonical team API contract.
type TeamWalletProcessingTransaction struct {
	Amount          string                                         `json:"amount"`
	ChainID         string                                         `json:"chain_id"`
	FromAddress     *string                                        `json:"from_address,omitempty"`
	Status          TeamWalletProcessingTransactionStatus          `json:"status"`
	ToAddress       *string                                        `json:"to_address,omitempty"`
	TokenID         string                                         `json:"token_id"`
	TransactionType TeamWalletProcessingTransactionTransactionType `json:"transaction_type"`
	TxID            *string                                        `json:"txid,omitempty"`
	WalletID        int64                                          `json:"wallet_id"`
}

// TeamWalletToken is generated from the canonical team API contract.
type TeamWalletToken struct {
	ChainID   string  `json:"chain_id"`
	ChainName *string `json:"chain_name,omitempty"`
	TokenID   string  `json:"token_id"`
	TokenName *string `json:"token_name,omitempty"`
}

// TeamWalletTransactionStatus is the documented value set for TeamWalletTransaction.Status.
type TeamWalletTransactionStatus int32

const (
	TeamWalletTransactionStatus1 TeamWalletTransactionStatus = 1
	TeamWalletTransactionStatus2 TeamWalletTransactionStatus = 2
)

// TeamWalletTransactionTransactionType is the documented value set for TeamWalletTransaction.TransactionType.
type TeamWalletTransactionTransactionType int32

const (
	TeamWalletTransactionTransactionType1 TeamWalletTransactionTransactionType = 1
	TeamWalletTransactionTransactionType2 TeamWalletTransactionTransactionType = 2
)

// TeamWalletTransaction is generated from the canonical team API contract.
type TeamWalletTransaction struct {
	Amount          string                               `json:"amount"`
	BlockHeight     *string                              `json:"block_height,omitempty"`
	BlockTime       *int64                               `json:"block_time,omitempty"`
	ChainID         string                               `json:"chain_id"`
	Fee             *string                              `json:"fee,omitempty"`
	FromAddress     *string                              `json:"from_address,omitempty"`
	Status          TeamWalletTransactionStatus          `json:"status"`
	ToAddress       *string                              `json:"to_address,omitempty"`
	TokenID         string                               `json:"token_id"`
	TransactionType TeamWalletTransactionTransactionType `json:"transaction_type"`
	TxID            *string                              `json:"txid,omitempty"`
	WalletID        int64                                `json:"wallet_id"`
}
