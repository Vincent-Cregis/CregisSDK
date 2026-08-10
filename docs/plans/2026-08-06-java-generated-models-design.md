# Java generated-model integration design

## Goal

The Java SDK must consume operation request and response models generated from
the canonical OpenAPI documents. Updating a canonical specification and
running one repository command must refresh those models deterministically.
The generator must not own HTTP transport, authentication, exceptions, or
webhook verification.

## Source and output boundaries

The canonical specifications remain in the separate
`cregis-developer-docs` repository. The SDK command requires an explicit local
`--spec-dir`; it does not fetch or retain a second canonical copy.

Prepared specifications are disposable build artifacts. Generated Java model
sources are committed under:

```text
sdks/java/src/generated/java/com/cregis/sdk/generated/payment/model
sdks/java/src/generated/java/com/cregis/sdk/generated/waas/model
sdks/java/src/generated/java/com/cregis/sdk/generated/team/model
```

Handwritten clients and runtime code remain under `src/main/java`. Maven adds
the generated source root during compilation and includes it in source and
Javadoc artifacts.

## Preprocessing contract

The preprocessor reads the 23-operation manifest and performs the following
for every operation:

1. Resolve local schema references and flatten object `allOf` composition.
2. Remove SDK-managed `pid`, `nonce`, `timestamp`, and `sign` fields from
   outbound Payment and WaaS request models.
3. Extract the successful response's `data` schema because the handwritten
   runtime already validates and unwraps the standard response envelope.
4. Materialize inline objects and array items as named component schemas.
5. Use explicit public model names from the manifest, with deterministic
   contextual names for newly discovered nested schemas.
6. Record canonical input hashes, operation inventory, and generated model
   inventory in a machine-readable manifest.

Team authentication headers never become model fields. Payment JSON-string
fields such as `order_details`, `sub_merchant`, and `tokens` remain strings on
the wire; a handwritten value helper may provide safe serialization without
changing the OpenAPI wire type.

## Runtime integration

The three public clients expose generated operation models directly. The
existing generic `ApiResponse<T>` remains an internal transport envelope and
is not generated. A small generated-model support class supplies only the URL
encoding helpers referenced by the upstream Java model template; it performs
no HTTP calls.

Webhook handlers, callback envelopes, event-specific callback data, signature
verification, and constant-time comparison stay handwritten. Their wire
compatibility continues to be checked against OpenAPI and backend fixtures,
but regeneration can never overwrite security-sensitive callback code.

The migration happens before Java GA, so generated package names are the new
public model API. The repository does not keep handwritten duplicates or
deprecated wrappers after the clients, tests, examples, and integration tests
have migrated.

## Reproducibility and verification

OpenAPI Generator is pinned by version and immutable image digest. Generation
first writes to a temporary directory, then replaces only the managed
generated source root. A `--check` mode regenerates in a temporary directory
and fails if committed output differs.

Completion requires:

- preprocessor tests for authentication stripping, stable naming, response
  unwrapping, nested objects, and invalid manifest/spec input;
- no SDK-managed authentication fields in generated outbound request models;
- all three clients importing generated models;
- no handwritten operation DTOs remaining;
- Java 11, 17, and 21 builds passing;
- operation drift, regeneration drift, Maven consumer, and Gradle consumer
  checks passing.
