package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentDetail {

    @JsonProperty("payment_address")
    private String paymentAddress;

    @JsonProperty("from_address")
    private String fromAddress;

    @JsonProperty("receive_amount")
    private String receiveAmount;

    @JsonProperty("receive_currency")
    private String receiveCurrency;

    @JsonProperty("pay_amount")
    private String payAmount;

    @JsonProperty("pay_currency")
    private String payCurrency;

    @JsonProperty("exchange_rate")
    private String exchangeRate;

    @JsonProperty("tx_id")
    private String txId;

    @JsonProperty("blockchain")
    private String blockchain;

    @JsonProperty("token_name")
    private String tokenName;
}
