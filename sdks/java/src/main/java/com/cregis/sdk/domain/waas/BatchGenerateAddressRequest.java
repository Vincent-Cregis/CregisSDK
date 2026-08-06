package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class BatchGenerateAddressRequest {

    @NonNull
    @JsonProperty("chain_id")
    private String chainId;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @NonNull
    @JsonProperty("number")
    private String number;
}
