package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QuerySubMerchant {

    @JsonProperty("sub_merchant_id")
    private String subMerchantId;

    @JsonProperty("sub_merchant_name")
    private String subMerchantName;
}
