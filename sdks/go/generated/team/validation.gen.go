// Code generated from the canonical Cregis OpenAPI specification. DO NOT EDIT.
package team

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

// UnmarshalJSON validates the declared ListTeamWalletAddressesRequest wire contract.
func (model *ListTeamWalletAddressesRequest) UnmarshalJSON(data []byte) error {
	type plain ListTeamWalletAddressesRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"wallet_id", "chain_id"}, nil, map[string]struct{}{
		"chain_id":  {},
		"page_num":  {},
		"page_size": {},
		"wallet_id": {},
	}, true); err != nil {
		return fmt.Errorf("decode ListTeamWalletAddressesRequest: %w", err)
	}
	decoded := ListTeamWalletAddressesRequest(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode ListTeamWalletAddressesRequest: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for ListTeamWalletAddressesRequest.
func (model ListTeamWalletAddressesRequest) Validate() error {
	if model.PageNum != nil {
		if *model.PageNum < 1 {
			return fmt.Errorf("page_num violates OpenAPI minimum")
		}
	}
	if model.PageSize != nil {
		if *model.PageSize < 1 {
			return fmt.Errorf("page_size violates OpenAPI minimum")
		}
		if *model.PageSize > 100 {
			return fmt.Errorf("page_size violates OpenAPI maximum")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared ListTeamWalletAddressesResponse wire contract.
func (model *ListTeamWalletAddressesResponse) UnmarshalJSON(data []byte) error {
	type plain ListTeamWalletAddressesResponse
	var value plain
	if err := unmarshalModel(data, &value, []string{"pageNum", "pageSize", "total", "rows"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode ListTeamWalletAddressesResponse: %w", err)
	}
	decoded := ListTeamWalletAddressesResponse(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode ListTeamWalletAddressesResponse: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for ListTeamWalletAddressesResponse.
func (model ListTeamWalletAddressesResponse) Validate() error {
	for index := range model.Rows {
		if err := (model.Rows)[index].Validate(); err != nil {
			return fmt.Errorf("rows[%d]: %w", index, err)
		}
	}
	return nil
}

// UnmarshalJSON validates the declared ListTeamWalletsRequest wire contract.
func (model *ListTeamWalletsRequest) UnmarshalJSON(data []byte) error {
	type plain ListTeamWalletsRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{}, nil, map[string]struct{}{
		"page_num":    {},
		"page_size":   {},
		"wallet_type": {},
	}, true); err != nil {
		return fmt.Errorf("decode ListTeamWalletsRequest: %w", err)
	}
	decoded := ListTeamWalletsRequest(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode ListTeamWalletsRequest: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for ListTeamWalletsRequest.
func (model ListTeamWalletsRequest) Validate() error {
	if model.PageNum != nil {
		if *model.PageNum < 1 {
			return fmt.Errorf("page_num violates OpenAPI minimum")
		}
	}
	if model.PageSize != nil {
		if *model.PageSize < 1 {
			return fmt.Errorf("page_size violates OpenAPI minimum")
		}
		if *model.PageSize > 100 {
			return fmt.Errorf("page_size violates OpenAPI maximum")
		}
	}
	if model.WalletType != nil {
		switch *model.WalletType {
		case "single_sign", "multi_sign":
		default:
			return fmt.Errorf("wallet_type must be one of the documented values")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared ListTeamWalletsResponse wire contract.
func (model *ListTeamWalletsResponse) UnmarshalJSON(data []byte) error {
	type plain ListTeamWalletsResponse
	var value plain
	if err := unmarshalModel(data, &value, []string{"pageNum", "pageSize", "total", "rows"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode ListTeamWalletsResponse: %w", err)
	}
	decoded := ListTeamWalletsResponse(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode ListTeamWalletsResponse: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for ListTeamWalletsResponse.
func (model ListTeamWalletsResponse) Validate() error {
	for index := range model.Rows {
		if err := (model.Rows)[index].Validate(); err != nil {
			return fmt.Errorf("rows[%d]: %w", index, err)
		}
	}
	return nil
}

// UnmarshalJSON validates the declared QueryTeamWalletAddressBalanceRequest wire contract.
func (model *QueryTeamWalletAddressBalanceRequest) UnmarshalJSON(data []byte) error {
	type plain QueryTeamWalletAddressBalanceRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"wallet_id"}, nil, map[string]struct{}{
		"address":         {},
		"chain_id":        {},
		"maximum_balance": {},
		"minimum_balance": {},
		"page_num":        {},
		"page_size":       {},
		"token_id":        {},
		"wallet_id":       {},
	}, true); err != nil {
		return fmt.Errorf("decode QueryTeamWalletAddressBalanceRequest: %w", err)
	}
	decoded := QueryTeamWalletAddressBalanceRequest(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode QueryTeamWalletAddressBalanceRequest: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for QueryTeamWalletAddressBalanceRequest.
func (model QueryTeamWalletAddressBalanceRequest) Validate() error {
	if model.PageNum != nil {
		if *model.PageNum < 1 {
			return fmt.Errorf("page_num violates OpenAPI minimum")
		}
	}
	if model.PageSize != nil {
		if *model.PageSize < 1 {
			return fmt.Errorf("page_size violates OpenAPI minimum")
		}
		if *model.PageSize > 100 {
			return fmt.Errorf("page_size violates OpenAPI maximum")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared QueryTeamWalletAddressBalanceResponse wire contract.
func (model *QueryTeamWalletAddressBalanceResponse) UnmarshalJSON(data []byte) error {
	type plain QueryTeamWalletAddressBalanceResponse
	var value plain
	if err := unmarshalModel(data, &value, []string{"pageNum", "pageSize", "total", "rows"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode QueryTeamWalletAddressBalanceResponse: %w", err)
	}
	decoded := QueryTeamWalletAddressBalanceResponse(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared QueryTeamWalletBalanceRequest wire contract.
func (model *QueryTeamWalletBalanceRequest) UnmarshalJSON(data []byte) error {
	type plain QueryTeamWalletBalanceRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"wallet_id"}, nil, map[string]struct{}{
		"chain_id":  {},
		"page_num":  {},
		"page_size": {},
		"token_id":  {},
		"wallet_id": {},
	}, true); err != nil {
		return fmt.Errorf("decode QueryTeamWalletBalanceRequest: %w", err)
	}
	decoded := QueryTeamWalletBalanceRequest(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode QueryTeamWalletBalanceRequest: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for QueryTeamWalletBalanceRequest.
func (model QueryTeamWalletBalanceRequest) Validate() error {
	if model.PageNum != nil {
		if *model.PageNum < 1 {
			return fmt.Errorf("page_num violates OpenAPI minimum")
		}
	}
	if model.PageSize != nil {
		if *model.PageSize < 1 {
			return fmt.Errorf("page_size violates OpenAPI minimum")
		}
		if *model.PageSize > 100 {
			return fmt.Errorf("page_size violates OpenAPI maximum")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared QueryTeamWalletBalanceResponse wire contract.
func (model *QueryTeamWalletBalanceResponse) UnmarshalJSON(data []byte) error {
	type plain QueryTeamWalletBalanceResponse
	var value plain
	if err := unmarshalModel(data, &value, []string{"pageNum", "pageSize", "total", "rows"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode QueryTeamWalletBalanceResponse: %w", err)
	}
	decoded := QueryTeamWalletBalanceResponse(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared QueryTeamWalletHistoryTransactionsRequest wire contract.
func (model *QueryTeamWalletHistoryTransactionsRequest) UnmarshalJSON(data []byte) error {
	type plain QueryTeamWalletHistoryTransactionsRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"wallet_id"}, nil, map[string]struct{}{
		"blocktime_end":      {},
		"blocktime_start":    {},
		"chain_id":           {},
		"page_num":           {},
		"page_size":          {},
		"token_id":           {},
		"transaction_status": {},
		"transaction_type":   {},
		"txid":               {},
		"wallet_id":          {},
	}, true); err != nil {
		return fmt.Errorf("decode QueryTeamWalletHistoryTransactionsRequest: %w", err)
	}
	decoded := QueryTeamWalletHistoryTransactionsRequest(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode QueryTeamWalletHistoryTransactionsRequest: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for QueryTeamWalletHistoryTransactionsRequest.
func (model QueryTeamWalletHistoryTransactionsRequest) Validate() error {
	if model.PageNum != nil {
		if *model.PageNum < 1 {
			return fmt.Errorf("page_num violates OpenAPI minimum")
		}
	}
	if model.PageSize != nil {
		if *model.PageSize < 1 {
			return fmt.Errorf("page_size violates OpenAPI minimum")
		}
		if *model.PageSize > 100 {
			return fmt.Errorf("page_size violates OpenAPI maximum")
		}
	}
	if model.TransactionStatus != nil {
		switch *model.TransactionStatus {
		case 1, 2:
		default:
			return fmt.Errorf("transaction_status must be one of the documented values")
		}
	}
	if model.TransactionType != nil {
		switch *model.TransactionType {
		case 1, 2:
		default:
			return fmt.Errorf("transaction_type must be one of the documented values")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared QueryTeamWalletHistoryTransactionsResponse wire contract.
func (model *QueryTeamWalletHistoryTransactionsResponse) UnmarshalJSON(data []byte) error {
	type plain QueryTeamWalletHistoryTransactionsResponse
	var value plain
	if err := unmarshalModel(data, &value, []string{"pageNum", "pageSize", "total", "rows"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode QueryTeamWalletHistoryTransactionsResponse: %w", err)
	}
	decoded := QueryTeamWalletHistoryTransactionsResponse(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode QueryTeamWalletHistoryTransactionsResponse: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for QueryTeamWalletHistoryTransactionsResponse.
func (model QueryTeamWalletHistoryTransactionsResponse) Validate() error {
	for index := range model.Rows {
		if err := (model.Rows)[index].Validate(); err != nil {
			return fmt.Errorf("rows[%d]: %w", index, err)
		}
	}
	return nil
}

// UnmarshalJSON validates the declared QueryTeamWalletProcessingTransactionsRequest wire contract.
func (model *QueryTeamWalletProcessingTransactionsRequest) UnmarshalJSON(data []byte) error {
	type plain QueryTeamWalletProcessingTransactionsRequest
	var value plain
	if err := unmarshalModel(data, &value, []string{"wallet_id"}, nil, map[string]struct{}{
		"chain_id":  {},
		"page_num":  {},
		"page_size": {},
		"token_id":  {},
		"txid":      {},
		"wallet_id": {},
	}, true); err != nil {
		return fmt.Errorf("decode QueryTeamWalletProcessingTransactionsRequest: %w", err)
	}
	decoded := QueryTeamWalletProcessingTransactionsRequest(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode QueryTeamWalletProcessingTransactionsRequest: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for QueryTeamWalletProcessingTransactionsRequest.
func (model QueryTeamWalletProcessingTransactionsRequest) Validate() error {
	if model.PageNum != nil {
		if *model.PageNum < 1 {
			return fmt.Errorf("page_num violates OpenAPI minimum")
		}
	}
	if model.PageSize != nil {
		if *model.PageSize < 1 {
			return fmt.Errorf("page_size violates OpenAPI minimum")
		}
		if *model.PageSize > 100 {
			return fmt.Errorf("page_size violates OpenAPI maximum")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared QueryTeamWalletProcessingTransactionsResponse wire contract.
func (model *QueryTeamWalletProcessingTransactionsResponse) UnmarshalJSON(data []byte) error {
	type plain QueryTeamWalletProcessingTransactionsResponse
	var value plain
	if err := unmarshalModel(data, &value, []string{"pageNum", "pageSize", "total", "rows"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode QueryTeamWalletProcessingTransactionsResponse: %w", err)
	}
	decoded := QueryTeamWalletProcessingTransactionsResponse(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode QueryTeamWalletProcessingTransactionsResponse: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for QueryTeamWalletProcessingTransactionsResponse.
func (model QueryTeamWalletProcessingTransactionsResponse) Validate() error {
	for index := range model.Rows {
		if err := (model.Rows)[index].Validate(); err != nil {
			return fmt.Errorf("rows[%d]: %w", index, err)
		}
	}
	return nil
}

// UnmarshalJSON validates the declared TeamWallet wire contract.
func (model *TeamWallet) UnmarshalJSON(data []byte) error {
	type plain TeamWallet
	var value plain
	if err := unmarshalModel(data, &value, []string{"wallet_id", "walletType"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode TeamWallet: %w", err)
	}
	decoded := TeamWallet(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode TeamWallet: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for TeamWallet.
func (model TeamWallet) Validate() error {
	switch model.WalletType {
	case "single_sign", "multi_sign":
	default:
		return fmt.Errorf("walletType must be one of the documented values")
	}
	if model.WalletStatus != nil {
		switch *model.WalletStatus {
		case "normal":
		default:
			return fmt.Errorf("wallet_status must be one of the documented values")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared TeamWalletAddress wire contract.
func (model *TeamWalletAddress) UnmarshalJSON(data []byte) error {
	type plain TeamWalletAddress
	var value plain
	if err := unmarshalModel(data, &value, []string{"address"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode TeamWalletAddress: %w", err)
	}
	decoded := TeamWalletAddress(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode TeamWalletAddress: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for TeamWalletAddress.
func (model TeamWalletAddress) Validate() error {
	if model.AddressStatus != nil {
		switch *model.AddressStatus {
		case "enable", "disable":
		default:
			return fmt.Errorf("address_status must be one of the documented values")
		}
	}
	return nil
}

// UnmarshalJSON validates the declared TeamWalletAddressBalance wire contract.
func (model *TeamWalletAddressBalance) UnmarshalJSON(data []byte) error {
	type plain TeamWalletAddressBalance
	var value plain
	if err := unmarshalModel(data, &value, []string{"address", "chain_id", "token_id", "total", "available", "processing"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode TeamWalletAddressBalance: %w", err)
	}
	decoded := TeamWalletAddressBalance(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared TeamWalletBalance wire contract.
func (model *TeamWalletBalance) UnmarshalJSON(data []byte) error {
	type plain TeamWalletBalance
	var value plain
	if err := unmarshalModel(data, &value, []string{"chain_id", "token_id", "total", "available", "processing"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode TeamWalletBalance: %w", err)
	}
	decoded := TeamWalletBalance(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared TeamWalletProcessingTransaction wire contract.
func (model *TeamWalletProcessingTransaction) UnmarshalJSON(data []byte) error {
	type plain TeamWalletProcessingTransaction
	var value plain
	if err := unmarshalModel(data, &value, []string{"wallet_id", "transaction_type", "status", "chain_id", "token_id", "amount"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode TeamWalletProcessingTransaction: %w", err)
	}
	decoded := TeamWalletProcessingTransaction(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode TeamWalletProcessingTransaction: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for TeamWalletProcessingTransaction.
func (model TeamWalletProcessingTransaction) Validate() error {
	switch model.Status {
	case 0:
	default:
		return fmt.Errorf("status must be one of the documented values")
	}
	switch model.TransactionType {
	case 1:
	default:
		return fmt.Errorf("transaction_type must be one of the documented values")
	}
	return nil
}

// UnmarshalJSON validates the declared TeamWalletToken wire contract.
func (model *TeamWalletToken) UnmarshalJSON(data []byte) error {
	type plain TeamWalletToken
	var value plain
	if err := unmarshalModel(data, &value, []string{"chain_id", "token_id"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode TeamWalletToken: %w", err)
	}
	decoded := TeamWalletToken(value)
	*model = decoded
	return nil
}

// UnmarshalJSON validates the declared TeamWalletTransaction wire contract.
func (model *TeamWalletTransaction) UnmarshalJSON(data []byte) error {
	type plain TeamWalletTransaction
	var value plain
	if err := unmarshalModel(data, &value, []string{"wallet_id", "transaction_type", "status", "chain_id", "token_id", "amount"}, nil, nil, false); err != nil {
		return fmt.Errorf("decode TeamWalletTransaction: %w", err)
	}
	decoded := TeamWalletTransaction(value)
	if err := decoded.Validate(); err != nil {
		return fmt.Errorf("decode TeamWalletTransaction: %w", err)
	}
	*model = decoded
	return nil
}

// Validate checks OpenAPI constraints for TeamWalletTransaction.
func (model TeamWalletTransaction) Validate() error {
	switch model.Status {
	case 1, 2:
	default:
		return fmt.Errorf("status must be one of the documented values")
	}
	switch model.TransactionType {
	case 1, 2:
	default:
		return fmt.Errorf("transaction_type must be one of the documented values")
	}
	return nil
}
