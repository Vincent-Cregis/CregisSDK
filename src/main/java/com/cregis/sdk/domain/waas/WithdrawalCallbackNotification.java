package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WithdrawalCallbackNotification {

    @JsonProperty("pid")
    private Long pid;

    @JsonProperty("cid")
    private Long cid;

    @JsonProperty("from_address")
    private String fromAddress;

    @JsonProperty("to_address")
    private String toAddress;

    @JsonProperty("chain_id")
    private String chainId;

    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("third_party_id")
    private String thirdPartyId;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("memo")
    private String memo;

    @JsonProperty("status")
    private Integer status; // 2, 4, 6, 7 like payout

    @JsonProperty("txid")
    private String txid;

    @JsonProperty("block_height")
    private String blockHeight;

    @JsonProperty("block_time")
    private Long blockTime;

    @JsonProperty("nonce")
    private String nonce;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("sign")
    private String sign;
}
