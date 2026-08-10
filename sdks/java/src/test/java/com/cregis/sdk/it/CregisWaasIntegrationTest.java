package com.cregis.sdk.it;

import com.cregis.sdk.client.CregisWaasClient;
import com.cregis.sdk.generated.waas.model.AddressBalanceRequest;
import com.cregis.sdk.generated.waas.model.AddressBalanceResponse;
import com.cregis.sdk.generated.waas.model.AddressBalanceV2Request;
import com.cregis.sdk.generated.waas.model.AddressBalanceV2Response;
import com.cregis.sdk.generated.waas.model.AddressUpdateRequest;
import com.cregis.sdk.generated.waas.model.BalanceCollectRequest;
import com.cregis.sdk.generated.waas.model.BalanceCollectResponse;
import com.cregis.sdk.generated.waas.model.BatchGenerateAddressRequest;
import com.cregis.sdk.generated.waas.model.GeneratedAddress;
import com.cregis.sdk.generated.waas.model.CheckAddressLegalityRequest;
import com.cregis.sdk.generated.waas.model.CheckAddressLegalityResponse;
import com.cregis.sdk.generated.waas.model.GenerateAddressRequest;
import com.cregis.sdk.generated.waas.model.GenerateAddressResponse;
import com.cregis.sdk.generated.waas.model.PayoutRequest;
import com.cregis.sdk.generated.waas.model.PayoutResponse;
import com.cregis.sdk.generated.waas.model.PayoutV1Request;
import com.cregis.sdk.generated.waas.model.ProjectCoinQueryResponse;
import com.cregis.sdk.generated.waas.model.ProjectCoin;
import com.cregis.sdk.generated.waas.model.QueryPayoutRequest;
import com.cregis.sdk.generated.waas.model.QueryPayoutResponse;
import com.cregis.sdk.generated.waas.model.QueryWithdrawalRequest;
import com.cregis.sdk.generated.waas.model.QueryWithdrawalResponse;
import com.cregis.sdk.generated.waas.model.TradeRecordQueryRequest;
import com.cregis.sdk.generated.waas.model.TradeRecordQueryResponse;
import com.cregis.sdk.generated.waas.model.ValidateAddressRequest;
import com.cregis.sdk.generated.waas.model.ValidateAddressResponse;
import com.cregis.sdk.generated.waas.model.WithdrawalRequest;
import com.cregis.sdk.generated.waas.model.WithdrawalResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * State-changing Sandbox coverage for every WaaS OpenAPI operation.
 */
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CregisWaasIntegrationTest {

    private static CregisWaasClient client;
    private static String chainId;
    private static String currency;
    private static String generatedAddress;
    private static String payoutDestination;
    private static String withdrawalDestination;
    private static String sourceAddress;
    private static String testAmount;
    private static Long payoutCid;
    private static Long withdrawalCid;
    private static String payoutWalletAddress;

    @BeforeAll
    static void setUp() {
        assumeTrue(
                IntegrationTestEnvironment.allPresent("WAAS_PID", "WAAS_API_KEY", "WAAS_ENDPOINT"),
                "Skipping: WaaS Sandbox credentials or project Base URL not found");
        assumeTrue(
                IntegrationTestEnvironment.isTrue("CREGIS_ALLOW_MUTATING_TESTS"),
                "Skipping state-changing WaaS tests: CREGIS_ALLOW_MUTATING_TESTS is not true");

        sourceAddress = IntegrationTestEnvironment.get("WITHDRAW_ADDRESS");
        testAmount = valueOrDefault(IntegrationTestEnvironment.get("WAAS_TEST_AMOUNT"), "0.001");
        client = CregisWaasClient.builder()
                .endpoint(IntegrationTestEnvironment.get("WAAS_ENDPOINT"))
                .credentials(
                        IntegrationTestEnvironment.get("WAAS_PID"),
                        IntegrationTestEnvironment.get("WAAS_API_KEY"))
                .build();
    }

    @Test
    @Order(1)
    void queriesProjectCoinsAndSelectsSandboxCurrency() {
        ProjectCoinQueryResponse response = client.queryProjectCoins();

        assertNotNull(response);
        assertNotNull(response.getAddressCoins());
        assertNotNull(response.getPayoutCoins());
        assertFalse(response.getAddressCoins().isEmpty(), "Project needs at least one address coin");
        assertFalse(response.getPayoutCoins().isEmpty(), "Project needs at least one payout coin");

        String preferredChainId = valueOrDefault(IntegrationTestEnvironment.get("WAAS_CHAIN_ID"), "198");
        ProjectCoin payoutCoin = selectPayoutCoin(
                response.getPayoutCoins(), response.getAddressCoins(), preferredChainId);
        assertNotNull(payoutCoin, "Project needs a chain that supports both address creation and payout");
        assertNotNull(payoutCoin.getChainId());
        assertNotNull(payoutCoin.getTokenId());

        chainId = payoutCoin.getChainId();
        currency = payoutCoin.getChainId() + "@" + payoutCoin.getTokenId();
    }

    @Test
    @Order(2)
    void createsAddress() {
        requireCoinSelection();
        GenerateAddressResponse response = client.generateAddress(GenerateAddressRequest.builder()
                .chainId(chainId)
                .alias(shortAlias("sdk"))
                .build());

        assertNotNull(response);
        assertNotNull(response.getAddress());
        generatedAddress = response.getAddress();
        payoutDestination = valueOrDefault(
                IntegrationTestEnvironment.get("WAAS_PAYOUT_TO_ADDRESS"), generatedAddress);
        withdrawalDestination = valueOrDefault(
                IntegrationTestEnvironment.get("WITHDRAW_TO_ADDRESS"), generatedAddress);
    }

    @Test
    @Order(3)
    void createsBatchAddresses() {
        requireCoinSelection();
        List<GeneratedAddress> addresses = client.batchGenerateAddress(
                BatchGenerateAddressRequest.builder()
                        .chainId(chainId)
                        .number("2")
                        .alias(shortAlias("batch"))
                        .build());

        assertNotNull(addresses);
        assertFalse(addresses.isEmpty());
        assertNotNull(addresses.get(0).getAddress());
    }

    @Test
    @Order(4)
    void updatesAddress() {
        requireGeneratedAddress();
        client.updateAddress(AddressUpdateRequest.builder()
                .address(generatedAddress)
                .alias(shortAlias("updated"))
                .build());
    }

    @Test
    @Order(5)
    void validatesInternalAddress() {
        requireGeneratedAddress();
        ValidateAddressResponse response = client.validateAddress(ValidateAddressRequest.builder()
                .chainId(chainId)
                .address(generatedAddress)
                .build());

        assertNotNull(response);
        assertTrue(Boolean.TRUE.equals(response.getResult()), "Generated address should belong to the project");
    }

    @Test
    @Order(6)
    void validatesAddressLegality() {
        requireGeneratedAddress();
        CheckAddressLegalityResponse response = client.checkAddressLegality(CheckAddressLegalityRequest.builder()
                .chainId(chainId)
                .address(generatedAddress)
                .build());

        assertNotNull(response);
        assertTrue(Boolean.TRUE.equals(response.getResult()), "Generated address should be legal for its chain");
    }

    @Test
    @Order(7)
    void queriesAddressBalanceV1() {
        requireGeneratedAddress();
        AddressBalanceResponse response = client.queryAddressBalance(AddressBalanceRequest.builder()
                .currency(currency)
                .address(valueOrDefault(sourceAddress, generatedAddress))
                .pageNum(1)
                .pageSize(10)
                .build());

        assertNotNull(response);
        assertNotNull(response.getRows());
    }

    @Test
    @Order(8)
    void queriesAddressBalanceV2() {
        requireGeneratedAddress();
        AddressBalanceV2Response response = client.queryAddressBalanceV2(AddressBalanceV2Request.builder()
                .address(valueOrDefault(sourceAddress, generatedAddress))
                .currency(currency)
                .pageNum(1)
                .pageSize(10)
                .build());

        assertNotNull(response);
        assertNotNull(response.getRows());
    }

    @Test
    @Order(9)
    void queriesTradeRecords() {
        TradeRecordQueryResponse response = client.queryTradeRecords(TradeRecordQueryRequest.builder()
                .pageNum(1)
                .pageSize(10)
                .build());

        assertNotNull(response);
        assertNotNull(response.getRows());
    }

    @Test
    @Order(10)
    void submitsPayoutV1() {
        requireGeneratedAddress();
        PayoutResponse response = client.payoutV1(PayoutV1Request.builder()
                .currency(currency)
                .address(payoutDestination)
                .amount(testAmount)
                .thirdPartyId(uniqueBusinessId("sdk-p1"))
                .remark("Java SDK Sandbox test")
                .build());

        assertNotNull(response);
        assertNotNull(response.getCid());
        payoutCid = response.getCid();
    }

    @Test
    @Order(11)
    void queriesPayout() {
        assumeTrue(payoutCid != null, "Payout query requires a successful payout submission");
        QueryPayoutResponse response = client.queryPayout(QueryPayoutRequest.builder().cid(payoutCid).build());

        assertNotNull(response);
        payoutWalletAddress = response.getFromAddress();
    }

    @Test
    @Order(12)
    void submitsPayoutV2() {
        requireGeneratedAddress();
        String walletId = IntegrationTestEnvironment.get("WAAS_WALLET_ID");
        PayoutRequest.Builder request = PayoutRequest.builder()
                .currency(currency)
                .toAddress(payoutDestination)
                .amount(testAmount)
                .thirdPartyId(uniqueBusinessId("sdk-p2"))
                .remark("Java SDK Sandbox test");
        if (walletId != null) {
            request.walletId(Long.parseLong(walletId));
        }

        PayoutResponse response = client.payoutV2(request.build());
        assertNotNull(response);
        assertNotNull(response.getCid());
    }

    @Test
    @Order(13)
    void submitsSubAddressWithdrawal() {
        requireGeneratedAddress();
        assumeTrue(sourceAddress != null, "Withdrawal requires WITHDRAW_ADDRESS");
        WithdrawalResponse response = client.withdrawal(WithdrawalRequest.builder()
                .currency(currency)
                .fromAddress(sourceAddress)
                .toAddress(withdrawalDestination)
                .amount(testAmount)
                .thirdPartyId(uniqueBusinessId("sdk-wd"))
                .remark("Java SDK Sandbox test")
                .build());

        assertNotNull(response);
        assertNotNull(response.getCid());
        withdrawalCid = response.getCid();
    }

    @Test
    @Order(14)
    void queriesSubAddressWithdrawal() {
        assumeTrue(withdrawalCid != null, "Withdrawal query requires a successful withdrawal submission");
        QueryWithdrawalResponse response = client.queryWithdrawal(
                QueryWithdrawalRequest.builder().cid(withdrawalCid).build());

        assertNotNull(response);
    }

    @Test
    @Order(15)
    void submitsBalanceCollection() {
        assumeTrue(sourceAddress != null, "Collection requires WITHDRAW_ADDRESS");
        String collectionDestination = valueOrDefault(
                IntegrationTestEnvironment.get("WAAS_COLLECTION_TO_ADDRESS"), payoutWalletAddress);
        assumeTrue(
                collectionDestination != null,
                "Collection requires WAAS_COLLECTION_TO_ADDRESS or a payout response with from_address");

        BalanceCollectResponse response = client.balanceCollect(BalanceCollectRequest.builder()
                .currency(currency)
                .fromAddress(sourceAddress)
                .toAddress(collectionDestination)
                .amount(testAmount)
                .build());

        assertNotNull(response);
        assertNotNull(response.getCid());
    }

    private static ProjectCoin selectPayoutCoin(
            List<ProjectCoin> payoutCoins,
            List<ProjectCoin> addressCoins,
            String preferredChainId) {
        ProjectCoin fallback = null;
        for (ProjectCoin payoutCoin : payoutCoins) {
            if (!hasAddressChain(addressCoins, payoutCoin.getChainId())) {
                continue;
            }
            if (preferredChainId.equals(payoutCoin.getChainId())) {
                return payoutCoin;
            }
            if (fallback == null) {
                fallback = payoutCoin;
            }
        }
        return fallback;
    }

    private static boolean hasAddressChain(
            List<ProjectCoin> addressCoins,
            String candidateChainId) {
        for (ProjectCoin addressCoin : addressCoins) {
            if (candidateChainId != null && candidateChainId.equals(addressCoin.getChainId())) {
                return true;
            }
        }
        return false;
    }

    private static void requireCoinSelection() {
        assumeTrue(chainId != null && currency != null, "Test requires a supported Sandbox currency");
    }

    private static void requireGeneratedAddress() {
        requireCoinSelection();
        assumeTrue(generatedAddress != null, "Test requires a successfully generated Sandbox address");
    }

    private static String valueOrDefault(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value;
    }

    private static String shortAlias(String prefix) {
        return prefix + "-" + Long.toString(System.currentTimeMillis()).substring(5);
    }

    private static String uniqueBusinessId(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }
}
