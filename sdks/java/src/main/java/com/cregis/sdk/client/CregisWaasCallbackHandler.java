package com.cregis.sdk.client;

import com.cregis.sdk.core.exception.CregisClientException;
import com.cregis.sdk.core.webhook.CregisProjectCallbackVerifier;
import com.cregis.sdk.webhook.waas.model.AddressDepositCallbackNotification;
import com.cregis.sdk.webhook.waas.model.PayoutCallbackNotification;
import com.cregis.sdk.webhook.waas.model.PayoutExternalVerificationCallbackNotification;
import com.cregis.sdk.webhook.waas.model.WithdrawalCallbackNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

/**
 * Utility for handling Cregis WaaS Callbacks/Webhooks.
 * Supports Deposit, Payout, and Withdrawal notifications.
 */
public class CregisWaasCallbackHandler {

    public static final String CALLBACK_SUCCESS = "success";
    public static final String EXTERNAL_VERIFICATION_APPROVE = "ok";
    public static final String EXTERNAL_VERIFICATION_DENY = "deny";

    private final String apiKey;
    private final ObjectMapper objectMapper;

    public CregisWaasCallbackHandler(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API Key is required");
        }
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Internal method to verify signature.
     */
    private void verifySignature(String rawJsonBody) {
        CregisProjectCallbackVerifier.verify(rawJsonBody, apiKey, objectMapper);
    }

    /**
     * Verify and parse Address Deposit notification.
     * 
     * @param rawJsonBody Raw JSON string.
     * @return AddressDepositCallbackNotification object.
     */
    public AddressDepositCallbackNotification handleDepositCallback(String rawJsonBody) {
        verifySignature(rawJsonBody);
        try {
            return objectMapper.readValue(rawJsonBody, AddressDepositCallbackNotification.class);
        } catch (JsonProcessingException e) {
            throw new CregisClientException("Failed to map JSON to AddressDepositCallbackNotification", e);
        }
    }

    /**
     * Verify and parse Payout notification.
     * 
     * @param rawJsonBody Raw JSON string.
     * @return PayoutCallbackNotification object.
     */
    public PayoutCallbackNotification handlePayoutCallback(String rawJsonBody) {
        verifySignature(rawJsonBody);
        try {
            return objectMapper.readValue(rawJsonBody, PayoutCallbackNotification.class);
        } catch (JsonProcessingException e) {
            throw new CregisClientException("Failed to map JSON to PayoutCallbackNotification", e);
        }
    }

    public PayoutExternalVerificationCallbackNotification handlePayoutExternalVerificationCallback(
            String rawJsonBody) {
        verifySignature(rawJsonBody);
        try {
            return objectMapper.readValue(rawJsonBody, PayoutExternalVerificationCallbackNotification.class);
        } catch (JsonProcessingException e) {
            throw new CregisClientException(
                    "Failed to map JSON to PayoutExternalVerificationCallbackNotification",
                    e);
        }
    }

    /**
     * Verify and parse Withdrawal notification.
     * 
     * @param rawJsonBody Raw JSON string.
     * @return WithdrawalCallbackNotification object.
     */
    public WithdrawalCallbackNotification handleWithdrawalCallback(String rawJsonBody) {
        verifySignature(rawJsonBody);
        try {
            return objectMapper.readValue(rawJsonBody, WithdrawalCallbackNotification.class);
        } catch (JsonProcessingException e) {
            throw new CregisClientException("Failed to map JSON to WithdrawalCallbackNotification", e);
        }
    }
}
