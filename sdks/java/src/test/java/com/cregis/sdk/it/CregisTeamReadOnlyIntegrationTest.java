package com.cregis.sdk.it;

import com.cregis.sdk.client.CregisTeamClient;
import com.cregis.sdk.contract.SandboxContractProbe;
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
import com.cregis.sdk.generated.team.model.TeamWallet;
import com.cregis.sdk.generated.team.model.TeamWalletAddress;
import com.cregis.sdk.generated.team.model.TeamWalletAddressBalance;
import com.cregis.sdk.generated.team.model.TeamWalletBalance;
import com.cregis.sdk.generated.team.model.TeamWalletProcessingTransaction;
import com.cregis.sdk.generated.team.model.TeamWalletToken;
import com.cregis.sdk.generated.team.model.TeamWalletTransaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Read-only Sandbox coverage for every Team API OpenAPI operation.
 */
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CregisTeamReadOnlyIntegrationTest {

    private static CregisTeamClient client;
    private static SandboxContractProbe contractProbe;
    private static Long walletId;
    private static String chainId;
    private static String tokenId;
    private static String walletAddress;

    @BeforeAll
    static void setUp() {
        assumeTrue(
                IntegrationTestEnvironment.allPresent(
                        "TEAM_ACCESS_KEY", "TEAM_ACCESS_SECRET", "TEAM_ENDPOINT"),
                "Skipping: Team Sandbox credentials or Base URL not found");

        contractProbe = SandboxContractProbe.forApi("team");
        client = CregisTeamClient.builder()
                .endpoint(IntegrationTestEnvironment.get("TEAM_ENDPOINT"))
                .credentials(
                        IntegrationTestEnvironment.get("TEAM_ACCESS_KEY"),
                        IntegrationTestEnvironment.get("TEAM_ACCESS_SECRET"))
                .httpConfig(contractProbe.httpConfig())
                .build();
    }

    @AfterAll
    static void verifyAllTeamContractsWereCovered() {
        if (contractProbe != null) {
            contractProbe.assertAllCallableOperationsCovered();
        }
    }

    @Test
    @Order(1)
    void listsTeamWallets() {
        ListTeamWalletsResponse response = client.listTeamWallets(
                ListTeamWalletsRequest.builder().pageNum(1).pageSize(10).build());

        assertNotNull(response);
        assertNotNull(response.getPageNum());
        assertNotNull(response.getPageSize());
        assertNotNull(response.getRows());
        assertFalse(response.getRows().isEmpty(), "Team needs at least one wallet for dependent queries");
        TeamWallet wallet = response.getRows().get(0);
        walletId = wallet.getWalletId();
        assertNotNull(walletId);
        assertNotNull(wallet.getWalletType());

        if (wallet.getTokens() != null && !wallet.getTokens().isEmpty()) {
            TeamWalletToken token = wallet.getTokens().get(0);
            chainId = token.getChainId();
            tokenId = token.getTokenId();
        }
    }

    @Test
    @Order(2)
    void listsTeamWalletAddresses() {
        requireWalletAndChain();
        ListTeamWalletAddressesResponse response = client.listTeamWalletAddresses(
                ListTeamWalletAddressesRequest.builder()
                        .walletId(walletId)
                        .chainId(chainId)
                        .pageNum(1)
                        .pageSize(10)
                        .build());

        assertNotNull(response);
        assertNotNull(response.getRows());
        if (!response.getRows().isEmpty()) {
            walletAddress = response.getRows().get(0).getAddress();
        }
    }

    @Test
    @Order(3)
    void queriesTeamWalletBalance() {
        requireWallet();
        QueryTeamWalletBalanceResponse response = client.queryTeamWalletBalance(
                QueryTeamWalletBalanceRequest.builder()
                        .walletId(walletId)
                        .chainId(chainId)
                        .tokenId(tokenId)
                        .pageNum(1)
                        .pageSize(10)
                        .build());

        assertNotNull(response);
        assertNotNull(response.getRows());
    }

    @Test
    @Order(4)
    void queriesTeamWalletAddressBalance() {
        requireWallet();
        QueryTeamWalletAddressBalanceResponse response = client.queryTeamWalletAddressBalance(
                QueryTeamWalletAddressBalanceRequest.builder()
                        .walletId(walletId)
                        .address(walletAddress)
                        .chainId(chainId)
                        .tokenId(tokenId)
                        .pageNum(1)
                        .pageSize(10)
                        .build());

        assertNotNull(response);
        assertNotNull(response.getRows());
    }

    @Test
    @Order(5)
    void queriesTeamWalletHistoryTransactions() {
        requireWallet();
        QueryTeamWalletHistoryTransactionsResponse response = client.queryTeamWalletHistoryTransactions(
                QueryTeamWalletHistoryTransactionsRequest.builder()
                        .walletId(walletId)
                        .chainId(chainId)
                        .tokenId(tokenId)
                        .pageNum(1)
                        .pageSize(10)
                        .build());

        assertNotNull(response);
        assertNotNull(response.getRows());
    }

    @Test
    @Order(6)
    void queriesTeamWalletProcessingTransactions() {
        requireWallet();
        QueryTeamWalletProcessingTransactionsResponse response = client.queryTeamWalletProcessingTransactions(
                QueryTeamWalletProcessingTransactionsRequest.builder()
                        .walletId(walletId)
                        .chainId(chainId)
                        .tokenId(tokenId)
                        .pageNum(1)
                        .pageSize(10)
                        .build());

        assertNotNull(response);
        assertNotNull(response.getRows());
    }

    private static void requireWallet() {
        assumeTrue(walletId != null, "Dependent Team query requires a wallet returned by /wallets");
    }

    private static void requireWalletAndChain() {
        requireWallet();
        assumeTrue(chainId != null, "Wallet address query requires token chain metadata from /wallets");
    }
}
