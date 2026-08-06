package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentInfo {

    @JsonProperty("payment_address")
    private String paymentAddress;

    @JsonProperty("token_symbol")
    private String tokenSymbol;

    @JsonProperty("blockchain")
    private String blockchain;

    @JsonProperty("token_name")
    private String tokenName;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("token_decimals")
    private Integer tokenDecimals;

    @JsonProperty("receive_amount")
    private String receiveAmount;

    @JsonProperty("receive_currency")
    private String receiveCurrency;

    @JsonProperty("exchange_rate")
    private String exchangeRate;

    @JsonProperty("asset_logo")
    private String assetLogo;
}
