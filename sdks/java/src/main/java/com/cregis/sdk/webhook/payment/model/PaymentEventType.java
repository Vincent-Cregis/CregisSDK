package com.cregis.sdk.webhook.payment.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Payment Engine webhook event_type values defined by OpenAPI.
 */
public enum PaymentEventType {
    EXPIRED("expired"),
    PAID("paid"),
    PAID_PARTIAL("paid_partial"),
    PAID_OVER("paid_over"),
    REFUNDED("refunded"),
    PAID_REMAIN("paid_remain");

    private final String value;

    PaymentEventType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PaymentEventType fromValue(String value) {
        for (PaymentEventType eventType : values()) {
            if (eventType.value.equals(value)) {
                return eventType;
            }
        }
        throw new IllegalArgumentException("Unsupported Payment callback event_type: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
