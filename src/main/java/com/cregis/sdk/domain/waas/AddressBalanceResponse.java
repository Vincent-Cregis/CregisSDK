package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class AddressBalanceResponse {

    @JsonProperty("total")
    private Long total;

    @JsonProperty("pageNum")
    private Integer pageNum;

    @JsonProperty("pageSize")
    private Integer pageSize;

    @JsonProperty("rows")
    private List<BalanceItem> rows;

    @Data
    public static class BalanceItem {
        @JsonProperty("pid")
        private Long pid;

        @JsonProperty("address")
        private String address;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("total")
        private String total;

        @JsonProperty("available")
        private String available;

        @JsonProperty("processing")
        private String processing;
    }
}
