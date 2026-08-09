package com.cregis.sdk.client;

import com.cregis.sdk.core.exception.CregisClientException;
import com.cregis.sdk.core.webhook.CregisProjectCallbackVerifier;
import com.cregis.sdk.domain.enums.PaymentEventType;
import com.cregis.sdk.domain.payment.PaymentCallbackData;
import com.cregis.sdk.domain.payment.PaymentCallbackNotification;
import com.cregis.sdk.domain.payment.PaymentCompletedCallbackData;
import com.cregis.sdk.domain.payment.PaymentExpiredCallbackData;
import com.cregis.sdk.domain.payment.PaymentRefundedCallbackData;
import com.cregis.sdk.domain.payment.PaymentRemainingCallbackData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.util.Map;

/**
 * Utility for handling Cregis Payment Callbacks/Webhooks.
 */
public class CregisPaymentCallbackHandler {

    public static final String CALLBACK_SUCCESS = "success";

    private final String apiKey;
    private final ObjectMapper objectMapper;

    public CregisPaymentCallbackHandler(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API Key is required");
        }
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Verifies the signature of the raw JSON body and parses it into a notification
     * object.
     *
     * @param rawJsonBody The raw JSON string received from the webhook.
     * @return The parsed notification object if signature is valid.
     * @throws CregisClientException If signature verification fails or parsing
     *                               error occurs.
     */
    public PaymentCallbackNotification<? extends PaymentCallbackData> verifyAndParse(String rawJsonBody) {
        try {
            Map<String, Object> parameters = CregisProjectCallbackVerifier.verify(
                    rawJsonBody,
                    apiKey,
                    objectMapper);
            if (!"order".equals(parameters.get("event_name"))) {
                throw new CregisClientException("Unsupported Payment callback event_name");
            }
            if (!(parameters.get("data") instanceof Map)) {
                throw new CregisClientException("Payment callback data must be a JSON object");
            }

            Object rawEventType = parameters.get("event_type");
            if (!(rawEventType instanceof String)) {
                throw new CregisClientException("Payment callback event_type is required");
            }
            PaymentEventType eventType;
            try {
                eventType = PaymentEventType.fromValue((String) rawEventType);
            } catch (IllegalArgumentException e) {
                throw new CregisClientException(e.getMessage(), e);
            }

            return parseNotification(rawJsonBody, dataTypeFor(eventType));

        } catch (JsonProcessingException e) {
            throw new CregisClientException("Failed to parse callback JSON", e);
        }
    }

    private Class<? extends PaymentCallbackData> dataTypeFor(PaymentEventType eventType) {
        switch (eventType) {
            case PAID:
            case PAID_PARTIAL:
            case PAID_OVER:
                return PaymentCompletedCallbackData.class;
            case EXPIRED:
                return PaymentExpiredCallbackData.class;
            case REFUNDED:
                return PaymentRefundedCallbackData.class;
            case PAID_REMAIN:
                return PaymentRemainingCallbackData.class;
            default:
                throw new CregisClientException("Unsupported Payment callback event_type: " + eventType.getValue());
        }
    }

    private <T extends PaymentCallbackData> PaymentCallbackNotification<T> parseNotification(
            String rawJsonBody,
            Class<T> dataType) throws JsonProcessingException {
        JavaType notificationType = objectMapper.getTypeFactory().constructParametricType(
                PaymentCallbackNotification.class,
                dataType);
        return objectMapper.readValue(rawJsonBody, notificationType);
    }
}
