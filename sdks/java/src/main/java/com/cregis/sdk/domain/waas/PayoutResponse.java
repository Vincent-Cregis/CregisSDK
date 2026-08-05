package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PayoutResponse {

    @JsonProperty("cid")
    private Long cid;
}
