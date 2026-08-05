package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatchGenerateAddressRequest {

    @JsonProperty("chain_id")
    private String chainId;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @JsonProperty("number")
    private Integer number;
}
