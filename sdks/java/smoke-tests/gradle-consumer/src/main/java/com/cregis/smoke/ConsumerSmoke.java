package com.cregis.smoke;

import com.cregis.sdk.client.CregisPaymentClient;
import com.cregis.sdk.client.CregisTeamClient;
import com.cregis.sdk.client.CregisWaasClient;

public final class ConsumerSmoke {

    private ConsumerSmoke() {
    }

    public static void main(String[] args) {
        CregisWaasClient waas = CregisWaasClient.builder()
                .endpoint("https://sandbox.example.com")
                .credentials(1L, "test-api-key")
                .build();
        CregisPaymentClient payment = CregisPaymentClient.builder()
                .endpoint("https://sandbox.example.com")
                .credentials(1L, "test-api-key")
                .build();
        CregisTeamClient team = CregisTeamClient.builder()
                .endpoint("https://sandbox.example.com")
                .credentials("test-access-key", "test-access-secret")
                .build();

        if (waas == null || payment == null || team == null) {
            throw new IllegalStateException("Cregis SDK clients were not initialized");
        }
    }
}
