package com.cregis.sdk.webhook.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Settlement fields shared by paid, refunded, and remaining-payment callbacks.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentSettlementCallbackData extends PaymentCallbackData {

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

    @JsonProperty("payment_address")
    private String paymentAddress;

    @JsonProperty("transact_time")
    private Long transactTime;

    @JsonProperty("tx_id")
    private String txId;
}
