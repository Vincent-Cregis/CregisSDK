package com.cregis.sdk.client;

import com.cregis.sdk.core.exception.CregisClientException;
import com.cregis.sdk.core.signer.CregisSigner;
import com.cregis.sdk.domain.payment.PaymentCallbackNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;
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
    public PaymentCallbackNotification verifyAndParse(String rawJsonBody) {
        try {
            // 1. Parse to Map for validation
            Map<String, Object> paramMap = objectMapper.readValue(
                    rawJsonBody,
                    new TypeReference<Map<String, Object>>() {
                    });

            // 2. Validate Signature
            if (!paramMap.containsKey("sign")) {
                throw new CregisClientException("Missing signature in callback");
            }

            Object incomingSignValue = paramMap.get("sign");
            if (!(incomingSignValue instanceof String)) {
                throw new CregisClientException("Callback signature must be a string");
            }
            String incomingSign = (String) incomingSignValue;
            // sign is not part of calculation
            paramMap.remove("sign");

            String calculatedSign = CregisSigner.sign(paramMap, apiKey);

            if (!MessageDigest.isEqual(
                    calculatedSign.getBytes(StandardCharsets.US_ASCII),
                    incomingSign.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII))) {
                throw new CregisClientException("Callback signature verification failed");
            }

            // 3. Parse to Object
            return objectMapper.readValue(rawJsonBody, PaymentCallbackNotification.class);

        } catch (JsonProcessingException e) {
            throw new CregisClientException("Failed to parse callback JSON", e);
        }
    }
}
