# Java OpenAPI Contract Validation

## Goal

Prove that the JSON sent to and received from the Cregis Sandbox matches the
canonical OpenAPI contracts before Jackson can coerce scalar values. Cover all
23 callable operations and all 5 inbound webhook definitions.

## Design

The validation code lives in the Java test source set. An OkHttp test
interceptor observes the final signed request and the untouched response body.
It resolves local OpenAPI references and `allOf` schemas, then validates:

- required request headers and JSON properties;
- documented property names;
- JSON primitive, object, and array types;
- integer width and enum values;
- nested objects and array items.

The interceptor is attached only by Sandbox integration tests through the
existing `CregisHttpConfig` hook. Production parsing remains backward
compatible, while tests fail before Jackson can turn a string into a number or
a number into a string.

The canonical specifications remain in the separate
`cregis-developer-docs/api-sources/specs` repository. Tests locate that checkout
locally, with `CREGIS_OPENAPI_SPEC_DIR` available as an override. No second copy
of the specifications is committed to the SDK repository.

## Coverage and failure behavior

The Payment, WaaS, and Team full integration suites assert that every callable
operation in their respective OpenAPI document was observed. Read-only suites
validate the operations they call without claiming full coverage. Webhook
contract tests use sanitized signed payloads because webhook operations are
inbound and cannot be invoked through the Sandbox clients.

A contract failure reports the operation and exact JSON path without printing
payload values, credentials, signatures, addresses, or transaction IDs.
Validator unit tests prove that scalar coercion, undocumented fields, and
missing required fields are rejected.

## Verification

Run unit tests, OpenAPI/codegen drift checks, reproducible model generation,
and the full credentialed Sandbox integration suite. The run is successful
only when all 23 callable operations are covered without skips and the 5
webhook samples match their OpenAPI request schemas.
