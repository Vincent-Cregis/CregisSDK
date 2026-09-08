package cregis

import (
	"encoding/json"
	"errors"
	"strings"
	"testing"
	"time"
)

const callbackAPIKey = "callback-api-key"

func callbackTestClock() time.Time { return time.UnixMilli(1687848653294) }

func signedCallbackBody(t *testing.T, payload map[string]any) []byte {
	t.Helper()
	signature, err := SignProjectParameters(payload, callbackAPIKey)
	if err != nil {
		t.Fatal(err)
	}
	signed := make(map[string]any, len(payload)+1)
	for key, value := range payload {
		signed[key] = value
	}
	signed["sign"] = signature
	body, err := json.Marshal(signed)
	if err != nil {
		t.Fatal(err)
	}
	return body
}

func callbackEnvelope() map[string]any {
	return map[string]any{
		"pid":       int64(1382528827416576),
		"nonce":     "m8jisx",
		"timestamp": int64(1687848653294),
	}
}

func paymentCallbackData(eventType string) map[string]any {
	status := "paid"
	if eventType == "expired" {
		status = "expired"
	} else if eventType == "refunded" {
		status = "canceled"
	}
	result := map[string]any{
		"cregis_id": "po-1",
		"order_id":  "merchant-1",
		"status":    status,
	}
	if eventType == "expired" {
		return result
	}
	result["payment_address"] = "T-payment"
	result["receive_amount"] = "10"
	result["receive_currency"] = "USD"
	result["pay_amount"] = "10"
	result["pay_currency"] = "USDT-TRC20"
	result["exchange_rate"] = "1"
	result["tx_id"] = "tx-1"
	result["transact_time"] = int64(1719994383015)
	return result
}

func waasCallbackBase() map[string]any {
	result := callbackEnvelope()
	result["cid"] = int64(1382813146816512)
	result["chain_id"] = "195"
	result["token_id"] = "195"
	result["currency"] = "195@195"
	result["amount"] = "10.5"
	return result
}

func cloneMap(source map[string]any) map[string]any {
	copy := make(map[string]any, len(source))
	for key, value := range source {
		copy[key] = value
	}
	return copy
}

func TestPaymentCallbackDispatchesAllDocumentedEvents(t *testing.T) {
	handler, err := NewPaymentCallbackHandler(callbackAPIKey, WithWebhookClock(callbackTestClock))
	if err != nil {
		t.Fatal(err)
	}
	for _, eventType := range []string{"paid", "paid_partial", "paid_over", "expired", "refunded", "paid_remain"} {
		t.Run(eventType, func(t *testing.T) {
			payload := callbackEnvelope()
			payload["event_name"] = "order"
			payload["event_type"] = eventType
			payload["data"] = paymentCallbackData(eventType)
			result, parseErr := handler.VerifyAndParse(signedCallbackBody(t, payload))
			if parseErr != nil {
				t.Fatal(parseErr)
			}
			if string(result.Envelope.EventType) != eventType {
				t.Fatalf("event type = %q, want %q", result.Envelope.EventType, eventType)
			}
			switch eventType {
			case "expired":
				if _, ok := result.Data.(*PaymentExpiredCallbackData); !ok {
					t.Fatalf("unexpected data type %T", result.Data)
				}
			case "refunded":
				if _, ok := result.Data.(*PaymentRefundedCallbackData); !ok {
					t.Fatalf("unexpected data type %T", result.Data)
				}
			case "paid_remain":
				if _, ok := result.Data.(*PaymentRemainingCallbackData); !ok {
					t.Fatalf("unexpected data type %T", result.Data)
				}
			default:
				if _, ok := result.Data.(*PaymentCompletedCallbackData); !ok {
					t.Fatalf("unexpected data type %T", result.Data)
				}
			}
		})
	}
	if CallbackSuccess != "success" {
		t.Fatalf("unexpected callback acknowledgement: %q", CallbackSuccess)
	}
}

func TestAllFourWaaSWebhookContractsVerifyAndParse(t *testing.T) {
	handler, err := NewWaaSCallbackHandler(callbackAPIKey, WithWebhookClock(callbackTestClock))
	if err != nil {
		t.Fatal(err)
	}

	depositPayload := waasCallbackBase()
	depositPayload["address"] = "deposit-address"
	depositPayload["status"] = "1"
	depositPayload["txid"] = "tx-1"
	depositPayload["block_time"] = "1734328473070"
	deposit, err := handler.HandleDepositCallback(signedCallbackBody(t, depositPayload))
	if err != nil || deposit.Status != "1" {
		t.Fatalf("deposit callback failed: value=%+v error=%v", deposit, err)
	}

	payoutPayload := waasCallbackBase()
	payoutPayload["address"] = "payout-address"
	payoutPayload["third_party_id"] = "payout-1"
	payoutPayload["status"] = 6
	payoutPayload["block_time"] = int64(1734328473070)
	payout, err := handler.HandlePayoutCallback(signedCallbackBody(t, payoutPayload))
	if err != nil || payout.Status != 6 {
		t.Fatalf("payout callback failed: value=%+v error=%v", payout, err)
	}

	externalPayload := cloneMap(waasCallbackBase())
	delete(externalPayload, "currency")
	externalPayload["third_party_id"] = "external-1"
	externalPayload["from_address"] = "from"
	externalPayload["to_address"] = "to"
	external, err := handler.HandlePayoutExternalVerificationCallback(signedCallbackBody(t, externalPayload))
	if err != nil || external.ThirdPartyID != "external-1" {
		t.Fatalf("external verification callback failed: value=%+v error=%v", external, err)
	}

	withdrawalPayload := waasCallbackBase()
	withdrawalPayload["from_address"] = "from"
	withdrawalPayload["to_address"] = "to"
	withdrawalPayload["third_party_id"] = "withdrawal-1"
	withdrawalPayload["status"] = 6
	withdrawal, err := handler.HandleWithdrawalCallback(signedCallbackBody(t, withdrawalPayload))
	if err != nil || withdrawal.ToAddress != "to" {
		t.Fatalf("withdrawal callback failed: value=%+v error=%v", withdrawal, err)
	}

	if ExternalVerificationApprove != "ok" || ExternalVerificationDeny != "deny" {
		t.Fatal("unexpected external verification acknowledgement constants")
	}
}

func TestWebhooksRejectBadSignaturesUnknownEventsAndWrongTypes(t *testing.T) {
	payment, err := NewPaymentCallbackHandler(callbackAPIKey, WithWebhookClock(callbackTestClock))
	if err != nil {
		t.Fatal(err)
	}
	waas, err := NewWaaSCallbackHandler(callbackAPIKey, WithWebhookClock(callbackTestClock))
	if err != nil {
		t.Fatal(err)
	}
	if _, err := payment.VerifyAndParse([]byte("{}")); err == nil {
		t.Fatal("expected unsigned callback to fail")
	}

	unknown := callbackEnvelope()
	unknown["event_name"] = "order"
	unknown["event_type"] = "future_event"
	unknown["data"] = map[string]any{}
	_, err = payment.VerifyAndParse(signedCallbackBody(t, unknown))
	var contractError *ContractError
	if !errors.As(err, &contractError) {
		t.Fatalf("expected unknown event ContractError, got %T: %v", err, err)
	}

	wrongType := waasCallbackBase()
	wrongType["address"] = "deposit-address"
	wrongType["status"] = 1
	wrongType["txid"] = "tx-1"
	_, err = waas.HandleDepositCallback(signedCallbackBody(t, wrongType))
	if !errors.As(err, &contractError) {
		t.Fatalf("expected wrong wire type ContractError, got %T: %v", err, err)
	}

	badSignature := signedCallbackBody(t, wrongType)
	var payload map[string]any
	if err := json.Unmarshal(badSignature, &payload); err != nil {
		t.Fatal(err)
	}
	payload["amount"] = "tampered"
	badSignature, err = json.Marshal(payload)
	if err != nil {
		t.Fatal(err)
	}
	if _, err := VerifyProjectWebhook(badSignature, callbackAPIKey); err == nil {
		t.Fatal("expected tampered callback to fail")
	}
}

func TestWebhookVerificationRejectsTimestampProjectAndReplayViolations(t *testing.T) {
	payload := callbackEnvelope()
	body := signedCallbackBody(t, payload)

	if _, err := VerifyProjectWebhook(body, callbackAPIKey,
		WithWebhookClock(func() time.Time { return callbackTestClock().Add(11 * time.Minute) }),
	); err == nil || !strings.Contains(err.Error(), "older") {
		t.Fatalf("expected stale callback rejection, got %v", err)
	}
	if _, err := VerifyProjectWebhook(body, callbackAPIKey,
		WithWebhookClock(func() time.Time { return callbackTestClock().Add(-2 * time.Minute) }),
	); err == nil || !strings.Contains(err.Error(), "future") {
		t.Fatalf("expected future callback rejection, got %v", err)
	}
	if _, err := VerifyProjectWebhook(body, callbackAPIKey,
		WithWebhookClock(callbackTestClock),
		WithExpectedProjectID(1382528827416577),
	); err == nil || !strings.Contains(err.Error(), "expected project") {
		t.Fatalf("expected project mismatch rejection, got %v", err)
	}

	replayed := errors.New("callback already processed")
	seen := false
	guard := WebhookReplayGuardFunc(func(projectID int64, nonce string, timestamp time.Time) error {
		if projectID != 1382528827416576 || nonce != "m8jisx" || !timestamp.Equal(callbackTestClock()) {
			t.Fatalf("unexpected replay identity: pid=%d nonce=%q timestamp=%s", projectID, nonce, timestamp)
		}
		if seen {
			return replayed
		}
		seen = true
		return nil
	})
	options := []WebhookOption{WithWebhookClock(callbackTestClock), WithWebhookReplayGuard(guard)}
	if _, err := VerifyProjectWebhook(body, callbackAPIKey, options...); err != nil {
		t.Fatalf("first callback failed: %v", err)
	}
	if _, err := VerifyProjectWebhook(body, callbackAPIKey, options...); !errors.Is(err, replayed) {
		t.Fatalf("expected replay guard error, got %v", err)
	}
}

func TestPaymentCallbackRejectsInvalidDocumentedStatus(t *testing.T) {
	handler, err := NewPaymentCallbackHandler(callbackAPIKey, WithWebhookClock(callbackTestClock))
	if err != nil {
		t.Fatal(err)
	}
	payload := callbackEnvelope()
	payload["event_name"] = "order"
	payload["event_type"] = "paid"
	data := paymentCallbackData("paid")
	data["status"] = "not-a-payment-status"
	payload["data"] = data
	_, err = handler.VerifyAndParse(signedCallbackBody(t, payload))
	var contractError *ContractError
	if !errors.As(err, &contractError) || !strings.Contains(err.Error(), "status must be one of") {
		t.Fatalf("expected callback enum ContractError, got %T: %v", err, err)
	}
}
