package com.cregis.sdk.it;

import com.cregis.sdk.client.CregisWaasClient;
import com.cregis.sdk.domain.waas.ProjectCoinQueryRequest;
import com.cregis.sdk.domain.waas.ProjectCoinQueryResponse;
import com.cregis.sdk.domain.waas.TradeRecordQueryRequest;
import com.cregis.sdk.domain.waas.TradeRecordQueryResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Read-only Sandbox checks for WaaS. These tests never create addresses or transactions.
 */
@Tag("integration")
class CregisWaasReadOnlyIntegrationTest {

    private static CregisWaasClient client;

    @BeforeAll
    static void setUp() {
        assumeTrue(
                IntegrationTestEnvironment.allPresent("WAAS_PID", "WAAS_API_KEY", "WAAS_ENDPOINT"),
                "Skipping: WaaS Sandbox credentials or project Base URL not found");

        client = CregisWaasClient.builder()
                .endpoint(IntegrationTestEnvironment.get("WAAS_ENDPOINT"))
                .credentials(
                        IntegrationTestEnvironment.get("WAAS_PID"),
                        IntegrationTestEnvironment.get("WAAS_API_KEY"))
                .build();
    }

    @Test
    void queriesProjectCoins() {
        ProjectCoinQueryResponse response = client.queryProjectCoins(
                ProjectCoinQueryRequest.builder().build());

        assertNotNull(response);
        assertNotNull(response.getAddressCoins(), "WaaS should return the address coin list");
    }

    @Test
    void queriesTradeRecords() {
        TradeRecordQueryResponse response = client.queryTradeRecords(
                TradeRecordQueryRequest.builder()
                        .pageNum(1)
                        .pageSize(10)
                        .build());

        assertNotNull(response);
        assertNotNull(response.getRows(), "WaaS should return the trade record list");
    }
}
