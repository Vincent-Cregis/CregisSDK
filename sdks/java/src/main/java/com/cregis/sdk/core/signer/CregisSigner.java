package com.cregis.sdk.core.signer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utility for generating Cregis API signatures.
 */
public class CregisSigner {

    /**
     * Calculates the sign for the given parameters and API Key.
     *
     * @param params The request parameters (will be sorted).
     * @param apiKey The API Key for the project.
     * @return The calculated MD5 signature.
     */
    public static String sign(Map<String, Object> params, String apiKey) {
        // 1. Sort parameters by key (ASCII dictionary order)
        TreeMap<String, Object> sortedParams = new TreeMap<>(params);

        StringBuilder sb = new StringBuilder();

        // 2. Prepend API Key (Step 2 & 3 combined in logic: Key + k1v1k2v2...)
        // Actually doc says:
        // 1. Sort keys.
        // 2. Concat "key1value1key2value2..."
        // 3. Prepend API Key.
        // 4. MD5.

        sb.append(apiKey);

        for (Map.Entry<String, Object> entry : sortedParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Skip 'sign' itself, null values, and empty strings (after trim)
            if ("sign".equals(key) || value == null) {
                continue;
            }

            String stringValue = value.toString();
            if (stringValue.isEmpty()) {
                continue;
            }

            sb.append(key).append(stringValue);
        }

        // 3. MD5 Hash -> Lowercase
        return md5(sb.toString()).toLowerCase();
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                // Convert byte to hex
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
