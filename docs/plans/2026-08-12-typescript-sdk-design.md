# TypeScript and Node.js SDK Design

## Goal

Deliver one `@cregis/sdk` npm package for JavaScript and TypeScript users. The
package covers the same 23 callable Payment Engine, WaaS, and Team API
operations as the Java SDK, plus the five inbound webhook contracts. It targets
maintained Node.js releases starting at Node.js 22 and publishes ESM, CommonJS,
and TypeScript declaration files.

## Architecture

The SDK uses a generated/handwritten boundary:

- `src/generated/` contains OpenAPI-derived request, response, webhook, enum,
  operation-metadata, and runtime-schema types. Generation may replace this
  directory in full.
- `src/core/` contains HTTP transport, configuration, retry policy, and errors.
- `src/signing/` contains Payment/WaaS parameter signing and Team API RFC 8785
  canonicalization and signing.
- `src/clients/` contains `CregisPaymentClient`, `CregisWaasClient`, and
  `CregisTeamClient`.
- `src/webhooks/` contains webhook types, signature verification, and parsing.
- `src/index.ts` is the supported public export surface.

The runtime uses Node's built-in `fetch`, Web Crypto-compatible primitives, and
`AbortSignal` support where practical. Consumers may supply a custom `fetch`
implementation. Production dependencies are kept small and reviewed.

## Requests and responses

Public request types omit `pid`, `nonce`, `timestamp`, and `sign` whenever the
SDK owns those fields. The runtime adds authentication fields immediately
before serialization and signing. Generated types preserve actual JSON wire
names so signing never depends on an implicit case conversion.

All requests and successful response data are checked against compact generated
runtime schemas before typed values cross the public boundary. JSON `int64`
values must fit JavaScript's safe-integer range so they cannot be silently
rounded.

All API responses use the common `{ code, msg, data }` envelope. HTTP failures
raise `CregisHttpError`, non-success Cregis response codes raise
`CregisApiError`, and network, timeout, serialization, or parsing failures raise
`CregisClientError`. Errors may expose status and business codes but must not
log secrets, signatures, canonical strings, or complete request payloads.

Retries are limited to safe transient failures. Calls that create orders,
addresses, payouts, withdrawals, or collections are not retried by default.

## OpenAPI generation

`cregis-developer-docs/api-sources/specs` remains the only source of truth. The
SDK repository does not commit another copy of the specifications. TypeScript
generation uses a pinned generator, stable operation/model mappings, and a lock
manifest. Generation removes SDK-managed authentication fields and writes only
the generated directory.

A reproducibility check regenerates into a temporary directory and compares the
result byte-for-byte. A separate drift check compares all OpenAPI operation IDs
and paths with the three handwritten clients. OpenAPI changes therefore produce
reviewable generated diffs without replacing the signing or transport runtime.

## Build and compatibility

TypeScript is the source language. The package exports ESM, CommonJS, and `.d.ts`
declarations through explicit `package.json` exports. CI tests Node.js 22 and 24.
The build uses the TypeScript compiler directly where possible and avoids adding
a bundler unless dual-module compatibility requires one.

## Testing and acceptance

Tests are layered as follows:

1. deterministic vectors for both signing schemes and RFC 8785 edge cases;
2. mock HTTP tests for all 23 client methods, headers, paths, errors, timeouts,
   and retry safety;
3. real regeneration, generated-boundary, and OpenAPI drift checks;
4. local contract tests for all five webhook definitions;
5. opt-in Sandbox tests using the same environment variables as Java.

Read-only Sandbox tests run separately. State-changing tests require an explicit
`CREGIS_ALLOW_MUTATING_TESTS=true` opt-in. A first complete baseline requires
all 23 client methods, both signing schemes, all five webhook contracts, clean
type checking, reproducible generation, dual-module package smoke tests, and no
credential leakage in output.
