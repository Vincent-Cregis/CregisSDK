package com.cregis.sdk.core.signer;

import org.erdtman.jcs.JsonCanonicalizer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * RFC 8785 and HMAC-SHA256 signing utilities for the Team API.
 */
public final class CregisTeamSigner {

    private static final String HMAC_SHA256 = "HmacSHA256";

    private CregisTeamSigner() {
    }

    public static String canonicalizeBody(String rawJsonBody) {
        if (rawJsonBody == null || rawJsonBody.isEmpty()) {
            return "";
        }
        try {
            return new JsonCanonicalizer(rawJsonBody).getEncodedString();
        } catch (IOException e) {
            throw new IllegalArgumentException("Team API request body must be valid JSON", e);
        }
    }

    public static String sign(
            String path,
            long timestamp,
            String nonce,
            String canonicalBody,
            String accessSecret) {
        if (path == null || !path.startsWith("/")) {
            throw new IllegalArgumentException("Team API signing path must start with '/'");
        }
        if (nonce == null || nonce.length() < 16 || nonce.length() > 64) {
            throw new IllegalArgumentException("Team API nonce must contain 16 to 64 characters");
        }
        if (accessSecret == null || accessSecret.isEmpty()) {
            throw new IllegalArgumentException("Access Secret is required");
        }

        String signingText = path
                + "\n" + timestamp
                + "\n" + nonce
                + "\n" + (canonicalBody == null ? "" : canonicalBody);

        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(accessSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return toLowerHex(mac.doFinal(signingText.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HmacSHA256 is not available", e);
        }
    }

    private static String toLowerHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            result.append(String.format("%02x", value));
        }
        return result.toString();
    }
}
