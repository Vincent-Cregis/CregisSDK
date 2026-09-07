package cregis

import (
	"crypto/subtle"
	"encoding/hex"
	"encoding/json"
	"errors"
	"fmt"
	"strconv"
	"strings"
	"time"
)

const (
	// CallbackSuccess is the response body expected by standard Cregis callbacks.
	CallbackSuccess = "success"
	// ExternalVerificationApprove approves a WaaS external-verification callback.
	ExternalVerificationApprove = "ok"
	// ExternalVerificationDeny denies a WaaS external-verification callback.
	ExternalVerificationDeny = "deny"
	// DefaultWebhookMaxAge is the default age limit for signed callbacks.
	DefaultWebhookMaxAge = 10 * time.Minute
	// DefaultWebhookFutureSkew is the default tolerated callback clock skew.
	DefaultWebhookFutureSkew = time.Minute
)

// WebhookReplayGuard atomically rejects a callback identity that it has already stored.
// Implementations used by multiple application instances should use shared durable storage.
type WebhookReplayGuard interface {
	CheckAndStore(projectID int64, nonce string, timestamp time.Time) error
}

// WebhookReplayGuardFunc adapts a function to WebhookReplayGuard.
type WebhookReplayGuardFunc func(projectID int64, nonce string, timestamp time.Time) error

// CheckAndStore calls the adapted replay-check function.
func (function WebhookReplayGuardFunc) CheckAndStore(
	projectID int64,
	nonce string,
	timestamp time.Time,
) error {
	return function(projectID, nonce, timestamp)
}

type webhookVerificationOptions struct {
	expectedProjectID int64
	maxAge            time.Duration
	futureSkew        time.Duration
	now               func() time.Time
	replayGuard       WebhookReplayGuard
}

// WebhookOption configures Payment Engine/WaaS callback verification.
type WebhookOption func(*webhookVerificationOptions) error

// WithExpectedProjectID requires callbacks to contain projectID.
func WithExpectedProjectID(projectID int64) WebhookOption {
	return func(options *webhookVerificationOptions) error {
		if projectID <= 0 || projectID > maxSafeProjectID {
			return &ClientError{Message: "Expected webhook ProjectID must be a positive JavaScript-safe int64 value"}
		}
		options.expectedProjectID = projectID
		return nil
	}
}

// WithWebhookMaxAge sets the accepted callback age. Zero explicitly disables the age check.
func WithWebhookMaxAge(maxAge time.Duration) WebhookOption {
	return func(options *webhookVerificationOptions) error {
		if maxAge < 0 {
			return &ClientError{Message: "Webhook MaxAge must not be negative"}
		}
		options.maxAge = maxAge
		return nil
	}
}

// WithWebhookFutureSkew sets the tolerated amount that a callback timestamp may be in the future.
func WithWebhookFutureSkew(futureSkew time.Duration) WebhookOption {
	return func(options *webhookVerificationOptions) error {
		if futureSkew < 0 {
			return &ClientError{Message: "Webhook FutureSkew must not be negative"}
		}
		options.futureSkew = futureSkew
		return nil
	}
}

// WithWebhookReplayGuard adds an application-provided atomic replay check.
func WithWebhookReplayGuard(guard WebhookReplayGuard) WebhookOption {
	return func(options *webhookVerificationOptions) error {
		if guard == nil {
			return &ClientError{Message: "Webhook ReplayGuard must not be nil"}
		}
		options.replayGuard = guard
		return nil
	}
}

// WithWebhookClock replaces the verification clock, primarily for deterministic tests.
func WithWebhookClock(now func() time.Time) WebhookOption {
	return func(options *webhookVerificationOptions) error {
		if now == nil {
			return &ClientError{Message: "Webhook clock must not be nil"}
		}
		options.now = now
		return nil
	}
}

// VerifyProjectWebhook verifies a Payment Engine/WaaS callback signature.
func VerifyProjectWebhook(
	rawBody []byte,
	apiKey string,
	optionValues ...WebhookOption,
) (map[string]any, error) {
	options, err := resolveWebhookOptions(optionValues)
	if err != nil {
		return nil, err
	}
	return verifyProjectWebhook(rawBody, apiKey, options)
}

func resolveWebhookOptions(optionValues []WebhookOption) (webhookVerificationOptions, error) {
	options := webhookVerificationOptions{
		maxAge:     DefaultWebhookMaxAge,
		futureSkew: DefaultWebhookFutureSkew,
		now:        time.Now,
	}
	for _, option := range optionValues {
		if option == nil {
			return webhookVerificationOptions{}, &ClientError{Message: "Webhook option must not be nil"}
		}
		if err := option(&options); err != nil {
			return webhookVerificationOptions{}, err
		}
	}
	return options, nil
}

func verifyProjectWebhook(
	rawBody []byte,
	apiKey string,
	options webhookVerificationOptions,
) (map[string]any, error) {
	if strings.TrimSpace(apiKey) == "" {
		return nil, &ClientError{Message: "API Key is required"}
	}
	if len(strings.TrimSpace(string(rawBody))) == 0 {
		return nil, &ClientError{Message: "Callback body is required"}
	}
	payload, err := decodeJSONObject(rawBody)
	if err != nil {
		return nil, &ClientError{
			Message: "Failed to parse callback JSON for signature verification",
			Cause:   err,
		}
	}
	incoming, ok := payload["sign"].(string)
	if !ok || len(incoming) != 32 {
		return nil, &ClientError{Message: "Callback signature must be a 32-character hexadecimal string"}
	}
	incomingBytes, err := hex.DecodeString(incoming)
	if err != nil || len(incomingBytes) != md5SignatureBytes {
		return nil, &ClientError{Message: "Callback signature must be a 32-character hexadecimal string"}
	}
	parsedIntegers := make(map[string]int64, 2)
	for _, field := range []string{"pid", "timestamp"} {
		number, ok := payload[field].(json.Number)
		if !ok {
			return nil, &ClientError{Message: fmt.Sprintf("Callback %s must be a positive integer", field)}
		}
		value, parseErr := strconv.ParseInt(number.String(), 10, 64)
		if parseErr != nil || value <= 0 {
			return nil, &ClientError{Message: fmt.Sprintf("Callback %s must be a positive integer", field)}
		}
		parsedIntegers[field] = value
	}
	nonce, ok := payload["nonce"].(string)
	if !ok || strings.TrimSpace(nonce) == "" {
		return nil, &ClientError{Message: "Callback nonce must be a non-empty string"}
	}
	unsigned := make(map[string]any, len(payload)-1)
	for key, value := range payload {
		if key != "sign" {
			unsigned[key] = value
		}
	}
	calculated, err := SignProjectParameters(unsigned, apiKey)
	if err != nil {
		return nil, err
	}
	calculatedBytes, _ := hex.DecodeString(calculated)
	if subtle.ConstantTimeCompare(calculatedBytes, incomingBytes) != 1 {
		return nil, &ClientError{Message: "Callback signature verification failed"}
	}
	projectID := parsedIntegers["pid"]
	if options.expectedProjectID != 0 && projectID != options.expectedProjectID {
		return nil, &ClientError{Message: "Callback pid does not match the expected project"}
	}
	callbackTime := time.UnixMilli(parsedIntegers["timestamp"])
	now := options.now()
	if options.maxAge > 0 && now.Sub(callbackTime) > options.maxAge {
		return nil, &ClientError{Message: "Callback timestamp is older than the configured MaxAge"}
	}
	if callbackTime.Sub(now) > options.futureSkew {
		return nil, &ClientError{Message: "Callback timestamp is too far in the future"}
	}
	if options.replayGuard != nil {
		if err := options.replayGuard.CheckAndStore(projectID, nonce, callbackTime); err != nil {
			return nil, &ClientError{Message: "Callback replay check failed", Cause: err}
		}
	}
	return payload, nil
}

const md5SignatureBytes = 16

// PaymentCallback contains a verified envelope and its event-specific data model.
type PaymentCallback struct {
	Envelope PaymentCallbackEnvelope
	Data     any
}

// PaymentCallbackHandler verifies and decodes Payment Engine callbacks.
type PaymentCallbackHandler struct {
	apiKey       string
	verification webhookVerificationOptions
}

// NewPaymentCallbackHandler returns a callback handler using apiKey.
func NewPaymentCallbackHandler(apiKey string, options ...WebhookOption) (*PaymentCallbackHandler, error) {
	if strings.TrimSpace(apiKey) == "" {
		return nil, &ClientError{Message: "API Key is required"}
	}
	verification, err := resolveWebhookOptions(options)
	if err != nil {
		return nil, err
	}
	return &PaymentCallbackHandler{apiKey: apiKey, verification: verification}, nil
}

// VerifyAndParse verifies a callback and dispatches its data by event_type.
func (handler *PaymentCallbackHandler) VerifyAndParse(rawBody []byte) (*PaymentCallback, error) {
	if handler == nil {
		return nil, &ClientError{Message: "Payment callback handler is nil"}
	}
	if _, err := verifyProjectWebhook(rawBody, handler.apiKey, handler.verification); err != nil {
		return nil, err
	}
	var envelope PaymentCallbackEnvelope
	if err := decodeJSON(rawBody, &envelope); err != nil {
		return nil, &ContractError{Context: "Payment callback", Cause: err}
	}
	if envelope.EventName != "order" {
		return nil, &ContractError{
			Context: "Payment callback",
			Cause:   fmt.Errorf("unsupported event_name: %q", envelope.EventName),
		}
	}
	var destination any
	switch envelope.EventType {
	case "paid", "paid_partial", "paid_over":
		destination = &PaymentCompletedCallbackData{}
	case "expired":
		destination = &PaymentExpiredCallbackData{}
	case "refunded":
		destination = &PaymentRefundedCallbackData{}
	case "paid_remain":
		destination = &PaymentRemainingCallbackData{}
	default:
		return nil, &ContractError{
			Context: "Payment callback",
			Cause:   fmt.Errorf("unsupported event_type: %q", envelope.EventType),
		}
	}
	if len(envelope.Data) == 0 || string(envelope.Data) == "null" {
		return nil, &ContractError{Context: "Payment callback", Cause: errors.New("data is required")}
	}
	if err := decodeJSON(envelope.Data, destination); err != nil {
		return nil, &ContractError{Context: "Payment callback", Cause: err}
	}
	return &PaymentCallback{Envelope: envelope, Data: destination}, nil
}

// WaaSCallbackHandler verifies and decodes WaaS callbacks.
type WaaSCallbackHandler struct {
	apiKey       string
	verification webhookVerificationOptions
}

// NewWaaSCallbackHandler returns a callback handler using apiKey.
func NewWaaSCallbackHandler(apiKey string, options ...WebhookOption) (*WaaSCallbackHandler, error) {
	if strings.TrimSpace(apiKey) == "" {
		return nil, &ClientError{Message: "API Key is required"}
	}
	verification, err := resolveWebhookOptions(options)
	if err != nil {
		return nil, err
	}
	return &WaaSCallbackHandler{apiKey: apiKey, verification: verification}, nil
}

// HandleDepositCallback verifies and decodes an address-deposit callback.
func (handler *WaaSCallbackHandler) HandleDepositCallback(
	rawBody []byte,
) (*AddressDepositCallbackNotification, error) {
	result := &AddressDepositCallbackNotification{}
	if err := handler.verify(rawBody, result, "WaaS deposit callback"); err != nil {
		return nil, err
	}
	return result, nil
}

// HandlePayoutCallback verifies and decodes a payout callback.
func (handler *WaaSCallbackHandler) HandlePayoutCallback(
	rawBody []byte,
) (*PayoutCallbackNotification, error) {
	result := &PayoutCallbackNotification{}
	if err := handler.verify(rawBody, result, "WaaS payout callback"); err != nil {
		return nil, err
	}
	return result, nil
}

// HandlePayoutExternalVerificationCallback verifies and decodes an external-verification callback.
func (handler *WaaSCallbackHandler) HandlePayoutExternalVerificationCallback(
	rawBody []byte,
) (*PayoutExternalVerificationCallbackNotification, error) {
	result := &PayoutExternalVerificationCallbackNotification{}
	if err := handler.verify(rawBody, result, "WaaS payout external verification callback"); err != nil {
		return nil, err
	}
	return result, nil
}

// HandleWithdrawalCallback verifies and decodes a sub-address withdrawal callback.
func (handler *WaaSCallbackHandler) HandleWithdrawalCallback(
	rawBody []byte,
) (*WithdrawalCallbackNotification, error) {
	result := &WithdrawalCallbackNotification{}
	if err := handler.verify(rawBody, result, "WaaS withdrawal callback"); err != nil {
		return nil, err
	}
	return result, nil
}

func (handler *WaaSCallbackHandler) verify(rawBody []byte, destination any, context string) error {
	if handler == nil {
		return &ClientError{Message: "WaaS callback handler is nil"}
	}
	if _, err := verifyProjectWebhook(rawBody, handler.apiKey, handler.verification); err != nil {
		return err
	}
	if err := decodeJSON(rawBody, destination); err != nil {
		return &ContractError{Context: context, Cause: err}
	}
	return nil
}
