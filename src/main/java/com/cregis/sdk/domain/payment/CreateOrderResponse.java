package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateOrderResponse {

    @JsonProperty("cregis_id")
    private String cregisId;

    @JsonProperty("checkout_url")
    private String checkoutUrl;

    @JsonProperty("order_amount")
    private String orderAmount;

    @JsonProperty("order_currency")
    private String orderCurrency;

    @JsonProperty("created_time")
    private Long createdTime;

    @JsonProperty("expire_time")
    private Long expireTime;
}
