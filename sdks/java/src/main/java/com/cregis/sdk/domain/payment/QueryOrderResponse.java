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

    // Skipping complex nested objects like order_details or refund_data for brevity
    // unless requested.
    // They can be mapped as generic Map or specific classes if detailed access is
    // needed.

    @Data
    public static class PaymentDetail {
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

    @Data
    public static class PaymentInfo {
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
}
