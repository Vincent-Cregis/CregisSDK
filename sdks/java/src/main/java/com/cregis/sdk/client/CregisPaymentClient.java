package com.cregis.sdk.client;

import com.cregis.sdk.core.client.CregisBaseClient;
import com.cregis.sdk.core.client.CregisHttpConfig;
import com.cregis.sdk.core.transport.ApiResponse;
import com.cregis.sdk.generated.payment.model.CreateOrderRequest;
import com.cregis.sdk.generated.payment.model.CreateOrderResponse;
import com.cregis.sdk.generated.payment.model.QueryOrderRequest;
import com.cregis.sdk.generated.payment.model.QueryOrderResponse;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Client for Cregis Payment Engine.
 */
public class CregisPaymentClient extends CregisBaseClient {

    private CregisPaymentClient(Builder builder) {
        super(
                builder.endpoint,
                builder.debug,
                builder.httpConfig,
                objectMapper -> new com.cregis.sdk.core.interceptor.CregisAuthInterceptor(
                        builder.pid,
                        builder.apiKey,
                        objectMapper));
    }

    /**
     * Create a new payment order.
     * 
     * @param request The order creation request.
     * @return The created order details.
     */
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        return execute(
                post("/api/v2/checkout", request).build(),
                new TypeReference<ApiResponse<CreateOrderResponse>>() {
                });
    }

    /**
     * Query order details.
     * 
     * @param request The query request containing cregis_id.
     * @return The order details.
     */
    public QueryOrderResponse queryOrder(QueryOrderRequest request) {
        return execute(
                post("/api/v2/order/info", request).build(),
                new TypeReference<ApiResponse<QueryOrderResponse>>() {
                });
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String endpoint;
        private Long pid;
        private String apiKey;
        private boolean debug = false;
        private CregisHttpConfig httpConfig = CregisHttpConfig.defaults();

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder credentials(long pid, String apiKey) {
            this.pid = pid;
            this.apiKey = apiKey;
            return this;
        }

        public Builder credentials(String pid, String apiKey) {
            if (pid == null) {
                throw new IllegalArgumentException("PID is required");
            }
            try {
                return credentials(Long.parseLong(pid), apiKey);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("PID must be an int64 value", e);
            }
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder httpConfig(CregisHttpConfig httpConfig) {
            if (httpConfig == null) {
                throw new IllegalArgumentException("HTTP config is required");
            }
            this.httpConfig = httpConfig;
            return this;
        }

        public CregisPaymentClient build() {
            if (pid == null || apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("PID and API Key are required");
            }
            return new CregisPaymentClient(this);
        }
    }
}
