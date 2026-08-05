package com.cregis.sdk.client;

import com.cregis.sdk.core.exception.CregisClientException;
import com.cregis.sdk.core.signer.CregisSigner;
import com.cregis.sdk.domain.payment.PaymentCallbackNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Utility for handling Cregis Payment Callbacks/Webhooks.
 */
public class CregisPaymentCallbackHandler {

    private final String apiKey;
    private final ObjectMapper objectMapper;

    public CregisPaymentCallbackHandler(String apiKey) {
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
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
            Map<String, Object> paramMap = objectMapper.readValue(rawJsonBody, Map.class);

            // 2. Validate Signature
            if (!paramMap.containsKey("sign")) {
                throw new CregisClientException("Missing signature in callback");
            }

            String incomingSign = (String) paramMap.get("sign");
            // sign is not part of calculation
            paramMap.remove("sign");

            String calculatedSign = CregisSigner.sign(paramMap, apiKey);

            if (!calculatedSign.equalsIgnoreCase(incomingSign)) {
                throw new CregisClientException(
                        "Signature verification failed. Expected: " + calculatedSign + ", Got: " + incomingSign);
            }

            // 3. Parse to Object
            return objectMapper.readValue(rawJsonBody, PaymentCallbackNotification.class);

        } catch (JsonProcessingException e) {
            throw new CregisClientException("Failed to parse callback JSON", e);
        }
    }
}
