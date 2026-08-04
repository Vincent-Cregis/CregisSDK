package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class TradeRecordQueryResponse {

    @JsonProperty("total")
    private Long total;

    @JsonProperty("pageNum")
    private Integer pageNum;

    @JsonProperty("pageSize")
    private Integer pageSize;

    @JsonProperty("rows")
    private List<TradeRecord> rows;

    @Data
    public static class TradeRecord {
        @JsonProperty("pid")
        private Long pid;

        @JsonProperty("cid")
        private Long cid;

        @JsonProperty("chain_id")
        private String chainId;

        @JsonProperty("token_id")
        private String tokenId;

        // Note: Docs say "currency", "to_address" etc.
        @JsonProperty("currency")
        private String currency;

        @JsonProperty("from_address")
        private String fromAddress;

        @JsonProperty("to_address")
        private String toAddress;

        @JsonProperty("amount")
        private String amount;

        @JsonProperty("fee")
        private String fee;

        @JsonProperty("status")
        private Integer status;

        @JsonProperty("txid")
        private String txid;

        @JsonProperty("block_height")
        private String blockHeight;

        @JsonProperty("block_time")
        private Long blockTime;
    }
}
