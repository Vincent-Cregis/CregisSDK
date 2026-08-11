package com.cregis.sdk.client;

import com.cregis.sdk.core.client.CregisBaseClient;
import com.cregis.sdk.core.client.CregisHttpConfig;
import com.cregis.sdk.core.interceptor.CregisTeamAuthInterceptor;
import com.cregis.sdk.core.transport.ApiResponse;
import com.cregis.sdk.generated.team.model.ListTeamWalletAddressesRequest;
import com.cregis.sdk.generated.team.model.ListTeamWalletAddressesResponse;
import com.cregis.sdk.generated.team.model.ListTeamWalletsRequest;
import com.cregis.sdk.generated.team.model.ListTeamWalletsResponse;
import com.cregis.sdk.generated.team.model.QueryTeamWalletAddressBalanceRequest;
import com.cregis.sdk.generated.team.model.QueryTeamWalletAddressBalanceResponse;
import com.cregis.sdk.generated.team.model.QueryTeamWalletBalanceRequest;
import com.cregis.sdk.generated.team.model.QueryTeamWalletBalanceResponse;
import com.cregis.sdk.generated.team.model.QueryTeamWalletHistoryTransactionsRequest;
import com.cregis.sdk.generated.team.model.QueryTeamWalletHistoryTransactionsResponse;
import com.cregis.sdk.generated.team.model.QueryTeamWalletProcessingTransactionsRequest;
import com.cregis.sdk.generated.team.model.QueryTeamWalletProcessingTransactionsResponse;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Client for team-level wallet, address, balance, and transaction queries.
 */
public class CregisTeamClient extends CregisBaseClient {

    private CregisTeamClient(Builder builder) {
        super(
                builder.endpoint,
                builder.debug,
                builder.httpConfig,
                ignored -> new CregisTeamAuthInterceptor(builder.accessKey, builder.accessSecret));
    }

    public ListTeamWalletsResponse listTeamWallets(ListTeamWalletsRequest request) {
        return execute(
                post("/openapi/v1/wallets", request).build(),
                new TypeReference<ApiResponse<ListTeamWalletsResponse>>() {
                });
    }

    public ListTeamWalletAddressesResponse listTeamWalletAddresses(
            ListTeamWalletAddressesRequest request) {
        return execute(
                post("/openapi/v1/wallet_address", request).build(),
                new TypeReference<ApiResponse<ListTeamWalletAddressesResponse>>() {
                });
    }

    public QueryTeamWalletBalanceResponse queryTeamWalletBalance(
            QueryTeamWalletBalanceRequest request) {
        return execute(
                post("/openapi/v1/wallet_balance", request).build(),
                new TypeReference<ApiResponse<QueryTeamWalletBalanceResponse>>() {
                });
    }

    public QueryTeamWalletAddressBalanceResponse queryTeamWalletAddressBalance(
            QueryTeamWalletAddressBalanceRequest request) {
        return execute(
                post("/openapi/v1/wallet_address_balance", request).build(),
                new TypeReference<ApiResponse<QueryTeamWalletAddressBalanceResponse>>() {
                });
    }

    public QueryTeamWalletHistoryTransactionsResponse queryTeamWalletHistoryTransactions(
            QueryTeamWalletHistoryTransactionsRequest request) {
        return execute(
                post("/openapi/v1/wallet_history_transaction_info", request).build(),
                new TypeReference<ApiResponse<QueryTeamWalletHistoryTransactionsResponse>>() {
                });
    }

    public QueryTeamWalletProcessingTransactionsResponse queryTeamWalletProcessingTransactions(
            QueryTeamWalletProcessingTransactionsRequest request) {
        return execute(
                post("/openapi/v1/wallet_processing_transaction_info", request).build(),
                new TypeReference<ApiResponse<QueryTeamWalletProcessingTransactionsResponse>>() {
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
        private CregisHttpConfig httpConfig = CregisHttpConfig.defaults();

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

        public Builder httpConfig(CregisHttpConfig httpConfig) {
            if (httpConfig == null) {
                throw new IllegalArgumentException("HTTP config is required");
            }
            this.httpConfig = httpConfig;
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
