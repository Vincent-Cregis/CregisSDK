package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SettlementDetails {

    @JsonProperty("id")
    private String id;

    @JsonProperty("created_time")
    private Long createdTime;

    @JsonProperty("from_address")
    private String fromAddress;

    @JsonProperty("to_address")
    private String toAddress;

    @JsonProperty("tx_id")
    private String txId;

    @JsonProperty("settlement_currency")
    private String settlementCurrency;

    @JsonProperty("order_count")
    private Long orderCount;

    @JsonProperty("total_settlement_amount")
    private String totalSettlementAmount;

    @JsonProperty("total_settlement_fee")
    private String totalSettlementFee;

    @JsonProperty("total_actual_settlement_amount")
    private String totalActualSettlementAmount;

    @JsonProperty("order_settlement_detail")
    private OrderSettlementDetail orderSettlementDetail;
}
