package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ValidateAddressRequest {

    @NonNull
    @JsonProperty("chain_id")
    private String chainId;

    @NonNull
    @JsonProperty("address")
    private String address;
}
