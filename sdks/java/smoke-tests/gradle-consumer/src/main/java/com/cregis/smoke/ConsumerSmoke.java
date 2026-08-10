package com.cregis.smoke;

import com.cregis.sdk.client.CregisPaymentClient;
import com.cregis.sdk.client.CregisTeamClient;
import com.cregis.sdk.client.CregisWaasClient;
import com.cregis.sdk.generated.payment.model.CreateOrderRequest;
import com.cregis.sdk.generated.team.model.ListTeamWalletsResponse;
import com.cregis.sdk.generated.waas.model.GenerateAddressRequest;
import com.cregis.sdk.payment.CregisPaymentValues;

import java.util.Collections;

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
        GenerateAddressRequest addressRequest = GenerateAddressRequest.builder()
                .chainId("195")
                .build();
        CreateOrderRequest orderRequest = CreateOrderRequest.builder()
                .orderId("smoke-order")
                .orderAmount("1")
                .orderCurrency("USD")
                .payerId("smoke-payer")
                .successUrl("https://merchant.example/success")
                .cancelUrl("https://merchant.example/cancel")
                .tokens(CregisPaymentValues.jsonString(Collections.singletonList("USDT-TRC20")))
                .build();
        ListTeamWalletsResponse teamResponse = ListTeamWalletsResponse.builder().build();

        if (waas == null || payment == null || team == null
                || !"195".equals(addressRequest.getChainId())
                || !"smoke-order".equals(orderRequest.getOrderId())
                || teamResponse.getRows() == null) {
            throw new IllegalStateException("Cregis SDK clients were not initialized");
        }
    }
}
