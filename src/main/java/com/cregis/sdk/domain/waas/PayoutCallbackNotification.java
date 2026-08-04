package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PayoutCallbackNotification {

    @JsonProperty("pid")
    private Long pid;

    @JsonProperty("cid")
    private Long cid;

    @JsonProperty("chain_id")
    private String chainId;

    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("address")
    private String address;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("third_party_id")
    private String thirdPartyId;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("status")
    private Integer status; // 2=Sign rejected, 4=Audit rejected, 6=Success, 7=Failed

    @JsonProperty("txid")
    private String txid;

    @JsonProperty("block_height")
    private String blockHeight;

    @JsonProperty("block_time")
    private Long blockTime;

    @JsonProperty("memo")
    private String memo;

    @JsonProperty("nonce")
    private String nonce;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("sign")
    private String sign;
}
