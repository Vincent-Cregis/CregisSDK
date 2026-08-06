package com.cregis.sdk.domain.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class QueryTeamWalletAddressBalanceRequest {

    @JsonProperty("page_num")
    private Integer pageNum;

    @JsonProperty("page_size")
    private Integer pageSize;

    @NonNull
    @JsonProperty("wallet_id")
    private Long walletId;

    @JsonProperty("address")
    private String address;

    @JsonProperty("chain_id")
    private String chainId;

    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("maximum_balance")
    private String maximumBalance;

    @JsonProperty("minimum_balance")
    private String minimumBalance;
}
