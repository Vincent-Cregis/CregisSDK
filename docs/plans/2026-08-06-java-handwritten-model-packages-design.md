# Java handwritten model package design

## Decision

Keep OpenAPI operation models under `src/generated/java` and keep handwritten
transport and webhook models under `src/main/java`, but remove the ambiguous
`domain` package.

The package layout becomes:

```text
com.cregis.sdk.core.transport.ApiResponse
com.cregis.sdk.webhook.payment.model.*
com.cregis.sdk.webhook.waas.model.*
com.cregis.sdk.generated.payment.model.*
com.cregis.sdk.generated.waas.model.*
com.cregis.sdk.generated.team.model.*
```

`ApiResponse` is an HTTP response envelope rather than a business model.
Payment and WaaS callback classes are webhook contracts that are not generated
by the current operation-model pipeline. Their new packages make that boundary
explicit.

## Compatibility and behavior

This is a package-breaking source change for callback model imports. It is safe
before the first GA release and does not change JSON fields, callback signature
verification, HTTP behavior, or generated operation models. No compatibility
aliases are added because the SDK has not been released and duplicate public
types would preserve the ambiguity this change removes.

## Verification

Update production imports, tests, and README examples; ensure no legacy domain
package reference remains; run the Java unit and integration test suites; and
run the generated-model boundary checks.
