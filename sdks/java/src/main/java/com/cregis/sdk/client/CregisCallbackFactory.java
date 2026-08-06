package com.cregis.sdk.client;

/**
 * Creates callback handlers for the two project-authenticated API families.
 * Callback URLs identify the payload type, so callers select the matching
 * handler method for the endpoint that received the request.
 */
public class CregisCallbackFactory {

    private final CregisPaymentCallbackHandler paymentHandler;
    private final CregisWaasCallbackHandler waasHandler;

    public CregisCallbackFactory(String paymentApiKey, String waasApiKey) {
        this.paymentHandler = new CregisPaymentCallbackHandler(paymentApiKey);
        this.waasHandler = new CregisWaasCallbackHandler(waasApiKey);
    }

    /**
     * Enum representing the type of callback.
     */
    public enum CallbackType {
        PAYMENT_ORDER,
        WAAS_DEPOSIT,
        WAAS_PAYOUT,
        WAAS_PAYOUT_EXTERNAL_VERIFICATION,
        WAAS_WITHDRAWAL,
        UNKNOWN
    }

    public CregisPaymentCallbackHandler getPaymentHandler() {
        return paymentHandler;
    }

    public CregisWaasCallbackHandler getWaasHandler() {
        return waasHandler;
    }
}
