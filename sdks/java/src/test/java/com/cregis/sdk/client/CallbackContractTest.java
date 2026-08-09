package com.cregis.sdk.client;

import com.cregis.sdk.core.signer.CregisSigner;
import com.cregis.sdk.core.exception.CregisClientException;
import com.cregis.sdk.domain.payment.PaymentCallbackData;
import com.cregis.sdk.domain.enums.PaymentEventType;
import com.cregis.sdk.domain.payment.PaymentCallbackNotification;
import com.cregis.sdk.domain.payment.PaymentCompletedCallbackData;
import com.cregis.sdk.domain.payment.PaymentExpiredCallbackData;
import com.cregis.sdk.domain.payment.PaymentRefundedCallbackData;
import com.cregis.sdk.domain.payment.PaymentRemainingCallbackData;
import com.cregis.sdk.domain.waas.AddressDepositCallbackNotification;
import com.cregis.sdk.domain.waas.PayoutCallbackNotification;
import com.cregis.sdk.domain.waas.PayoutExternalVerificationCallbackNotification;
import com.cregis.sdk.domain.waas.WithdrawalCallbackNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CallbackContractTest {

    private static final String API_KEY = "callback-api-key";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void parsesPaymentRefundFieldsUsingOpenApiTypes() throws Exception {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("cregis_id", "po-1");
        data.put("order_id", "merchant-1");
        data.put("type", 1);
        data.put("refund_id", "rf-1");
        data.put("refund_status", 1);
        data.put("refund_created_time", 1719994183015L);
        data.put("refund_transact_time", 1719994383015L);
        data.put("future_field", "ignored-for-forward-compatibility");

        Map<String, Object> envelope = paymentEnvelope("refunded", data);
        PaymentCallbackNotification<? extends PaymentCallbackData> notification =
                new CregisPaymentCallbackHandler(API_KEY)
                .verifyAndParse(sign(envelope));
        PaymentRefundedCallbackData refund = assertInstanceOf(
                PaymentRefundedCallbackData.class,
                notification.getData());

        assertEquals("rf-1", refund.getRefundId());
        assertEquals(PaymentEventType.REFUNDED, notification.getEventType());
        assertEquals(1, refund.getRefundStatus());
        assertEquals(1719994383015L, refund.getRefundTransactTime());
    }

    @Test
    void parsesPaymentPaidRemainTimestampUsingOpenApiWireName() throws Exception {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("cregis_id", "po-1");
        data.put("additional_payment_transact_time", 1719994483015L);

        Map<String, Object> envelope = paymentEnvelope("paid_remain", data);
        PaymentCallbackNotification<? extends PaymentCallbackData> notification =
                new CregisPaymentCallbackHandler(API_KEY)
                .verifyAndParse(sign(envelope));
        PaymentRemainingCallbackData remaining = assertInstanceOf(
                PaymentRemainingCallbackData.class,
                notification.getData());

        assertEquals(1719994483015L, remaining.getAdditionalPaymentTransactTime());
        assertEquals("success", CregisPaymentCallbackHandler.CALLBACK_SUCCESS);
    }

    @Test
    void dispatchesPaymentEventsToSpecificDataTypes() throws Exception {
        CregisPaymentCallbackHandler handler = new CregisPaymentCallbackHandler(API_KEY);

        assertInstanceOf(
                PaymentCompletedCallbackData.class,
                handler.verifyAndParse(sign(paymentEnvelope("paid", new LinkedHashMap<>())))
                        .getData());
        assertInstanceOf(
                PaymentCompletedCallbackData.class,
                handler.verifyAndParse(sign(paymentEnvelope("paid_partial", new LinkedHashMap<>())))
                        .getData());
        assertInstanceOf(
                PaymentCompletedCallbackData.class,
                handler.verifyAndParse(sign(paymentEnvelope("paid_over", new LinkedHashMap<>())))
                        .getData());
        assertInstanceOf(
                PaymentExpiredCallbackData.class,
                handler.verifyAndParse(sign(paymentEnvelope("expired", new LinkedHashMap<>())))
                        .getData());
    }

    @Test
    void rejectsMalformedSignaturesAndUnknownPaymentEvents() throws Exception {
        CregisPaymentCallbackHandler handler = new CregisPaymentCallbackHandler(API_KEY);

        assertThrows(CregisClientException.class, () -> handler.verifyAndParse(null));
        assertThrows(CregisClientException.class, () -> handler.verifyAndParse("{}"));

        Map<String, Object> unknown = paymentEnvelope("future_event", new LinkedHashMap<>());
        assertThrows(CregisClientException.class, () -> handler.verifyAndParse(sign(unknown)));
    }

    @Test
    void rejectsSignedCallbacksWithInvalidCommonEnvelopeFields() throws Exception {
        CregisPaymentCallbackHandler handler = new CregisPaymentCallbackHandler(API_KEY);

        Map<String, Object> missingPid = paymentEnvelope("paid", new LinkedHashMap<>());
        missingPid.remove("pid");
        CregisClientException pidError = assertThrows(
                CregisClientException.class,
                () -> handler.verifyAndParse(sign(missingPid)));
        assertEquals("Callback pid must be a positive integer", pidError.getMessage());

        Map<String, Object> blankNonce = paymentEnvelope("paid", new LinkedHashMap<>());
        blankNonce.put("nonce", " ");
        CregisClientException nonceError = assertThrows(
                CregisClientException.class,
                () -> handler.verifyAndParse(sign(blankNonce)));
        assertEquals("Callback nonce must be a non-empty string", nonceError.getMessage());

        Map<String, Object> fractionalTimestamp = paymentEnvelope("paid", new LinkedHashMap<>());
        fractionalTimestamp.put("timestamp", 1.5);
        CregisClientException timestampError = assertThrows(
                CregisClientException.class,
                () -> handler.verifyAndParse(sign(fractionalTimestamp)));
        assertEquals("Callback timestamp must be a positive integer", timestampError.getMessage());
    }

    @Test
    void verifiesStableSyntheticNestedPaymentFixture() throws Exception {
        String rawJson = readResource("/webhooks/payment-refunded-synthetic.json");

        PaymentCallbackNotification<? extends PaymentCallbackData> notification =
                new CregisPaymentCallbackHandler("fixture-api-key").verifyAndParse(rawJson);
        PaymentRefundedCallbackData refund = assertInstanceOf(
                PaymentRefundedCallbackData.class,
                notification.getData());

        assertEquals("rf-test", refund.getRefundId());
    }

    @Test
    void verifiesAndParsesWaasPayoutExternalVerificationCallback() throws Exception {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("pid", 1382528827416576L);
        payload.put("cid", 1382813146816512L);
        payload.put("third_party_id", "third-party-1");
        payload.put("chain_id", "195");
        payload.put("token_id", "195");
        payload.put("from_address", "from");
        payload.put("to_address", "to");
        payload.put("amount", "10.5");
        payload.put("nonce", "hwlkk6");
        payload.put("timestamp", 1688004243314L);

        PayoutExternalVerificationCallbackNotification notification = new CregisWaasCallbackHandler(API_KEY)
                .handlePayoutExternalVerificationCallback(sign(payload));

        assertEquals("third-party-1", notification.getThirdPartyId());
        assertEquals("ok", CregisWaasCallbackHandler.EXTERNAL_VERIFICATION_APPROVE);
        assertEquals("deny", CregisWaasCallbackHandler.EXTERNAL_VERIFICATION_DENY);
    }

    @Test
    void verifiesAllOtherWaasWebhookShapes() throws Exception {
        CregisWaasCallbackHandler handler = new CregisWaasCallbackHandler(API_KEY);

        Map<String, Object> depositPayload = waasCallbackBase();
        depositPayload.put("address", "deposit-address");
        depositPayload.put("status", "1");
        depositPayload.put("block_time", "1734328473070");
        AddressDepositCallbackNotification deposit = handler.handleDepositCallback(sign(depositPayload));
        assertEquals("deposit-address", deposit.getAddress());

        Map<String, Object> payoutPayload = waasCallbackBase();
        payoutPayload.put("address", "payout-address");
        payoutPayload.put("third_party_id", "payout-1");
        payoutPayload.put("status", 6);
        payoutPayload.put("block_time", 1734328473070L);
        PayoutCallbackNotification payout = handler.handlePayoutCallback(sign(payoutPayload));
        assertEquals(6, payout.getStatus());

        Map<String, Object> withdrawalPayload = waasCallbackBase();
        withdrawalPayload.put("from_address", "from");
        withdrawalPayload.put("to_address", "to");
        withdrawalPayload.put("third_party_id", "withdrawal-1");
        withdrawalPayload.put("status", 6);
        withdrawalPayload.put("block_time", 1734328473070L);
        WithdrawalCallbackNotification withdrawal = handler.handleWithdrawalCallback(sign(withdrawalPayload));
        assertEquals("to", withdrawal.getToAddress());
        assertEquals("success", CregisWaasCallbackHandler.CALLBACK_SUCCESS);
    }

    private Map<String, Object> paymentEnvelope(String eventType, Map<String, Object> data) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("event_name", "order");
        envelope.put("event_type", eventType);
        envelope.put("pid", 1382528827416576L);
        envelope.put("nonce", "m8jisx");
        envelope.put("timestamp", 1687848653294L);
        envelope.put("data", data);
        return envelope;
    }

    private Map<String, Object> waasCallbackBase() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("pid", 1382528827416576L);
        payload.put("cid", 1382813146816512L);
        payload.put("chain_id", "195");
        payload.put("token_id", "195");
        payload.put("currency", "195@195");
        payload.put("amount", "10.5");
        payload.put("txid", "tx-1");
        payload.put("block_height", "45123456");
        payload.put("nonce", "hwlkk6");
        payload.put("timestamp", 1688004243314L);
        return payload;
    }

    private String sign(Map<String, Object> payload) throws Exception {
        Map<String, Object> signed = new LinkedHashMap<>(payload);
        signed.put("sign", CregisSigner.sign(payload, API_KEY));
        return objectMapper.writeValueAsString(signed);
    }

    private String readResource(String path) throws Exception {
        try (InputStream input = CallbackContractTest.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IllegalStateException("Missing test resource: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
