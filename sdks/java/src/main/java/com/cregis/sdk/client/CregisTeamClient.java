package com.cregis.sdk.client;

import com.cregis.sdk.core.client.CregisBaseClient;
import com.cregis.sdk.core.interceptor.CregisTeamAuthInterceptor;
import com.cregis.sdk.domain.common.ApiResponse;
import com.cregis.sdk.domain.team.ListTeamWalletAddressesRequest;
import com.cregis.sdk.domain.team.ListTeamWalletsRequest;
import com.cregis.sdk.domain.team.QueryTeamWalletAddressBalanceRequest;
import com.cregis.sdk.domain.team.QueryTeamWalletBalanceRequest;
import com.cregis.sdk.domain.team.QueryTeamWalletHistoryTransactionsRequest;
import com.cregis.sdk.domain.team.QueryTeamWalletProcessingTransactionsRequest;
import com.cregis.sdk.domain.team.TeamPagedResponse;
import com.cregis.sdk.domain.team.TeamWallet;
import com.cregis.sdk.domain.team.TeamWalletAddress;
import com.cregis.sdk.domain.team.TeamWalletAddressBalance;
import com.cregis.sdk.domain.team.TeamWalletBalance;
import com.cregis.sdk.domain.team.TeamWalletProcessingTransaction;
import com.cregis.sdk.domain.team.TeamWalletTransaction;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Client for team-level wallet, address, balance, and transaction queries.
 */
public class CregisTeamClient extends CregisBaseClient {

    private CregisTeamClient(Builder builder) {
        super(
                builder.endpoint,
                builder.debug,
                ignored -> new CregisTeamAuthInterceptor(builder.accessKey, builder.accessSecret));
    }

    public TeamPagedResponse<TeamWallet> listTeamWallets(ListTeamWalletsRequest request) {
        return execute(
                post("/openapi/v1/wallets", request).build(),
                new TypeReference<ApiResponse<TeamPagedResponse<TeamWallet>>>() {
                });
    }

    public TeamPagedResponse<TeamWalletAddress> listTeamWalletAddresses(
            ListTeamWalletAddressesRequest request) {
        return execute(
                post("/openapi/v1/wallet_address", request).build(),
                new TypeReference<ApiResponse<TeamPagedResponse<TeamWalletAddress>>>() {
                });
    }

    public TeamPagedResponse<TeamWalletBalance> queryTeamWalletBalance(
            QueryTeamWalletBalanceRequest request) {
        return execute(
                post("/openapi/v1/wallet_balance", request).build(),
                new TypeReference<ApiResponse<TeamPagedResponse<TeamWalletBalance>>>() {
                });
    }

    public TeamPagedResponse<TeamWalletAddressBalance> queryTeamWalletAddressBalance(
            QueryTeamWalletAddressBalanceRequest request) {
        return execute(
                post("/openapi/v1/wallet_address_balance", request).build(),
                new TypeReference<ApiResponse<TeamPagedResponse<TeamWalletAddressBalance>>>() {
                });
    }

    public TeamPagedResponse<TeamWalletTransaction> queryTeamWalletHistoryTransactions(
            QueryTeamWalletHistoryTransactionsRequest request) {
        return execute(
                post("/openapi/v1/wallet_history_transaction_info", request).build(),
                new TypeReference<ApiResponse<TeamPagedResponse<TeamWalletTransaction>>>() {
                });
    }

    public TeamPagedResponse<TeamWalletProcessingTransaction> queryTeamWalletProcessingTransactions(
            QueryTeamWalletProcessingTransactionsRequest request) {
        return execute(
                post("/openapi/v1/wallet_processing_transaction_info", request).build(),
                new TypeReference<ApiResponse<TeamPagedResponse<TeamWalletProcessingTransaction>>>() {
                });
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String endpoint;
        private String accessKey;
        private String accessSecret;
        private boolean debug;

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder credentials(String accessKey, String accessSecret) {
            this.accessKey = accessKey;
            this.accessSecret = accessSecret;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public CregisTeamClient build() {
            if (accessKey == null || accessKey.trim().isEmpty()
                    || accessSecret == null || accessSecret.trim().isEmpty()) {
                throw new IllegalArgumentException("Access Key and Access Secret are required");
            }
            return new CregisTeamClient(this);
        }
    }
}
