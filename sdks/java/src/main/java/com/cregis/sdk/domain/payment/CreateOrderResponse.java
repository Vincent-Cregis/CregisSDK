package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderResponse {

    @JsonProperty("cregis_id")
    private String cregisId;

    @JsonProperty("checkout_url")
    private String checkoutUrl;

    @JsonProperty("merchant_name")
    private String merchantName;

    @JsonProperty("merchant_logo_url")
    private String merchantLogoUrl;

    @JsonProperty("order_amount")
    private String orderAmount;

    @JsonProperty("order_currency")
    private String orderCurrency;

    @JsonProperty("created_time")
    private Long createdTime;

    @JsonProperty("expire_time")
    private Long expireTime;

    @JsonProperty("payment_info")
    private List<PaymentInfo> paymentInfo;
}
