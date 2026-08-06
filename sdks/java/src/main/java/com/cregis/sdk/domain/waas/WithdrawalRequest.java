package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class WithdrawalRequest {

    @NonNull
    @JsonProperty("currency")
    private String currency; // WaaS coin identifier

    @NonNull
    @JsonProperty("from_address")
    private String fromAddress; // Must belong to a project wallet

    @NonNull
    @JsonProperty("to_address")
    private String toAddress;

    @NonNull
    @JsonProperty("amount")
    private String amount;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @NonNull
    @JsonProperty("third_party_id")
    private String thirdPartyId;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("memo")
    private String memo;
}
