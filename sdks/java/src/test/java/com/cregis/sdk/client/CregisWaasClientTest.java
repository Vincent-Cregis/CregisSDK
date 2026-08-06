package com.cregis.sdk.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CregisWaasClientTest {

    @Test
    void builderRequiresCredentials() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> CregisWaasClient.builder()
                        .endpoint("https://example.com")
                        .build());
        assertEquals("PID and API Key are required", error.getMessage());
    }

    @Test
    void builderRequiresBaseUrl() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> CregisWaasClient.builder()
                        .credentials(123L, "key")
                        .build());
        assertEquals("Base URL is required", error.getMessage());
    }

    @Test
    void builderRejectsNonNumericPidString() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> CregisWaasClient.builder().credentials("not-a-number", "key"));
        assertEquals("PID must be an int64 value", error.getMessage());
    }

    @Test
    void builderAcceptsOpenApiInt64Pid() {
        assertDoesNotThrow(() -> CregisWaasClient.builder()
                .endpoint("https://example.com")
                .credentials(1382528827416576L, "key")
                .build());
    }

    @Test
    void builderRejectsNonTlsRemoteBaseUrl() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> CregisWaasClient.builder()
                        .endpoint("http://example.com")
                        .credentials(123L, "key")
                        .build());
        assertEquals("Base URL must use HTTPS", error.getMessage());
    }
}
