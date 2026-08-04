package com.cregis.sdk.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Payment Order Status.
 */
public enum OrderStatus {
    NEW("new"),
    PAID("paid"),
    EXPIRED("expired"),
    PAID_OVER("paid_over"),
    PAID_PARTIAL("paid_partial"),
    PAID_REMAIN("paid_remain"),
    REFUNDED("refunded");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
