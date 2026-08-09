package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data for a remaining-payment callback.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentRemainingCallbackData extends PaymentSettlementCallbackData {

    @JsonProperty("additional_pay_currency")
    private String additionalPayCurrency;

    @JsonProperty("additional_pay_amount")
    private String additionalPayAmount;

    @JsonProperty("additional_payment_address")
    private String additionalPaymentAddress;

    @JsonProperty("additional_payment_tx_id")
    private String additionalPaymentTxId;

    @JsonProperty("additional_payment_transact_time")
    private Long additionalPaymentTransactTime;
}
