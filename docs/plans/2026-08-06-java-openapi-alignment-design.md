# Java SDK OpenAPI alignment design

## Decision

The Chinese Payment Engine, WaaS, and Team API OpenAPI files in
`cregis-developer-docs/api-sources/specs` are the authoritative wire contract.
The existing Java SDK is still a pre-release `1.0.0-SNAPSHOT`, so incorrect
public fields and types may be changed instead of preserving incompatible
behavior.

This phase does not implement the OpenAPI fetch/update script. It creates a
complete, tested Java baseline that the later generator can reproduce.

## Chosen approach

Keep the public convenience clients and handwritten security runtime, while
aligning every operation and wire model with OpenAPI. Generated operations and
models will be introduced only after this baseline is stable. This avoids
mixing generator work with contract repairs and preserves Cregis-specific
behavior that OpenAPI Generator cannot infer, such as body signing, Team API
header signing, and webhook acknowledgements.

The alternatives were:

- Patch only the obvious missing fields. This is faster but leaves incomplete
  coverage and weak tests, so it is not a useful generation baseline.
- Replace the SDK with generated Java immediately. This would expose signing
  fields to users, create unstable names from inline schemas, and still require
  a handwritten runtime before the result is usable.

## Architecture

The Java SDK has three public clients:

- `CregisPaymentClient` for Payment Engine operations.
- `CregisWaasClient` for WaaS operations.
- `CregisTeamClient` for Team API operations.

Payment Engine and WaaS share project authentication. Their runtime injects a
numeric `pid`, a six-character nonce, a millisecond timestamp, and an MD5
signature into the JSON body. Team API has a separate runtime that produces a
canonical JSON body and signs `PATH`, `TIMESTAMP`, `NONCE`, and `BODY` with
HMAC-SHA256. It sends the result using the four `Access-*` headers.

Every client requires an explicit project- or team-specific Base URL. No
production-looking default URL is allowed. URL normalization removes a single
trailing slash and rejects invalid or non-HTTPS URLs, except loopback HTTP URLs
used by local tests.

## Models and callbacks

Java wire names, required fields, primitive types, and nested response objects
follow OpenAPI. Payment order responses include merchant, payment, refund,
order-detail, sub-merchant, and settlement information. WaaS includes both
payout versions, both balance-query versions, and all four documented
webhooks. Team API includes all six operations and their complete paginated
models.

Payment callback data uses event-specific models behind a common envelope so
integer/string differences are not hidden in one catch-all object. Callback
verification parses forward-compatible payloads, verifies signatures using a
documented canonical value representation, and exposes exact acknowledgement
constants (`success`, `ok`, and `deny`) where OpenAPI requires them.

If OpenAPI contains an unusual type, such as a numeric-looking string, Java
uses the OpenAPI type. The SDK does not silently change the wire contract based
on assumptions about the backend.

## Error handling and safety

HTTP failures retain the status code and response body. Cregis business errors
retain `code` and `msg`. Debug logging is opt-in and redacts signatures,
credentials, personal information, addresses, and transaction identifiers.
Callback signature comparison is constant-time. Timestamp and replay policy
remain configurable because nonce persistence belongs to the integrating
application.

Sandbox integration tests remain opt-in. Read-only and state-changing tests
are separated, and state-changing tests require an additional explicit opt-in
flag.

## Verification

The baseline is complete only when:

1. All 23 OpenAPI operations have a Java client method.
2. All five OpenAPI webhooks have a Java model and verification path.
3. Request serialization and response deserialization fixtures cover every
   operation family, including pagination and nested Payment data.
4. Project MD5 and Team HMAC-SHA256 signers have deterministic test vectors.
5. Java 11 `mvn clean verify` succeeds without network credentials.
6. The README examples contain all OpenAPI-required fields and require an
   explicit Base URL.

