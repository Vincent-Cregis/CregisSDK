# Java SDK Sandbox All-Endpoints Test Design

## Scope

Exercise every callable operation defined by the canonical OpenAPI documents:

- Payment Engine: 2 operations
- WaaS: 15 operations
- Team API: 6 operations

The five OpenAPI webhook definitions are inbound callbacks rather than callable Sandbox operations. They remain covered by deterministic local callback signature and deserialization tests.

The OpenAPI fetch script remains out of scope.

## Safety boundaries

All live requests must use the configured `.dev` Sandbox Base URLs. Credentials stay in the ignored `sdks/java/.env` file and must never appear in test output.

Tests are divided into three groups:

1. Read-only queries run without an opt-in flag.
2. Address and order creation tests require `CREGIS_ALLOW_MUTATING_TESTS=true`.
3. Payout, withdrawal, and collection tests also require that flag and use the smallest configured test amount, defaulting to `0.001`.

The opt-in is supplied only to the Maven process that runs the state-changing suite. It is not persisted as enabled in `.env`.

## WaaS data flow

The suite first queries supported project coins and selects a coin for the configured Sandbox chain, preferring chain `198`. It then creates a project address and reuses that address for update, ownership validation, legality validation, balance queries, and as an internal destination for payout and withdrawal tests.

Every submitted payout or withdrawal uses a unique `third_party_id`. A successful submission stores its `cid`, which is then used by the corresponding query operation. Collection uses the configured project source address and a configured wallet destination when available. If a required business resource is missing or has insufficient Sandbox funds, the test records the returned business error without substituting production data.

## Payment and Team data flow

Payment Engine creates one Sandbox order and immediately queries that order. The separate read-only test continues to query a previously created order.

Team API first lists wallets. If at least one wallet is returned, its wallet ID and available token metadata drive the remaining five queries. An empty team is a valid list result but blocks resource-dependent checks. Invalid or disabled credentials are reported as an environment blocker rather than worked around with undocumented Team ID parameters.

## Result reporting

Each OpenAPI path has a distinct test method or an explicit local contract assertion. Results are reported as passed, failed due to SDK/contract behavior, or blocked by Sandbox configuration/business state. No secrets, full request bodies, addresses, or transaction identifiers are printed.
