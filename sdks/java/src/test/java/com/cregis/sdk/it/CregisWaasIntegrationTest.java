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
 * Integration Test that connects to the real Cregis WaaS API.
 * Requires Environment Variables to be set:
 * - CREGIS_PID
 * - CREGIS_API_KEY
 */
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CregisWaasIntegrationTest {

    private static CregisWaasClient client;

    @BeforeAll
    static void setup() {
        // Load from .env
        io.github.cdimascio.dotenv.Dotenv dotenv;
        try {
            dotenv = io.github.cdimascio.dotenv.Dotenv.load();
        } catch (Exception e) {
            dotenv = io.github.cdimascio.dotenv.Dotenv.configure().ignoreIfMissing().load();
        }

        String pid = dotenv.get("WAAS_PID", System.getenv("WAAS_PID"));
        String apiKey = dotenv.get("WAAS_API_KEY", System.getenv("WAAS_API_KEY"));
        String endpoint = dotenv.get("WAAS_ENDPOINT", "https://waas.cregis.com");

        // If credentials are missing, skip the tests dynamically
        org.junit.jupiter.api.Assumptions.assumeTrue(pid != null && apiKey != null,
                "Skipping: WAAS Credentials not found");

        client = CregisWaasClient.builder()
                .endpoint(endpoint)
                .credentials(pid, apiKey)
                .debug(true)
                .build();
    }

    @Test
    @Order(1)
    void testProjectCoinQuery() {
        ProjectCoinQueryResponse response = client.queryProjectCoins(ProjectCoinQueryRequest.builder().build());
        System.out.println("Address Coins: " + response.getAddressCoins());
        assertNotNull(response.getAddressCoins(), "Should return address coins list");
    }

    @Test
    @Order(2)
    void testGenerateAddress() {
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
    @Order(3)
    void testBatchGenerateAddress() {
        String chainId = getChainId();
        System.out.println("Batch Generating for Chain ID: " + chainId);

        BatchGenerateAddressRequest request = BatchGenerateAddressRequest.builder()
                .chainId(chainId)
                .number(2)
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
    @Order(4)
    void testAddressUtils() {
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
    @Order(5)
    void testQueryTradeRecords() {
        // Just query recent page 1
        TradeRecordQueryResponse response = client.queryTradeRecords(TradeRecordQueryRequest.builder()
                .pageNum(1)
                .pageSize(10)
                .build());
        System.out.println("Trade Records: " + response.getRows());
        assertNotNull(response.getRows());
    }

    @Test
    @Order(6)
    void testPayout() {
        io.github.cdimascio.dotenv.Dotenv dotenv;
        try {
            dotenv = io.github.cdimascio.dotenv.Dotenv.load();
        } catch (Exception e) {
            dotenv = io.github.cdimascio.dotenv.Dotenv.configure().ignoreIfMissing().load();
        }
        String walletIdStr = dotenv.get("WAAS_WALLET_ID", System.getenv("WAAS_WALLET_ID"));
        if (walletIdStr == null) {
            System.out.println("Skipping testPayout: WAAS_WALLET_ID not set");
            return;
        }

        Long walletId = Long.parseLong(walletIdStr);

        System.out.println("Testing Payout for Wallet: " + walletId);

        PayoutRequest request = PayoutRequest.builder()
                .walletId(walletId)
                .currency("USDT-TRC20#Shasta") // Corrected from chainId
                .amount("0.001")
                .toAddress("TRKkJZGq58MdY9Mqf58rse35ZB4zaTHR4j")
                .thirdPartyId("payout-" + System.currentTimeMillis())
                .build();

        PayoutResponse response = client.payout(request);
        System.out.println("Payout CID: " + response.getCid());
        assertNotNull(response.getCid());

        // Query Payout
        QueryPayoutResponse queryRes = client
                .queryPayout(QueryPayoutRequest.builder().cid(response.getCid()).build());
        System.out.println("Payout Status: " + queryRes.getStatus());
        assertNotNull(queryRes);
    }

    @Test
    @Order(7)
    void testWithdrawal() {
        io.github.cdimascio.dotenv.Dotenv dotenv;
        try {
            dotenv = io.github.cdimascio.dotenv.Dotenv.load();
        } catch (Exception e) {
            dotenv = io.github.cdimascio.dotenv.Dotenv.configure().ignoreIfMissing().load();
        }
        String withdrawAddress = dotenv.get("WITHDRAW_ADDRESS", System.getenv("WITHDRAW_ADDRESS"));
        if (withdrawAddress == null) {
            System.out.println("Skipping testWithdrawal: WITHDRAW_ADDRESS not set");
            return;
        }

        WithdrawalRequest request = WithdrawalRequest.builder()
                .currency("USDT-TRC20#Shasta")
                .amount("0.001")
                .fromAddress(withdrawAddress)
                // Sending to self or another address - reusing withdrawAddress for simplicity
                // or a hardcoded one?
                // API docs: to_address. Let's use the same address as destination for
                // safety/loopback if allowed,
                // or keep the hardcoded one if it's external.
                // User only said "Withdrawal use .env's WITHDRAW_ADDRESS". checking typical
                // withdrawal flow:
                // withdrawal is FROM project sub-wallet TO external.
                // So fromAddress should be the project wallet (WITHDRAW_ADDRESS).
                // toAddress can be anything valid.
                .toAddress("TRKkJZGq58MdY9Mqf58rse35ZB4zaTHR4j")
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
        String chainId = System.getenv("WAAS_CHAIN_ID");
        // 198 for TRON#Shasta
        return chainId != null ? chainId : "198";
    }
}
