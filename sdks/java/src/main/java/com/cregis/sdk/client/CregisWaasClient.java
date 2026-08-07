package com.cregis.sdk.client;

import com.cregis.sdk.core.client.CregisBaseClient;
import com.cregis.sdk.domain.common.ApiResponse;
import com.cregis.sdk.domain.waas.AddressBalanceRequest;
import com.cregis.sdk.domain.waas.AddressBalanceResponse;
import com.cregis.sdk.domain.waas.AddressBalanceV2Request;
import com.cregis.sdk.domain.waas.AddressBalanceV2Response;
import com.cregis.sdk.domain.waas.AddressUpdateRequest;
import com.cregis.sdk.domain.waas.BalanceCollectRequest;
import com.cregis.sdk.domain.waas.BalanceCollectResponse;
import com.cregis.sdk.domain.waas.BatchGenerateAddressRequest;
import com.cregis.sdk.domain.waas.BatchGenerateAddressResponse;
import com.cregis.sdk.domain.waas.CheckAddressLegalityRequest;
import com.cregis.sdk.domain.waas.CheckAddressLegalityResponse;
import com.cregis.sdk.domain.waas.GenerateAddressRequest;
import com.cregis.sdk.domain.waas.GenerateAddressResponse;
import com.cregis.sdk.domain.waas.PayoutRequest;
import com.cregis.sdk.domain.waas.PayoutResponse;
import com.cregis.sdk.domain.waas.PayoutV1Request;
import com.cregis.sdk.domain.waas.ProjectCoinQueryRequest;
import com.cregis.sdk.domain.waas.ProjectCoinQueryResponse;
import com.cregis.sdk.domain.waas.QueryPayoutRequest;
import com.cregis.sdk.domain.waas.QueryPayoutResponse;
import com.cregis.sdk.domain.waas.QueryWithdrawalRequest;
import com.cregis.sdk.domain.waas.QueryWithdrawalResponse;
import com.cregis.sdk.domain.waas.TradeRecordQueryRequest;
import com.cregis.sdk.domain.waas.TradeRecordQueryResponse;
import com.cregis.sdk.domain.waas.ValidateAddressRequest;
import com.cregis.sdk.domain.waas.ValidateAddressResponse;
import com.cregis.sdk.domain.waas.WithdrawalRequest;
import com.cregis.sdk.domain.waas.WithdrawalResponse;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Client for Cregis WaaS (Wallet as a Service).
 */
public class CregisWaasClient extends CregisBaseClient {

    private CregisWaasClient(Builder builder) {
        super(
                builder.endpoint,
                builder.debug,
                objectMapper -> new com.cregis.sdk.core.interceptor.CregisAuthInterceptor(
                        builder.pid,
                        builder.apiKey,
                        objectMapper));
    }

    /**
     * Generate a new deposit address.
     * 
     * @param request The generation request.
     * @return The generated address information.
     */
    public GenerateAddressResponse generateAddress(GenerateAddressRequest request) {
        return execute(
                post("/api/v1/address/create", request).build(),
                new TypeReference<ApiResponse<GenerateAddressResponse>>() {
                });
    }

    /**
     * Batch generate deposit addresses.
     * 
     * @param request The batch generation request.
     * @return The list of generated addresses.
     */
    public java.util.List<BatchGenerateAddressResponse.GeneratedAddress> batchGenerateAddress(
            BatchGenerateAddressRequest request) {
        return execute(
                post("/api/v1/batch/address/create", request).build(),
                new TypeReference<ApiResponse<java.util.List<BatchGenerateAddressResponse.GeneratedAddress>>>() {
                });
    }

    /**
     * Update address information.
     * 
     * @param request The update request.
     */
    public void updateAddress(AddressUpdateRequest request) {
        execute(
                post("/api/v1/address/update", request).build(),
                new TypeReference<ApiResponse<Void>>() {
                });
    }

    /**
     * Validate if an address belongs to the project.
     * 
     * @param request The validation request.
     * @return The validation result.
     */
    public ValidateAddressResponse validateAddress(ValidateAddressRequest request) {
        return execute(
                post("/api/v1/address/inner", request).build(),
                new TypeReference<ApiResponse<ValidateAddressResponse>>() {
                });
    }

    /**
     * Submit a payout request.
     * 
     * @param request The payout details.
     * @return The payout response containing cid.
     */
    public PayoutResponse payoutV1(PayoutV1Request request) {
        return execute(
                post("/api/v1/payout", request).build(),
                new TypeReference<ApiResponse<PayoutResponse>>() {
                });
    }

    public PayoutResponse payoutV2(PayoutRequest request) {
        return execute(
                post("/api/v2/payout", request).build(),
                new TypeReference<ApiResponse<PayoutResponse>>() {
                });
    }

    /**
     * @deprecated Use {@link #payoutV2(PayoutRequest)} to make the API version explicit.
     * @param request The payout details.
     * @return The payout response containing cid.
     */
    @Deprecated
    public PayoutResponse payout(PayoutRequest request) {
        return payoutV2(request);
    }

    /**
     * Check if an external address is legal/valid format format for the chain.
     * 
     * @param request The check request.
     * @return The validation result.
     */
    public CheckAddressLegalityResponse checkAddressLegality(CheckAddressLegalityRequest request) {
        return execute(
                post("/api/v1/address/legal", request).build(),
                new TypeReference<ApiResponse<CheckAddressLegalityResponse>>() {
                });
    }

    /**
     * Collect/Sweep funds from a sub-address to a main wallet address.
     * 
     * @param request The collection request.
     * @return The collection response containing cid.
     */
    public BalanceCollectResponse balanceCollect(BalanceCollectRequest request) {
        return execute(
                post("/api/v1/collection", request).build(),
                new TypeReference<ApiResponse<BalanceCollectResponse>>() {
                });
    }

    /**
     * Query supported project currencies.
     * 
     * @param request The query request (empty body).
     * @return The list of supported coins.
     */
    public ProjectCoinQueryResponse queryProjectCoins(ProjectCoinQueryRequest request) {
        return execute(
                post("/api/v1/coins", request).build(),
                new TypeReference<ApiResponse<ProjectCoinQueryResponse>>() {
                });
    }

    /**
     * Query trade records/history.
     * 
     * @param request The query criteria.
     * @return The paginated trade history.
     */
    public TradeRecordQueryResponse queryTradeRecords(TradeRecordQueryRequest request) {
        return execute(
                post("/api/v1/trade/page", request).build(),
                new TypeReference<ApiResponse<TradeRecordQueryResponse>>() {
                });
    }

    /**
     * Query payout status.
     * 
     * @param request The query request containing cid.
     * @return The payout status details.
     */
    public QueryPayoutResponse queryPayout(QueryPayoutRequest request) {
        return execute(
                post("/api/v1/payout/query", request).build(),
                new TypeReference<ApiResponse<QueryPayoutResponse>>() {
                });
    }

    /**
     * Submit a withdrawal request for a sub-address.
     * 
     * @param request The withdrawal details.
     * @return The withdrawal response containing cid.
     */
    public WithdrawalResponse withdrawal(WithdrawalRequest request) {
        return execute(
                post("/api/v1/sub_address_withdrawal", request).build(),
                new TypeReference<ApiResponse<WithdrawalResponse>>() {
                });
    }

    /**
     * Query withdrawal status.
     * 
     * @param request The query request containing cid.
     * @return The withdrawal status.
     */
    public QueryWithdrawalResponse queryWithdrawal(QueryWithdrawalRequest request) {
        return execute(
                post("/api/v1/sub_address_withdrawal/info", request).build(),
                new TypeReference<ApiResponse<QueryWithdrawalResponse>>() {
                });
    }

    /**
     * Query address balances.
     * 
     * @param request The query request details.
     * @return The paginated balance list.
     */
    public AddressBalanceResponse queryAddressBalance(AddressBalanceRequest request) {
        return execute(
                post("/api/v1/sub_address_balance", request).build(),
                new TypeReference<ApiResponse<AddressBalanceResponse>>() {
                });
    }

    public AddressBalanceV2Response queryAddressBalanceV2(AddressBalanceV2Request request) {
        return execute(
                post("/api/v2/sub_address_balance", request).build(),
                new TypeReference<ApiResponse<AddressBalanceV2Response>>() {
                });
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String endpoint;
        private Long pid;
        private String apiKey;
        private boolean debug = false;

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder credentials(long pid, String apiKey) {
            this.pid = pid;
            this.apiKey = apiKey;
            return this;
        }

        public Builder credentials(String pid, String apiKey) {
            if (pid == null) {
                throw new IllegalArgumentException("PID is required");
            }
            try {
                return credentials(Long.parseLong(pid), apiKey);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("PID must be an int64 value", e);
            }
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public CregisWaasClient build() {
            if (pid == null || apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("PID and API Key are required");
            }
            return new CregisWaasClient(this);
        }
    }
}
