package cregis

import "context"

// PaymentClient calls Payment Engine operations.
type PaymentClient struct{ base *baseClient }

// NewPaymentClient validates config and returns a Payment Engine client.
func NewPaymentClient(config ProjectConfig) (*PaymentClient, error) {
	base, err := newProjectBase(config)
	if err != nil {
		return nil, err
	}
	return &PaymentClient{base: base}, nil
}

// CloseIdleConnections closes idle connections owned by the configured HTTP transport.
func (client *PaymentClient) CloseIdleConnections() {
	if client != nil && client.base != nil {
		client.base.httpClient.CloseIdleConnections()
	}
}

// WaaSClient calls Wallet-as-a-Service operations.
type WaaSClient struct{ base *baseClient }

// NewWaaSClient validates config and returns a WaaS client.
func NewWaaSClient(config ProjectConfig) (*WaaSClient, error) {
	base, err := newProjectBase(config)
	if err != nil {
		return nil, err
	}
	return &WaaSClient{base: base}, nil
}

// CloseIdleConnections closes idle connections owned by the configured HTTP transport.
func (client *WaaSClient) CloseIdleConnections() {
	if client != nil && client.base != nil {
		client.base.httpClient.CloseIdleConnections()
	}
}

// Payout is retained for compatibility. Use PayoutV2 for new code.
// Deprecated: use PayoutV2.
func (client *WaaSClient) Payout(ctx context.Context, request *PayoutRequest) (*PayoutResponse, error) {
	return client.PayoutV2(ctx, request)
}

// TeamClient calls Team API operations.
type TeamClient struct{ base *baseClient }

// NewTeamClient validates config and returns a Team API client.
func NewTeamClient(config TeamConfig) (*TeamClient, error) {
	base, err := newTeamBase(config)
	if err != nil {
		return nil, err
	}
	return &TeamClient{base: base}, nil
}

// CloseIdleConnections closes idle connections owned by the configured HTTP transport.
func (client *TeamClient) CloseIdleConnections() {
	if client != nil && client.base != nil {
		client.base.httpClient.CloseIdleConnections()
	}
}
