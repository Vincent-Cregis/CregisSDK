package com.cregis.sdk.client;

import com.cregis.sdk.core.client.CregisBaseClient;
import com.cregis.sdk.domain.common.ApiResponse;
import com.cregis.sdk.domain.payment.CreateOrderRequest;
import com.cregis.sdk.domain.payment.CreateOrderResponse;
import com.cregis.sdk.domain.payment.QueryOrderRequest;
import com.cregis.sdk.domain.payment.QueryOrderResponse;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Client for Cregis Payment Engine.
 */
public class CregisPaymentClient extends CregisBaseClient {

    private CregisPaymentClient(Builder builder) {
        super(builder.endpoint, builder.pid, builder.apiKey, builder.debug);
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
        private String endpoint = "https://payment.cregis.com";
        private String pid;
        private String apiKey;
        private boolean debug = false;

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder credentials(String pid, String apiKey) {
            this.pid = pid;
            this.apiKey = apiKey;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public CregisPaymentClient build() {
            if (pid == null || apiKey == null) {
                throw new IllegalArgumentException("PID and API Key are required");
            }
            return new CregisPaymentClient(this);
        }
    }
}
