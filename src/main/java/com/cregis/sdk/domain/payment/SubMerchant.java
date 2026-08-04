package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubMerchant {
    @JsonProperty("sub_merchant_id")
    private String subMerchantId;

    @JsonProperty("sub_merchant_name")
    private String subMerchantName;
}
