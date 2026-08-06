package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AddressBalanceRequest {

    @NonNull
    @JsonProperty("currency")
    private String currency;

    @JsonProperty("address")
    private String address;

    @JsonProperty("maximum_balance")
    private String maximumBalance;

    @JsonProperty("minimum_balance")
    private String minimumBalance;

    @JsonProperty("page_num")
    private Integer pageNum;

    @JsonProperty("page_size")
    private Integer pageSize;
}
