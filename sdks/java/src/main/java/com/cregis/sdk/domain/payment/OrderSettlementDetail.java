package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderSettlementDetail {

    @JsonProperty("status")
    private String status;

    @JsonProperty("settlement_amount")
    private String settlementAmount;

    @JsonProperty("settlement_fee")
    private String settlementFee;

    @JsonProperty("actual_settlement_amount")
    private String actualSettlementAmount;
}
