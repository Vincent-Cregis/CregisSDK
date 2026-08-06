package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PayoutExternalVerificationCallbackNotification {

    @JsonProperty("pid")
    private Long pid;

    @JsonProperty("cid")
    private Long cid;

    @JsonProperty("third_party_id")
    private String thirdPartyId;

    @JsonProperty("chain_id")
    private String chainId;

    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("from_address")
    private String fromAddress;

    @JsonProperty("to_address")
    private String toAddress;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("memo")
    private String memo;

    @JsonProperty("nonce")
    private String nonce;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("sign")
    private String sign;
}
