package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class QueryOrderResponse {

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

    @JsonProperty("transact_time")
    private Long transactTime;

    @JsonProperty("valid_time")
    private Integer validTime;

    @JsonProperty("status")
    private String status;

    @JsonProperty("refund_requested")
    private String refundRequested;

    @JsonProperty("payer_id")
    private String payerId;

    @JsonProperty("payer_name")
    private String payerName;

    @JsonProperty("payer_email")
    private String payerEmail;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("settlement_status")
    private String settlementStatus;

    @JsonProperty("settlement_type")
    private String settlementType;

    @JsonProperty("payment_detail")
    private List<PaymentDetail> paymentDetail;

    @JsonProperty("payment_info")
    private List<PaymentInfo> paymentInfo;

    @JsonProperty("refund_data")
    private RefundData refundData;

    @JsonProperty("order_details")
    private QueryOrderDetails orderDetails;

    @JsonProperty("sub_merchant")
    private QuerySubMerchant subMerchant;

    @JsonProperty("settlement_details")
    private SettlementDetails settlementDetails;
}
