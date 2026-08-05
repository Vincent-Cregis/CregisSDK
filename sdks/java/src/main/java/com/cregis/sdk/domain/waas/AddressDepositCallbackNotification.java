package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddressDepositCallbackNotification {

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

    @JsonProperty("status")
    private String status; // "1" success, "2" failed

    @JsonProperty("txid")
    private String txid;

    @JsonProperty("block_height")
    private String blockHeight;

    @JsonProperty("block_time")
    private String blockTime;

    @JsonProperty("memo")
    private String memo;

    @JsonProperty("nonce")
    private String nonce;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("sign")
    private String sign;
}
