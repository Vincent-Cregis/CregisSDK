package cregis

import (
	"context"
	"fmt"
	"net/http"
	"os"
	"strconv"
	"strings"
	"sync"
	"testing"
	"time"
)

func sandboxEnabled(names ...string) bool {
	if os.Getenv("CREGIS_RUN_SANDBOX_TESTS") != "true" {
		return false
	}
	for _, name := range names {
		if strings.TrimSpace(os.Getenv(name)) == "" {
			return false
		}
	}
	return true
}

func readonlySandboxEnabled(names ...string) bool {
	return os.Getenv("CREGIS_SANDBOX_SUITE") != "all" && sandboxEnabled(names...)
}

func mutatingSandboxEnabled(names ...string) bool {
	return os.Getenv("CREGIS_SANDBOX_SUITE") == "all" &&
		os.Getenv("CREGIS_ALLOW_MUTATING_TESTS") == "true" &&
		sandboxEnabled(names...)
}

func sandboxProjectID(t *testing.T, name string) int64 {
	t.Helper()
	value, err := strconv.ParseInt(strings.TrimSpace(os.Getenv(name)), 10, 64)
	if err != nil {
		t.Fatalf("%s must be an int64: %v", name, err)
	}
	return value
}

func sandboxID(prefix string) string {
	return fmt.Sprintf("%s-%d", prefix, time.Now().UnixMilli())
}

func sandboxAlias(prefix string) string {
	value := strconv.FormatInt(time.Now().UnixMilli(), 10)
	if len(value) > 8 {
		value = value[len(value)-8:]
	}
	return prefix + "-" + value
}

type coverageTransport struct {
	base    http.RoundTripper
	mutex   sync.Mutex
	covered map[string]struct{}
}

func newCoverageHTTPClient() (*http.Client, *coverageTransport) {
	transport := &coverageTransport{
		base:    http.DefaultTransport,
		covered: make(map[string]struct{}),
	}
	return &http.Client{Transport: transport}, transport
}

func (transport *coverageTransport) RoundTrip(request *http.Request) (*http.Response, error) {
	transport.mutex.Lock()
	transport.covered[request.Method+" "+request.URL.Path] = struct{}{}
	transport.mutex.Unlock()
	return transport.base.RoundTrip(request)
}

func (transport *coverageTransport) assertCovered(t *testing.T, paths []string) {
	t.Helper()
	transport.mutex.Lock()
	defer transport.mutex.Unlock()
	for _, path := range paths {
		key := http.MethodPost + " " + path
		if _, ok := transport.covered[key]; !ok {
			t.Errorf("Sandbox did not call %s", key)
		}
	}
	if len(transport.covered) != len(paths) {
		t.Errorf("Sandbox called %d unique operations, want %d", len(transport.covered), len(paths))
	}
}

func TestSandboxWaaSReadOnlyOperations(t *testing.T) {
	if !readonlySandboxEnabled("WAAS_PID", "WAAS_API_KEY", "WAAS_ENDPOINT") {
		t.Skip("read-only WaaS Sandbox credentials are not enabled")
	}
	client, err := NewWaaSClient(ProjectConfig{
		BaseURL: os.Getenv("WAAS_ENDPOINT"), ProjectID: sandboxProjectID(t, "WAAS_PID"), APIKey: os.Getenv("WAAS_API_KEY"),
	})
	if err != nil {
		t.Fatal(err)
	}
	defer client.CloseIdleConnections()
	coins, err := client.QueryProjectCoins(context.Background())
	if err != nil {
		t.Fatal(err)
	}
	if len(coins.AddressCoins) == 0 || coins.AddressCoins[0].Decimals == nil {
		t.Fatal("Sandbox returned no address coin with decimals")
	}
	trades, err := client.QueryTradeRecords(context.Background(), &TradeRecordQueryRequest{PageNum: Int32(1), PageSize: Int32(10)})
	if err != nil {
		t.Fatal(err)
	}
	if trades.PageNum == nil || trades.Rows == nil {
		t.Fatal("Sandbox trade page omitted pagination or rows")
	}
}

func TestSandboxPaymentReadOnlyQuery(t *testing.T) {
	if !readonlySandboxEnabled("PAYMENT_PID", "PAYMENT_API_KEY", "PAYMENT_ENDPOINT", "PAYMENT_CREGIS_ID") {
		t.Skip("read-only Payment Sandbox credentials are not enabled")
	}
	client, err := NewPaymentClient(ProjectConfig{
		BaseURL: os.Getenv("PAYMENT_ENDPOINT"), ProjectID: sandboxProjectID(t, "PAYMENT_PID"), APIKey: os.Getenv("PAYMENT_API_KEY"),
	})
	if err != nil {
		t.Fatal(err)
	}
	defer client.CloseIdleConnections()
	cregisID := strings.TrimSpace(os.Getenv("PAYMENT_CREGIS_ID"))
	order, err := client.QueryOrder(context.Background(), &QueryOrderRequest{CregisID: cregisID})
	if err != nil {
		t.Fatal(err)
	}
	if order.CregisID == nil || *order.CregisID != cregisID {
		t.Fatalf("queried Cregis ID = %v, want %q", order.CregisID, cregisID)
	}
}

func TestSandboxAllPaymentOperations(t *testing.T) {
	if !mutatingSandboxEnabled("PAYMENT_PID", "PAYMENT_API_KEY", "PAYMENT_ENDPOINT") {
		t.Skip("full mutating Payment Sandbox suite is not enabled")
	}
	httpClient, coverage := newCoverageHTTPClient()
	client, err := NewPaymentClient(ProjectConfig{
		BaseURL: os.Getenv("PAYMENT_ENDPOINT"), ProjectID: sandboxProjectID(t, "PAYMENT_PID"), APIKey: os.Getenv("PAYMENT_API_KEY"), HTTPClient: httpClient,
	})
	if err != nil {
		t.Fatal(err)
	}
	created, err := client.CreateOrder(context.Background(), &CreateOrderRequest{
		OrderID: sandboxID("go-sdk-order"), OrderAmount: "1.0", OrderCurrency: "USDT",
		PayerID: "go-sdk-sandbox", PayerName: String("Go SDK Sandbox"),
		CallbackURL: String("https://webhook.site/test"), SuccessURL: "https://example.com/success",
		CancelURL: "https://example.com/cancel", Remark: String("Go SDK Sandbox test"),
		ValidTime: Int(60), Language: Ptr(CreateOrderRequestLanguageSc), UnderpaidTolerance: Float32(0.1), OverpaidTolerance: Float32(0.1),
	})
	if err != nil {
		t.Fatal(err)
	}
	if created.CregisID == nil || created.CheckoutURL == nil {
		t.Fatal("created order omitted cregis_id or checkout_url")
	}
	queried, err := client.QueryOrder(context.Background(), &QueryOrderRequest{CregisID: *created.CregisID})
	if err != nil {
		t.Fatal(err)
	}
	if queried.CregisID == nil || *queried.CregisID != *created.CregisID {
		t.Fatal("queried order did not match created order")
	}
	coverage.assertCovered(t, []string{"/api/v2/checkout", "/api/v2/order/info"})
}

func TestSandboxAllWaaSOperations(t *testing.T) {
	if !mutatingSandboxEnabled("WAAS_PID", "WAAS_API_KEY", "WAAS_ENDPOINT", "WITHDRAW_ADDRESS") {
		t.Skip("full mutating WaaS Sandbox suite is not enabled")
	}
	ctx := context.Background()
	httpClient, coverage := newCoverageHTTPClient()
	client, err := NewWaaSClient(ProjectConfig{
		BaseURL: os.Getenv("WAAS_ENDPOINT"), ProjectID: sandboxProjectID(t, "WAAS_PID"), APIKey: os.Getenv("WAAS_API_KEY"), HTTPClient: httpClient,
	})
	if err != nil {
		t.Fatal(err)
	}
	coins, err := client.QueryProjectCoins(ctx)
	if err != nil {
		t.Fatal(err)
	}
	addressChains := make(map[string]struct{})
	for _, coin := range coins.AddressCoins {
		if coin.ChainID != nil {
			addressChains[*coin.ChainID] = struct{}{}
		}
	}
	preferredChain := os.Getenv("WAAS_CHAIN_ID")
	if preferredChain == "" {
		preferredChain = "198"
	}
	var selected *ProjectCoin
	for index := range coins.PayoutCoins {
		coin := &coins.PayoutCoins[index]
		if coin.ChainID != nil && coin.TokenID != nil {
			if _, ok := addressChains[*coin.ChainID]; ok && *coin.ChainID == preferredChain {
				selected = coin
				break
			}
		}
	}
	if selected == nil {
		for index := range coins.PayoutCoins {
			coin := &coins.PayoutCoins[index]
			if coin.ChainID != nil && coin.TokenID != nil {
				if _, ok := addressChains[*coin.ChainID]; ok {
					selected = coin
					break
				}
			}
		}
	}
	if selected == nil {
		t.Fatal("no Sandbox coin supports both address creation and payout")
	}
	chainID := *selected.ChainID
	currency := chainID + "@" + *selected.TokenID
	generated, err := client.GenerateAddress(ctx, &GenerateAddressRequest{ChainID: chainID, Alias: String(sandboxAlias("go-sdk"))})
	if err != nil || generated.Address == nil {
		t.Fatalf("generate address failed: %v", err)
	}
	address := *generated.Address
	batch, err := client.BatchGenerateAddress(ctx, &BatchGenerateAddressRequest{ChainID: chainID, Number: "2", Alias: String(sandboxAlias("go-batch"))})
	if err != nil || len(batch) == 0 || batch[0].Address == nil {
		t.Fatalf("batch generate address failed: %v", err)
	}
	if err := client.UpdateAddress(ctx, &AddressUpdateRequest{Address: address, Alias: String(sandboxAlias("go-updated"))}); err != nil {
		t.Fatal(err)
	}
	validated, err := client.ValidateAddress(ctx, &ValidateAddressRequest{ChainID: chainID, Address: address})
	if err != nil || validated.Result == nil || !*validated.Result {
		t.Fatalf("validate address failed: value=%v error=%v", validated, err)
	}
	legal, err := client.CheckAddressLegality(ctx, &CheckAddressLegalityRequest{ChainID: chainID, Address: address})
	if err != nil || legal.Result == nil || !*legal.Result {
		t.Fatalf("check address legality failed: value=%v error=%v", legal, err)
	}
	source := strings.TrimSpace(os.Getenv("WITHDRAW_ADDRESS"))
	if _, err := client.QueryAddressBalance(ctx, &AddressBalanceRequest{Currency: currency, Address: String(source), PageNum: Int32(1), PageSize: Int32(10)}); err != nil {
		t.Fatal(err)
	}
	if _, err := client.QueryAddressBalanceV2(ctx, &AddressBalanceV2Request{Address: source, Currency: String(currency), PageNum: Int32(1), PageSize: Int32(10)}); err != nil {
		t.Fatal(err)
	}
	if _, err := client.QueryTradeRecords(ctx, &TradeRecordQueryRequest{PageNum: Int32(1), PageSize: Int32(10)}); err != nil {
		t.Fatal(err)
	}
	amount := os.Getenv("WAAS_TEST_AMOUNT")
	if amount == "" {
		amount = "0.001"
	}
	destination := os.Getenv("WAAS_PAYOUT_TO_ADDRESS")
	if destination == "" {
		destination = address
	}
	payoutV1, err := client.PayoutV1(ctx, &PayoutV1Request{Currency: currency, Address: destination, Amount: amount, ThirdPartyID: sandboxID("go-sdk-p1"), Remark: String("Go SDK Sandbox test")})
	if err != nil || payoutV1.CID == nil {
		t.Fatalf("payout v1 failed: %v", err)
	}
	payout, err := client.QueryPayout(ctx, &QueryPayoutRequest{CID: *payoutV1.CID})
	if err != nil {
		t.Fatal(err)
	}
	payoutV2Request := &PayoutRequest{Currency: currency, ToAddress: destination, Amount: amount, ThirdPartyID: sandboxID("go-sdk-p2"), Remark: String("Go SDK Sandbox test")}
	if walletID := strings.TrimSpace(os.Getenv("WAAS_WALLET_ID")); walletID != "" {
		parsed, parseErr := strconv.ParseInt(walletID, 10, 64)
		if parseErr != nil {
			t.Fatalf("WAAS_WALLET_ID must be an int64: %v", parseErr)
		}
		payoutV2Request.WalletID = Int64(parsed)
	}
	payoutV2, err := client.PayoutV2(ctx, payoutV2Request)
	if err != nil || payoutV2.CID == nil {
		t.Fatalf("payout v2 failed: %v", err)
	}
	withdrawTo := os.Getenv("WITHDRAW_TO_ADDRESS")
	if withdrawTo == "" {
		withdrawTo = address
	}
	withdrawal, err := client.Withdrawal(ctx, &WithdrawalRequest{Currency: currency, FromAddress: source, ToAddress: withdrawTo, Amount: amount, ThirdPartyID: sandboxID("go-sdk-wd"), Remark: String("Go SDK Sandbox test")})
	if err != nil || withdrawal.CID == nil {
		t.Fatalf("withdrawal failed: %v", err)
	}
	if _, err := client.QueryWithdrawal(ctx, &QueryWithdrawalRequest{CID: *withdrawal.CID}); err != nil {
		t.Fatal(err)
	}
	collectionTo := os.Getenv("WAAS_COLLECTION_TO_ADDRESS")
	if collectionTo == "" && payout.FromAddress != nil {
		collectionTo = *payout.FromAddress
	}
	if collectionTo == "" {
		t.Fatal("collection destination is unavailable")
	}
	collection, err := client.BalanceCollect(ctx, &BalanceCollectRequest{Currency: currency, FromAddress: source, ToAddress: collectionTo, Amount: String(amount)})
	if err != nil || collection.CID == nil {
		t.Fatalf("balance collection failed: %v", err)
	}
	coverage.assertCovered(t, []string{
		"/api/v1/address/create", "/api/v1/batch/address/create", "/api/v1/address/update",
		"/api/v1/address/inner", "/api/v1/address/legal", "/api/v1/payout", "/api/v2/payout",
		"/api/v1/sub_address_withdrawal", "/api/v1/collection", "/api/v1/coins", "/api/v1/trade/page",
		"/api/v1/payout/query", "/api/v1/sub_address_withdrawal/info", "/api/v1/sub_address_balance", "/api/v2/sub_address_balance",
	})
}

func TestSandboxAllTeamOperations(t *testing.T) {
	if !sandboxEnabled("TEAM_ACCESS_KEY", "TEAM_ACCESS_SECRET", "TEAM_ENDPOINT") {
		t.Skip("Team Sandbox credentials are not enabled")
	}
	ctx := context.Background()
	httpClient, coverage := newCoverageHTTPClient()
	client, err := NewTeamClient(TeamConfig{
		BaseURL: os.Getenv("TEAM_ENDPOINT"), AccessKey: os.Getenv("TEAM_ACCESS_KEY"), AccessSecret: os.Getenv("TEAM_ACCESS_SECRET"), HTTPClient: httpClient,
	})
	if err != nil {
		t.Fatal(err)
	}
	wallets, err := client.ListTeamWallets(ctx, &ListTeamWalletsRequest{PageNum: Int32(1), PageSize: Int32(10)})
	if err != nil {
		t.Fatal(err)
	}
	if len(wallets.Rows) == 0 || len(wallets.Rows[0].Tokens) == 0 {
		t.Fatal("Sandbox returned no Team wallet with tokens")
	}
	wallet := wallets.Rows[0]
	token := wallet.Tokens[0]
	addresses, err := client.ListTeamWalletAddresses(ctx, &ListTeamWalletAddressesRequest{WalletID: wallet.WalletID, ChainID: token.ChainID, PageNum: Int32(1), PageSize: Int32(10)})
	if err != nil {
		t.Fatal(err)
	}
	if _, err := client.QueryTeamWalletBalance(ctx, &QueryTeamWalletBalanceRequest{WalletID: wallet.WalletID, ChainID: String(token.ChainID), TokenID: String(token.TokenID), PageNum: Int32(1), PageSize: Int32(10)}); err != nil {
		t.Fatal(err)
	}
	addressBalance := &QueryTeamWalletAddressBalanceRequest{WalletID: wallet.WalletID, ChainID: String(token.ChainID), TokenID: String(token.TokenID), PageNum: Int32(1), PageSize: Int32(10)}
	if len(addresses.Rows) > 0 {
		addressBalance.Address = String(addresses.Rows[0].Address)
	}
	if _, err := client.QueryTeamWalletAddressBalance(ctx, addressBalance); err != nil {
		t.Fatal(err)
	}
	if _, err := client.QueryTeamWalletHistoryTransactions(ctx, &QueryTeamWalletHistoryTransactionsRequest{WalletID: wallet.WalletID, ChainID: String(token.ChainID), TokenID: String(token.TokenID), PageNum: Int32(1), PageSize: Int32(10)}); err != nil {
		t.Fatal(err)
	}
	if _, err := client.QueryTeamWalletProcessingTransactions(ctx, &QueryTeamWalletProcessingTransactionsRequest{WalletID: wallet.WalletID, ChainID: String(token.ChainID), TokenID: String(token.TokenID), PageNum: Int32(1), PageSize: Int32(10)}); err != nil {
		t.Fatal(err)
	}
	coverage.assertCovered(t, []string{
		"/openapi/v1/wallets", "/openapi/v1/wallet_address", "/openapi/v1/wallet_balance",
		"/openapi/v1/wallet_address_balance", "/openapi/v1/wallet_history_transaction_info", "/openapi/v1/wallet_processing_transaction_info",
	})
}
