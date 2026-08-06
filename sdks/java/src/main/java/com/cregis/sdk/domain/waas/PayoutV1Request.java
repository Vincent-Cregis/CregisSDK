package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class PayoutV1Request {

    @NonNull
    @JsonProperty("currency")
    private String currency;

    @NonNull
    @JsonProperty("address")
    private String address;

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
