package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WithdrawalRequest {

    @JsonProperty("currency")
    private String currency; // WaaS coin identifier

    @JsonProperty("from_address")
    private String fromAddress; // Must belong to a project wallet

    @JsonProperty("to_address")
    private String toAddress;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @JsonProperty("third_party_id")
    private String thirdPartyId;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("memo")
    private String memo;
}
