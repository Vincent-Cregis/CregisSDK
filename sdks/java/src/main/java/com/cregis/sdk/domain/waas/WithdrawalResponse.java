package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WithdrawalResponse {

    @JsonProperty("cid")
    private Long cid;
}
