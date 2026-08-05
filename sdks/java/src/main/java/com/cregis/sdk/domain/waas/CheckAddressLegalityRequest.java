package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckAddressLegalityRequest {

    @JsonProperty("chain_id")
    private String chainId;

    @JsonProperty("address")
    private String address;
}
