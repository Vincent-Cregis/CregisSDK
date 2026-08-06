package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddressBalanceV2Response {

    @JsonProperty("total")
    private Long total;

    @JsonProperty("rows")
    private List<BalanceItem> rows;

    @JsonProperty("page_num")
    private Integer pageNum;

    @JsonProperty("page_size")
    private Integer pageSize;

    @Data
    public static class BalanceItem {

        @JsonProperty("address")
        private String address;

        @JsonProperty("chain_id")
        private String chainId;

        @JsonProperty("token_id")
        private String tokenId;

        @JsonProperty("total")
        private String total;

        @JsonProperty("available")
        private String available;

        @JsonProperty("processing")
        private String processing;
    }
}
