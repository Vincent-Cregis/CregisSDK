package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class BalanceCollectRequest {

    @NonNull
    @JsonProperty("currency")
    private String currency;

    @NonNull
    @JsonProperty("from_address")
    private String fromAddress;

    @NonNull
    @JsonProperty("to_address")
    private String toAddress;

    @JsonProperty("amount")
    private String amount;
}
