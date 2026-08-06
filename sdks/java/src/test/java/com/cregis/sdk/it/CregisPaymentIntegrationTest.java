package com.cregis.sdk.it;

import com.cregis.sdk.client.CregisPaymentClient;
import com.cregis.sdk.domain.payment.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * State-changing Sandbox tests for Cregis Payment Engine.
 */
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CregisPaymentIntegrationTest {

    private static CregisPaymentClient client;

    @BeforeAll
    static void setup() {
        String pid = IntegrationTestEnvironment.get("PAYMENT_PID");
        String apiKey = IntegrationTestEnvironment.get("PAYMENT_API_KEY");
        String endpoint = IntegrationTestEnvironment.get("PAYMENT_ENDPOINT");
        boolean mutatingTestsEnabled = IntegrationTestEnvironment.isTrue("CREGIS_ALLOW_MUTATING_TESTS");

        // If credentials are missing, skip the tests dynamically
        org.junit.jupiter.api.Assumptions.assumeTrue(pid != null && apiKey != null && endpoint != null,
                "Skipping: Payment Sandbox credentials or project Base URL not found");
        org.junit.jupiter.api.Assumptions.assumeTrue(mutatingTestsEnabled,
                "Skipping state-changing Payment tests: CREGIS_ALLOW_MUTATING_TESTS is not true");

        client = CregisPaymentClient.builder()
                .endpoint(endpoint)
                .credentials(pid, apiKey)
                .debug(true)
                .build();
    }

    @Test
    @Order(1)
    void testCreateOrder() {
        String orderId = "test-order-" + System.currentTimeMillis();
        CreateOrderRequest request = CreateOrderRequest.builder()
                .orderId(orderId)
                .orderAmount("1.0")
                .orderCurrency("USDT")
                .payerId("test-payer-001")
                .payerName("Test Payer")
                .callbackUrl("https://webhook.site/test")
                .successUrl("https://example.com/success")
                .cancelUrl("https://example.com/cancel")
                .remark("Integration Test Order")
                .validTime(60)
                .language("sc")
                .underpaidTolerance(0.1f)
                .overpaidTolerance(0.1f)
                .build();

        CreateOrderResponse response = client.createOrder(request);

        System.out.println("Created Order Cregis ID: " + response.getCregisId());
        System.out.println("Checkout URL: " + response.getCheckoutUrl());

        assertNotNull(response.getCregisId(), "Cregis ID should not be null");
        assertNotNull(response.getCheckoutUrl(), "Checkout URL should not be null");
    }

    @Test
    @Order(2)
    void testQueryOrder() {
        // First create an order to query
        String orderId = "test-query-" + System.currentTimeMillis();
        CreateOrderRequest createRequest = CreateOrderRequest.builder()
                .orderId(orderId)
                .orderAmount("1.0")
                .orderCurrency("USDT")
                .payerId("test-payer-002")
                .payerName("Test Payer")
                .callbackUrl("https://webhook.site/test")
                .successUrl("https://example.com/success")
                .cancelUrl("https://example.com/cancel")
                .validTime(60)
                .build();

        CreateOrderResponse createResponse = client.createOrder(createRequest);
        String cregisId = createResponse.getCregisId();

        // Query the order
        QueryOrderRequest queryRequest = QueryOrderRequest.builder()
                .cregisId(cregisId)
                .build();

        QueryOrderResponse queryResponse = client.queryOrder(queryRequest);

        System.out.println("Queried Order Status: " + queryResponse.getStatus());

        assertNotNull(queryResponse, "Query response should not be null");
        assertEquals(cregisId, queryResponse.getCregisId(), "Cregis ID should match");
    }
}
