package com.cregis.sdk.it;

import com.cregis.sdk.client.CregisTeamClient;
import com.cregis.sdk.domain.team.ListTeamWalletAddressesRequest;
import com.cregis.sdk.domain.team.ListTeamWalletsRequest;
import com.cregis.sdk.domain.team.QueryTeamWalletAddressBalanceRequest;
import com.cregis.sdk.domain.team.QueryTeamWalletBalanceRequest;
import com.cregis.sdk.domain.team.QueryTeamWalletHistoryTransactionsRequest;
import com.cregis.sdk.domain.team.QueryTeamWalletProcessingTransactionsRequest;
import com.cregis.sdk.domain.team.TeamPagedResponse;
import com.cregis.sdk.domain.team.TeamWallet;
import com.cregis.sdk.domain.team.TeamWalletAddress;
import com.cregis.sdk.domain.team.TeamWalletAddressBalance;
import com.cregis.sdk.domain.team.TeamWalletBalance;
import com.cregis.sdk.domain.team.TeamWalletProcessingTransaction;
import com.cregis.sdk.domain.team.TeamWalletToken;
import com.cregis.sdk.domain.team.TeamWalletTransaction;
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

        client = CregisTeamClient.builder()
                .endpoint(IntegrationTestEnvironment.get("TEAM_ENDPOINT"))
                .credentials(
                        IntegrationTestEnvironment.get("TEAM_ACCESS_KEY"),
                        IntegrationTestEnvironment.get("TEAM_ACCESS_SECRET"))
                .build();
    }

    @Test
    @Order(1)
    void listsTeamWallets() {
        TeamPagedResponse<TeamWallet> response = client.listTeamWallets(
                ListTeamWalletsRequest.builder().pageNum(1).pageSize(10).build());

        assertNotNull(response);
        assertNotNull(response.getRows());
        assertFalse(response.getRows().isEmpty(), "Team needs at least one wallet for dependent queries");
        TeamWallet wallet = response.getRows().get(0);
        walletId = wallet.getWalletId();
        assertNotNull(walletId);

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
        TeamPagedResponse<TeamWalletAddress> response = client.listTeamWalletAddresses(
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
        TeamPagedResponse<TeamWalletBalance> response = client.queryTeamWalletBalance(
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
        TeamPagedResponse<TeamWalletAddressBalance> response = client.queryTeamWalletAddressBalance(
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
        TeamPagedResponse<TeamWalletTransaction> response = client.queryTeamWalletHistoryTransactions(
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
        TeamPagedResponse<TeamWalletProcessingTransaction> response = client.queryTeamWalletProcessingTransactions(
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
