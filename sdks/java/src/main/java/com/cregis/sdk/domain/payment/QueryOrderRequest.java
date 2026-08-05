package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryOrderRequest {

    @JsonProperty("cregis_id")
    private String cregisId;
}
