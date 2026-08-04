package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TradeRecordQueryRequest {

    @JsonProperty("cid")
    private Long cid;

    @JsonProperty("tx_id")
    private String txId;

    @JsonProperty("status")
    private Integer status; // 0-Pending, 1-Success, 2-Failed

    @JsonProperty("trade_type")
    private Integer tradeType; // 1-In, 2-Out

    @JsonProperty("business_type")
    private Integer businessType; // 0:Normal, 2:Audit, 3:Collection, 4:Fee, 5:Collect

    @JsonProperty("blocktime_start")
    private Long blocktimeStart;

    @JsonProperty("blocktime_end")
    private Long blocktimeEnd;

    @JsonProperty("chain_id")
    private String chainId;

    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("page_num")
    private Integer pageNum;

    @JsonProperty("page_size")
    private Integer pageSize;
}
