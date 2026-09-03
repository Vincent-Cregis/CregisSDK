// Code generated from the canonical Cregis OpenAPI specification. DO NOT EDIT.
package payment

import (
	"bytes"
	"encoding/json"
	"fmt"
)

func unmarshalModel(data []byte, destination any, required []string, nullable map[string]struct{}, allowed map[string]struct{}, rejectUnknown bool) error {
	var fields map[string]json.RawMessage
	if err := json.Unmarshal(data, &fields); err != nil {
		return err
	}
	if fields == nil {
		return fmt.Errorf("model must be a JSON object")
	}
	if rejectUnknown {
		for field := range fields {
			if _, ok := allowed[field]; !ok {
				return fmt.Errorf("unknown field %q", field)
			}
		}
	}
	for _, field := range required {
		raw, ok := fields[field]
		if !ok {
			return fmt.Errorf("required field %q is missing", field)
		}
		if bytes.Equal(bytes.TrimSpace(raw), []byte("null")) {
			if _, allowed := nullable[field]; !allowed {
				return fmt.Errorf("required field %q must not be null", field)
			}
		}
	}
	return json.Unmarshal(data, destination)
}

// UnmarshalJSON validates the declared CreateOrderRequest wire contract.
func (model *CreateOrderRequest) UnmarshalJSON(data []byte) error {
	type plain CreateOrderRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"order_amount", "order_currency", "order_id", "payer_id", "success_url", "cancel_url"}, nil, map[string]struct{}{
		"accept_over_payment":      {},
		"accept_partial_payment":   {},
		"callback_url":             {},
		"cancel_url":               {},
		"language":                 {},
		"order_amount":             {},
		"order_currency":           {},
		"order_details":            {},
		"order_id":                 {},
		"overpaid_tolerance":       {},
		"payer_email":              {},
		"payer_id":                 {},
		"payer_name":               {},
		"remark":                   {},
		"stablecoin_realtime_rate": {},
		"sub_merchant":             {},
		"success_url":              {},
		"tokens":                   {},
		"underpaid_tolerance":      {},
		"valid_time":               {},
	}, true); err != nil {
		return fmt.Errorf("decode CreateOrderRequest: %w", err)
	}
	decoded := CreateOrderRequest(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode CreateOrderRequest: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for CreateOrderRequest.
func (model CreateOrderRequest) Validate() error {
	if model.AcceptOverPayment != nil {
		switch *model.AcceptOverPayment {
		case "true", "false":
		default:
			return fmt.Errorf("accept_over_payment must be one of the documented values")
		}
	}
	if model.AcceptPartialPayment != nil {
		switch *model.AcceptPartialPayment {
		case "true", "false":
		default:
			return fmt.Errorf("accept_partial_payment must be one of the documented values")
		}
	}
	if model.Language != nil {
		switch *model.Language {
		case "en", "tc", "sc":
		default:
			return fmt.Errorf("language must be one of the documented values")
		}
	}
	if model.StablecoinRealtimeRate != nil {
		switch *model.StablecoinRealtimeRate {
		case "true", "false":
		default:
			return fmt.Errorf("stablecoin_realtime_rate must be one of the documented values")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared OrderDetails wire contract.
func (model *OrderDetails) UnmarshalJSON(data []byte) error {
	type plain OrderDetails
	var value plain
	if err := unmarshalModel(data, &value, []string{}, nil, map[string]struct{}{
		"items":         {},
		"shopping_cost": {},
		"tax_cost":      {},
	}, true); err != nil {
		return fmt.Errorf("decode OrderDetails: %w", err)
	}
	decoded := OrderDetails(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared OrderItem wire contract.
func (model *OrderItem) UnmarshalJSON(data []byte) error {
	type plain OrderItem
	var value plain
	if err := unmarshalModel(data, &value, []string{}, nil, map[string]struct{}{
		"item_id":        {},
		"item_name":      {},
		"item_price":     {},
		"item_quantity":  {},
		"price_currency": {},
	}, true); err != nil {
		return fmt.Errorf("decode OrderItem: %w", err)
	}
	decoded := OrderItem(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared OrderSettlementDetail wire contract.
func (model *OrderSettlementDetail) UnmarshalJSON(data []byte) error {
	type plain OrderSettlementDetail
	var value plain
	if err := unmarshalModel(data, &value, []string{}, nil, nil, false); err != nil {
		return fmt.Errorf("decode OrderSettlementDetail: %w", err)
	}
	decoded := OrderSettlementDetail(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode OrderSettlementDetail: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for OrderSettlementDetail.
func (model OrderSettlementDetail) Validate() error {
	if model.Status != nil {
		switch *model.Status {
		case "unsettled", "settling", "settled":
		default:
			return fmt.Errorf("status must be one of the documented values")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared PaymentCallbackEnvelope wire contract.
func (model *PaymentCallbackEnvelope) UnmarshalJSON(data []byte) error {
	type plain PaymentCallbackEnvelope
	var value plain
	if err := unmarshalModel(data, &value, []string{"pid", "nonce", "timestamp", "sign", "event_name", "event_type", "data"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode PaymentCallbackEnvelope: %w", err)
	}
	decoded := PaymentCallbackEnvelope(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode PaymentCallbackEnvelope: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for PaymentCallbackEnvelope.
func (model PaymentCallbackEnvelope) Validate() error {
	switch model.EventName {
	case "order":
	default:
		return fmt.Errorf("event_name must be one of the documented values")
	}
	switch model.EventType {
	case "expired", "paid", "paid_partial", "paid_over", "refunded", "paid_remain":
	default:
		return fmt.Errorf("event_type must be one of the documented values")
	}
	return nil
}

// UnmarshalJSON validates the declared PaymentCompletedCallbackData wire contract.
func (model *PaymentCompletedCallbackData) UnmarshalJSON(data []byte) error {
	type plain PaymentCompletedCallbackData
	var value plain
	if err := unmarshalModel(data, &value, []string{"cregis_id", "order_id", "status", "payment_address", "receive_amount", "receive_currency", "pay_amount", "pay_currency", "exchange_rate", "tx_id", "transact_time"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode PaymentCompletedCallbackData: %w", err)
	}
	decoded := PaymentCompletedCallbackData(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode PaymentCompletedCallbackData: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for PaymentCompletedCallbackData.
func (model PaymentCompletedCallbackData) Validate() error {
	switch model.Status {
	case "new", "paid", "expired", "paid_over", "paid_partial", "canceled":
	default:
		return fmt.Errorf("status must be one of the documented values")
	}
	return nil
}

// UnmarshalJSON validates the declared PaymentExpiredCallbackData wire contract.
func (model *PaymentExpiredCallbackData) UnmarshalJSON(data []byte) error {
	type plain PaymentExpiredCallbackData
	var value plain
	if err := unmarshalModel(data, &value, []string{"cregis_id", "order_id", "status"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode PaymentExpiredCallbackData: %w", err)
	}
	decoded := PaymentExpiredCallbackData(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode PaymentExpiredCallbackData: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for PaymentExpiredCallbackData.
func (model PaymentExpiredCallbackData) Validate() error {
	switch model.Status {
	case "new", "paid", "expired", "paid_over", "paid_partial", "canceled":
	default:
		return fmt.Errorf("status must be one of the documented values")
	}
	return nil
}

// UnmarshalJSON validates the declared PaymentRefundedCallbackData wire contract.
func (model *PaymentRefundedCallbackData) UnmarshalJSON(data []byte) error {
	type plain PaymentRefundedCallbackData
	var value plain
	if err := unmarshalModel(data, &value, []string{"cregis_id", "order_id", "status", "payment_address", "receive_amount", "receive_currency", "pay_amount", "pay_currency", "exchange_rate", "tx_id", "transact_time"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode PaymentRefundedCallbackData: %w", err)
	}
	decoded := PaymentRefundedCallbackData(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode PaymentRefundedCallbackData: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for PaymentRefundedCallbackData.
func (model PaymentRefundedCallbackData) Validate() error {
	if model.RefundRequested != nil {
		switch *model.RefundRequested {
		case "yes", "no":
		default:
			return fmt.Errorf("refund_requested must be one of the documented values")
		}
	}
	if model.RefundStatus != nil {
		switch *model.RefundStatus {
		case 0, 1, 2:
		default:
			return fmt.Errorf("refund_status must be one of the documented values")
		}
	}
	switch model.Status {
	case "new", "paid", "expired", "paid_over", "paid_partial", "canceled":
	default:
		return fmt.Errorf("status must be one of the documented values")
	}
	if model.Type != nil {
		switch *model.Type {
		case 0, 1:
		default:
			return fmt.Errorf("type must be one of the documented values")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared PaymentRemainingCallbackData wire contract.
func (model *PaymentRemainingCallbackData) UnmarshalJSON(data []byte) error {
	type plain PaymentRemainingCallbackData
	var value plain
	if err := unmarshalModel(data, &value, []string{"cregis_id", "order_id", "status", "payment_address", "receive_amount", "receive_currency", "pay_amount", "pay_currency", "exchange_rate", "tx_id", "transact_time"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode PaymentRemainingCallbackData: %w", err)
	}
	decoded := PaymentRemainingCallbackData(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode PaymentRemainingCallbackData: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for PaymentRemainingCallbackData.
func (model PaymentRemainingCallbackData) Validate() error {
	switch model.Status {
	case "new", "paid", "expired", "paid_over", "paid_partial", "canceled":
	default:
		return fmt.Errorf("status must be one of the documented values")
	}
	return nil
}

// UnmarshalJSON validates the declared QueryOrderRequest wire contract.
func (model *QueryOrderRequest) UnmarshalJSON(data []byte) error {
	type plain QueryOrderRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"cregis_id"}, nil, map[string]struct{}{
		"cregis_id": {},
	}, true); err != nil {
		return fmt.Errorf("decode QueryOrderRequest: %w", err)
	}
	decoded := QueryOrderRequest(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared QueryOrderResponse wire contract.
func (model *QueryOrderResponse) UnmarshalJSON(data []byte) error {
	type plain QueryOrderResponse
	var value plain
	if err := unmarshalModel(data, &value, []string{}, nil, nil, false); err != nil {
		return fmt.Errorf("decode QueryOrderResponse: %w", err)
	}
	decoded := QueryOrderResponse(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode QueryOrderResponse: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for QueryOrderResponse.
func (model QueryOrderResponse) Validate() error {
	if model.RefundData != nil {
		if err := model.RefundData.Validate(); err != nil {
			return fmt.Errorf("refund_data: %w", err)
		}
	}
	if model.RefundRequested != nil {
		switch *model.RefundRequested {
		case "yes", "no":
		default:
			return fmt.Errorf("refund_requested must be one of the documented values")
		}
	}
	if model.SettlementDetails != nil {
		if err := model.SettlementDetails.Validate(); err != nil {
			return fmt.Errorf("settlement_details: %w", err)
		}
	}
	if model.SettlementStatus != nil {
		switch *model.SettlementStatus {
		case "unsettled", "settling", "settled":
		default:
			return fmt.Errorf("settlement_status must be one of the documented values")
		}
	}
	if model.SettlementType != nil {
		switch *model.SettlementType {
		case "", "system", "manual":
		default:
			return fmt.Errorf("settlement_type must be one of the documented values")
		}
	}
	if model.Status != nil {
		switch *model.Status {
		case "new", "paid", "expired", "paid_over", "paid_partial", "canceled":
		default:
			return fmt.Errorf("status must be one of the documented values")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared RefundData wire contract.
func (model *RefundData) UnmarshalJSON(data []byte) error {
	type plain RefundData
	var value plain
	if err := unmarshalModel(data, &value, []string{}, nil, nil, false); err != nil {
		return fmt.Errorf("decode RefundData: %w", err)
	}
	decoded := RefundData(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode RefundData: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for RefundData.
func (model RefundData) Validate() error {
	if model.RefundStatus != nil {
		switch *model.RefundStatus {
		case 0, 1, 2:
		default:
			return fmt.Errorf("refund_status must be one of the documented values")
		}
	}
	if model.Type != nil {
		switch *model.Type {
		case 1, 2:
		default:
			return fmt.Errorf("type must be one of the documented values")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared SettlementDetails wire contract.
func (model *SettlementDetails) UnmarshalJSON(data []byte) error {
	type plain SettlementDetails
	var value plain
	if err := unmarshalModel(data, &value, []string{}, nil, nil, false); err != nil {
		return fmt.Errorf("decode SettlementDetails: %w", err)
	}
	decoded := SettlementDetails(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode SettlementDetails: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for SettlementDetails.
func (model SettlementDetails) Validate() error {
	if model.OrderSettlementDetail != nil {
		if err := model.OrderSettlementDetail.Validate(); err != nil {
			return fmt.Errorf("order_settlement_detail: %w", err)
		}
	}
	return nil
}

// UnmarshalJSON validates the declared SubMerchant wire contract.
func (model *SubMerchant) UnmarshalJSON(data []byte) error {
	type plain SubMerchant
	var value plain
	if err := unmarshalModel(data, &value, []string{}, nil, map[string]struct{}{
		"sub_merchant_id":   {},
		"sub_merchant_name": {},
	}, true); err != nil {
		return fmt.Errorf("decode SubMerchant: %w", err)
	}
	decoded := SubMerchant(value)
	*model = decoded
	return nil
}
