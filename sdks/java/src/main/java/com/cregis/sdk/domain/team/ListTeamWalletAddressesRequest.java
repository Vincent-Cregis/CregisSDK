package com.cregis.sdk.domain.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ListTeamWalletAddressesRequest {

    @JsonProperty("page_num")
    private Integer pageNum;

    @JsonProperty("page_size")
    private Integer pageSize;

    @NonNull
    @JsonProperty("wallet_id")
    private Long walletId;

    @NonNull
    @JsonProperty("chain_id")
    private String chainId;
}
