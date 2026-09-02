# Go SDK design

## Scope

The Go SDK exposes all 23 Payment Engine, WaaS, and Team API operations from
the canonical OpenAPI specifications. It targets Go 1.21 and later and uses
synchronous `net/http` calls with `context.Context` on every operation.

## Public surface

Consumers import one module package:

```go
import cregis "github.com/Vincent-Cregis/CregisSDK/sdks/go"
```

The root package exports `PaymentClient`, `WaaSClient`, and `TeamClient`, along
with aliases for every generated request, response, and webhook model. Client
methods use concrete request and response types and return typed SDK errors.

## Generated and handwritten boundaries

OpenAPI generation owns:

- `generated/{payment,waas,team}/*.gen.go`
- `models_aliases.gen.go`
- `clients.gen.go`
- `codegen/manifests/go-models.lock.json`

Authentication, HTTP transport, errors, retry policy, and webhook processing
remain handwritten. The generator reads the shared operation/model mapping,
checks all canonical operations before writing output, and supports a
byte-for-byte `--check` mode.

Generated request decoders reject undocumented JSON fields. Required fields
are always serialized. OpenAPI constraints, named enum values, nested model
constraints, and cross-field `anyOf` requirements are checked before requests
are sent. Generated response and webhook decoders reject missing required
fields, incorrect declared field types, and invalid documented values while
ignoring new response fields for forward compatibility.

## Transport and authentication

All requests use HTTPS, except loopback HTTP for local tests. Redirects are not
followed, automatic retries are disabled, and non-2xx HTTP failures, API code
failures, contract failures, and client/network failures retain distinct error
types.

Payment Engine and WaaS requests add `pid`, a six-character nonce, a
millisecond timestamp, and the existing sorted-parameter MD5 signature. Team
requests send RFC 8785 canonical JSON and the existing HMAC-SHA256 headers.
The canonicalizer rejects integer values outside the interoperable IEEE-754
safe range instead of changing their value. Project signatures preserve the
exact decimal representation of all signed `int64` fields.

## Webhooks

Webhook helpers verify the project signature using a constant-time comparison
before decoding generated models. Payment callbacks dispatch all documented
event types to their concrete data model. WaaS provides typed handlers for
deposit, payout, external verification, and withdrawal callbacks.

Verification rejects stale and excessively future-dated callbacks by default,
can pin an expected project ID, and accepts an application-provided atomic
replay guard. The guard is intentionally injected so multi-instance services
can use shared durable storage. Business handlers still apply idempotency to
the callback's order or transaction identity.

## Verification

Local tests cover signing vectors and integer boundaries, request/response and
webhook constraints, strict request decoding, every generated operation,
redirects, errors, timestamp/replay protection, and webhook dispatch. Sandbox tests are opt-in;
mutating Payment/WaaS cases require an additional explicit environment flag.
