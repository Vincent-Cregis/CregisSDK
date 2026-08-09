package com.cregis.sdk.core.client;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CregisBaseClientTransportTest {

    @Test
    void secureDefaultsDisableRetriesAndRedirects() {
        TestClient client = new TestClient(CregisHttpConfig.defaults());

        assertEquals(30_000, client.transport().connectTimeoutMillis());
        assertEquals(30_000, client.transport().readTimeoutMillis());
        assertEquals(30_000, client.transport().writeTimeoutMillis());
        assertFalse(client.transport().retryOnConnectionFailure());
        assertFalse(client.transport().followRedirects());
        assertFalse(client.transport().followSslRedirects());
    }

    @Test
    void inheritsApplicationTransportAndAppliesExplicitOverrides() {
        OkHttpClient applicationClient = new OkHttpClient.Builder()
                .connectTimeout(7, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(9, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .build();

        CregisHttpConfig config = CregisHttpConfig.builder()
                .httpClient(applicationClient)
                .readTimeout(Duration.ofMillis(1_500))
                .retryOnConnectionFailure(true)
                .build();

        TestClient client = new TestClient(config);

        assertEquals(7_000, client.transport().connectTimeoutMillis());
        assertEquals(1_500, client.transport().readTimeoutMillis());
        assertEquals(9_000, client.transport().writeTimeoutMillis());
        assertTrue(client.transport().retryOnConnectionFailure());
        assertFalse(client.transport().followRedirects());
        assertFalse(client.transport().followSslRedirects());
        assertEquals(applicationClient.connectionPool(), client.transport().connectionPool());
        assertEquals(applicationClient.dispatcher(), client.transport().dispatcher());
    }

    @Test
    void rejectsUnsafeTimeoutValuesAndNullPayloads() {
        assertThrows(
                IllegalArgumentException.class,
                () -> CregisHttpConfig.builder().readTimeout(Duration.ZERO));

        TestClient client = new TestClient(CregisHttpConfig.defaults());
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                client::postNullPayload);
        assertEquals("Request payload is required", error.getMessage());
    }

    private static final class TestClient extends CregisBaseClient {

        private TestClient(CregisHttpConfig httpConfig) {
            super("http://localhost", false, httpConfig, null);
        }

        private OkHttpClient transport() {
            return httpClient;
        }

        private void postNullPayload() {
            post("/test", null);
        }
    }
}
