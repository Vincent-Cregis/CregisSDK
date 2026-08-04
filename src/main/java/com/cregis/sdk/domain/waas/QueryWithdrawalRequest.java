package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryWithdrawalRequest {

    @JsonProperty("cid")
    private Long cid;
}
