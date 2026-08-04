package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentCallbackNotification {

    @JsonProperty("event_name")
    private String eventName;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("pid")
    private Long pid;

    @JsonProperty("nonce")
    private String nonce;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("sign")
    private String sign;

    @JsonProperty("data")
    private PaymentCallbackData data;
}
