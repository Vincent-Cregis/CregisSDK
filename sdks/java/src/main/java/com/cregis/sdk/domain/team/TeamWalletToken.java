package com.cregis.sdk.domain.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TeamWalletToken {

    @JsonProperty("chain_id")
    private String chainId;

    @JsonProperty("chain_name")
    private String chainName;

    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("token_name")
    private String tokenName;
}
