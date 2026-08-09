package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentCallbackData {

    @JsonProperty("cregis_id")
    private String cregisId;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("order_amount")
    private String orderAmount;

    @JsonProperty("order_currency")
    private String orderCurrency;

    @JsonProperty("created_time")
    private Long createdTime;

    @JsonProperty("cancel_time")
    private Long cancelTime;

    @JsonProperty("valid_time")
    private Integer validTime;

    @JsonProperty("status")
    private String status;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("payer_id")
    private String payerId;

    @JsonProperty("payer_name")
    private String payerName;

    @JsonProperty("payer_email")
    private String payerEmail;

}
