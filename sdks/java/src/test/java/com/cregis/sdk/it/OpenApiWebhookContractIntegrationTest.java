package com.cregis.sdk.it;

import com.cregis.sdk.contract.OpenApiContractValidator;
import com.cregis.sdk.contract.OpenApiSpecLocator;
import com.cregis.sdk.core.signer.CregisSigner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/** Validates sanitized payloads for all inbound OpenAPI webhook operations. */
@Tag("integration")
class OpenApiWebhookContractIntegrationTest {

    private static final String FIXTURE_API_KEY = "openapi-contract-fixture-key";

    private static OpenApiContractValidator paymentContract;
    private static OpenApiContractValidator waasContract;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        paymentContract = OpenApiContractValidator.load(OpenApiSpecLocator.locateSpec("payment"));
        waasContract = OpenApiContractValidator.load(OpenApiSpecLocator.locateSpec("waas"));
        objectMapper = new ObjectMapper();
    }

    @Test
    void validatesPaymentOrderCallback() throws Exception {
        paymentContract.validateWebhook(
                "orderCallback",
                readResource("/webhooks/payment-refunded-synthetic.json"));
    }

    @Test
    void validatesAllWaasCallbacks() throws Exception {
        Map<String, Object> deposit = commonWaasCallback();
        deposit.put("currency", "195@195");
        deposit.put("address", "deposit-address");
        deposit.put("status", "1");
        deposit.put("txid", "deposit-tx");
        deposit.put("block_height", "45123456");
        deposit.put("block_time", "1734328473070");
        waasContract.validateWebhook("depositCallback", sign(deposit));

        Map<String, Object> payout = commonWaasCallback();
        payout.put("currency", "195@195");
        payout.put("address", "payout-address");
        payout.put("third_party_id", "payout-1");
        payout.put("status", 6);
        payout.put("txid", "payout-tx");
        payout.put("block_height", "45123456");
        payout.put("block_time", 1734328473070L);
        waasContract.validateWebhook("payoutCallback", sign(payout));

        Map<String, Object> externalVerification = new LinkedHashMap<>();
        externalVerification.put("pid", 1382528827416576L);
        externalVerification.put("cid", 1382813146816512L);
        externalVerification.put("third_party_id", "external-1");
        externalVerification.put("chain_id", "195");
        externalVerification.put("token_id", "195");
        externalVerification.put("from_address", "from-address");
        externalVerification.put("to_address", "to-address");
        externalVerification.put("amount", "10.5");
        externalVerification.put("nonce", "hwlkk6");
        externalVerification.put("timestamp", 1688004243314L);
        waasContract.validateWebhook(
                "payoutExternalVerificationCallback",
                sign(externalVerification));

        Map<String, Object> withdrawal = commonWaasCallback();
        withdrawal.put("currency", "195@195");
        withdrawal.put("from_address", "from-address");
        withdrawal.put("to_address", "to-address");
        withdrawal.put("third_party_id", "withdrawal-1");
        withdrawal.put("status", 6);
        withdrawal.put("txid", "withdrawal-tx");
        withdrawal.put("block_height", "45123456");
        withdrawal.put("block_time", 1734328473070L);
        waasContract.validateWebhook("withdrawalCallback", sign(withdrawal));
    }

    private static Map<String, Object> commonWaasCallback() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("pid", 1382528827416576L);
        payload.put("cid", 1382813146816512L);
        payload.put("chain_id", "195");
        payload.put("token_id", "195");
        payload.put("amount", "10.5");
        payload.put("nonce", "hwlkk6");
        payload.put("timestamp", 1688004243314L);
        return payload;
    }

    private static String sign(Map<String, Object> payload) throws Exception {
        Map<String, Object> signed = new LinkedHashMap<>(payload);
        signed.put("sign", CregisSigner.sign(payload, FIXTURE_API_KEY));
        return objectMapper.writeValueAsString(signed);
    }

    private static String readResource(String path) throws Exception {
        try (InputStream input = OpenApiWebhookContractIntegrationTest.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IllegalStateException("Missing test resource: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
