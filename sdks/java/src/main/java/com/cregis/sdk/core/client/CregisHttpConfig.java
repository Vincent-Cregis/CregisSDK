package com.cregis.sdk.core.client;

import okhttp3.OkHttpClient;

import java.time.Duration;
import java.util.Objects;

/**
 * Optional HTTP transport configuration shared by all Cregis API clients.
 *
 * <p>When an application-owned {@link OkHttpClient} is supplied, the SDK
 * clones it with {@code newBuilder()} so its connection pool, dispatcher,
 * proxy, and application interceptors are retained. Cregis authentication is
 * then added to the clone.</p>
 */
public final class CregisHttpConfig {

    private final OkHttpClient httpClient;
    private final Duration connectTimeout;
    private final Duration readTimeout;
    private final Duration writeTimeout;
    private final boolean retryOnConnectionFailure;

    private CregisHttpConfig(Builder builder) {
        this.httpClient = builder.httpClient;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.writeTimeout = builder.writeTimeout;
        this.retryOnConnectionFailure = builder.retryOnConnectionFailure;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static CregisHttpConfig defaults() {
        return builder().build();
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public Duration getWriteTimeout() {
        return writeTimeout;
    }

    public boolean isRetryOnConnectionFailure() {
        return retryOnConnectionFailure;
    }

    public static final class Builder {

        private OkHttpClient httpClient;
        private Duration connectTimeout;
        private Duration readTimeout;
        private Duration writeTimeout;
        private boolean retryOnConnectionFailure;

        private Builder() {
        }

        /**
         * Uses an application-owned OkHttp client as the transport base.
         * The supplied instance is not modified.
         */
        public Builder httpClient(OkHttpClient httpClient) {
            this.httpClient = Objects.requireNonNull(httpClient, "HTTP client is required");
            return this;
        }

        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = requirePositive(connectTimeout, "Connect timeout");
            return this;
        }

        public Builder readTimeout(Duration readTimeout) {
            this.readTimeout = requirePositive(readTimeout, "Read timeout");
            return this;
        }

        public Builder writeTimeout(Duration writeTimeout) {
            this.writeTimeout = requirePositive(writeTimeout, "Write timeout");
            return this;
        }

        /**
         * Enables OkHttp connection recovery.
         *
         * <p>Cregis operations use POST, including operations that create
         * financial state. Keep this disabled unless the caller has an
         * idempotency and reconciliation strategy.</p>
         */
        public Builder retryOnConnectionFailure(boolean retryOnConnectionFailure) {
            this.retryOnConnectionFailure = retryOnConnectionFailure;
            return this;
        }

        public CregisHttpConfig build() {
            return new CregisHttpConfig(this);
        }

        private static Duration requirePositive(Duration value, String label) {
            Objects.requireNonNull(value, label + " is required");
            long millis;
            try {
                millis = value.toMillis();
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException(label + " is too large", e);
            }
            if (millis <= 0) {
                throw new IllegalArgumentException(label + " must be at least 1 millisecond");
            }
            return value;
        }
    }
}
