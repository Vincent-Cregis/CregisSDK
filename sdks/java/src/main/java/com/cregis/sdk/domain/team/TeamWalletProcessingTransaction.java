package com.cregis.sdk.domain.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TeamWalletProcessingTransaction {

    @JsonProperty("wallet_id")
    private Long walletId;

    @JsonProperty("chain_id")
    private String chainId;

    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("from_address")
    private String fromAddress;

    @JsonProperty("to_address")
    private String toAddress;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("transaction_type")
    private Integer transactionType;

    @JsonProperty("txid")
    private String txid;
}
