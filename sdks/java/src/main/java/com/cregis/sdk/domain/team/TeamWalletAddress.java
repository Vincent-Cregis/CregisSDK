package com.cregis.sdk.domain.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TeamWalletAddress {

    @JsonProperty("address")
    private String address;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("address_status")
    private String addressStatus;

    @JsonProperty("create_time")
    private Long createTime;
}
