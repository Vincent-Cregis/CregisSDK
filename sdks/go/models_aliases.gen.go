// Code generated from the canonical Cregis OpenAPI specification. DO NOT EDIT.
package cregis

import (
	paymentmodels "github.com/Vincent-Cregis/CregisSDK/sdks/go/generated/payment"
	teammodels "github.com/Vincent-Cregis/CregisSDK/sdks/go/generated/team"
	waasmodels "github.com/Vincent-Cregis/CregisSDK/sdks/go/generated/waas"
)

// CreateOrderRequest is the generated payment API contract model.
type CreateOrderRequest = paymentmodels.CreateOrderRequest

// CreateOrderResponse is the generated payment API contract model.
type CreateOrderResponse = paymentmodels.CreateOrderResponse

// OrderDetails is the generated payment API contract model.
type OrderDetails = paymentmodels.OrderDetails

// OrderItem is the generated payment API contract model.
type OrderItem = paymentmodels.OrderItem

// OrderSettlementDetail is the generated payment API contract model.
type OrderSettlementDetail = paymentmodels.OrderSettlementDetail

// PaymentCallbackEnvelope is the generated payment API contract model.
type PaymentCallbackEnvelope = paymentmodels.PaymentCallbackEnvelope

// PaymentCompletedCallbackData is the generated payment API contract model.
type PaymentCompletedCallbackData = paymentmodels.PaymentCompletedCallbackData

// PaymentDetail is the generated payment API contract model.
type PaymentDetail = paymentmodels.PaymentDetail

// PaymentExpiredCallbackData is the generated payment API contract model.
type PaymentExpiredCallbackData = paymentmodels.PaymentExpiredCallbackData

// PaymentInfo is the generated payment API contract model.
type PaymentInfo = paymentmodels.PaymentInfo

// PaymentInfoConsolidatedQrcodesItem is the generated payment API contract model.
type PaymentInfoConsolidatedQrcodesItem = paymentmodels.PaymentInfoConsolidatedQrcodesItem

// PaymentRefundedCallbackData is the generated payment API contract model.
type PaymentRefundedCallbackData = paymentmodels.PaymentRefundedCallbackData

// PaymentRemainingCallbackData is the generated payment API contract model.
type PaymentRemainingCallbackData = paymentmodels.PaymentRemainingCallbackData

// QueryOrderDetails is the generated payment API contract model.
type QueryOrderDetails = paymentmodels.QueryOrderDetails

// QueryOrderItem is the generated payment API contract model.
type QueryOrderItem = paymentmodels.QueryOrderItem

// QueryOrderRequest is the generated payment API contract model.
type QueryOrderRequest = paymentmodels.QueryOrderRequest

// QueryOrderResponse is the generated payment API contract model.
type QueryOrderResponse = paymentmodels.QueryOrderResponse

// QuerySubMerchant is the generated payment API contract model.
type QuerySubMerchant = paymentmodels.QuerySubMerchant

// RefundData is the generated payment API contract model.
type RefundData = paymentmodels.RefundData

// SettlementDetails is the generated payment API contract model.
type SettlementDetails = paymentmodels.SettlementDetails

// SubMerchant is the generated payment API contract model.
type SubMerchant = paymentmodels.SubMerchant

// CreateOrderRequestAcceptOverPayment is the generated payment API enum type.
type CreateOrderRequestAcceptOverPayment = paymentmodels.CreateOrderRequestAcceptOverPayment

const (
	CreateOrderRequestAcceptOverPaymentTrue  = paymentmodels.CreateOrderRequestAcceptOverPaymentTrue
	CreateOrderRequestAcceptOverPaymentFalse = paymentmodels.CreateOrderRequestAcceptOverPaymentFalse
)

// CreateOrderRequestAcceptPartialPayment is the generated payment API enum type.
type CreateOrderRequestAcceptPartialPayment = paymentmodels.CreateOrderRequestAcceptPartialPayment

const (
	CreateOrderRequestAcceptPartialPaymentTrue  = paymentmodels.CreateOrderRequestAcceptPartialPaymentTrue
	CreateOrderRequestAcceptPartialPaymentFalse = paymentmodels.CreateOrderRequestAcceptPartialPaymentFalse
)

// CreateOrderRequestLanguage is the generated payment API enum type.
type CreateOrderRequestLanguage = paymentmodels.CreateOrderRequestLanguage

const (
	CreateOrderRequestLanguageEn = paymentmodels.CreateOrderRequestLanguageEn
	CreateOrderRequestLanguageTc = paymentmodels.CreateOrderRequestLanguageTc
	CreateOrderRequestLanguageSc = paymentmodels.CreateOrderRequestLanguageSc
)

// CreateOrderRequestStablecoinRealtimeRate is the generated payment API enum type.
type CreateOrderRequestStablecoinRealtimeRate = paymentmodels.CreateOrderRequestStablecoinRealtimeRate

const (
	CreateOrderRequestStablecoinRealtimeRateTrue  = paymentmodels.CreateOrderRequestStablecoinRealtimeRateTrue
	CreateOrderRequestStablecoinRealtimeRateFalse = paymentmodels.CreateOrderRequestStablecoinRealtimeRateFalse
)

// OrderSettlementDetailStatus is the generated payment API enum type.
type OrderSettlementDetailStatus = paymentmodels.OrderSettlementDetailStatus

const (
	OrderSettlementDetailStatusUnsettled = paymentmodels.OrderSettlementDetailStatusUnsettled
	OrderSettlementDetailStatusSettling  = paymentmodels.OrderSettlementDetailStatusSettling
	OrderSettlementDetailStatusSettled   = paymentmodels.OrderSettlementDetailStatusSettled
)

// PaymentCallbackEnvelopeEventName is the generated payment API enum type.
type PaymentCallbackEnvelopeEventName = paymentmodels.PaymentCallbackEnvelopeEventName

const (
	PaymentCallbackEnvelopeEventNameOrder = paymentmodels.PaymentCallbackEnvelopeEventNameOrder
)

// PaymentCallbackEnvelopeEventType is the generated payment API enum type.
type PaymentCallbackEnvelopeEventType = paymentmodels.PaymentCallbackEnvelopeEventType

const (
	PaymentCallbackEnvelopeEventTypeExpired     = paymentmodels.PaymentCallbackEnvelopeEventTypeExpired
	PaymentCallbackEnvelopeEventTypePaid        = paymentmodels.PaymentCallbackEnvelopeEventTypePaid
	PaymentCallbackEnvelopeEventTypePaidPartial = paymentmodels.PaymentCallbackEnvelopeEventTypePaidPartial
	PaymentCallbackEnvelopeEventTypePaidOver    = paymentmodels.PaymentCallbackEnvelopeEventTypePaidOver
	PaymentCallbackEnvelopeEventTypeRefunded    = paymentmodels.PaymentCallbackEnvelopeEventTypeRefunded
	PaymentCallbackEnvelopeEventTypePaidRemain  = paymentmodels.PaymentCallbackEnvelopeEventTypePaidRemain
)

// PaymentCompletedCallbackDataStatus is the generated payment API enum type.
type PaymentCompletedCallbackDataStatus = paymentmodels.PaymentCompletedCallbackDataStatus

const (
	PaymentCompletedCallbackDataStatusNew         = paymentmodels.PaymentCompletedCallbackDataStatusNew
	PaymentCompletedCallbackDataStatusPaid        = paymentmodels.PaymentCompletedCallbackDataStatusPaid
	PaymentCompletedCallbackDataStatusExpired     = paymentmodels.PaymentCompletedCallbackDataStatusExpired
	PaymentCompletedCallbackDataStatusPaidOver    = paymentmodels.PaymentCompletedCallbackDataStatusPaidOver
	PaymentCompletedCallbackDataStatusPaidPartial = paymentmodels.PaymentCompletedCallbackDataStatusPaidPartial
	PaymentCompletedCallbackDataStatusCanceled    = paymentmodels.PaymentCompletedCallbackDataStatusCanceled
)

// PaymentExpiredCallbackDataStatus is the generated payment API enum type.
type PaymentExpiredCallbackDataStatus = paymentmodels.PaymentExpiredCallbackDataStatus

const (
	PaymentExpiredCallbackDataStatusNew         = paymentmodels.PaymentExpiredCallbackDataStatusNew
	PaymentExpiredCallbackDataStatusPaid        = paymentmodels.PaymentExpiredCallbackDataStatusPaid
	PaymentExpiredCallbackDataStatusExpired     = paymentmodels.PaymentExpiredCallbackDataStatusExpired
	PaymentExpiredCallbackDataStatusPaidOver    = paymentmodels.PaymentExpiredCallbackDataStatusPaidOver
	PaymentExpiredCallbackDataStatusPaidPartial = paymentmodels.PaymentExpiredCallbackDataStatusPaidPartial
	PaymentExpiredCallbackDataStatusCanceled    = paymentmodels.PaymentExpiredCallbackDataStatusCanceled
)

// PaymentRefundedCallbackDataRefundRequested is the generated payment API enum type.
type PaymentRefundedCallbackDataRefundRequested = paymentmodels.PaymentRefundedCallbackDataRefundRequested

const (
	PaymentRefundedCallbackDataRefundRequestedYes = paymentmodels.PaymentRefundedCallbackDataRefundRequestedYes
	PaymentRefundedCallbackDataRefundRequestedNo  = paymentmodels.PaymentRefundedCallbackDataRefundRequestedNo
)

// PaymentRefundedCallbackDataRefundStatus is the generated payment API enum type.
type PaymentRefundedCallbackDataRefundStatus = paymentmodels.PaymentRefundedCallbackDataRefundStatus

const (
	PaymentRefundedCallbackDataRefundStatus0 = paymentmodels.PaymentRefundedCallbackDataRefundStatus0
	PaymentRefundedCallbackDataRefundStatus1 = paymentmodels.PaymentRefundedCallbackDataRefundStatus1
	PaymentRefundedCallbackDataRefundStatus2 = paymentmodels.PaymentRefundedCallbackDataRefundStatus2
)

// PaymentRefundedCallbackDataStatus is the generated payment API enum type.
type PaymentRefundedCallbackDataStatus = paymentmodels.PaymentRefundedCallbackDataStatus

const (
	PaymentRefundedCallbackDataStatusNew         = paymentmodels.PaymentRefundedCallbackDataStatusNew
	PaymentRefundedCallbackDataStatusPaid        = paymentmodels.PaymentRefundedCallbackDataStatusPaid
	PaymentRefundedCallbackDataStatusExpired     = paymentmodels.PaymentRefundedCallbackDataStatusExpired
	PaymentRefundedCallbackDataStatusPaidOver    = paymentmodels.PaymentRefundedCallbackDataStatusPaidOver
	PaymentRefundedCallbackDataStatusPaidPartial = paymentmodels.PaymentRefundedCallbackDataStatusPaidPartial
	PaymentRefundedCallbackDataStatusCanceled    = paymentmodels.PaymentRefundedCallbackDataStatusCanceled
)

// PaymentRefundedCallbackDataType is the generated payment API enum type.
type PaymentRefundedCallbackDataType = paymentmodels.PaymentRefundedCallbackDataType

const (
	PaymentRefundedCallbackDataType0 = paymentmodels.PaymentRefundedCallbackDataType0
	PaymentRefundedCallbackDataType1 = paymentmodels.PaymentRefundedCallbackDataType1
)

// PaymentRemainingCallbackDataStatus is the generated payment API enum type.
type PaymentRemainingCallbackDataStatus = paymentmodels.PaymentRemainingCallbackDataStatus

const (
	PaymentRemainingCallbackDataStatusNew         = paymentmodels.PaymentRemainingCallbackDataStatusNew
	PaymentRemainingCallbackDataStatusPaid        = paymentmodels.PaymentRemainingCallbackDataStatusPaid
	PaymentRemainingCallbackDataStatusExpired     = paymentmodels.PaymentRemainingCallbackDataStatusExpired
	PaymentRemainingCallbackDataStatusPaidOver    = paymentmodels.PaymentRemainingCallbackDataStatusPaidOver
	PaymentRemainingCallbackDataStatusPaidPartial = paymentmodels.PaymentRemainingCallbackDataStatusPaidPartial
	PaymentRemainingCallbackDataStatusCanceled    = paymentmodels.PaymentRemainingCallbackDataStatusCanceled
)

// QueryOrderResponseRefundRequested is the generated payment API enum type.
type QueryOrderResponseRefundRequested = paymentmodels.QueryOrderResponseRefundRequested

const (
	QueryOrderResponseRefundRequestedYes = paymentmodels.QueryOrderResponseRefundRequestedYes
	QueryOrderResponseRefundRequestedNo  = paymentmodels.QueryOrderResponseRefundRequestedNo
)

// QueryOrderResponseSettlementStatus is the generated payment API enum type.
type QueryOrderResponseSettlementStatus = paymentmodels.QueryOrderResponseSettlementStatus

const (
	QueryOrderResponseSettlementStatusUnsettled = paymentmodels.QueryOrderResponseSettlementStatusUnsettled
	QueryOrderResponseSettlementStatusSettling  = paymentmodels.QueryOrderResponseSettlementStatusSettling
	QueryOrderResponseSettlementStatusSettled   = paymentmodels.QueryOrderResponseSettlementStatusSettled
)

// QueryOrderResponseSettlementType is the generated payment API enum type.
type QueryOrderResponseSettlementType = paymentmodels.QueryOrderResponseSettlementType

const (
	QueryOrderResponseSettlementTypeEmpty  = paymentmodels.QueryOrderResponseSettlementTypeEmpty
	QueryOrderResponseSettlementTypeSystem = paymentmodels.QueryOrderResponseSettlementTypeSystem
	QueryOrderResponseSettlementTypeManual = paymentmodels.QueryOrderResponseSettlementTypeManual
)

// QueryOrderResponseStatus is the generated payment API enum type.
type QueryOrderResponseStatus = paymentmodels.QueryOrderResponseStatus

const (
	QueryOrderResponseStatusNew         = paymentmodels.QueryOrderResponseStatusNew
	QueryOrderResponseStatusPaid        = paymentmodels.QueryOrderResponseStatusPaid
	QueryOrderResponseStatusExpired     = paymentmodels.QueryOrderResponseStatusExpired
	QueryOrderResponseStatusPaidOver    = paymentmodels.QueryOrderResponseStatusPaidOver
	QueryOrderResponseStatusPaidPartial = paymentmodels.QueryOrderResponseStatusPaidPartial
	QueryOrderResponseStatusCanceled    = paymentmodels.QueryOrderResponseStatusCanceled
)

// RefundDataRefundStatus is the generated payment API enum type.
type RefundDataRefundStatus = paymentmodels.RefundDataRefundStatus

const (
	RefundDataRefundStatus0 = paymentmodels.RefundDataRefundStatus0
	RefundDataRefundStatus1 = paymentmodels.RefundDataRefundStatus1
	RefundDataRefundStatus2 = paymentmodels.RefundDataRefundStatus2
)

// RefundDataType is the generated payment API enum type.
type RefundDataType = paymentmodels.RefundDataType

const (
	RefundDataType1 = paymentmodels.RefundDataType1
	RefundDataType2 = paymentmodels.RefundDataType2
)

// ListTeamWalletAddressesRequest is the generated team API contract model.
type ListTeamWalletAddressesRequest = teammodels.ListTeamWalletAddressesRequest

// ListTeamWalletAddressesResponse is the generated team API contract model.
type ListTeamWalletAddressesResponse = teammodels.ListTeamWalletAddressesResponse

// ListTeamWalletsRequest is the generated team API contract model.
type ListTeamWalletsRequest = teammodels.ListTeamWalletsRequest

// ListTeamWalletsResponse is the generated team API contract model.
type ListTeamWalletsResponse = teammodels.ListTeamWalletsResponse

// QueryTeamWalletAddressBalanceRequest is the generated team API contract model.
type QueryTeamWalletAddressBalanceRequest = teammodels.QueryTeamWalletAddressBalanceRequest

// QueryTeamWalletAddressBalanceResponse is the generated team API contract model.
type QueryTeamWalletAddressBalanceResponse = teammodels.QueryTeamWalletAddressBalanceResponse

// QueryTeamWalletBalanceRequest is the generated team API contract model.
type QueryTeamWalletBalanceRequest = teammodels.QueryTeamWalletBalanceRequest

// QueryTeamWalletBalanceResponse is the generated team API contract model.
type QueryTeamWalletBalanceResponse = teammodels.QueryTeamWalletBalanceResponse

// QueryTeamWalletHistoryTransactionsRequest is the generated team API contract model.
type QueryTeamWalletHistoryTransactionsRequest = teammodels.QueryTeamWalletHistoryTransactionsRequest

// QueryTeamWalletHistoryTransactionsResponse is the generated team API contract model.
type QueryTeamWalletHistoryTransactionsResponse = teammodels.QueryTeamWalletHistoryTransactionsResponse

// QueryTeamWalletProcessingTransactionsRequest is the generated team API contract model.
type QueryTeamWalletProcessingTransactionsRequest = teammodels.QueryTeamWalletProcessingTransactionsRequest

// QueryTeamWalletProcessingTransactionsResponse is the generated team API contract model.
type QueryTeamWalletProcessingTransactionsResponse = teammodels.QueryTeamWalletProcessingTransactionsResponse

// TeamWallet is the generated team API contract model.
type TeamWallet = teammodels.TeamWallet

// TeamWalletAddress is the generated team API contract model.
type TeamWalletAddress = teammodels.TeamWalletAddress

// TeamWalletAddressBalance is the generated team API contract model.
type TeamWalletAddressBalance = teammodels.TeamWalletAddressBalance

// TeamWalletBalance is the generated team API contract model.
type TeamWalletBalance = teammodels.TeamWalletBalance

// TeamWalletProcessingTransaction is the generated team API contract model.
type TeamWalletProcessingTransaction = teammodels.TeamWalletProcessingTransaction

// TeamWalletToken is the generated team API contract model.
type TeamWalletToken = teammodels.TeamWalletToken

// TeamWalletTransaction is the generated team API contract model.
type TeamWalletTransaction = teammodels.TeamWalletTransaction

// ListTeamWalletsRequestWalletType is the generated team API enum type.
type ListTeamWalletsRequestWalletType = teammodels.ListTeamWalletsRequestWalletType

const (
	ListTeamWalletsRequestWalletTypeSingleSign = teammodels.ListTeamWalletsRequestWalletTypeSingleSign
	ListTeamWalletsRequestWalletTypeMultiSign  = teammodels.ListTeamWalletsRequestWalletTypeMultiSign
)

// QueryTeamWalletHistoryTransactionsRequestTransactionStatus is the generated team API enum type.
type QueryTeamWalletHistoryTransactionsRequestTransactionStatus = teammodels.QueryTeamWalletHistoryTransactionsRequestTransactionStatus

const (
	QueryTeamWalletHistoryTransactionsRequestTransactionStatus1 = teammodels.QueryTeamWalletHistoryTransactionsRequestTransactionStatus1
	QueryTeamWalletHistoryTransactionsRequestTransactionStatus2 = teammodels.QueryTeamWalletHistoryTransactionsRequestTransactionStatus2
)

// QueryTeamWalletHistoryTransactionsRequestTransactionType is the generated team API enum type.
type QueryTeamWalletHistoryTransactionsRequestTransactionType = teammodels.QueryTeamWalletHistoryTransactionsRequestTransactionType

const (
	QueryTeamWalletHistoryTransactionsRequestTransactionType1 = teammodels.QueryTeamWalletHistoryTransactionsRequestTransactionType1
	QueryTeamWalletHistoryTransactionsRequestTransactionType2 = teammodels.QueryTeamWalletHistoryTransactionsRequestTransactionType2
)

// TeamWalletWalletType is the generated team API enum type.
type TeamWalletWalletType = teammodels.TeamWalletWalletType

const (
	TeamWalletWalletTypeSingleSign = teammodels.TeamWalletWalletTypeSingleSign
	TeamWalletWalletTypeMultiSign  = teammodels.TeamWalletWalletTypeMultiSign
)

// TeamWalletWalletStatus is the generated team API enum type.
type TeamWalletWalletStatus = teammodels.TeamWalletWalletStatus

const (
	TeamWalletWalletStatusNormal = teammodels.TeamWalletWalletStatusNormal
)

// TeamWalletAddressAddressStatus is the generated team API enum type.
type TeamWalletAddressAddressStatus = teammodels.TeamWalletAddressAddressStatus

const (
	TeamWalletAddressAddressStatusEnable  = teammodels.TeamWalletAddressAddressStatusEnable
	TeamWalletAddressAddressStatusDisable = teammodels.TeamWalletAddressAddressStatusDisable
)

// TeamWalletProcessingTransactionStatus is the generated team API enum type.
type TeamWalletProcessingTransactionStatus = teammodels.TeamWalletProcessingTransactionStatus

const (
	TeamWalletProcessingTransactionStatus0 = teammodels.TeamWalletProcessingTransactionStatus0
)

// TeamWalletProcessingTransactionTransactionType is the generated team API enum type.
type TeamWalletProcessingTransactionTransactionType = teammodels.TeamWalletProcessingTransactionTransactionType

const (
	TeamWalletProcessingTransactionTransactionType1 = teammodels.TeamWalletProcessingTransactionTransactionType1
)

// TeamWalletTransactionStatus is the generated team API enum type.
type TeamWalletTransactionStatus = teammodels.TeamWalletTransactionStatus

const (
	TeamWalletTransactionStatus1 = teammodels.TeamWalletTransactionStatus1
	TeamWalletTransactionStatus2 = teammodels.TeamWalletTransactionStatus2
)

// TeamWalletTransactionTransactionType is the generated team API enum type.
type TeamWalletTransactionTransactionType = teammodels.TeamWalletTransactionTransactionType

const (
	TeamWalletTransactionTransactionType1 = teammodels.TeamWalletTransactionTransactionType1
	TeamWalletTransactionTransactionType2 = teammodels.TeamWalletTransactionTransactionType2
)

// AddressBalance is the generated waas API contract model.
type AddressBalance = waasmodels.AddressBalance

// AddressBalanceRequest is the generated waas API contract model.
type AddressBalanceRequest = waasmodels.AddressBalanceRequest

// AddressBalanceResponse is the generated waas API contract model.
type AddressBalanceResponse = waasmodels.AddressBalanceResponse

// AddressBalanceV2 is the generated waas API contract model.
type AddressBalanceV2 = waasmodels.AddressBalanceV2

// AddressBalanceV2Request is the generated waas API contract model.
type AddressBalanceV2Request = waasmodels.AddressBalanceV2Request

// AddressBalanceV2Response is the generated waas API contract model.
type AddressBalanceV2Response = waasmodels.AddressBalanceV2Response

// AddressDepositCallbackNotification is the generated waas API contract model.
type AddressDepositCallbackNotification = waasmodels.AddressDepositCallbackNotification

// AddressUpdateRequest is the generated waas API contract model.
type AddressUpdateRequest = waasmodels.AddressUpdateRequest

// BalanceCollectRequest is the generated waas API contract model.
type BalanceCollectRequest = waasmodels.BalanceCollectRequest

// BalanceCollectResponse is the generated waas API contract model.
type BalanceCollectResponse = waasmodels.BalanceCollectResponse

// BatchGenerateAddressRequest is the generated waas API contract model.
type BatchGenerateAddressRequest = waasmodels.BatchGenerateAddressRequest

// CheckAddressLegalityRequest is the generated waas API contract model.
type CheckAddressLegalityRequest = waasmodels.CheckAddressLegalityRequest

// CheckAddressLegalityResponse is the generated waas API contract model.
type CheckAddressLegalityResponse = waasmodels.CheckAddressLegalityResponse

// GenerateAddressRequest is the generated waas API contract model.
type GenerateAddressRequest = waasmodels.GenerateAddressRequest

// GenerateAddressResponse is the generated waas API contract model.
type GenerateAddressResponse = waasmodels.GenerateAddressResponse

// GeneratedAddress is the generated waas API contract model.
type GeneratedAddress = waasmodels.GeneratedAddress

// PayoutCallbackNotification is the generated waas API contract model.
type PayoutCallbackNotification = waasmodels.PayoutCallbackNotification

// PayoutExternalVerificationCallbackNotification is the generated waas API contract model.
type PayoutExternalVerificationCallbackNotification = waasmodels.PayoutExternalVerificationCallbackNotification

// PayoutRequest is the generated waas API contract model.
type PayoutRequest = waasmodels.PayoutRequest

// PayoutResponse is the generated waas API contract model.
type PayoutResponse = waasmodels.PayoutResponse

// PayoutV1Request is the generated waas API contract model.
type PayoutV1Request = waasmodels.PayoutV1Request

// ProjectCoin is the generated waas API contract model.
type ProjectCoin = waasmodels.ProjectCoin

// ProjectCoinQueryResponse is the generated waas API contract model.
type ProjectCoinQueryResponse = waasmodels.ProjectCoinQueryResponse

// ProjectCoinQueryResponseOrderCoinsItem is the generated waas API contract model.
type ProjectCoinQueryResponseOrderCoinsItem = waasmodels.ProjectCoinQueryResponseOrderCoinsItem

// QueryPayoutRequest is the generated waas API contract model.
type QueryPayoutRequest = waasmodels.QueryPayoutRequest

// QueryPayoutResponse is the generated waas API contract model.
type QueryPayoutResponse = waasmodels.QueryPayoutResponse

// QueryWithdrawalRequest is the generated waas API contract model.
type QueryWithdrawalRequest = waasmodels.QueryWithdrawalRequest

// QueryWithdrawalResponse is the generated waas API contract model.
type QueryWithdrawalResponse = waasmodels.QueryWithdrawalResponse

// TradeRecord is the generated waas API contract model.
type TradeRecord = waasmodels.TradeRecord

// TradeRecordQueryRequest is the generated waas API contract model.
type TradeRecordQueryRequest = waasmodels.TradeRecordQueryRequest

// TradeRecordQueryResponse is the generated waas API contract model.
type TradeRecordQueryResponse = waasmodels.TradeRecordQueryResponse

// ValidateAddressRequest is the generated waas API contract model.
type ValidateAddressRequest = waasmodels.ValidateAddressRequest

// ValidateAddressResponse is the generated waas API contract model.
type ValidateAddressResponse = waasmodels.ValidateAddressResponse

// WithdrawalCallbackNotification is the generated waas API contract model.
type WithdrawalCallbackNotification = waasmodels.WithdrawalCallbackNotification

// WithdrawalRequest is the generated waas API contract model.
type WithdrawalRequest = waasmodels.WithdrawalRequest

// WithdrawalResponse is the generated waas API contract model.
type WithdrawalResponse = waasmodels.WithdrawalResponse

// AddressDepositCallbackNotificationStatus is the generated waas API enum type.
type AddressDepositCallbackNotificationStatus = waasmodels.AddressDepositCallbackNotificationStatus

const (
	AddressDepositCallbackNotificationStatus1 = waasmodels.AddressDepositCallbackNotificationStatus1
	AddressDepositCallbackNotificationStatus2 = waasmodels.AddressDepositCallbackNotificationStatus2
)

// AddressUpdateRequestStatus is the generated waas API enum type.
type AddressUpdateRequestStatus = waasmodels.AddressUpdateRequestStatus

const (
	AddressUpdateRequestStatus0 = waasmodels.AddressUpdateRequestStatus0
	AddressUpdateRequestStatus1 = waasmodels.AddressUpdateRequestStatus1
)

// PayoutCallbackNotificationStatus is the generated waas API enum type.
type PayoutCallbackNotificationStatus = waasmodels.PayoutCallbackNotificationStatus

const (
	PayoutCallbackNotificationStatus2 = waasmodels.PayoutCallbackNotificationStatus2
	PayoutCallbackNotificationStatus4 = waasmodels.PayoutCallbackNotificationStatus4
	PayoutCallbackNotificationStatus6 = waasmodels.PayoutCallbackNotificationStatus6
	PayoutCallbackNotificationStatus7 = waasmodels.PayoutCallbackNotificationStatus7
)

// QueryPayoutResponseStatus is the generated waas API enum type.
type QueryPayoutResponseStatus = waasmodels.QueryPayoutResponseStatus

const (
	QueryPayoutResponseStatus0 = waasmodels.QueryPayoutResponseStatus0
	QueryPayoutResponseStatus1 = waasmodels.QueryPayoutResponseStatus1
	QueryPayoutResponseStatus2 = waasmodels.QueryPayoutResponseStatus2
	QueryPayoutResponseStatus3 = waasmodels.QueryPayoutResponseStatus3
	QueryPayoutResponseStatus4 = waasmodels.QueryPayoutResponseStatus4
	QueryPayoutResponseStatus5 = waasmodels.QueryPayoutResponseStatus5
	QueryPayoutResponseStatus6 = waasmodels.QueryPayoutResponseStatus6
	QueryPayoutResponseStatus7 = waasmodels.QueryPayoutResponseStatus7
)

// QueryWithdrawalResponseStatus is the generated waas API enum type.
type QueryWithdrawalResponseStatus = waasmodels.QueryWithdrawalResponseStatus

const (
	QueryWithdrawalResponseStatus0 = waasmodels.QueryWithdrawalResponseStatus0
	QueryWithdrawalResponseStatus1 = waasmodels.QueryWithdrawalResponseStatus1
	QueryWithdrawalResponseStatus2 = waasmodels.QueryWithdrawalResponseStatus2
	QueryWithdrawalResponseStatus3 = waasmodels.QueryWithdrawalResponseStatus3
	QueryWithdrawalResponseStatus4 = waasmodels.QueryWithdrawalResponseStatus4
	QueryWithdrawalResponseStatus5 = waasmodels.QueryWithdrawalResponseStatus5
	QueryWithdrawalResponseStatus6 = waasmodels.QueryWithdrawalResponseStatus6
	QueryWithdrawalResponseStatus7 = waasmodels.QueryWithdrawalResponseStatus7
)

// TradeRecordStatus is the generated waas API enum type.
type TradeRecordStatus = waasmodels.TradeRecordStatus

const (
	TradeRecordStatus0 = waasmodels.TradeRecordStatus0
	TradeRecordStatus1 = waasmodels.TradeRecordStatus1
	TradeRecordStatus2 = waasmodels.TradeRecordStatus2
)

// TradeRecordQueryRequestBusinessType is the generated waas API enum type.
type TradeRecordQueryRequestBusinessType = waasmodels.TradeRecordQueryRequestBusinessType

const (
	TradeRecordQueryRequestBusinessType0 = waasmodels.TradeRecordQueryRequestBusinessType0
	TradeRecordQueryRequestBusinessType2 = waasmodels.TradeRecordQueryRequestBusinessType2
	TradeRecordQueryRequestBusinessType3 = waasmodels.TradeRecordQueryRequestBusinessType3
	TradeRecordQueryRequestBusinessType4 = waasmodels.TradeRecordQueryRequestBusinessType4
	TradeRecordQueryRequestBusinessType5 = waasmodels.TradeRecordQueryRequestBusinessType5
)

// TradeRecordQueryRequestStatus is the generated waas API enum type.
type TradeRecordQueryRequestStatus = waasmodels.TradeRecordQueryRequestStatus

const (
	TradeRecordQueryRequestStatus0 = waasmodels.TradeRecordQueryRequestStatus0
	TradeRecordQueryRequestStatus1 = waasmodels.TradeRecordQueryRequestStatus1
	TradeRecordQueryRequestStatus2 = waasmodels.TradeRecordQueryRequestStatus2
)

// TradeRecordQueryRequestTradeType is the generated waas API enum type.
type TradeRecordQueryRequestTradeType = waasmodels.TradeRecordQueryRequestTradeType

const (
	TradeRecordQueryRequestTradeType1 = waasmodels.TradeRecordQueryRequestTradeType1
	TradeRecordQueryRequestTradeType2 = waasmodels.TradeRecordQueryRequestTradeType2
)

// WithdrawalCallbackNotificationStatus is the generated waas API enum type.
type WithdrawalCallbackNotificationStatus = waasmodels.WithdrawalCallbackNotificationStatus

const (
	WithdrawalCallbackNotificationStatus2 = waasmodels.WithdrawalCallbackNotificationStatus2
	WithdrawalCallbackNotificationStatus4 = waasmodels.WithdrawalCallbackNotificationStatus4
	WithdrawalCallbackNotificationStatus6 = waasmodels.WithdrawalCallbackNotificationStatus6
	WithdrawalCallbackNotificationStatus7 = waasmodels.WithdrawalCallbackNotificationStatus7
)
