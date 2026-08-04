package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentCallbackData {

    @JsonProperty("cregis_id")
    private String cregisId;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("receive_amount")
    private String receiveAmount;

    @JsonProperty("receive_currency")
    private String receiveCurrency;

    @JsonProperty("pay_amount")
    private String payAmount;

    @JsonProperty("pay_currency")
    private String payCurrency;

    @JsonProperty("order_amount")
    private String orderAmount;

    @JsonProperty("order_currency")
    private String orderCurrency;

    @JsonProperty("exchange_rate")
    private String exchangeRate;

    @JsonProperty("payment_address")
    private String paymentAddress;

    @JsonProperty("created_time")
    private Long createdTime;

    @JsonProperty("cancel_time")
    private Long cancelTime;

    @JsonProperty("transact_time")
    private Long transactTime;

    @JsonProperty("valid_time")
    private Integer validTime;

    @JsonProperty("status")
    private String status;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("tx_id")
    private String txId;

    @JsonProperty("payer_id")
    private String payerId;

    @JsonProperty("payer_name")
    private String payerName;

    @JsonProperty("payer_email")
    private String payerEmail;

    // Refund Related Fields
    @JsonProperty("refund_requested")
    private String refundRequested;

    @JsonProperty("type")
    private String type;

    @JsonProperty("refund_id")
    private Long refundId;

    @JsonProperty("refund_address")
    private String refundAddress;

    @JsonProperty("refund_currency")
    private String refundCurrency;

    @JsonProperty("refund_amount")
    private String refundAmount;

    @JsonProperty("refund_status")
    private String refundStatus;

    @JsonProperty("refund_tx_id")
    private String refundTxId;

    @JsonProperty("refund_fee")
    private String refundFee;

    @JsonProperty("actual_refund_amount")
    private String actualRefundAmount;

    @JsonProperty("refund_created_time")
    private String refundCreatedTime;

    @JsonProperty("refund_transact_time")
    private String refundTransactTime;

    // Additional Payment (Replenishment) Fields
    @JsonProperty("additional_pay_currency")
    private String additionalPayCurrency;

    @JsonProperty("additional_pay_amount")
    private String additionalPayAmount;

    @JsonProperty("additional_payment_address")
    private String additionalPaymentAddress;

    @JsonProperty("additional_payment_tx_id")
    private String additionalPaymentTxId;

    @JsonProperty("additional_payment_time")
    private Long additionalPaymentTime;
}
