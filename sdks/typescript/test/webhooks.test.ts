import assert from "node:assert/strict";
import test from "node:test";

import {
  CregisClientError,
  CregisPaymentCallbackHandler,
  CregisWaasCallbackHandler,
  signProjectParameters,
} from "../src/index.js";

const API_KEY = "callback-api-key";

function signedBody(payload: Record<string, unknown>, apiKey = API_KEY): string {
  return JSON.stringify({ ...payload, sign: signProjectParameters(payload, apiKey) });
}

function commonEnvelope(): Record<string, unknown> {
  return {
    pid: 1382528827416576,
    nonce: "m8jisx",
    timestamp: 1687848653294,
  };
}

function waasBase(): Record<string, unknown> {
  return {
    ...commonEnvelope(),
    cid: 1382813146816512,
    chain_id: "195",
    token_id: "195",
    currency: "195@195",
    amount: "10.5",
  };
}

function paymentData(eventType: string): Record<string, unknown> {
  const common = {
    cregis_id: "po-1",
    order_id: "merchant-1",
    status: eventType === "expired" ? "expired" : eventType === "refunded" ? "canceled" : "paid",
  };
  if (eventType === "expired") return common;
  return {
    ...common,
    payment_address: "T-payment",
    receive_amount: "10",
    receive_currency: "USD",
    pay_amount: "10",
    pay_currency: "USDT-TRC20",
    exchange_rate: "1",
    tx_id: "tx-1",
    transact_time: 1719994383015,
  };
}

test("Payment callback verifies signatures and preserves event-specific wire types", () => {
  const handler = new CregisPaymentCallbackHandler(API_KEY);
  const notification = handler.verifyAndParse(signedBody({
    ...commonEnvelope(),
    event_name: "order",
    event_type: "refunded",
    data: {
      ...paymentData("refunded"),
      type: 1,
      refund_id: "rf-1",
      refund_status: 1,
      refund_created_time: 1719994183015,
      refund_transact_time: 1719994383015,
      future_field: "ignored",
    },
  }));

  assert.equal(notification.event_type, "refunded");
  if (notification.event_type === "refunded") {
    assert.equal(notification.data.refund_id, "rf-1");
    assert.equal(notification.data.refund_status, 1);
    assert.equal(notification.data.refund_transact_time, 1719994383015);
  }
  assert.equal(CregisPaymentCallbackHandler.CALLBACK_SUCCESS, "success");
});

test("Payment callback dispatches every documented event", () => {
  const handler = new CregisPaymentCallbackHandler(API_KEY);
  for (const event_type of ["paid", "paid_partial", "paid_over", "expired", "refunded", "paid_remain"] as const) {
    const result = handler.verifyAndParse(signedBody({
      ...commonEnvelope(),
      event_name: "order",
      event_type,
      data: paymentData(event_type),
    }));
    assert.equal(result.event_type, event_type);
  }
});

test("all four WaaS webhook contracts verify and parse", () => {
  const handler = new CregisWaasCallbackHandler(API_KEY);

  const deposit = handler.handleDepositCallback(signedBody({
    ...waasBase(),
    address: "deposit-address",
    status: "1",
    txid: "tx-1",
    block_time: "1734328473070",
  }));
  assert.equal(deposit.status, "1");

  const payout = handler.handlePayoutCallback(signedBody({
    ...waasBase(),
    address: "payout-address",
    third_party_id: "payout-1",
    status: 6,
    block_time: 1734328473070,
  }));
  assert.equal(payout.status, 6);

  const external = handler.handlePayoutExternalVerificationCallback(signedBody({
    ...waasBase(),
    third_party_id: "external-1",
    from_address: "from",
    to_address: "to",
  }));
  assert.equal(external.third_party_id, "external-1");

  const withdrawal = handler.handleWithdrawalCallback(signedBody({
    ...waasBase(),
    from_address: "from",
    to_address: "to",
    third_party_id: "withdrawal-1",
    status: 6,
  }));
  assert.equal(withdrawal.to_address, "to");
  assert.equal(CregisWaasCallbackHandler.CALLBACK_SUCCESS, "success");
  assert.equal(CregisWaasCallbackHandler.EXTERNAL_VERIFICATION_APPROVE, "ok");
  assert.equal(CregisWaasCallbackHandler.EXTERNAL_VERIFICATION_DENY, "deny");
});

test("webhooks reject bad signatures, missing fields, and wrong wire types", () => {
  const payment = new CregisPaymentCallbackHandler(API_KEY);
  const waas = new CregisWaasCallbackHandler(API_KEY);

  assert.throws(() => payment.verifyAndParse("{}"), CregisClientError);
  assert.throws(() => payment.verifyAndParse(signedBody({
    ...commonEnvelope(),
    event_name: "order",
    event_type: "future_event",
    data: {},
  })), /has no payload contract mapping/);
  assert.throws(() => payment.verifyAndParse(signedBody({
    ...commonEnvelope(),
    event_name: "order",
    event_type: "refunded",
    data: { ...paymentData("refunded"), transact_time: "1719994383015" },
  })), /transact_time must be integer/);
  assert.throws(() => payment.verifyAndParse(signedBody({
    ...commonEnvelope(),
    event_name: "order",
    event_type: "paid",
    data: {},
  })), /missing required field cregis_id/);
  assert.throws(() => waas.handleDepositCallback(signedBody({
    ...waasBase(),
    address: "deposit-address",
    status: 1,
    txid: "tx-1",
  })), /status must be string/);
});

test("stable nested Payment fixture remains compatible with Java SDK", () => {
  const rawBody = JSON.stringify({
    event_name: "order",
    event_type: "refunded",
    pid: 123456789,
    nonce: "abc123",
    timestamp: 1719994383015,
    data: {
      cregis_id: "po-test",
      order_id: "merchant-test",
      status: "canceled",
      payment_address: "T-payment",
      receive_amount: "10",
      receive_currency: "USD",
      pay_amount: "10",
      pay_currency: "USDT-TRC20",
      exchange_rate: "1",
      tx_id: "tx-test",
      transact_time: 1719994383015,
      refund_id: "rf-test",
      refund_status: 1,
    },
    sign: "79ecbe54381b0bd9b580fdca0fa536ac",
  });

  const result = new CregisPaymentCallbackHandler("fixture-api-key").verifyAndParse(rawBody);
  assert.equal(result.event_type, "refunded");
  if (result.event_type === "refunded") {
    assert.equal(result.data.refund_id, "rf-test");
  }
});
