package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class QueryOrderRequest {

    @NonNull
    @JsonProperty("cregis_id")
    private String cregisId;
}
