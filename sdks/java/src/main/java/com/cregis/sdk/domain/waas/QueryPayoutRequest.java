package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class QueryPayoutRequest {

    @NonNull
    @JsonProperty("cid")
    private Long cid;
}
