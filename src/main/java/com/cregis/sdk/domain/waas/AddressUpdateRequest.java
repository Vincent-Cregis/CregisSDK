package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressUpdateRequest {

    @JsonProperty("address")
    private String address;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("status")
    private String status; // 0-Enable, 1-Disable
}
