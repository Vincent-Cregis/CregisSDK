package com.cregis.sdk.domain.enums;

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

    @JsonValue
    public String getValue() {
        return value;
    }
}
