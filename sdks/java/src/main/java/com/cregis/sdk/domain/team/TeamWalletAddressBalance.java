package com.cregis.sdk.domain.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TeamWalletAddressBalance {

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
