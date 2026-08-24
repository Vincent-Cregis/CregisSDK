# Cregis SDK for TypeScript and Node.js

The official Node.js SDK for Cregis Payment Engine, WaaS, and Team APIs. It
supports Node.js 22 or newer and ships ESM, CommonJS, and TypeScript declarations.

## Install

```bash
npm install @cregis/sdk
```

## WaaS

```ts
import { CregisWaasClient } from "@cregis/sdk";

const waas = new CregisWaasClient({
  baseUrl: process.env.WAAS_ENDPOINT!,
  pid: process.env.WAAS_PID!,
  apiKey: process.env.WAAS_API_KEY!,
});

const coins = await waas.queryProjectCoins();
```

## Payment Engine

```ts
import { CregisPaymentClient } from "@cregis/sdk";

const payment = new CregisPaymentClient({
  baseUrl: process.env.PAYMENT_ENDPOINT!,
  pid: process.env.PAYMENT_PID!,
  apiKey: process.env.PAYMENT_API_KEY!,
});

const order = await payment.queryOrder({ cregis_id: "po_..." });
```

## Team API

```ts
import { CregisTeamClient } from "@cregis/sdk";

const team = new CregisTeamClient({
  baseUrl: process.env.TEAM_ENDPOINT!,
  accessKey: process.env.TEAM_ACCESS_KEY!,
  accessSecret: process.env.TEAM_ACCESS_SECRET!,
});

const wallets = await team.listTeamWallets({ page_num: 1, page_size: 10 });
```

The SDK preserves the JSON field names documented by Cregis. Payment Engine
and WaaS credentials add `pid`, `nonce`, `timestamp`, and `sign` automatically.
Team API credentials are sent through the four signed `Access-*` headers.

## Errors

- `CregisHttpError`: the server returned a non-2xx HTTP status.
- `CregisApiError`: the JSON envelope contains a Cregis code other than `00000`.
- `CregisClientError`: request validation, JSON, network, or timeout failure.

Signed POST requests are not retried and redirects are not followed. A custom
`fetch` function, timeout, and metadata-only logger may be supplied in the
client constructor.

## Webhooks

```ts
import { CregisPaymentCallbackHandler } from "@cregis/sdk";

const handler = new CregisPaymentCallbackHandler(process.env.PAYMENT_API_KEY!);
const event = handler.verifyAndParse(rawRequestBody);

// Return this exact plain-text value after processing succeeds.
const responseBody = CregisPaymentCallbackHandler.CALLBACK_SUCCESS;
```

`CregisWaasCallbackHandler` verifies and parses deposit, payout, external payout
verification, and sub-address withdrawal callbacks. Verification uses a
constant-time signature comparison and validates required wire fields and types.

## Development

```bash
npm ci
npm run verify
```

`npm run test:sandbox` loads `.env` when present and requires credentials for
all three read-only Sandbox suites. Missing credentials make the command fail;
they cannot be reported as skipped tests. The current safe integration suite
covers 9 read-only operations (Payment Engine 1, WaaS 2, Team API 6). Mutating
operations require dedicated disposable fixtures and are never run implicitly.

Run every callable Sandbox operation, including state-changing Payment and
WaaS requests, only with an explicit mutation opt-in:

```bash
CREGIS_ALLOW_MUTATING_TESTS=true npm run test:sandbox:all
```

The full suite covers all 23 OpenAPI operations. It creates a Payment order and
WaaS addresses and submits two payouts, one sub-address withdrawal, and one
collection. `WITHDRAW_ADDRESS` is required. `WAAS_TEST_AMOUNT` defaults to
`0.001`; optional destination and chain overrides use the same environment
variables documented for the Java integration suite. Never point this command
at production endpoints. The runner also refuses full-suite endpoints unless
they use HTTPS and match the Cregis Sandbox hostname pattern
`t-*.cregis.dev`.

OpenAPI request and successful-response data are validated at runtime. JSON
`int64` values must fit JavaScript's safe-integer range; the SDK throws a
`CregisClientError` instead of silently rounding a larger value.

Regenerate OpenAPI-derived types from the repository root:

```bash
./codegen/scripts/generate-typescript-models.sh \
  --spec-dir ../cregis-developer-docs/api-sources/specs
```

The canonical OpenAPI files stay in `cregis-developer-docs`; this package only
commits generated types and a reproducibility lock.

The only production dependency is the Apache-2.0 `canonicalize` reference
implementation maintained by RFC 8785 contributors. Version `2.1.0` is pinned
because it supports both the ESM and CommonJS builds; deterministic RFC and
cross-language signature vectors are part of the test suite.
