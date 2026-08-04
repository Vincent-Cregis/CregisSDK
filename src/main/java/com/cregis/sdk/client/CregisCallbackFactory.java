package com.cregis.sdk.client;

import com.cregis.sdk.core.exception.CregisClientException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Factory and dispatcher for Cregis Callbacks.
 * Helps determine the type of callback and routes it to the correct handler
 * method.
 */
public class CregisCallbackFactory {

    private final CregisPaymentCallbackHandler paymentHandler;
    private final CregisWaasCallbackHandler waasHandler;
    private final ObjectMapper objectMapper;

    public CregisCallbackFactory(String paymentApiKey, String waasApiKey) {
        this.paymentHandler = new CregisPaymentCallbackHandler(paymentApiKey);
        this.waasHandler = new CregisWaasCallbackHandler(waasApiKey);
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Enum representing the type of callback.
     */
    public enum CallbackType {
        PAYMENT_ORDER,
        WAAS_DEPOSIT,
        WAAS_PAYOUT,
        WAAS_WITHDRAWAL,
        UNKNOWN
    }

    // Note: Since Cregis callbacks don't always have a distinct "type" field in the
    // root,
    // we might need to rely on the user knowing which endpoint was hit,
    // OR inspect unique fields if possible.
    // However, usually, different callbacks go to different URLs configured by the
    // user.
    // precise dispatching might differ based on implementation.
    // This factory provides access to underlying handlers.

    public CregisPaymentCallbackHandler getPaymentHandler() {
        return paymentHandler;
    }

    public CregisWaasCallbackHandler getWaasHandler() {
        return waasHandler;
    }
}
