package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data for a refunded order callback.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentRefundedCallbackData extends PaymentSettlementCallbackData {

    @JsonProperty("refund_requested")
    private String refundRequested;

    @JsonProperty("type")
    private Integer type;

    @JsonProperty("refund_id")
    private String refundId;

    @JsonProperty("refund_address")
    private String refundAddress;

    @JsonProperty("refund_currency")
    private String refundCurrency;

    @JsonProperty("refund_amount")
    private String refundAmount;

    @JsonProperty("refund_status")
    private Integer refundStatus;

    @JsonProperty("refund_tx_id")
    private String refundTxId;

    @JsonProperty("refund_fee")
    private String refundFee;

    @JsonProperty("actual_refund_amount")
    private String actualRefundAmount;

    @JsonProperty("refund_created_time")
    private Long refundCreatedTime;

    @JsonProperty("refund_transact_time")
    private Long refundTransactTime;
}
