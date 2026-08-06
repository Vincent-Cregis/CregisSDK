package com.cregis.sdk.it;

import com.cregis.sdk.client.CregisTeamClient;
import com.cregis.sdk.domain.team.ListTeamWalletsRequest;
import com.cregis.sdk.domain.team.TeamPagedResponse;
import com.cregis.sdk.domain.team.TeamWallet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Read-only Sandbox check for Team API.
 */
@Tag("integration")
class CregisTeamReadOnlyIntegrationTest {

    private static CregisTeamClient client;

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
    void listsTeamWallets() {
        TeamPagedResponse<TeamWallet> response = client.listTeamWallets(
                ListTeamWalletsRequest.builder()
                        .pageNum(1)
                        .pageSize(10)
                        .build());

        assertNotNull(response);
        assertNotNull(response.getRows(), "Team API should return the wallet list");
    }
}
