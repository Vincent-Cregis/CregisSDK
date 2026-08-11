package com.cregis.sdk.contract;

/** Assertion used by test-only OpenAPI contract validation. */
public final class OpenApiContractViolation extends AssertionError {

    OpenApiContractViolation(String message) {
        super(message);
    }
}
