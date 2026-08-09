# Java SDK hardening and generation-boundary design

## Context

The current Java SDK is a complete handwritten baseline for the Payment
Engine, WaaS, and Team APIs. All 23 callable OpenAPI operations compile and
have passed Sandbox verification. This design hardens that baseline before it
becomes the template for the other language SDKs.

The goal is not to replace the working clients. It is to make their public
behavior safe and stable while creating a strict boundary for future OpenAPI
generation.

## Approaches considered

### Replace the SDK with standard generated Java

This gives the shortest path to generated code, but standard output owns the
HTTP client, authentication surface, model names, and error types. It cannot
infer Cregis body signing, Team canonical signing, or callback verification.
The resulting public API would also differ substantially from the tested SDK.

### Keep the entire SDK handwritten and add only a drift report

This preserves current behavior and is initially simple. It does not meet the
long-term requirement: every OpenAPI change would still require manual model
and operation edits in every language.

### Keep a handwritten runtime and isolate generated wire code

This is the selected approach. Stable public clients and all security-sensitive
runtime code remain handwritten. OpenAPI-owned operation metadata and wire DTOs
live under a generated package and may be replaced as a unit. A generation run
must never modify handwritten files.

## Public architecture

The three public clients remain:

- `CregisPaymentClient`
- `CregisWaasClient`
- `CregisTeamClient`

They keep explicit project- or team-specific endpoints and credentials. The
clients delegate transport, response decoding, and authentication to the
handwritten runtime. They expose OpenAPI-aligned request and response models.

The long-term package boundary is:

```text
com.cregis.sdk.client                 stable public clients
com.cregis.sdk.runtime                handwritten transport and security code
com.cregis.sdk.generated.payment      generated Payment wire code
com.cregis.sdk.generated.waas         generated WaaS wire code
com.cregis.sdk.generated.team         generated Team wire code
```

The first generation proof of concept may generate into a staging directory
and compare output before public models move packages. A package move must be a
deliberate pre-GA change, not a side effect of running the generator.

## Transport and retry behavior

The SDK creates a secure default HTTP client but accepts an application-owned
`OkHttpClient` as a base. SDK authentication and safe logging interceptors are
always applied by cloning the supplied client with `newBuilder()`.

Defaults are:

- 30-second connect, read, and write timeouts.
- Connection-failure retry disabled because every Cregis operation is POST and
  several operations create financial state.
- HTTP and HTTPS redirects disabled so signed requests cannot be forwarded to
  another origin.
- HTTPS required for non-loopback endpoints.

Builders expose timeout and connection-retry overrides. Retry is an explicit
opt-in and documentation warns callers to use `order_id`, `third_party_id`, or
the returned `cid` for reconciliation after an ambiguous failure.

Invalid requests, serialization failures, malformed responses, and network
failures use client-side exceptions. Non-2xx responses retain HTTP status and
body. Business failures retain Cregis `code` and `msg`.

## Webhook behavior

Webhook code remains fully handwritten. Verification must operate on the raw
request body, compare signatures in constant time, and only deserialize after
successful verification.

Payment callback payloads are discriminated by `event_type`. Event-specific
data types represent payment, expiration, refund, and remaining-payment data.
The legacy combined data accessor may remain during the release-candidate
period only when it can be implemented without weakening type safety.

Webhook handlers validate required envelope fields. Timestamp freshness and
business idempotency remain application concerns because Cregis may retry a
valid callback. The SDK documents a recommended idempotency key but does not
store callback state.

Nested callback signature encoding requires an independent backend fixture.
Tests generated and verified by the same signer are insufficient evidence. The
implementation keeps the current canonical JSON rule until a backend vector is
available, marks that rule explicitly, and maintains a fixture test boundary
that can accept a sanitized real callback without code changes.

## Generation and drift checks

The documentation repository remains the source of truth. Specifications are
not permanently copied into this repository.

The Java codegen command accepts an explicit `--spec-dir`. It validates that
the three expected specifications exist, records their hashes and operation
inventory, and generates only into a disposable staging directory. A drift
check compares the current 23-operation Java surface with the OpenAPI method,
path, and operation ID inventory and fails on additions, removals, or path
changes.

Schema generation follows after operation drift is reliable. Generated output
is reproducible by pinning the generator image or CLI version and checking a
manifest into `codegen/configs`.

## Verification gates

Before Java GA:

1. Java 11 unit and contract tests pass.
2. Java 11, 17, and 21 CI passes.
3. All 23 OpenAPI operations are present.
4. Authentication has independent deterministic vectors.
5. Redirects and connection retries have explicit tests.
6. Webhook signature failure and event dispatch have explicit tests.
7. A sanitized real nested Payment callback or backend-provided signature
   vector verifies successfully.
8. A local OpenAPI drift command detects an added or changed operation.

