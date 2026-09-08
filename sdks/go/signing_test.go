package cregis

import (
	"encoding/json"
	"math"
	"strings"
	"testing"
)

func TestProjectSignerMatchesPublishedPayoutVector(t *testing.T) {
	t.Parallel()
	signature, err := SignProjectParameters(map[string]any{
		"pid":            int64(1382528827416576),
		"currency":       "195@195",
		"address":        "TXsmKpEuW7qWnXzJLGP9eDLvWPR2GRn1FS",
		"amount":         "1.1",
		"remark":         "payout",
		"third_party_id": "c9231e604da54469a735af3f449c880f",
		"callback_url":   "https://your-domain.com/callback",
		"nonce":          "hwlkk6",
		"timestamp":      int64(1688004243314),
	}, "f502a9ac9ca54327986f29c03b271491")
	if err != nil {
		t.Fatal(err)
	}
	if signature != "f76fb193e9d34d2e59fef64e3418f79b" {
		t.Fatalf("unexpected signature: %s", signature)
	}
}

func TestProjectSignerCanonicalizesNestedWebhookData(t *testing.T) {
	t.Parallel()
	signature, err := SignProjectParameters(map[string]any{
		"event_name": "order",
		"event_type": "refunded",
		"pid":        int64(123456789),
		"nonce":      "abc123",
		"timestamp":  int64(1719994383015),
		"data": map[string]any{
			"cregis_id":     "po-test",
			"order_id":      "merchant-test",
			"refund_id":     "rf-test",
			"refund_status": 1,
		},
	}, "fixture-api-key")
	if err != nil {
		t.Fatal(err)
	}
	if signature != "9e2b39ae45341e1d912dda3e31a3dddf" {
		t.Fatalf("unexpected signature: %s", signature)
	}
}

func TestTeamSignerAndRFC8785Canonicalization(t *testing.T) {
	t.Parallel()
	body, err := CanonicalizeJSON(map[string]any{"name": "Demo Team"})
	if err != nil {
		t.Fatal(err)
	}
	signature, err := SignTeamRequest(
		"/openapi/team/profile",
		1717380000000,
		"9f7c6a2b47e34f19",
		body,
		"team-secret",
	)
	if err != nil {
		t.Fatal(err)
	}
	if signature != "cef9805fa4ee7bf9376b140153c5e3f80e74e4f950eb6f92ef780fa42aa44289" {
		t.Fatalf("unexpected signature: %s", signature)
	}

	canonical, err := CanonicalizeJSON(`{ "b": 2, "a": 1 }`)
	if err != nil {
		t.Fatal(err)
	}
	if string(canonical) != `{"a":1,"b":2}` {
		t.Fatalf("unexpected canonical JSON: %s", canonical)
	}
}

func TestProjectSignerPreservesInt64JSONNumbers(t *testing.T) {
	t.Parallel()
	signature, err := SignProjectParameters(map[string]any{
		"cid": json.Number("9007199254740993"),
	}, "key")
	if err != nil {
		t.Fatal(err)
	}
	if signature != "84ebf6a4564c37ddf28ce80a64aadcba" {
		t.Fatalf("large int64 was not signed exactly: %s", signature)
	}

	signature, err = SignProjectParameters(map[string]any{
		"cid": json.Number("9223372036854775807"),
	}, "key")
	if err != nil {
		t.Fatal(err)
	}
	if signature == "" {
		t.Fatal("max int64 signature is empty")
	}

	negativeZero, err := SignProjectParameters(map[string]any{"value": json.Number("-0")}, "key")
	if err != nil {
		t.Fatal(err)
	}
	zero, err := SignProjectParameters(map[string]any{"value": 0}, "key")
	if err != nil {
		t.Fatal(err)
	}
	if negativeZero != zero {
		t.Fatalf("negative zero signature = %s, want %s", negativeZero, zero)
	}
}

func TestRFC8785RejectsUnsafeIntegerValuesInsteadOfRounding(t *testing.T) {
	t.Parallel()
	canonical, err := CanonicalizeJSON([]byte(`{"wallet_id":9007199254740991}`))
	if err != nil || string(canonical) != `{"wallet_id":9007199254740991}` {
		t.Fatalf("safe integer canonicalization failed: body=%s error=%v", canonical, err)
	}
	for _, raw := range []string{
		`{"wallet_id":9007199254740992}`,
		`{"wallet_id":9007199254740993}`,
		`{"wallet_id":9223372036854775807}`,
		`{"wallet_id":-9007199254740992}`,
	} {
		if _, err := CanonicalizeJSON([]byte(raw)); err == nil || !strings.Contains(err.Error(), "safe integer range") {
			t.Fatalf("expected unsafe integer rejection for %s, got %v", raw, err)
		}
	}
}

func TestSignersRejectInvalidInputs(t *testing.T) {
	t.Parallel()
	if _, err := SignProjectParameters(map[string]any{}, ""); err == nil {
		t.Fatal("expected empty API key to fail")
	}
	if _, err := SignTeamRequest("relative", 1, "1234567890123456", []byte("{}"), "secret"); err == nil {
		t.Fatal("expected relative Team API path to fail")
	}
	if _, err := CanonicalizeJSON(map[string]any{"invalid": math.NaN()}); err == nil {
		t.Fatal("expected non-finite JSON number to fail")
	}
}
