package cregis

import (
	"bytes"
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"reflect"
	"strconv"
	"strings"
	"sync"
	"testing"
	"time"
)

const (
	testProjectID    int64 = 1382528827416576
	testAPIKey             = "test-api-key"
	testAccessKey          = "test-access-key"
	testAccessSecret       = "test-access-secret"
)

type roundTripFunc func(*http.Request) (*http.Response, error)

func (function roundTripFunc) RoundTrip(request *http.Request) (*http.Response, error) {
	return function(request)
}

type capturedRequest struct {
	path    string
	headers http.Header
	body    []byte
}

func jsonResponse(status int, body string) *http.Response {
	return &http.Response{
		StatusCode: status,
		Status:     fmt.Sprintf("%d %s", status, http.StatusText(status)),
		Header:     make(http.Header),
		Body:       io.NopCloser(strings.NewReader(body)),
	}
}

func TestAll23ClientOperationsValidatePathsAndSignatures(t *testing.T) {
	var mutex sync.Mutex
	requests := make([]capturedRequest, 0, 23)
	transport := roundTripFunc(func(request *http.Request) (*http.Response, error) {
		body, err := io.ReadAll(request.Body)
		if err != nil {
			return nil, err
		}
		mutex.Lock()
		requests = append(requests, capturedRequest{
			path:    request.URL.Path,
			headers: request.Header.Clone(),
			body:    body,
		})
		mutex.Unlock()

		data := `{}`
		if request.URL.Path == "/api/v1/address/update" {
			data = `null`
		} else if request.URL.Path == "/api/v1/batch/address/create" {
			data = `[]`
		} else if strings.HasPrefix(request.URL.Path, "/openapi/") {
			data = `{"pageNum":1,"pageSize":10,"rows":[],"total":0}`
		}
		return jsonResponse(http.StatusOK, `{"code":"00000","msg":"ok","data":`+data+`}`), nil
	})
	httpClient := &http.Client{Transport: transport}
	payment, err := NewPaymentClient(ProjectConfig{
		BaseURL: "https://sandbox.example", ProjectID: testProjectID, APIKey: testAPIKey, HTTPClient: httpClient,
	})
	if err != nil {
		t.Fatal(err)
	}
	waas, err := NewWaaSClient(ProjectConfig{
		BaseURL: "https://sandbox.example", ProjectID: testProjectID, APIKey: testAPIKey, HTTPClient: httpClient,
	})
	if err != nil {
		t.Fatal(err)
	}
	team, err := NewTeamClient(TeamConfig{
		BaseURL: "https://sandbox.example", AccessKey: testAccessKey, AccessSecret: testAccessSecret, HTTPClient: httpClient,
	})
	if err != nil {
		t.Fatal(err)
	}
	ctx := context.Background()

	mustSucceed(t, func() error {
		_, callErr := payment.CreateOrder(ctx, &CreateOrderRequest{
			OrderID: "order-1", OrderAmount: "1", OrderCurrency: "USD", PayerID: "payer-1",
			SuccessURL: "https://merchant.example/success", CancelURL: "https://merchant.example/cancel",
		})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := payment.QueryOrder(ctx, &QueryOrderRequest{CregisID: "po-1"})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := waas.GenerateAddress(ctx, &GenerateAddressRequest{ChainID: "195"})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := waas.BatchGenerateAddress(ctx, &BatchGenerateAddressRequest{ChainID: "195", Number: "2"})
		return callErr
	})
	mustSucceed(t, func() error {
		return waas.UpdateAddress(ctx, &AddressUpdateRequest{Address: "T-test", Alias: String("updated")})
	})
	mustSucceed(t, func() error {
		_, callErr := waas.ValidateAddress(ctx, &ValidateAddressRequest{ChainID: "195", Address: "T-test"})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := waas.CheckAddressLegality(ctx, &CheckAddressLegalityRequest{ChainID: "195", Address: "T-test"})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := waas.PayoutV1(ctx, &PayoutV1Request{Currency: "195@195", Address: "T-to", Amount: "1", ThirdPartyID: "payout-v1-1"})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := waas.PayoutV2(ctx, &PayoutRequest{Currency: "195@195", ToAddress: "T-to", Amount: "1", ThirdPartyID: "payout-v2-1"})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := waas.Withdrawal(ctx, &WithdrawalRequest{Currency: "195@195", FromAddress: "T-from", ToAddress: "T-to", Amount: "1", ThirdPartyID: "withdrawal-1"})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := waas.BalanceCollect(ctx, &BalanceCollectRequest{Currency: "195@195", FromAddress: "T-from", ToAddress: "T-to"})
		return callErr
	})
	mustSucceed(t, func() error { _, callErr := waas.QueryProjectCoins(ctx); return callErr })
	mustSucceed(t, func() error { _, callErr := waas.QueryTradeRecords(ctx, &TradeRecordQueryRequest{}); return callErr })
	mustSucceed(t, func() error { _, callErr := waas.QueryPayout(ctx, &QueryPayoutRequest{CID: 1}); return callErr })
	mustSucceed(t, func() error { _, callErr := waas.QueryWithdrawal(ctx, &QueryWithdrawalRequest{CID: 1}); return callErr })
	mustSucceed(t, func() error {
		_, callErr := waas.QueryAddressBalance(ctx, &AddressBalanceRequest{Currency: "195@195"})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := waas.QueryAddressBalanceV2(ctx, &AddressBalanceV2Request{Address: "T-test"})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := team.ListTeamWallets(ctx, &ListTeamWalletsRequest{
			PageNum: Int32(1), PageSize: Int32(10),
			WalletType: Ptr(ListTeamWalletsRequestWalletTypeSingleSign),
		})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := team.ListTeamWalletAddresses(ctx, &ListTeamWalletAddressesRequest{WalletID: 1, ChainID: "195"})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := team.QueryTeamWalletBalance(ctx, &QueryTeamWalletBalanceRequest{WalletID: 1})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := team.QueryTeamWalletAddressBalance(ctx, &QueryTeamWalletAddressBalanceRequest{WalletID: 1})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := team.QueryTeamWalletHistoryTransactions(ctx, &QueryTeamWalletHistoryTransactionsRequest{WalletID: 1})
		return callErr
	})
	mustSucceed(t, func() error {
		_, callErr := team.QueryTeamWalletProcessingTransactions(ctx, &QueryTeamWalletProcessingTransactionsRequest{WalletID: 1})
		return callErr
	})

	wantPaths := []string{
		"/api/v2/checkout", "/api/v2/order/info", "/api/v1/address/create",
		"/api/v1/batch/address/create", "/api/v1/address/update", "/api/v1/address/inner",
		"/api/v1/address/legal", "/api/v1/payout", "/api/v2/payout",
		"/api/v1/sub_address_withdrawal", "/api/v1/collection", "/api/v1/coins",
		"/api/v1/trade/page", "/api/v1/payout/query", "/api/v1/sub_address_withdrawal/info",
		"/api/v1/sub_address_balance", "/api/v2/sub_address_balance", "/openapi/v1/wallets",
		"/openapi/v1/wallet_address", "/openapi/v1/wallet_balance", "/openapi/v1/wallet_address_balance",
		"/openapi/v1/wallet_history_transaction_info", "/openapi/v1/wallet_processing_transaction_info",
	}
	if len(requests) != len(wantPaths) {
		t.Fatalf("captured %d requests, want %d", len(requests), len(wantPaths))
	}
	for index, request := range requests {
		if request.path != wantPaths[index] {
			t.Errorf("request %d path = %q, want %q", index, request.path, wantPaths[index])
		}
		if request.headers.Get("User-Agent") != "cregis-go/"+Version {
			t.Errorf("request %d has unexpected User-Agent", index)
		}
		if index < 17 {
			verifyProjectRequest(t, request)
		} else {
			verifyTeamRequest(t, request)
		}
	}
}

func mustSucceed(t *testing.T, call func() error) {
	t.Helper()
	if err := call(); err != nil {
		t.Fatal(err)
	}
}

func verifyProjectRequest(t *testing.T, request capturedRequest) {
	t.Helper()
	parameters, err := decodeJSONObject(request.body)
	if err != nil {
		t.Fatal(err)
	}
	if parameters["pid"] != json.Number(strconv.FormatInt(testProjectID, 10)) {
		t.Errorf("unexpected pid: %v", parameters["pid"])
	}
	nonce, ok := parameters["nonce"].(string)
	if !ok || len(nonce) != 6 {
		t.Errorf("unexpected project nonce: %v", parameters["nonce"])
	}
	if _, ok := parameters["timestamp"].(json.Number); !ok {
		t.Errorf("timestamp is not a JSON integer: %T", parameters["timestamp"])
	}
	signature, ok := parameters["sign"].(string)
	if !ok {
		t.Fatalf("signature is not a string: %T", parameters["sign"])
	}
	delete(parameters, "sign")
	want, err := SignProjectParameters(parameters, testAPIKey)
	if err != nil {
		t.Fatal(err)
	}
	if signature != want {
		t.Errorf("signature = %q, want %q", signature, want)
	}
}

func verifyTeamRequest(t *testing.T, request capturedRequest) {
	t.Helper()
	canonical, err := CanonicalizeJSON(request.body)
	if err != nil {
		t.Fatal(err)
	}
	if !bytes.Equal(request.body, canonical) {
		t.Errorf("Team request body is not RFC 8785 canonical JSON: %s", request.body)
	}
	if request.headers.Get("Access-Key") != testAccessKey {
		t.Errorf("unexpected Access-Key header")
	}
	timestamp, err := strconv.ParseInt(request.headers.Get("Access-Timestamp"), 10, 64)
	if err != nil || timestamp <= 0 {
		t.Fatalf("invalid Access-Timestamp: %q", request.headers.Get("Access-Timestamp"))
	}
	nonce := request.headers.Get("Access-Nonce")
	if len(nonce) != 32 {
		t.Fatalf("invalid Access-Nonce: %q", nonce)
	}
	want, err := SignTeamRequest(request.path, timestamp, nonce, request.body, testAccessSecret)
	if err != nil {
		t.Fatal(err)
	}
	if request.headers.Get("Access-Signature") != want {
		t.Errorf("unexpected Access-Signature")
	}
}

func TestRequestAndResponseContractValidation(t *testing.T) {
	client := projectClientWithTransport(t, roundTripFunc(func(_ *http.Request) (*http.Response, error) {
		return jsonResponse(http.StatusOK, `{"code":"00000","msg":"ok","data":{"cregis_id":123}}`), nil
	}))
	_, err := client.QueryOrder(context.Background(), &QueryOrderRequest{CregisID: "po-1"})
	var contractError *ContractError
	if !errors.As(err, &contractError) {
		t.Fatalf("expected ContractError for wrong response type, got %T: %v", err, err)
	}

	waas := waasClientWithTransport(t, roundTripFunc(func(_ *http.Request) (*http.Response, error) {
		return jsonResponse(http.StatusOK, `{"code":"00000","msg":"ok","data":null}`), nil
	}))
	if err := waas.UpdateAddress(context.Background(), &AddressUpdateRequest{Address: "T-test"}); !errors.As(err, &contractError) {
		t.Fatalf("expected AddressUpdate contract failure, got %T: %v", err, err)
	}
	if _, err := waas.BatchGenerateAddress(context.Background(), &BatchGenerateAddressRequest{ChainID: "195", Number: "101"}); !errors.As(err, &contractError) {
		t.Fatalf("expected batch number contract failure, got %T: %v", err, err)
	}

	team := teamClientWithTransport(t, roundTripFunc(func(_ *http.Request) (*http.Response, error) {
		return jsonResponse(http.StatusOK, `{"code":"00000","msg":"ok","data":{"pageNum":1,"rows":[],"total":0}}`), nil
	}))
	_, err = team.ListTeamWallets(context.Background(), &ListTeamWalletsRequest{})
	if !errors.As(err, &contractError) || !strings.Contains(err.Error(), `"pageSize"`) {
		t.Fatalf("expected missing response field contract failure, got %T: %v", err, err)
	}
	_, err = team.ListTeamWallets(context.Background(), &ListTeamWalletsRequest{PageSize: Int32(101)})
	if !errors.As(err, &contractError) {
		t.Fatalf("expected page size contract failure, got %T: %v", err, err)
	}

	team = teamClientWithTransport(t, roundTripFunc(func(_ *http.Request) (*http.Response, error) {
		return jsonResponse(http.StatusOK, `{"code":"00000","msg":"ok","data":{"pageNum":1,"pageSize":10,"total":1,"rows":[{"wallet_id":1,"walletType":"unknown"}]}}`), nil
	}))
	_, err = team.ListTeamWallets(context.Background(), &ListTeamWalletsRequest{})
	if !errors.As(err, &contractError) || !strings.Contains(err.Error(), "walletType must be one of") {
		t.Fatalf("expected response enum contract failure, got %T: %v", err, err)
	}

	var request ListTeamWalletsRequest
	if err := json.Unmarshal([]byte(`{"page_num":1,"unexpected":true}`), &request); err == nil || !strings.Contains(err.Error(), "unknown field") {
		t.Fatalf("expected unknown request field rejection, got %v", err)
	}
}

func TestTeamRequestsRejectUnsafeRFC8785IntegersBeforeNetwork(t *testing.T) {
	calls := 0
	team := teamClientWithTransport(t, roundTripFunc(func(_ *http.Request) (*http.Response, error) {
		calls++
		return jsonResponse(http.StatusOK, `{"code":"00000","msg":"ok","data":{"pageNum":1,"pageSize":10,"total":0,"rows":[]}}`), nil
	}))
	_, err := team.ListTeamWalletAddresses(context.Background(), &ListTeamWalletAddressesRequest{
		WalletID: 9_007_199_254_740_992,
		ChainID:  "195",
	})
	var clientError *ClientError
	if !errors.As(err, &clientError) || !strings.Contains(err.Error(), "safe integer range") {
		t.Fatalf("expected unsafe Team integer ClientError, got %T: %v", err, err)
	}
	if calls != 0 {
		t.Fatalf("unsafe Team request reached the network %d times", calls)
	}
}

func TestProjectRequestPreservesLargeInt64DuringSigning(t *testing.T) {
	var captured []byte
	waas := waasClientWithTransport(t, roundTripFunc(func(request *http.Request) (*http.Response, error) {
		body, err := io.ReadAll(request.Body)
		if err != nil {
			return nil, err
		}
		captured = body
		return jsonResponse(http.StatusOK, `{"code":"00000","msg":"ok","data":{}}`), nil
	}))
	const cid int64 = 9_007_199_254_740_993
	if _, err := waas.QueryPayout(context.Background(), &QueryPayoutRequest{CID: cid}); err != nil {
		t.Fatal(err)
	}
	parameters, err := decodeJSONObject(captured)
	if err != nil {
		t.Fatal(err)
	}
	if parameters["cid"] != json.Number("9007199254740993") {
		t.Fatalf("CID changed on the wire: %v", parameters["cid"])
	}
	incoming, ok := parameters["sign"].(string)
	if !ok {
		t.Fatalf("signature has type %T", parameters["sign"])
	}
	delete(parameters, "sign")
	expected, err := SignProjectParameters(parameters, testAPIKey)
	if err != nil {
		t.Fatal(err)
	}
	if incoming != expected {
		t.Fatalf("signature = %q, want %q", incoming, expected)
	}
}

func TestHTTPAPIEnvelopeRedirectAndNetworkErrorsRemainDistinct(t *testing.T) {
	responses := []*http.Response{
		jsonResponse(http.StatusTooManyRequests, "rate limited"),
		jsonResponse(http.StatusOK, `{"code":"B0001","msg":"Signature Error","data":null}`),
		jsonResponse(http.StatusOK, `{"code":"00000","msg":"ok"}`),
		jsonResponse(http.StatusOK, "not-json"),
	}
	index := 0
	waas := waasClientWithTransport(t, roundTripFunc(func(_ *http.Request) (*http.Response, error) {
		response := responses[index]
		index++
		return response, nil
	}))
	_, err := waas.QueryProjectCoins(context.Background())
	var httpError *HTTPError
	if !errors.As(err, &httpError) || httpError.StatusCode != http.StatusTooManyRequests || httpError.ResponseBody != "rate limited" {
		t.Fatalf("unexpected HTTP error: %T %v", err, err)
	}
	_, err = waas.QueryProjectCoins(context.Background())
	var apiError *APIError
	if !errors.As(err, &apiError) || apiError.Code != "B0001" {
		t.Fatalf("unexpected API error: %T %v", err, err)
	}
	_, err = waas.QueryProjectCoins(context.Background())
	var contractError *ContractError
	if !errors.As(err, &contractError) {
		t.Fatalf("unexpected envelope error: %T %v", err, err)
	}
	_, err = waas.QueryProjectCoins(context.Background())
	if !errors.As(err, &contractError) {
		t.Fatalf("unexpected malformed JSON error: %T %v", err, err)
	}

	calls := 0
	redirectClient := &http.Client{
		Transport: roundTripFunc(func(_ *http.Request) (*http.Response, error) {
			calls++
			response := jsonResponse(http.StatusFound, "redirect")
			response.Header.Set("Location", "https://other.example/redirect")
			return response, nil
		}),
		CheckRedirect: func(_ *http.Request, _ []*http.Request) error {
			t.Fatal("SDK must replace an injected redirect policy")
			return nil
		},
	}
	redirectWaaS, err := NewWaaSClient(ProjectConfig{BaseURL: "https://sandbox.example", ProjectID: testProjectID, APIKey: testAPIKey, HTTPClient: redirectClient})
	if err != nil {
		t.Fatal(err)
	}
	_, err = redirectWaaS.QueryProjectCoins(context.Background())
	if !errors.As(err, &httpError) || httpError.StatusCode != http.StatusFound || calls != 1 {
		t.Fatalf("redirect handling failed: calls=%d error=%T %v", calls, err, err)
	}
	if redirectClient.Timeout != 0 || redirectClient.CheckRedirect == nil {
		t.Fatal("SDK mutated the caller's HTTP client")
	}

	networkWaaS := waasClientWithTransport(t, roundTripFunc(func(_ *http.Request) (*http.Response, error) {
		return nil, errors.New("connection failed")
	}))
	_, err = networkWaaS.QueryProjectCoins(context.Background())
	var clientError *ClientError
	if !errors.As(err, &clientError) || !strings.Contains(err.Error(), "Network error") {
		t.Fatalf("unexpected network error: %T %v", err, err)
	}
}

func TestConfigurationContextAndBaseURLSafety(t *testing.T) {
	if _, err := NewWaaSClient(ProjectConfig{BaseURL: "http://api.example.com", ProjectID: testProjectID, APIKey: testAPIKey}); err == nil {
		t.Fatal("expected non-loopback HTTP base URL to fail")
	}
	if _, err := NewWaaSClient(ProjectConfig{BaseURL: "https://user:pass@api.example.com", ProjectID: testProjectID, APIKey: testAPIKey}); err == nil {
		t.Fatal("expected base URL user information to fail")
	}
	if _, err := NewWaaSClient(ProjectConfig{BaseURL: "https://api.example.com?x=1", ProjectID: testProjectID, APIKey: testAPIKey}); err == nil {
		t.Fatal("expected base URL query to fail")
	}
	got, err := normalizeBaseURL("https://api.example.com/root%20path/")
	if err != nil {
		t.Fatal(err)
	}
	if got != "https://api.example.com/root%20path" {
		t.Fatalf("encoded base path changed: %q", got)
	}

	waas := waasClientWithTransport(t, roundTripFunc(func(_ *http.Request) (*http.Response, error) {
		return jsonResponse(http.StatusOK, `{"code":"00000","msg":"ok","data":{}}`), nil
	}))
	if _, err := waas.QueryProjectCoins(nil); err == nil {
		t.Fatal("expected nil context to fail")
	}
	var request *QueryPayoutRequest
	if _, err := waas.QueryPayout(context.Background(), request); err == nil {
		t.Fatal("expected typed nil request to fail")
	}
}

func projectClientWithTransport(t *testing.T, transport http.RoundTripper) *PaymentClient {
	t.Helper()
	client, err := NewPaymentClient(ProjectConfig{
		BaseURL: "https://sandbox.example", ProjectID: testProjectID, APIKey: testAPIKey,
		HTTPClient: &http.Client{Transport: transport, Timeout: time.Minute},
	})
	if err != nil {
		t.Fatal(err)
	}
	return client
}

func waasClientWithTransport(t *testing.T, transport http.RoundTripper) *WaaSClient {
	t.Helper()
	client, err := NewWaaSClient(ProjectConfig{
		BaseURL: "https://sandbox.example", ProjectID: testProjectID, APIKey: testAPIKey,
		HTTPClient: &http.Client{Transport: transport},
	})
	if err != nil {
		t.Fatal(err)
	}
	return client
}

func teamClientWithTransport(t *testing.T, transport http.RoundTripper) *TeamClient {
	t.Helper()
	client, err := NewTeamClient(TeamConfig{
		BaseURL: "https://sandbox.example", AccessKey: testAccessKey, AccessSecret: testAccessSecret,
		HTTPClient: &http.Client{Transport: transport},
	})
	if err != nil {
		t.Fatal(err)
	}
	return client
}

func TestPointerHelpers(t *testing.T) {
	if !reflect.DeepEqual([]any{*String("x"), *Bool(true), *Int(1), *Int32(2), *Int64(3), *Float32(4), *Float64(5)}, []any{"x", true, 1, int32(2), int64(3), float32(4), float64(5)}) {
		t.Fatal("pointer helpers returned unexpected values")
	}
}
