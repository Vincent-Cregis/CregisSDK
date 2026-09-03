// Code generated from the canonical Cregis OpenAPI specification. DO NOT EDIT.
package payment

import "encoding/json"

// CreateOrderRequestAcceptOverPayment is the documented value set for CreateOrderRequest.AcceptOverPayment.
type CreateOrderRequestAcceptOverPayment string

const (
	CreateOrderRequestAcceptOverPaymentTrue  CreateOrderRequestAcceptOverPayment = "true"
	CreateOrderRequestAcceptOverPaymentFalse CreateOrderRequestAcceptOverPayment = "false"
)

// CreateOrderRequestAcceptPartialPayment is the documented value set for CreateOrderRequest.AcceptPartialPayment.
type CreateOrderRequestAcceptPartialPayment string

const (
	CreateOrderRequestAcceptPartialPaymentTrue  CreateOrderRequestAcceptPartialPayment = "true"
	CreateOrderRequestAcceptPartialPaymentFalse CreateOrderRequestAcceptPartialPayment = "false"
)

// CreateOrderRequestLanguage is the documented value set for CreateOrderRequest.Language.
type CreateOrderRequestLanguage string

const (
	CreateOrderRequestLanguageEn CreateOrderRequestLanguage = "en"
	CreateOrderRequestLanguageTc CreateOrderRequestLanguage = "tc"
	CreateOrderRequestLanguageSc CreateOrderRequestLanguage = "sc"
)

// CreateOrderRequestStablecoinRealtimeRate is the documented value set for CreateOrderRequest.StablecoinRealtimeRate.
type CreateOrderRequestStablecoinRealtimeRate string

const (
	CreateOrderRequestStablecoinRealtimeRateTrue  CreateOrderRequestStablecoinRealtimeRate = "true"
	CreateOrderRequestStablecoinRealtimeRateFalse CreateOrderRequestStablecoinRealtimeRate = "false"
)

// CreateOrderRequest is generated from the canonical payment API contract.
type CreateOrderRequest struct {
	AcceptOverPayment      *CreateOrderRequestAcceptOverPayment      `json:"accept_over_payment,omitempty"`
	AcceptPartialPayment   *CreateOrderRequestAcceptPartialPayment   `json:"accept_partial_payment,omitempty"`
	CallbackURL            *string                                   `json:"callback_url,omitempty"`
	CancelURL              string                                    `json:"cancel_url"`
	Language               *CreateOrderRequestLanguage               `json:"language,omitempty"`
	OrderAmount            string                                    `json:"order_amount"`
	OrderCurrency          string                                    `json:"order_currency"`
	OrderDetails           *string                                   `json:"order_details,omitempty"`
	OrderID                string                                    `json:"order_id"`
	OverpaidTolerance      *float32                                  `json:"overpaid_tolerance,omitempty"`
	PayerEmail             *string                                   `json:"payer_email,omitempty"`
	PayerID                string                                    `json:"payer_id"`
	PayerName              *string                                   `json:"payer_name,omitempty"`
	Remark                 *string                                   `json:"remark,omitempty"`
	StablecoinRealtimeRate *CreateOrderRequestStablecoinRealtimeRate `json:"stablecoin_realtime_rate,omitempty"`
	SubMerchant            *string                                   `json:"sub_merchant,omitempty"`
	SuccessURL             string                                    `json:"success_url"`
	Tokens                 *string                                   `json:"tokens,omitempty"`
	UnderpaidTolerance     *float32                                  `json:"underpaid_tolerance,omitempty"`
	ValidTime              *int                                      `json:"valid_time,omitempty"`
}

// CreateOrderResponse is generated from the canonical payment API contract.
type CreateOrderResponse struct {
	CheckoutURL     *string       `json:"checkout_url,omitempty"`
	CreatedTime     *int64        `json:"created_time,omitempty"`
	CregisID        *string       `json:"cregis_id,omitempty"`
	ExpireTime      *int64        `json:"expire_time,omitempty"`
	MerchantLogoURL *string       `json:"merchant_logo_url,omitempty"`
	MerchantName    *string       `json:"merchant_name,omitempty"`
	OrderAmount     *string       `json:"order_amount,omitempty"`
	OrderCurrency   *string       `json:"order_currency,omitempty"`
	PaymentInfo     []PaymentInfo `json:"payment_info,omitempty"`
}

// OrderDetails is generated from the canonical payment API contract.
type OrderDetails struct {
	Items        []OrderItem `json:"items,omitempty"`
	ShoppingCost *float64    `json:"shopping_cost,omitempty"`
	TaxCost      *float64    `json:"tax_cost,omitempty"`
}

// OrderItem is generated from the canonical payment API contract.
type OrderItem struct {
	ItemID        *string  `json:"item_id,omitempty"`
	ItemName      *string  `json:"item_name,omitempty"`
	ItemPrice     *float64 `json:"item_price,omitempty"`
	ItemQuantity  *int64   `json:"item_quantity,omitempty"`
	PriceCurrency *string  `json:"price_currency,omitempty"`
}

// OrderSettlementDetailStatus is the documented value set for OrderSettlementDetail.Status.
type OrderSettlementDetailStatus string

const (
	OrderSettlementDetailStatusUnsettled OrderSettlementDetailStatus = "unsettled"
	OrderSettlementDetailStatusSettling  OrderSettlementDetailStatus = "settling"
	OrderSettlementDetailStatusSettled   OrderSettlementDetailStatus = "settled"
)

// OrderSettlementDetail is generated from the canonical payment API contract.
type OrderSettlementDetail struct {
	ActualSettlementAmount *string                      `json:"actual_settlement_amount,omitempty"`
	SettlementAmount       *string                      `json:"settlement_amount,omitempty"`
	SettlementFee          *string                      `json:"settlement_fee,omitempty"`
	Status                 *OrderSettlementDetailStatus `json:"status,omitempty"`
}

// PaymentCallbackEnvelopeEventName is the documented value set for PaymentCallbackEnvelope.EventName.
type PaymentCallbackEnvelopeEventName string

const (
	PaymentCallbackEnvelopeEventNameOrder PaymentCallbackEnvelopeEventName = "order"
)

// PaymentCallbackEnvelopeEventType is the documented value set for PaymentCallbackEnvelope.EventType.
type PaymentCallbackEnvelopeEventType string

const (
	PaymentCallbackEnvelopeEventTypeExpired     PaymentCallbackEnvelopeEventType = "expired"
	PaymentCallbackEnvelopeEventTypePaid        PaymentCallbackEnvelopeEventType = "paid"
	PaymentCallbackEnvelopeEventTypePaidPartial PaymentCallbackEnvelopeEventType = "paid_partial"
	PaymentCallbackEnvelopeEventTypePaidOver    PaymentCallbackEnvelopeEventType = "paid_over"
	PaymentCallbackEnvelopeEventTypeRefunded    PaymentCallbackEnvelopeEventType = "refunded"
	PaymentCallbackEnvelopeEventTypePaidRemain  PaymentCallbackEnvelopeEventType = "paid_remain"
)

// PaymentCallbackEnvelope is generated from the canonical payment API contract.
type PaymentCallbackEnvelope struct {
	Data      json.RawMessage                  `json:"data"`
	EventName PaymentCallbackEnvelopeEventName `json:"event_name"`
	EventType PaymentCallbackEnvelopeEventType `json:"event_type"`
	Nonce     string                           `json:"nonce"`
	PID       int64                            `json:"pid"`
	Sign      string                           `json:"sign"`
	Timestamp int64                            `json:"timestamp"`
}

// PaymentCompletedCallbackDataStatus is the documented value set for PaymentCompletedCallbackData.Status.
type PaymentCompletedCallbackDataStatus string

const (
	PaymentCompletedCallbackDataStatusNew         PaymentCompletedCallbackDataStatus = "new"
	PaymentCompletedCallbackDataStatusPaid        PaymentCompletedCallbackDataStatus = "paid"
	PaymentCompletedCallbackDataStatusExpired     PaymentCompletedCallbackDataStatus = "expired"
	PaymentCompletedCallbackDataStatusPaidOver    PaymentCompletedCallbackDataStatus = "paid_over"
	PaymentCompletedCallbackDataStatusPaidPartial PaymentCompletedCallbackDataStatus = "paid_partial"
	PaymentCompletedCallbackDataStatusCanceled    PaymentCompletedCallbackDataStatus = "canceled"
)

// PaymentCompletedCallbackData is generated from the canonical payment API contract.
type PaymentCompletedCallbackData struct {
	CancelTime      *int64                             `json:"cancel_time,omitempty"`
	CreatedTime     *int64                             `json:"created_time,omitempty"`
	CregisID        string                             `json:"cregis_id"`
	ExchangeRate    string                             `json:"exchange_rate"`
	OrderAmount     *string                            `json:"order_amount,omitempty"`
	OrderCurrency   *string                            `json:"order_currency,omitempty"`
	OrderID         string                             `json:"order_id"`
	PayAmount       string                             `json:"pay_amount"`
	PayCurrency     string                             `json:"pay_currency"`
	PayerEmail      *string                            `json:"payer_email,omitempty"`
	PayerID         *string                            `json:"payer_id,omitempty"`
	PayerName       *string                            `json:"payer_name,omitempty"`
	PaymentAddress  string                             `json:"payment_address"`
	ReceiveAmount   string                             `json:"receive_amount"`
	ReceiveCurrency string                             `json:"receive_currency"`
	Remark          *string                            `json:"remark,omitempty"`
	Status          PaymentCompletedCallbackDataStatus `json:"status"`
	TransactTime    int64                              `json:"transact_time"`
	TxID            string                             `json:"tx_id"`
	ValidTime       *int32                             `json:"valid_time,omitempty"`
}

// PaymentDetail is generated from the canonical payment API contract.
type PaymentDetail struct {
	Blockchain      *string `json:"blockchain,omitempty"`
	ExchangeRate    *string `json:"exchange_rate,omitempty"`
	FromAddress     *string `json:"from_address,omitempty"`
	PayAmount       *string `json:"pay_amount,omitempty"`
	PayCurrency     *string `json:"pay_currency,omitempty"`
	PaymentAddress  *string `json:"payment_address,omitempty"`
	ReceiveAmount   *string `json:"receive_amount,omitempty"`
	ReceiveCurrency *string `json:"receive_currency,omitempty"`
	TokenName       *string `json:"token_name,omitempty"`
	TxID            *string `json:"tx_id,omitempty"`
}

// PaymentExpiredCallbackDataStatus is the documented value set for PaymentExpiredCallbackData.Status.
type PaymentExpiredCallbackDataStatus string

const (
	PaymentExpiredCallbackDataStatusNew         PaymentExpiredCallbackDataStatus = "new"
	PaymentExpiredCallbackDataStatusPaid        PaymentExpiredCallbackDataStatus = "paid"
	PaymentExpiredCallbackDataStatusExpired     PaymentExpiredCallbackDataStatus = "expired"
	PaymentExpiredCallbackDataStatusPaidOver    PaymentExpiredCallbackDataStatus = "paid_over"
	PaymentExpiredCallbackDataStatusPaidPartial PaymentExpiredCallbackDataStatus = "paid_partial"
	PaymentExpiredCallbackDataStatusCanceled    PaymentExpiredCallbackDataStatus = "canceled"
)

// PaymentExpiredCallbackData is generated from the canonical payment API contract.
type PaymentExpiredCallbackData struct {
	CancelTime    *int64                           `json:"cancel_time,omitempty"`
	CreatedTime   *int64                           `json:"created_time,omitempty"`
	CregisID      string                           `json:"cregis_id"`
	OrderAmount   *string                          `json:"order_amount,omitempty"`
	OrderCurrency *string                          `json:"order_currency,omitempty"`
	OrderID       string                           `json:"order_id"`
	PayerEmail    *string                          `json:"payer_email,omitempty"`
	PayerID       *string                          `json:"payer_id,omitempty"`
	PayerName     *string                          `json:"payer_name,omitempty"`
	Remark        *string                          `json:"remark,omitempty"`
	Status        PaymentExpiredCallbackDataStatus `json:"status"`
	ValidTime     *int32                           `json:"valid_time,omitempty"`
}

// PaymentInfo is generated from the canonical payment API contract.
type PaymentInfo struct {
	AssetLogo           *string                               `json:"asset_logo,omitempty"`
	Blockchain          *string                               `json:"blockchain,omitempty"`
	ConsolidatedQRCodes *[]PaymentInfoConsolidatedQrcodesItem `json:"consolidated_qrcodes,omitempty"`
	ExchangeRate        *string                               `json:"exchange_rate,omitempty"`
	LogoURL             *string                               `json:"logo_url,omitempty"`
	PaymentAddress      *string                               `json:"payment_address,omitempty"`
	ReceiveAmount       *string                               `json:"receive_amount,omitempty"`
	ReceiveCurrency     *string                               `json:"receive_currency,omitempty"`
	TokenDecimals       *int                                  `json:"token_decimals,omitempty"`
	TokenName           *string                               `json:"token_name,omitempty"`
	TokenSymbol         *string                               `json:"token_symbol,omitempty"`
}

// PaymentInfoConsolidatedQrcodesItem is generated from the canonical payment API contract.
type PaymentInfoConsolidatedQrcodesItem struct {
	QRCode     *string `json:"qrcode,omitempty"`
	WalletIcon *string `json:"wallet_icon,omitempty"`
	WalletName *string `json:"wallet_name,omitempty"`
}

// PaymentRefundedCallbackDataRefundRequested is the documented value set for PaymentRefundedCallbackData.RefundRequested.
type PaymentRefundedCallbackDataRefundRequested string

const (
	PaymentRefundedCallbackDataRefundRequestedYes PaymentRefundedCallbackDataRefundRequested = "yes"
	PaymentRefundedCallbackDataRefundRequestedNo  PaymentRefundedCallbackDataRefundRequested = "no"
)

// PaymentRefundedCallbackDataRefundStatus is the documented value set for PaymentRefundedCallbackData.RefundStatus.
type PaymentRefundedCallbackDataRefundStatus int

const (
	PaymentRefundedCallbackDataRefundStatus0 PaymentRefundedCallbackDataRefundStatus = 0
	PaymentRefundedCallbackDataRefundStatus1 PaymentRefundedCallbackDataRefundStatus = 1
	PaymentRefundedCallbackDataRefundStatus2 PaymentRefundedCallbackDataRefundStatus = 2
)

// PaymentRefundedCallbackDataStatus is the documented value set for PaymentRefundedCallbackData.Status.
type PaymentRefundedCallbackDataStatus string

const (
	PaymentRefundedCallbackDataStatusNew         PaymentRefundedCallbackDataStatus = "new"
	PaymentRefundedCallbackDataStatusPaid        PaymentRefundedCallbackDataStatus = "paid"
	PaymentRefundedCallbackDataStatusExpired     PaymentRefundedCallbackDataStatus = "expired"
	PaymentRefundedCallbackDataStatusPaidOver    PaymentRefundedCallbackDataStatus = "paid_over"
	PaymentRefundedCallbackDataStatusPaidPartial PaymentRefundedCallbackDataStatus = "paid_partial"
	PaymentRefundedCallbackDataStatusCanceled    PaymentRefundedCallbackDataStatus = "canceled"
)

// PaymentRefundedCallbackDataType is the documented value set for PaymentRefundedCallbackData.Type.
type PaymentRefundedCallbackDataType int

const (
	PaymentRefundedCallbackDataType0 PaymentRefundedCallbackDataType = 0
	PaymentRefundedCallbackDataType1 PaymentRefundedCallbackDataType = 1
)

// PaymentRefundedCallbackData is generated from the canonical payment API contract.
type PaymentRefundedCallbackData struct {
	ActualRefundAmount *string                                     `json:"actual_refund_amount,omitempty"`
	CancelTime         *int64                                      `json:"cancel_time,omitempty"`
	CreatedTime        *int64                                      `json:"created_time,omitempty"`
	CregisID           string                                      `json:"cregis_id"`
	ExchangeRate       string                                      `json:"exchange_rate"`
	OrderAmount        *string                                     `json:"order_amount,omitempty"`
	OrderCurrency      *string                                     `json:"order_currency,omitempty"`
	OrderID            string                                      `json:"order_id"`
	PayAmount          string                                      `json:"pay_amount"`
	PayCurrency        string                                      `json:"pay_currency"`
	PayerEmail         *string                                     `json:"payer_email,omitempty"`
	PayerID            *string                                     `json:"payer_id,omitempty"`
	PayerName          *string                                     `json:"payer_name,omitempty"`
	PaymentAddress     string                                      `json:"payment_address"`
	ReceiveAmount      string                                      `json:"receive_amount"`
	ReceiveCurrency    string                                      `json:"receive_currency"`
	RefundAddress      *string                                     `json:"refund_address,omitempty"`
	RefundAmount       *string                                     `json:"refund_amount,omitempty"`
	RefundCreatedTime  *int64                                      `json:"refund_created_time,omitempty"`
	RefundCurrency     *string                                     `json:"refund_currency,omitempty"`
	RefundFee          *string                                     `json:"refund_fee,omitempty"`
	RefundID           *string                                     `json:"refund_id,omitempty"`
	RefundRequested    *PaymentRefundedCallbackDataRefundRequested `json:"refund_requested,omitempty"`
	RefundStatus       *PaymentRefundedCallbackDataRefundStatus    `json:"refund_status,omitempty"`
	RefundTransactTime *int64                                      `json:"refund_transact_time,omitempty"`
	RefundTxID         *string                                     `json:"refund_tx_id,omitempty"`
	Remark             *string                                     `json:"remark,omitempty"`
	Status             PaymentRefundedCallbackDataStatus           `json:"status"`
	TransactTime       int64                                       `json:"transact_time"`
	TxID               string                                      `json:"tx_id"`
	Type               *PaymentRefundedCallbackDataType            `json:"type,omitempty"`
	ValidTime          *int32                                      `json:"valid_time,omitempty"`
}

// PaymentRemainingCallbackDataStatus is the documented value set for PaymentRemainingCallbackData.Status.
type PaymentRemainingCallbackDataStatus string

const (
	PaymentRemainingCallbackDataStatusNew         PaymentRemainingCallbackDataStatus = "new"
	PaymentRemainingCallbackDataStatusPaid        PaymentRemainingCallbackDataStatus = "paid"
	PaymentRemainingCallbackDataStatusExpired     PaymentRemainingCallbackDataStatus = "expired"
	PaymentRemainingCallbackDataStatusPaidOver    PaymentRemainingCallbackDataStatus = "paid_over"
	PaymentRemainingCallbackDataStatusPaidPartial PaymentRemainingCallbackDataStatus = "paid_partial"
	PaymentRemainingCallbackDataStatusCanceled    PaymentRemainingCallbackDataStatus = "canceled"
)

// PaymentRemainingCallbackData is generated from the canonical payment API contract.
type PaymentRemainingCallbackData struct {
	AdditionalPayAmount           *string                            `json:"additional_pay_amount,omitempty"`
	AdditionalPayCurrency         *string                            `json:"additional_pay_currency,omitempty"`
	AdditionalPaymentAddress      *string                            `json:"additional_payment_address,omitempty"`
	AdditionalPaymentTransactTime *int64                             `json:"additional_payment_transact_time,omitempty"`
	AdditionalPaymentTxID         *string                            `json:"additional_payment_tx_id,omitempty"`
	CancelTime                    *int64                             `json:"cancel_time,omitempty"`
	CreatedTime                   *int64                             `json:"created_time,omitempty"`
	CregisID                      string                             `json:"cregis_id"`
	ExchangeRate                  string                             `json:"exchange_rate"`
	OrderAmount                   *string                            `json:"order_amount,omitempty"`
	OrderCurrency                 *string                            `json:"order_currency,omitempty"`
	OrderID                       string                             `json:"order_id"`
	PayAmount                     string                             `json:"pay_amount"`
	PayCurrency                   string                             `json:"pay_currency"`
	PayerEmail                    *string                            `json:"payer_email,omitempty"`
	PayerID                       *string                            `json:"payer_id,omitempty"`
	PayerName                     *string                            `json:"payer_name,omitempty"`
	PaymentAddress                string                             `json:"payment_address"`
	ReceiveAmount                 string                             `json:"receive_amount"`
	ReceiveCurrency               string                             `json:"receive_currency"`
	Remark                        *string                            `json:"remark,omitempty"`
	Status                        PaymentRemainingCallbackDataStatus `json:"status"`
	TransactTime                  int64                              `json:"transact_time"`
	TxID                          string                             `json:"tx_id"`
	ValidTime                     *int32                             `json:"valid_time,omitempty"`
}

// QueryOrderDetails is generated from the canonical payment API contract.
type QueryOrderDetails struct {
	Items        *[]QueryOrderItem `json:"items,omitempty"`
	ShoppingCost *float64          `json:"shopping_cost,omitempty"`
	TaxCost      *float64          `json:"tax_cost,omitempty"`
}

// QueryOrderItem is generated from the canonical payment API contract.
type QueryOrderItem struct {
	ItemID        *string  `json:"item_id,omitempty"`
	ItemName      *string  `json:"item_name,omitempty"`
	ItemPrice     *float64 `json:"item_price,omitempty"`
	ItemQuantity  *int64   `json:"item_quantity,omitempty"`
	PriceCurrency *string  `json:"price_currency,omitempty"`
}

// QueryOrderRequest is generated from the canonical payment API contract.
type QueryOrderRequest struct {
	CregisID string `json:"cregis_id"`
}

// QueryOrderResponseRefundRequested is the documented value set for QueryOrderResponse.RefundRequested.
type QueryOrderResponseRefundRequested string

const (
	QueryOrderResponseRefundRequestedYes QueryOrderResponseRefundRequested = "yes"
	QueryOrderResponseRefundRequestedNo  QueryOrderResponseRefundRequested = "no"
)

// QueryOrderResponseSettlementStatus is the documented value set for QueryOrderResponse.SettlementStatus.
type QueryOrderResponseSettlementStatus string

const (
	QueryOrderResponseSettlementStatusUnsettled QueryOrderResponseSettlementStatus = "unsettled"
	QueryOrderResponseSettlementStatusSettling  QueryOrderResponseSettlementStatus = "settling"
	QueryOrderResponseSettlementStatusSettled   QueryOrderResponseSettlementStatus = "settled"
)

// QueryOrderResponseSettlementType is the documented value set for QueryOrderResponse.SettlementType.
type QueryOrderResponseSettlementType string

const (
	QueryOrderResponseSettlementTypeEmpty  QueryOrderResponseSettlementType = ""
	QueryOrderResponseSettlementTypeSystem QueryOrderResponseSettlementType = "system"
	QueryOrderResponseSettlementTypeManual QueryOrderResponseSettlementType = "manual"
)

// QueryOrderResponseStatus is the documented value set for QueryOrderResponse.Status.
type QueryOrderResponseStatus string

const (
	QueryOrderResponseStatusNew         QueryOrderResponseStatus = "new"
	QueryOrderResponseStatusPaid        QueryOrderResponseStatus = "paid"
	QueryOrderResponseStatusExpired     QueryOrderResponseStatus = "expired"
	QueryOrderResponseStatusPaidOver    QueryOrderResponseStatus = "paid_over"
	QueryOrderResponseStatusPaidPartial QueryOrderResponseStatus = "paid_partial"
	QueryOrderResponseStatusCanceled    QueryOrderResponseStatus = "canceled"
)

// QueryOrderResponse is generated from the canonical payment API contract.
type QueryOrderResponse struct {
	CancelTime        *int64                              `json:"cancel_time,omitempty"`
	CreatedTime       *int64                              `json:"created_time,omitempty"`
	CregisID          *string                             `json:"cregis_id,omitempty"`
	OrderAmount       *string                             `json:"order_amount,omitempty"`
	OrderCurrency     *string                             `json:"order_currency,omitempty"`
	OrderDetails      *QueryOrderDetails                  `json:"order_details,omitempty"`
	OrderID           *string                             `json:"order_id,omitempty"`
	PayerEmail        *string                             `json:"payer_email,omitempty"`
	PayerID           *string                             `json:"payer_id,omitempty"`
	PayerName         *string                             `json:"payer_name,omitempty"`
	PaymentDetail     *[]PaymentDetail                    `json:"payment_detail,omitempty"`
	PaymentInfo       []PaymentInfo                       `json:"payment_info,omitempty"`
	RefundData        *RefundData                         `json:"refund_data,omitempty"`
	RefundRequested   *QueryOrderResponseRefundRequested  `json:"refund_requested,omitempty"`
	Remark            *string                             `json:"remark,omitempty"`
	SettlementDetails *SettlementDetails                  `json:"settlement_details,omitempty"`
	SettlementStatus  *QueryOrderResponseSettlementStatus `json:"settlement_status,omitempty"`
	SettlementType    *QueryOrderResponseSettlementType   `json:"settlement_type,omitempty"`
	Status            *QueryOrderResponseStatus           `json:"status,omitempty"`
	SubMerchant       *QuerySubMerchant                   `json:"sub_merchant,omitempty"`
	TransactTime      *int64                              `json:"transact_time,omitempty"`
	ValidTime         *int32                              `json:"valid_time,omitempty"`
}

// QuerySubMerchant is generated from the canonical payment API contract.
type QuerySubMerchant struct {
	SubMerchantID   *string `json:"sub_merchant_id,omitempty"`
	SubMerchantName *string `json:"sub_merchant_name,omitempty"`
}

// RefundDataRefundStatus is the documented value set for RefundData.RefundStatus.
type RefundDataRefundStatus int

const (
	RefundDataRefundStatus0 RefundDataRefundStatus = 0
	RefundDataRefundStatus1 RefundDataRefundStatus = 1
	RefundDataRefundStatus2 RefundDataRefundStatus = 2
)

// RefundDataType is the documented value set for RefundData.Type.
type RefundDataType int

const (
	RefundDataType1 RefundDataType = 1
	RefundDataType2 RefundDataType = 2
)

// RefundData is generated from the canonical payment API contract.
type RefundData struct {
	ActualRefundAmount *string                 `json:"actual_refund_amount,omitempty"`
	CregisID           *string                 `json:"cregis_id,omitempty"`
	Network            *string                 `json:"network,omitempty"`
	RecipientAddress   *string                 `json:"recipient_address,omitempty"`
	RecipientEmail     *string                 `json:"recipient_email,omitempty"`
	RecipientID        *string                 `json:"recipient_id,omitempty"`
	RecipientName      *string                 `json:"recipient_name,omitempty"`
	ReferenceID        *string                 `json:"reference_id,omitempty"`
	RefundAmount       *string                 `json:"refund_amount,omitempty"`
	RefundCreatedTime  *int64                  `json:"refund_created_time,omitempty"`
	RefundFee          *string                 `json:"refund_fee,omitempty"`
	RefundID           *string                 `json:"refund_id,omitempty"`
	RefundStatus       *RefundDataRefundStatus `json:"refund_status,omitempty"`
	RefundTransactTime *int64                  `json:"refund_transact_time,omitempty"`
	RefundTxID         *string                 `json:"refund_tx_id,omitempty"`
	Token              *string                 `json:"token,omitempty"`
	Type               *RefundDataType         `json:"type,omitempty"`
}

// SettlementDetails is generated from the canonical payment API contract.
type SettlementDetails struct {
	CreatedTime                 *int64                 `json:"created_time,omitempty"`
	FromAddress                 *string                `json:"from_address,omitempty"`
	ID                          *string                `json:"id,omitempty"`
	OrderCount                  *int64                 `json:"order_count,omitempty"`
	OrderSettlementDetail       *OrderSettlementDetail `json:"order_settlement_detail,omitempty"`
	SettlementCurrency          *string                `json:"settlement_currency,omitempty"`
	ToAddress                   *string                `json:"to_address,omitempty"`
	TotalActualSettlementAmount *string                `json:"total_actual_settlement_amount,omitempty"`
	TotalSettlementAmount       *string                `json:"total_settlement_amount,omitempty"`
	TotalSettlementFee          *string                `json:"total_settlement_fee,omitempty"`
	TxID                        *string                `json:"tx_id,omitempty"`
}

// SubMerchant is generated from the canonical payment API contract.
type SubMerchant struct {
	SubMerchantID   *string `json:"sub_merchant_id,omitempty"`
	SubMerchantName *string `json:"sub_merchant_name,omitempty"`
}
