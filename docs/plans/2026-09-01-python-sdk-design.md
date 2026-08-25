# Python SDK design

## Goal

Deliver a synchronous Python SDK covering the same 23 Payment Engine, WaaS,
and Team API operations and inbound webhook contracts as the Java and
TypeScript SDKs. The distribution name is `cregis-sdk`, the import package is
`cregis`, and supported Python versions are 3.9 through 3.14.

## Architecture

The SDK lives under `sdks/python` and keeps a strict generated/handwritten
boundary:

- `src/cregis/generated` contains OpenAPI-derived request, response, enum,
  webhook, operation-metadata, and runtime-validation models.
- `src/cregis/clients` contains `CregisPaymentClient`, `CregisWaasClient`, and
  `CregisTeamClient` with Python-style snake_case methods.
- `src/cregis/signing` contains Payment/WaaS parameter signing and Team API
  RFC 8785 canonicalization and signing.
- `src/cregis/webhooks` contains callback signature verification and parsing.
- `src/cregis/transport.py` contains synchronous HTTP behavior.
- `src/cregis/errors.py` contains the supported exception hierarchy.

The public API accepts and returns strict Pydantic v2 models. It does not
coerce values such as numeric strings into integers. SDK-managed `pid`,
`nonce`, `timestamp`, and `sign` fields are removed from public request models
and added immediately before signing.

## HTTP and errors

The runtime uses a synchronous `httpx.Client`, defaults to a 30-second timeout,
and permits client injection for proxies, connection pooling, and tests.
State-changing POST operations are not automatically retried.

Failures are separated into:

- `CregisClientError` for configuration, serialization, network, and timeout
  failures;
- `CregisHttpError` for non-success HTTP status codes;
- `CregisApiError` for non-success Cregis business codes;
- `CregisContractError` when request or successful response data does not match
  the generated OpenAPI contract.

Errors and debug logs must not expose API keys, access secrets, signatures,
canonical strings, or full request payloads.

## OpenAPI generation

The canonical specifications remain only in
`cregis-developer-docs/api-sources/specs`. Python generation reuses the shared
operation and stable-model maps, plus Python-specific naming overrides and a
pinned generator configuration. Regeneration replaces only
`src/cregis/generated` and writes a source/generator lock manifest. A check mode
regenerates into a temporary directory and compares output byte for byte.

The cross-repository update workflow regenerates Java, TypeScript, and Python,
runs all three SDK test suites, and opens one reviewable pull request.

## Testing and acceptance

Acceptance requires:

1. deterministic vectors for both signing schemes and RFC 8785 behavior;
2. local request and response contract tests for all 23 client operations;
3. Payment and WaaS webhook signature and strict wire-type tests;
4. reproducible OpenAPI generation and client-surface drift checks;
5. package build, wheel installation, and Python 3.9-3.14 CI coverage;
6. a safe read-only Sandbox suite;
7. an explicit full Sandbox suite covering all 23 operations.

The full Sandbox suite requires `CREGIS_ALLOW_MUTATING_TESTS=true`, uses unique
business identifiers and a minimal test amount, and refuses state-changing
requests unless every configured endpoint is HTTPS and matches
`t-*.cregis.dev`.
