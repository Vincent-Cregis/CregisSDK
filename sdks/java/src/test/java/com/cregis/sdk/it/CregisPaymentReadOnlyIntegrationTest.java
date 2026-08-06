package com.cregis.sdk.it;

import com.cregis.sdk.client.CregisPaymentClient;
import com.cregis.sdk.domain.payment.QueryOrderRequest;
import com.cregis.sdk.domain.payment.QueryOrderResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Read-only Sandbox check for Payment Engine using an existing order.
 */
@Tag("integration")
class CregisPaymentReadOnlyIntegrationTest {

    private static CregisPaymentClient client;
    private static String cregisId;

    @BeforeAll
    static void setUp() {
        assumeTrue(
                IntegrationTestEnvironment.allPresent(
                        "PAYMENT_PID", "PAYMENT_API_KEY", "PAYMENT_ENDPOINT", "PAYMENT_CREGIS_ID"),
                "Skipping: Payment Sandbox credentials, project Base URL, or existing Cregis ID not found");

        cregisId = IntegrationTestEnvironment.get("PAYMENT_CREGIS_ID");
        client = CregisPaymentClient.builder()
                .endpoint(IntegrationTestEnvironment.get("PAYMENT_ENDPOINT"))
                .credentials(
                        IntegrationTestEnvironment.get("PAYMENT_PID"),
                        IntegrationTestEnvironment.get("PAYMENT_API_KEY"))
                .build();
    }

    @Test
    void queriesExistingOrder() {
        QueryOrderResponse response = client.queryOrder(
                QueryOrderRequest.builder().cregisId(cregisId).build());

        assertNotNull(response);
        assertEquals(cregisId, response.getCregisId(), "Payment Engine should return the requested order");
    }
}
