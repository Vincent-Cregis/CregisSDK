# Cregis Go SDK

Synchronous Go client for the Cregis Payment Engine, WaaS, and Team APIs. It
supports all 23 documented operations, generated OpenAPI models, both request
signature schemes, and verified webhook parsing.

## Requirements

- Go 1.21 or later

## Install

```bash
go get github.com/Vincent-Cregis/CregisSDK/sdks/go
```

## Payment Engine

```go
package main

import (
    "context"
    "log"

    cregis "github.com/Vincent-Cregis/CregisSDK/sdks/go"
)

func main() {
    client, err := cregis.NewPaymentClient(cregis.ProjectConfig{
        ProjectID: 123456789,
        APIKey:    "your-api-key",
        BaseURL:   "https://your-cregis-endpoint.example",
    })
    if err != nil {
        log.Fatal(err)
    }

    order, err := client.CreateOrder(context.Background(), &cregis.CreateOrderRequest{
        OrderID:       "merchant-order-1",
        OrderAmount:   "10.00",
        OrderCurrency: "USD",
        PayerID:       "customer-1",
        SuccessURL:    "https://merchant.example/success",
        CancelURL:     "https://merchant.example/cancel",
    })
    if err != nil {
        log.Fatal(err)
    }
    log.Println(*order.CregisID)
}
```

Optional generated fields use pointers so omitted values stay distinct from
zero values. Helpers such as `cregis.String`, `cregis.Int64`, and
`cregis.Float32` make struct literals concise. Named enum fields use generated
constants and the generic `cregis.Ptr` helper, for example:

```go
request := &cregis.ListTeamWalletsRequest{
    WalletType: cregis.Ptr(cregis.ListTeamWalletsRequestWalletTypeSingleSign),
}
```

## Team API

```go
client, err := cregis.NewTeamClient(cregis.TeamConfig{
    AccessKey:    "your-access-key",
    AccessSecret: "your-access-secret",
    BaseURL:      "https://your-cregis-endpoint.example",
})
```

## WaaS

WaaS uses the same `ProjectConfig` as Payment Engine:

```go
client, err := cregis.NewWaaSClient(cregis.ProjectConfig{
    ProjectID: 123456789,
    APIKey:    "your-api-key",
    BaseURL:   "https://your-cregis-endpoint.example",
})

coins, err := client.QueryProjectCoins(context.Background())
```

Every operation accepts `context.Context`. The SDK does not retry POST requests
or follow redirects because several operations create financial state.

Team API signatures use RFC 8785 canonical JSON. Integer request values must
therefore stay within the exact interoperable range from
`-9007199254740991` through `9007199254740991`; the SDK rejects unsafe values
before sending them instead of silently rounding them.

## Webhooks

Webhook handlers verify signatures and, by default, reject callbacks older
than 10 minutes or more than one minute in the future. Pin the expected project
and provide an atomic replay guard backed by shared storage in production:

```go
handler, err := cregis.NewPaymentCallbackHandler(
    "your-api-key",
    cregis.WithExpectedProjectID(123456789),
    cregis.WithWebhookReplayGuard(yourReplayGuard),
)
```

`yourReplayGuard` implements `WebhookReplayGuard.CheckAndStore` and should
atomically store `(projectID, nonce, timestamp)` for at least the accepted
callback window. Timestamp checking limits old replays; the guard and your
business-level idempotency check prevent duplicate processing inside that
window and across application instances.

If an intentionally delayed manual callback retry retains its original
timestamp, pass `cregis.WithWebhookMaxAge(0)` only for that controlled path and
keep the replay/idempotency checks enabled.

## Errors

Use `errors.As` to distinguish `*cregis.HTTPError`, `*cregis.APIError`,
`*cregis.ContractError`, and `*cregis.ClientError`.

The SDK has one runtime dependency: the Apache-2.0-licensed
`github.com/gowebpki/jcs` implementation used for Team API RFC 8785 JSON
canonicalization. HTTP and JSON handling use the Go standard library.

## Development

From the repository root, regenerate models from the canonical specifications:

```bash
./codegen/scripts/generate-go-models.sh \
  --spec-dir ../cregis-developer-docs/api-sources/specs
```

Use `--check` for byte-for-byte drift detection. Run local tests with:

```bash
cd sdks/go
go test ./...
```

Sandbox tests are disabled unless `CREGIS_RUN_SANDBOX_TESTS=true`. The default
suite only queries existing Payment/WaaS data and runs the six read-only Team
operations. Mutating coverage additionally requires both
`CREGIS_SANDBOX_SUITE=all` and `CREGIS_ALLOW_MUTATING_TESTS=true`.
