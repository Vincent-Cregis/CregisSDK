package com.cregis.sdk.client;

import com.cregis.sdk.core.signer.CregisTeamSigner;
import com.cregis.sdk.domain.team.ListTeamWalletAddressesRequest;
import com.cregis.sdk.domain.team.ListTeamWalletsRequest;
import com.cregis.sdk.domain.team.QueryTeamWalletAddressBalanceRequest;
import com.cregis.sdk.domain.team.QueryTeamWalletBalanceRequest;
import com.cregis.sdk.domain.team.QueryTeamWalletHistoryTransactionsRequest;
import com.cregis.sdk.domain.team.QueryTeamWalletProcessingTransactionsRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TeamClientContractTest {

    private static final String ACCESS_KEY = "test-access-key";
    private static final String ACCESS_SECRET = "test-access-secret";

    private MockWebServer server;
    private CregisTeamClient client;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        client = CregisTeamClient.builder()
                .endpoint(server.url("/").toString())
                .credentials(ACCESS_KEY, ACCESS_SECRET)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void teamSignerMatchesIndependentHmacVector() {
        String body = CregisTeamSigner.canonicalizeBody("{\"name\":\"Demo Team\"}");
        String signature = CregisTeamSigner.sign(
                "/openapi/team/profile",
                1717380000000L,
                "9f7c6a2b47e34f19",
                body,
                "team-secret");

        assertEquals("cef9805fa4ee7bf9376b140153c5e3f80e74e4f950eb6f92ef780fa42aa44289", signature);
    }

    @Test
    void canonicalizesTeamRequestBodyUsingRfc8785() {
        assertEquals("{\"a\":1,\"b\":2}", CregisTeamSigner.canonicalizeBody("{ \"b\": 2, \"a\": 1 }"));
    }

    @Test
    void allTeamMethodsMatchOpenApiPathsAndHeaders() throws Exception {
        enqueuePage();
        client.listTeamWallets(ListTeamWalletsRequest.builder()
                .walletType("single_sign")
                .pageSize(10)
                .pageNum(1)
                .build());
        RecordedRequest wallets = assertNextPath("/openapi/v1/wallets");
        assertTeamSignature(wallets);
        assertEquals("{\"page_num\":1,\"page_size\":10,\"wallet_type\":\"single_sign\"}",
                wallets.getBody().readUtf8());

        enqueuePage();
        client.listTeamWalletAddresses(ListTeamWalletAddressesRequest.builder()
                .walletId(1L)
                .chainId("195")
                .build());
        assertNextPath("/openapi/v1/wallet_address");

        enqueuePage();
        client.queryTeamWalletBalance(QueryTeamWalletBalanceRequest.builder().walletId(1L).build());
        assertNextPath("/openapi/v1/wallet_balance");

        enqueuePage();
        client.queryTeamWalletAddressBalance(QueryTeamWalletAddressBalanceRequest.builder().walletId(1L).build());
        assertNextPath("/openapi/v1/wallet_address_balance");

        enqueuePage();
        client.queryTeamWalletHistoryTransactions(QueryTeamWalletHistoryTransactionsRequest.builder()
                .walletId(1L)
                .build());
        assertNextPath("/openapi/v1/wallet_history_transaction_info");

        enqueuePage();
        client.queryTeamWalletProcessingTransactions(QueryTeamWalletProcessingTransactionsRequest.builder()
                .walletId(1L)
                .build());
        assertNextPath("/openapi/v1/wallet_processing_transaction_info");
    }

    private void assertTeamSignature(RecordedRequest request) {
        String timestamp = request.getHeader("Access-Timestamp");
        String nonce = request.getHeader("Access-Nonce");
        String signature = request.getHeader("Access-Signature");

        assertEquals(ACCESS_KEY, request.getHeader("Access-Key"));
        assertNotNull(timestamp);
        assertNotNull(nonce);
        assertNotNull(signature);

        String body = request.getBody().clone().readUtf8();
        String expected = CregisTeamSigner.sign(
                request.getRequestUrl().encodedPath(),
                Long.parseLong(timestamp),
                nonce,
                body,
                ACCESS_SECRET);
        assertEquals(expected, signature);
    }

    private void enqueuePage() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"code\":\"00000\",\"msg\":\"ok\",\"data\":"
                        + "{\"total\":0,\"page_num\":1,\"page_size\":10,\"rows\":[]}}"));
    }

    private RecordedRequest assertNextPath(String expectedPath) throws Exception {
        RecordedRequest recorded = server.takeRequest();
        assertEquals(expectedPath, recorded.getPath());
        return recorded;
    }
}
