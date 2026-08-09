package com.cregis.sdk.client;

import com.cregis.sdk.domain.payment.CreateOrderRequest;
import com.cregis.sdk.domain.payment.QueryOrderRequest;
import com.cregis.sdk.core.exception.CregisHttpException;
import com.cregis.sdk.core.exception.CregisServerException;
import com.cregis.sdk.core.exception.CregisClientException;
import com.cregis.sdk.domain.waas.AddressBalanceRequest;
import com.cregis.sdk.domain.waas.AddressBalanceV2Request;
import com.cregis.sdk.domain.waas.AddressUpdateRequest;
import com.cregis.sdk.domain.waas.BalanceCollectRequest;
import com.cregis.sdk.domain.waas.BatchGenerateAddressRequest;
import com.cregis.sdk.domain.waas.CheckAddressLegalityRequest;
import com.cregis.sdk.domain.waas.GenerateAddressRequest;
import com.cregis.sdk.domain.waas.PayoutRequest;
import com.cregis.sdk.domain.waas.PayoutV1Request;
import com.cregis.sdk.domain.waas.ProjectCoinQueryRequest;
import com.cregis.sdk.domain.waas.QueryPayoutRequest;
import com.cregis.sdk.domain.waas.QueryWithdrawalRequest;
import com.cregis.sdk.domain.waas.TradeRecordQueryRequest;
import com.cregis.sdk.domain.waas.ValidateAddressRequest;
import com.cregis.sdk.domain.waas.WithdrawalRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectClientContractTest {

    private static final long PID = 1382528827416576L;
    private static final String API_KEY = "test-api-key";

    private MockWebServer server;
    private CregisWaasClient waasClient;
    private CregisPaymentClient paymentClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        String endpoint = server.url("/").toString();
        waasClient = CregisWaasClient.builder()
                .endpoint(endpoint)
                .credentials(PID, API_KEY)
                .build();
        paymentClient = CregisPaymentClient.builder()
                .endpoint(endpoint)
                .credentials(PID, API_KEY)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void paymentMethodsMatchOpenApiPaths() throws Exception {
        enqueueData("{}");
        paymentClient.createOrder(CreateOrderRequest.builder()
                .orderId("order-1")
                .orderAmount("1")
                .orderCurrency("USD")
                .payerId("payer-1")
                .successUrl("https://merchant.example/success")
                .cancelUrl("https://merchant.example/cancel")
                .build());
        assertNextPath("/api/v2/checkout");

        enqueueData("{}");
        paymentClient.queryOrder(QueryOrderRequest.builder().cregisId("po-1").build());
        assertNextPath("/api/v2/order/info");
    }

    @Test
    void allWaasMethodsMatchOpenApiPaths() throws Exception {
        enqueueData("{\"address\":\"T-test\"}");
        waasClient.generateAddress(GenerateAddressRequest.builder().chainId("195").build());
        RecordedRequest first = assertNextPath("/api/v1/address/create");
        JsonNode firstBody = objectMapper.readTree(first.getBody().readUtf8());
        assertTrue(firstBody.get("pid").isIntegralNumber(), "OpenAPI defines pid as int64");

        enqueueData("[]");
        waasClient.batchGenerateAddress(BatchGenerateAddressRequest.builder()
                .chainId("195")
                .number("2")
                .build());
        assertNextPath("/api/v1/batch/address/create");

        enqueueData("null");
        waasClient.updateAddress(AddressUpdateRequest.builder().address("T-test").alias("updated").build());
        assertNextPath("/api/v1/address/update");

        enqueueData("{\"result\":true}");
        waasClient.validateAddress(ValidateAddressRequest.builder().chainId("195").address("T-test").build());
        assertNextPath("/api/v1/address/inner");

        enqueueData("{\"result\":true}");
        waasClient.checkAddressLegality(CheckAddressLegalityRequest.builder()
                .chainId("195")
                .address("T-test")
                .build());
        assertNextPath("/api/v1/address/legal");

        enqueueData("{\"cid\":1}");
        waasClient.payoutV1(PayoutV1Request.builder()
                .currency("195@195")
                .address("T-to")
                .amount("1")
                .thirdPartyId("payout-v1-1")
                .build());
        assertNextPath("/api/v1/payout");

        enqueueData("{\"cid\":1}");
        waasClient.payoutV2(PayoutRequest.builder()
                .currency("195@195")
                .toAddress("T-to")
                .amount("1")
                .thirdPartyId("payout-v2-1")
                .build());
        assertNextPath("/api/v2/payout");

        enqueueData("{\"cid\":1}");
        waasClient.withdrawal(WithdrawalRequest.builder()
                .currency("195@195")
                .fromAddress("T-from")
                .toAddress("T-to")
                .amount("1")
                .thirdPartyId("withdrawal-1")
                .build());
        assertNextPath("/api/v1/sub_address_withdrawal");

        enqueueData("{\"cid\":1}");
        waasClient.balanceCollect(BalanceCollectRequest.builder()
                .currency("195@195")
                .fromAddress("T-from")
                .toAddress("T-to")
                .build());
        assertNextPath("/api/v1/collection");

        enqueueData("{\"payout_coins\":[],\"address_coins\":[]}");
        waasClient.queryProjectCoins(ProjectCoinQueryRequest.builder().build());
        assertNextPath("/api/v1/coins");

        enqueueData("{\"total\":0,\"page_num\":1,\"page_size\":10,\"rows\":[]}");
        waasClient.queryTradeRecords(TradeRecordQueryRequest.builder().build());
        assertNextPath("/api/v1/trade/page");

        enqueueData("{}");
        waasClient.queryPayout(QueryPayoutRequest.builder().cid(1L).build());
        assertNextPath("/api/v1/payout/query");

        enqueueData("{}");
        waasClient.queryWithdrawal(QueryWithdrawalRequest.builder().cid(1L).build());
        assertNextPath("/api/v1/sub_address_withdrawal/info");

        enqueueData("{\"total\":0,\"page_num\":1,\"page_size\":10,\"rows\":[]}");
        waasClient.queryAddressBalance(AddressBalanceRequest.builder().currency("195@195").build());
        assertNextPath("/api/v1/sub_address_balance");

        enqueueData("{\"total\":0,\"page_num\":1,\"page_size\":10,\"rows\":[]}");
        waasClient.queryAddressBalanceV2(AddressBalanceV2Request.builder().address("T-test").build());
        assertNextPath("/api/v2/sub_address_balance");
    }

    @Test
    void preservesHttpAndBusinessErrorDetails() {
        server.enqueue(new MockResponse()
                .setResponseCode(429)
                .setBody("rate limited"));
        CregisHttpException httpError = assertThrows(
                CregisHttpException.class,
                () -> waasClient.queryProjectCoins(ProjectCoinQueryRequest.builder().build()));
        assertEquals(429, httpError.getStatusCode());
        assertEquals("rate limited", httpError.getResponseBody());

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"code\":\"B0001\",\"msg\":\"Signature Error\",\"data\":null}"));
        CregisServerException businessError = assertThrows(
                CregisServerException.class,
                () -> waasClient.queryProjectCoins(ProjectCoinQueryRequest.builder().build()));
        assertEquals("B0001", businessError.getCode());
        assertEquals("Signature Error", businessError.getMsg());
    }

    @Test
    void reportsMalformedSuccessResponsesAsParsingErrors() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("not-json"));

        CregisClientException error = assertThrows(
                CregisClientException.class,
                () -> waasClient.queryProjectCoins(ProjectCoinQueryRequest.builder().build()));

        assertTrue(error.getMessage().startsWith("Failed to parse Cregis response for POST /api/v1/coins"));
    }

    @Test
    void rejectsMissingOrNullResponseEnvelopesAsClientErrors() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("null"));

        CregisClientException nullEnvelope = assertThrows(
                CregisClientException.class,
                () -> waasClient.queryProjectCoins(ProjectCoinQueryRequest.builder().build()));
        assertEquals("Cregis response must be a JSON object", nullEnvelope.getMessage());

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"msg\":\"ok\",\"data\":{}}"));

        CregisClientException missingCode = assertThrows(
                CregisClientException.class,
                () -> waasClient.queryProjectCoins(ProjectCoinQueryRequest.builder().build()));
        assertEquals("Cregis response is missing required field: code", missingCode.getMessage());
    }

    @Test
    void doesNotRetryAnAmbiguousPostFailureByDefault() {
        server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
        enqueueData("{\"payout_coins\":[],\"address_coins\":[]}");

        assertThrows(
                CregisClientException.class,
                () -> waasClient.queryProjectCoins(ProjectCoinQueryRequest.builder().build()));

        assertEquals(1, server.getRequestCount());
    }

    @Test
    void doesNotFollowSignedRequestRedirects() throws Exception {
        try (MockWebServer redirectTarget = new MockWebServer()) {
            redirectTarget.start();
            server.enqueue(new MockResponse()
                    .setResponseCode(307)
                    .setHeader("Location", redirectTarget.url("/capture")));

            CregisHttpException error = assertThrows(
                    CregisHttpException.class,
                    () -> waasClient.queryProjectCoins(ProjectCoinQueryRequest.builder().build()));

            assertEquals(307, error.getStatusCode());
            assertEquals(0, redirectTarget.getRequestCount());
        }
    }

    private void enqueueData(String dataJson) {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"code\":\"00000\",\"msg\":\"ok\",\"data\":" + dataJson + "}"));
    }

    private RecordedRequest assertNextPath(String expectedPath) throws Exception {
        RecordedRequest recorded = server.takeRequest();
        assertEquals(expectedPath, recorded.getPath());
        return recorded;
    }
}
