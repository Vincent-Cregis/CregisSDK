package com.cregis.sdk.client;

import com.cregis.sdk.core.exception.CregisClientException;
import com.cregis.sdk.core.signer.CregisSigner;
import com.cregis.sdk.domain.waas.AddressDepositCallbackNotification;
import com.cregis.sdk.domain.waas.PayoutCallbackNotification;
import com.cregis.sdk.domain.waas.WithdrawalCallbackNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Utility for handling Cregis WaaS Callbacks/Webhooks.
 * Supports Deposit, Payout, and Withdrawal notifications.
 */
public class CregisWaasCallbackHandler {

    private final String apiKey;
    private final ObjectMapper objectMapper;

    public CregisWaasCallbackHandler(String apiKey) {
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Internal method to verify signature.
     */
    private void verifySignature(String rawJsonBody) {
        try {
            Map<String, Object> paramMap = objectMapper.readValue(rawJsonBody, Map.class);

            if (!paramMap.containsKey("sign")) {
                throw new CregisClientException("Missing signature in callback");
            }

            String incomingSign = (String) paramMap.get("sign");
            paramMap.remove("sign");

            String calculatedSign = CregisSigner.sign(paramMap, apiKey);

            if (!calculatedSign.equalsIgnoreCase(incomingSign)) {
                throw new CregisClientException(
                        "Signature verification failed. Expected: " + calculatedSign + ", Got: " + incomingSign);
            }
        } catch (JsonProcessingException e) {
            throw new CregisClientException("Failed to parse callback JSON for signature verification", e);
        }
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
