package com.cregis.sdk.core.webhook;

import com.cregis.sdk.core.exception.CregisClientException;
import com.cregis.sdk.core.signer.CregisSigner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.Map;

/**
 * Shared signature verification for Payment Engine and WaaS callbacks.
 */
public final class CregisProjectCallbackVerifier {

    private CregisProjectCallbackVerifier() {
    }

    /**
     * Verifies a raw callback body and returns its top-level fields without
     * the {@code sign} field.
     */
    public static Map<String, Object> verify(
            String rawJsonBody,
            String apiKey,
            ObjectMapper objectMapper) {
        if (rawJsonBody == null || rawJsonBody.trim().isEmpty()) {
            throw new CregisClientException("Callback body is required");
        }

        try {
            Map<String, Object> parameters = objectMapper.readValue(
                    rawJsonBody,
                    new TypeReference<Map<String, Object>>() {
                    });
            if (parameters == null) {
                throw new CregisClientException("Callback body must be a JSON object");
            }

            Object incomingSignValue = parameters.remove("sign");
            if (!(incomingSignValue instanceof String)
                    || !((String) incomingSignValue).matches("(?i)[0-9a-f]{32}")) {
                throw new CregisClientException("Callback signature must be a 32-character hexadecimal string");
            }

            String incomingSign = ((String) incomingSignValue).toLowerCase(Locale.ROOT);
            String calculatedSign = CregisSigner.sign(parameters, apiKey);
            if (!MessageDigest.isEqual(
                    calculatedSign.getBytes(StandardCharsets.US_ASCII),
                    incomingSign.getBytes(StandardCharsets.US_ASCII))) {
                throw new CregisClientException("Callback signature verification failed");
            }
            return parameters;
        } catch (JsonProcessingException e) {
            throw new CregisClientException("Failed to parse callback JSON for signature verification", e);
        }
    }
}
