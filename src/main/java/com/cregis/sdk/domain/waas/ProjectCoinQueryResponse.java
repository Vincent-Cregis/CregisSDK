package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ProjectCoinQueryResponse {

    @JsonProperty("payout_coins")
    private List<CoinInfo> payoutCoins;

    @JsonProperty("address_coins")
    private List<CoinInfo> addressCoins;

    @Data
    public static class CoinInfo {
        @JsonProperty("coin_name")
        private String coinName;

        @JsonProperty("chain_id")
        private String chainId;

        @JsonProperty("token_id")
        private String tokenId;
    }
}
