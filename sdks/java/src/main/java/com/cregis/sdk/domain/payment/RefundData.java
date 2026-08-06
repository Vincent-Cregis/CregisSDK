package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RefundData {

    @JsonProperty("refund_status")
    private Integer refundStatus;

    @JsonProperty("refund_id")
    private String refundId;

    @JsonProperty("cregis_id")
    private String cregisId;

    @JsonProperty("reference_id")
    private String referenceId;

    @JsonProperty("recipient_id")
    private String recipientId;

    @JsonProperty("recipient_name")
    private String recipientName;

    @JsonProperty("recipient_email")
    private String recipientEmail;

    @JsonProperty("recipient_address")
    private String recipientAddress;

    @JsonProperty("token")
    private String token;

    @JsonProperty("network")
    private String network;

    @JsonProperty("refund_amount")
    private String refundAmount;

    @JsonProperty("refund_fee")
    private String refundFee;

    @JsonProperty("refund_tx_id")
    private String refundTxId;

    @JsonProperty("refund_created_time")
    private Long refundCreatedTime;

    @JsonProperty("refund_transact_time")
    private Long refundTransactTime;

    @JsonProperty("actual_refund_amount")
    private String actualRefundAmount;

    @JsonProperty("type")
    private Integer type;
}
