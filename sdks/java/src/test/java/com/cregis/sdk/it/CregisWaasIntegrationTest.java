package com.cregis.sdk.it;

import com.cregis.sdk.client.CregisWaasClient;
import com.cregis.sdk.domain.waas.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * State-changing Sandbox tests for the Cregis WaaS API.
 */
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CregisWaasIntegrationTest {

    private static CregisWaasClient client;
    private static boolean mutatingTestsEnabled;

    @BeforeAll
    static void setup() {
        String pid = IntegrationTestEnvironment.get("WAAS_PID");
        String apiKey = IntegrationTestEnvironment.get("WAAS_API_KEY");
        String endpoint = IntegrationTestEnvironment.get("WAAS_ENDPOINT");
        mutatingTestsEnabled = IntegrationTestEnvironment.isTrue("CREGIS_ALLOW_MUTATING_TESTS");

        // If credentials are missing, skip the tests dynamically
        org.junit.jupiter.api.Assumptions.assumeTrue(pid != null && apiKey != null && endpoint != null,
                "Skipping: WAAS Sandbox credentials or project Base URL not found");

        client = CregisWaasClient.builder()
                .endpoint(endpoint)
                .credentials(pid, apiKey)
                .debug(true)
                .build();
    }

    @Test
    @Order(1)
    void testGenerateAddress() {
        requireMutatingTestsEnabled();
        String chainId = getChainId();
        System.out.println("Using Chain ID: " + chainId);

        // Warning: This creates a real address in your project!
        GenerateAddressRequest request = GenerateAddressRequest.builder()
                .chainId(chainId)
                .alias("it-test-" + System.currentTimeMillis())
                .build();

        try {
            GenerateAddressResponse response = client.generateAddress(request);
            System.out.println("Generated Address: " + response.getAddress());
            assertNotNull(response.getAddress(), "Should return a valid address");
        } catch (com.cregis.sdk.core.exception.CregisServerException e) {
            if ("E0033".equals(e.getCode())) {
                System.out.println("Skipping testGenerateAddress: Quota exceeded (E0033)");
            } else {
                throw e;
            }
        }
    }

    @Test
    @Order(2)
    void testBatchGenerateAddress() {
        requireMutatingTestsEnabled();
        String chainId = getChainId();
        System.out.println("Batch Generating for Chain ID: " + chainId);

        BatchGenerateAddressRequest request = BatchGenerateAddressRequest.builder()
                .chainId(chainId)
                .number("2")
                .alias("batch-test-" + System.currentTimeMillis())
                .build();

        try {
            java.util.List<BatchGenerateAddressResponse.GeneratedAddress> addresses = client
                    .batchGenerateAddress(request);
            System.out.println("Batch Generated: " + addresses);
            assertNotNull(addresses, "Should return list");
            assertEquals(2, addresses.size(), "Should return 2 addresses");
        } catch (com.cregis.sdk.core.exception.CregisServerException e) {
            if ("E0033".equals(e.getCode())) {
                System.out.println("Skipping testBatchGenerateAddress: Quota exceeded (E0033)");
            } else {
                throw e;
            }
        }
    }

    @Test
    @Order(3)
    void testAddressUtils() {
        requireMutatingTestsEnabled();
        String chainId = getChainId();
        // 1. Generate one to reuse
        String address = null;
        try {
            GenerateAddressResponse genRes = client.generateAddress(GenerateAddressRequest.builder()
                    .chainId(chainId)
                    .alias("util-test")
                    .build());
            address = genRes.getAddress();
            assertNotNull(address);
        } catch (com.cregis.sdk.core.exception.CregisServerException e) {
            if ("E0033".equals(e.getCode())) {
                System.out.println("Skipping testAddressUtils: Quota exceeded (E0033)");
                return;
            }
            throw e;
        }

        // 2. Validate (Inner check)
        ValidateAddressResponse valRes = client.validateAddress(ValidateAddressRequest.builder()
                .address(address)
                .chainId(chainId)
                .build());
        System.out.println("Validation Result: " + valRes);
        // assertNotNull(valRes);

        // 3. Check Legality (Format check)
        CheckAddressLegalityResponse legRes = client.checkAddressLegality(CheckAddressLegalityRequest.builder()
                .address(address)
                .chainId(chainId)
                .build());
        System.out.println("Legality Result: " + legRes.getResult());
        // assertTrue(legRes.getIsLegal());
    }

    @Test
    @Order(4)
    void testPayout() {
        requireMutatingTestsEnabled();
        String walletIdStr = IntegrationTestEnvironment.get("WAAS_WALLET_ID");
        String payoutToAddress = IntegrationTestEnvironment.get("WAAS_PAYOUT_TO_ADDRESS");
        if (walletIdStr == null || payoutToAddress == null) {
            System.out.println("Skipping testPayout: WAAS_WALLET_ID or WAAS_PAYOUT_TO_ADDRESS not set");
            return;
        }

        Long walletId = Long.parseLong(walletIdStr);

        System.out.println("Testing Payout for Wallet: " + walletId);

        PayoutRequest request = PayoutRequest.builder()
                .walletId(walletId)
                .currency("USDT-TRC20#Shasta") // Corrected from chainId
                .amount("0.001")
                .toAddress(payoutToAddress)
                .thirdPartyId("payout-" + System.currentTimeMillis())
                .build();

        PayoutResponse response = client.payoutV2(request);
        System.out.println("Payout CID: " + response.getCid());
        assertNotNull(response.getCid());

        // Query Payout
        QueryPayoutResponse queryRes = client
                .queryPayout(QueryPayoutRequest.builder().cid(response.getCid()).build());
        System.out.println("Payout Status: " + queryRes.getStatus());
        assertNotNull(queryRes);
    }

    @Test
    @Order(5)
    void testWithdrawal() {
        requireMutatingTestsEnabled();
        String withdrawAddress = IntegrationTestEnvironment.get("WITHDRAW_ADDRESS");
        String withdrawToAddress = IntegrationTestEnvironment.get("WITHDRAW_TO_ADDRESS");
        if (withdrawAddress == null || withdrawToAddress == null) {
            System.out.println("Skipping testWithdrawal: WITHDRAW_ADDRESS or WITHDRAW_TO_ADDRESS not set");
            return;
        }

        WithdrawalRequest request = WithdrawalRequest.builder()
                .currency("USDT-TRC20#Shasta")
                .amount("0.001")
                .fromAddress(withdrawAddress)
                .toAddress(withdrawToAddress)
                .thirdPartyId("withdraw-" + System.currentTimeMillis())
                .build();

        WithdrawalResponse response = client.withdrawal(request);
        System.out.println("Withdrawal CID: " + response.getCid());

        QueryWithdrawalResponse queryRes = client
                .queryWithdrawal(QueryWithdrawalRequest.builder().cid(response.getCid()).build());
        System.out.println("Withdrawal Status: " + queryRes.getStatus());
    }

    // Helper to get cached or default Chain ID
    private String getChainId() {
        String chainId = IntegrationTestEnvironment.get("WAAS_CHAIN_ID");
        // 198 for TRON#Shasta
        return chainId != null ? chainId : "198";
    }

    private void requireMutatingTestsEnabled() {
        org.junit.jupiter.api.Assumptions.assumeTrue(
                mutatingTestsEnabled,
                "Skipping state-changing test: CREGIS_ALLOW_MUTATING_TESTS is not true");
    }
}
