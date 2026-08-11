package com.cregis.sdk.contract;

import okhttp3.Headers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenApiContractValidatorTest {

    private final OpenApiContractValidator validator = OpenApiContractValidator.parse("{"
            + "\"openapi\":\"3.1.0\","
            + "\"paths\":{\"/items\":{\"post\":{"
            + "\"operationId\":\"createItem\","
            + "\"parameters\":[{\"name\":\"Access-Timestamp\",\"in\":\"header\","
            + "\"required\":true,\"schema\":{\"type\":\"integer\",\"format\":\"int64\"}}],"
            + "\"requestBody\":{\"content\":{\"application/json\":{\"schema\":{"
            + "\"type\":\"object\",\"required\":[\"quantity\"],\"properties\":{"
            + "\"quantity\":{\"type\":\"integer\",\"format\":\"int32\"}}}}}},"
            + "\"responses\":{\"200\":{\"content\":{\"application/json\":{\"schema\":{"
            + "\"type\":\"object\",\"required\":[\"data\"],\"properties\":{"
            + "\"data\":{\"type\":\"object\",\"properties\":{"
            + "\"active\":{\"type\":\"boolean\"},\"count\":{\"type\":\"integer\"}}}}}}}}}"
            + "}}}}}");

    private final Headers headers = new Headers.Builder()
            .add("Access-Timestamp", "1786420800000")
            .build();

    @Test
    void acceptsExactRequestAndResponseTypes() {
        assertDoesNotThrow(() -> validator.validateRequest(
                "POST", "/items", headers, "{\"quantity\":2}"));
        assertDoesNotThrow(() -> validator.validateResponse(
                "POST", "/items", 200, "{\"data\":{\"active\":true,\"count\":2}}"));
    }

    @Test
    void rejectsJacksonStyleScalarCoercion() {
        OpenApiContractViolation violation = assertThrows(
                OpenApiContractViolation.class,
                () -> validator.validateResponse(
                        "POST", "/items", 200,
                        "{\"data\":{\"active\":\"true\",\"count\":\"2\"}}"));

        assertTrue(violation.getMessage().contains("$.data.active expected boolean but got string"));
    }

    @Test
    void rejectsMissingRequiredAndUndocumentedFields() {
        OpenApiContractViolation missing = assertThrows(
                OpenApiContractViolation.class,
                () -> validator.validateRequest("POST", "/items", headers, "{}"));
        assertTrue(missing.getMessage().contains("$.quantity is required but missing"));

        OpenApiContractViolation undocumented = assertThrows(
                OpenApiContractViolation.class,
                () -> validator.validateResponse(
                        "POST", "/items", 200,
                        "{\"data\":{\"active\":true,\"count\":2,\"future\":1}}"));
        assertTrue(undocumented.getMessage().contains(
                "$.data.future is not documented (actual type integer)"));
    }

    @Test
    void validatesSerializedHeaderTypes() {
        Headers invalid = new Headers.Builder().add("Access-Timestamp", "not-a-number").build();
        OpenApiContractViolation violation = assertThrows(
                OpenApiContractViolation.class,
                () -> validator.validateRequest("POST", "/items", invalid, "{\"quantity\":2}"));
        assertTrue(violation.getMessage().contains("Access-Timestamp expected integer"));
    }
}
