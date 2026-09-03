// Code generated from the canonical Cregis OpenAPI specification. DO NOT EDIT.
package waas

import (
	"bytes"
	"encoding/json"
	"fmt"
	"regexp"
	"unicode/utf8"
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

// UnmarshalJSON validates the declared AddressBalanceRequest wire contract.
func (model *AddressBalanceRequest) UnmarshalJSON(data []byte) error {
	type plain AddressBalanceRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"currency"}, nil, map[string]struct{}{
		"address":         {},
		"currency":        {},
		"maximum_balance": {},
		"minimum_balance": {},
		"page_num":        {},
		"page_size":       {},
	}, true); err != nil {
		return fmt.Errorf("decode AddressBalanceRequest: %w", err)
	}
	decoded := AddressBalanceRequest(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared AddressBalanceV2Request wire contract.
func (model *AddressBalanceV2Request) UnmarshalJSON(data []byte) error {
	type plain AddressBalanceV2Request
	var value plain
	if err := unmarshalModel(data, &value, []string{"address"}, nil, map[string]struct{}{
		"address":         {},
		"currency":        {},
		"maximum_balance": {},
		"minimum_balance": {},
		"page_num":        {},
		"page_size":       {},
	}, true); err != nil {
		return fmt.Errorf("decode AddressBalanceV2Request: %w", err)
	}
	decoded := AddressBalanceV2Request(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode AddressBalanceV2Request: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for AddressBalanceV2Request.
func (model AddressBalanceV2Request) Validate() error {
	if model.PageSize != nil {
		if *model.PageSize > 100 {
			return fmt.Errorf("page_size violates OpenAPI maximum")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared AddressDepositCallbackNotification wire contract.
func (model *AddressDepositCallbackNotification) UnmarshalJSON(data []byte) error {
	type plain AddressDepositCallbackNotification
	var value plain
	if err := unmarshalModel(data, &value, []string{"pid", "cid", "chain_id", "token_id", "currency", "address", "amount", "status", "txid", "nonce", "timestamp", "sign"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode AddressDepositCallbackNotification: %w", err)
	}
	decoded := AddressDepositCallbackNotification(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode AddressDepositCallbackNotification: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for AddressDepositCallbackNotification.
func (model AddressDepositCallbackNotification) Validate() error {
	switch model.Status {
	case "1", "2":
	default:
		return fmt.Errorf("status must be one of the documented values")
	}
	return nil
}

// UnmarshalJSON validates the declared AddressUpdateRequest wire contract.
func (model *AddressUpdateRequest) UnmarshalJSON(data []byte) error {
	type plain AddressUpdateRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"address"}, nil, map[string]struct{}{
		"address":      {},
		"alias":        {},
		"callback_url": {},
		"status":       {},
	}, true); err != nil {
		return fmt.Errorf("decode AddressUpdateRequest: %w", err)
	}
	decoded := AddressUpdateRequest(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode AddressUpdateRequest: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for AddressUpdateRequest.
func (model AddressUpdateRequest) Validate() error {
	if model.Status != nil {
		switch *model.Status {
		case "0", "1":
		default:
			return fmt.Errorf("status must be one of the documented values")
		}
	}
	if !((model.Alias != nil) || (model.CallbackURL != nil) || (model.Status != nil)) {
		return fmt.Errorf("at least one OpenAPI anyOf required-field group must be present")
	}
	return nil
}

// UnmarshalJSON validates the declared BalanceCollectRequest wire contract.
func (model *BalanceCollectRequest) UnmarshalJSON(data []byte) error {
	type plain BalanceCollectRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"currency", "from_address", "to_address"}, nil, map[string]struct{}{
		"amount":       {},
		"currency":     {},
		"from_address": {},
		"to_address":   {},
	}, true); err != nil {
		return fmt.Errorf("decode BalanceCollectRequest: %w", err)
	}
	decoded := BalanceCollectRequest(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared BatchGenerateAddressRequest wire contract.
func (model *BatchGenerateAddressRequest) UnmarshalJSON(data []byte) error {
	type plain BatchGenerateAddressRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"chain_id", "number"}, nil, map[string]struct{}{
		"alias":        {},
		"callback_url": {},
		"chain_id":     {},
		"number":       {},
	}, true); err != nil {
		return fmt.Errorf("decode BatchGenerateAddressRequest: %w", err)
	}
	decoded := BatchGenerateAddressRequest(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode BatchGenerateAddressRequest: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for BatchGenerateAddressRequest.
func (model BatchGenerateAddressRequest) Validate() error {
	if model.Alias != nil {
		if utf8.RuneCountInString(*model.Alias) < 1 {
			return fmt.Errorf("alias is shorter than OpenAPI minLength")
		}
		if utf8.RuneCountInString(*model.Alias) > 40 {
			return fmt.Errorf("alias is longer than OpenAPI maxLength")
		}
	}
	if matched, err := regexp.MatchString("^(?:[1-9]|[1-9][0-9]|100)$", model.Number); err != nil || !matched {
		return fmt.Errorf("number does not match its OpenAPI pattern")
	}
	return nil
}

// UnmarshalJSON validates the declared CheckAddressLegalityRequest wire contract.
func (model *CheckAddressLegalityRequest) UnmarshalJSON(data []byte) error {
	type plain CheckAddressLegalityRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"address", "chain_id"}, nil, map[string]struct{}{
		"address":  {},
		"chain_id": {},
	}, true); err != nil {
		return fmt.Errorf("decode CheckAddressLegalityRequest: %w", err)
	}
	decoded := CheckAddressLegalityRequest(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared GenerateAddressRequest wire contract.
func (model *GenerateAddressRequest) UnmarshalJSON(data []byte) error {
	type plain GenerateAddressRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"chain_id"}, nil, map[string]struct{}{
		"alias":        {},
		"callback_url": {},
		"chain_id":     {},
	}, true); err != nil {
		return fmt.Errorf("decode GenerateAddressRequest: %w", err)
	}
	decoded := GenerateAddressRequest(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared PayoutCallbackNotification wire contract.
func (model *PayoutCallbackNotification) UnmarshalJSON(data []byte) error {
	type plain PayoutCallbackNotification
	var value plain
	if err := unmarshalModel(data, &value, []string{"pid", "cid", "chain_id", "token_id", "currency", "address", "amount", "third_party_id", "status", "nonce", "timestamp", "sign"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode PayoutCallbackNotification: %w", err)
	}
	decoded := PayoutCallbackNotification(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode PayoutCallbackNotification: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for PayoutCallbackNotification.
func (model PayoutCallbackNotification) Validate() error {
	switch model.Status {
	case 2, 4, 6, 7:
	default:
		return fmt.Errorf("status must be one of the documented values")
	}
	return nil
}

// UnmarshalJSON validates the declared PayoutExternalVerificationCallbackNotification wire contract.
func (model *PayoutExternalVerificationCallbackNotification) UnmarshalJSON(data []byte) error {
	type plain PayoutExternalVerificationCallbackNotification
	var value plain
	if err := unmarshalModel(data, &value, []string{"pid", "cid", "third_party_id", "chain_id", "token_id", "from_address", "to_address", "amount", "nonce", "timestamp", "sign"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode PayoutExternalVerificationCallbackNotification: %w", err)
	}
	decoded := PayoutExternalVerificationCallbackNotification(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared PayoutRequest wire contract.
func (model *PayoutRequest) UnmarshalJSON(data []byte) error {
	type plain PayoutRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"currency", "to_address", "amount", "third_party_id"}, nil, map[string]struct{}{
		"amount":         {},
		"callback_url":   {},
		"currency":       {},
		"from_address":   {},
		"memo":           {},
		"remark":         {},
		"third_party_id": {},
		"to_address":     {},
		"wallet_id":      {},
	}, true); err != nil {
		return fmt.Errorf("decode PayoutRequest: %w", err)
	}
	decoded := PayoutRequest(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared PayoutV1Request wire contract.
func (model *PayoutV1Request) UnmarshalJSON(data []byte) error {
	type plain PayoutV1Request
	var value plain
	if err := unmarshalModel(data, &value, []string{"currency", "address", "amount", "third_party_id"}, nil, map[string]struct{}{
		"address":        {},
		"amount":         {},
		"callback_url":   {},
		"currency":       {},
		"memo":           {},
		"remark":         {},
		"third_party_id": {},
	}, true); err != nil {
		return fmt.Errorf("decode PayoutV1Request: %w", err)
	}
	decoded := PayoutV1Request(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared QueryPayoutRequest wire contract.
func (model *QueryPayoutRequest) UnmarshalJSON(data []byte) error {
	type plain QueryPayoutRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"cid"}, nil, map[string]struct{}{
		"cid": {},
	}, true); err != nil {
		return fmt.Errorf("decode QueryPayoutRequest: %w", err)
	}
	decoded := QueryPayoutRequest(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared QueryPayoutResponse wire contract.
func (model *QueryPayoutResponse) UnmarshalJSON(data []byte) error {
	type plain QueryPayoutResponse
	var value plain
	if err := unmarshalModel(data, &value, []string{}, nil, nil, false); err != nil {
		return fmt.Errorf("decode QueryPayoutResponse: %w", err)
	}
	decoded := QueryPayoutResponse(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode QueryPayoutResponse: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for QueryPayoutResponse.
func (model QueryPayoutResponse) Validate() error {
	if model.Status != nil {
		switch *model.Status {
		case 0, 1, 2, 3, 4, 5, 6, 7:
		default:
			return fmt.Errorf("status must be one of the documented values")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared QueryWithdrawalRequest wire contract.
func (model *QueryWithdrawalRequest) UnmarshalJSON(data []byte) error {
	type plain QueryWithdrawalRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"cid"}, nil, map[string]struct{}{
		"cid": {},
	}, true); err != nil {
		return fmt.Errorf("decode QueryWithdrawalRequest: %w", err)
	}
	decoded := QueryWithdrawalRequest(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared QueryWithdrawalResponse wire contract.
func (model *QueryWithdrawalResponse) UnmarshalJSON(data []byte) error {
	type plain QueryWithdrawalResponse
	var value plain
	if err := unmarshalModel(data, &value, []string{}, nil, nil, false); err != nil {
		return fmt.Errorf("decode QueryWithdrawalResponse: %w", err)
	}
	decoded := QueryWithdrawalResponse(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode QueryWithdrawalResponse: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for QueryWithdrawalResponse.
func (model QueryWithdrawalResponse) Validate() error {
	if model.Status != nil {
		switch *model.Status {
		case 0, 1, 2, 3, 4, 5, 6, 7:
		default:
			return fmt.Errorf("status must be one of the documented values")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared TradeRecord wire contract.
func (model *TradeRecord) UnmarshalJSON(data []byte) error {
	type plain TradeRecord
	var value plain
	if err := unmarshalModel(data, &value, []string{}, nil, nil, false); err != nil {
		return fmt.Errorf("decode TradeRecord: %w", err)
	}
	decoded := TradeRecord(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode TradeRecord: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for TradeRecord.
func (model TradeRecord) Validate() error {
	if model.Status != nil {
		switch *model.Status {
		case 0, 1, 2:
		default:
			return fmt.Errorf("status must be one of the documented values")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared TradeRecordQueryRequest wire contract.
func (model *TradeRecordQueryRequest) UnmarshalJSON(data []byte) error {
	type plain TradeRecordQueryRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{}, nil, map[string]struct{}{
		"blocktime_end":   {},
		"blocktime_start": {},
		"business_type":   {},
		"chain_id":        {},
		"cid":             {},
		"page_num":        {},
		"page_size":       {},
		"status":          {},
		"token_id":        {},
		"trade_type":      {},
		"tx_id":           {},
	}, true); err != nil {
		return fmt.Errorf("decode TradeRecordQueryRequest: %w", err)
	}
	decoded := TradeRecordQueryRequest(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode TradeRecordQueryRequest: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for TradeRecordQueryRequest.
func (model TradeRecordQueryRequest) Validate() error {
	if model.BusinessType != nil {
		switch *model.BusinessType {
		case 0, 2, 3, 4, 5:
		default:
			return fmt.Errorf("business_type must be one of the documented values")
		}
	}
	if model.Status != nil {
		switch *model.Status {
		case 0, 1, 2:
		default:
			return fmt.Errorf("status must be one of the documented values")
		}
	}
	if model.TradeType != nil {
		switch *model.TradeType {
		case 1, 2:
		default:
			return fmt.Errorf("trade_type must be one of the documented values")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared TradeRecordQueryResponse wire contract.
func (model *TradeRecordQueryResponse) UnmarshalJSON(data []byte) error {
	type plain TradeRecordQueryResponse
	var value plain
	if err := unmarshalModel(data, &value, []string{}, nil, nil, false); err != nil {
		return fmt.Errorf("decode TradeRecordQueryResponse: %w", err)
	}
	decoded := TradeRecordQueryResponse(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode TradeRecordQueryResponse: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for TradeRecordQueryResponse.
func (model TradeRecordQueryResponse) Validate() error {
	for index := range model.Rows {
		if err := (model.Rows)[index].Validate(); err != nil {
			return fmt.Errorf("rows[%d]: %w", index, err)
		}
	}
	return nil
}

// UnmarshalJSON validates the declared ValidateAddressRequest wire contract.
func (model *ValidateAddressRequest) UnmarshalJSON(data []byte) error {
	type plain ValidateAddressRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"address", "chain_id"}, nil, map[string]struct{}{
		"address":  {},
		"chain_id": {},
	}, true); err != nil {
		return fmt.Errorf("decode ValidateAddressRequest: %w", err)
	}
	decoded := ValidateAddressRequest(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared WithdrawalCallbackNotification wire contract.
func (model *WithdrawalCallbackNotification) UnmarshalJSON(data []byte) error {
	type plain WithdrawalCallbackNotification
	var value plain
	if err := unmarshalModel(data, &value, []string{"pid", "cid", "chain_id", "token_id", "currency", "from_address", "to_address", "amount", "third_party_id", "status", "nonce", "timestamp", "sign"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode WithdrawalCallbackNotification: %w", err)
	}
	decoded := WithdrawalCallbackNotification(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode WithdrawalCallbackNotification: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for WithdrawalCallbackNotification.
func (model WithdrawalCallbackNotification) Validate() error {
	switch model.Status {
	case 2, 4, 6, 7:
	default:
		return fmt.Errorf("status must be one of the documented values")
	}
	return nil
}

// UnmarshalJSON validates the declared WithdrawalRequest wire contract.
func (model *WithdrawalRequest) UnmarshalJSON(data []byte) error {
	type plain WithdrawalRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"currency", "from_address", "to_address", "amount", "third_party_id"}, nil, map[string]struct{}{
		"amount":         {},
		"callback_url":   {},
		"currency":       {},
		"from_address":   {},
		"memo":           {},
		"remark":         {},
		"third_party_id": {},
		"to_address":     {},
	}, true); err != nil {
		return fmt.Errorf("decode WithdrawalRequest: %w", err)
	}
	decoded := WithdrawalRequest(value)
	*model = decoded
	return nil
}
