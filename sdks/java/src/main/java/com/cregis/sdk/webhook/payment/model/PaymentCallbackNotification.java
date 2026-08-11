package com.cregis.sdk.webhook.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentCallbackNotification<T extends PaymentCallbackData> {

    @JsonProperty("event_name")
    private String eventName;

    @JsonProperty("event_type")
    private PaymentEventType eventType;

    @JsonProperty("pid")
    private Long pid;

    @JsonProperty("nonce")
    private String nonce;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("sign")
    private String sign;

    @JsonProperty("data")
    private T data;
}
