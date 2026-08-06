package com.cregis.sdk.domain.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TeamWallet {

    @JsonProperty("wallet_id")
    private Long walletId;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("wallet_type")
    private String walletType;

    @JsonProperty("wallet_status")
    private String walletStatus;

    @JsonProperty("create_time")
    private Long createTime;

    @JsonProperty("tokens")
    private List<TeamWalletToken> tokens;
}
