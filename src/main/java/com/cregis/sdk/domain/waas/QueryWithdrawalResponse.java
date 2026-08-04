package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QueryWithdrawalResponse {

    @JsonProperty("pid")
    private Long pid;

    @JsonProperty("chain_id")
    private String chainId;

    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("from_address")
    private String fromAddress;

    @JsonProperty("to_address")
    private String toAddress;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("status")
    private Integer status; // 0, 4, 5, 1, 2, 6, 7

    @JsonProperty("third_party_id")
    private String thirdPartyId;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("memo")
    private String memo;

    @JsonProperty("txid")
    private String txid;

    @JsonProperty("block_height")
    private String blockHeight;

    @JsonProperty("block_time")
    private Long blockTime;
}
