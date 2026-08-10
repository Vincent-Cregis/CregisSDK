package com.cregis.sdk.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

/**
 * Encodes structured Payment Engine values that the API represents as JSON strings.
 */
public final class CregisPaymentValues {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private CregisPaymentValues() {
    }

    /**
     * Serializes a structured value for fields such as {@code order_details},
     * {@code sub_merchant}, and {@code tokens}.
     *
     * @param value object or collection to encode
     * @return compact JSON text suitable for a generated request model
     * @throws NullPointerException if {@code value} is null
     * @throws IllegalArgumentException if Jackson cannot serialize the value
     */
    public static String jsonString(Object value) {
        Objects.requireNonNull(value, "value");
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Payment value cannot be encoded as JSON", e);
        }
    }
}
