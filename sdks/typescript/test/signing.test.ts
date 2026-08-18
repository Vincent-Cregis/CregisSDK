import assert from "node:assert/strict";
import test from "node:test";

import {
  canonicalizeJson,
  CregisClientError,
  signProjectParameters,
  signTeamRequest,
} from "../src/index.js";

test("project signer matches the published payout vector", () => {
  const signature = signProjectParameters({
    pid: 1382528827416576,
    currency: "195@195",
    address: "TXsmKpEuW7qWnXzJLGP9eDLvWPR2GRn1FS",
    amount: "1.1",
    remark: "payout",
    third_party_id: "c9231e604da54469a735af3f449c880f",
    callback_url: "https://your-domain.com/callback",
    nonce: "hwlkk6",
    timestamp: 1688004243314,
  }, "f502a9ac9ca54327986f29c03b271491");

  assert.equal(signature, "f76fb193e9d34d2e59fef64e3418f79b");
});

test("project signer canonicalizes nested webhook data", () => {
  const signature = signProjectParameters({
    event_name: "order",
    event_type: "refunded",
    pid: 123456789,
    nonce: "abc123",
    timestamp: 1719994383015,
    data: {
      cregis_id: "po-test",
      order_id: "merchant-test",
      refund_id: "rf-test",
      refund_status: 1,
    },
  }, "fixture-api-key");

  assert.equal(signature, "9e2b39ae45341e1d912dda3e31a3dddf");
});

test("Team signer matches the independent HMAC vector", () => {
  const body = canonicalizeJson({ name: "Demo Team" });
  const signature = signTeamRequest(
    "/openapi/team/profile",
    1717380000000,
    "9f7c6a2b47e34f19",
    body,
    "team-secret",
  );

  assert.equal(signature, "cef9805fa4ee7bf9376b140153c5e3f80e74e4f950eb6f92ef780fa42aa44289");
});

test("RFC 8785 canonicalization is stable", () => {
  assert.equal(canonicalizeJson('{ "b": 2, "a": 1 }'), '{"a":1,"b":2}');
  assert.equal(
    canonicalizeJson({ z: [3, { b: true, a: null }], a: "text" }),
    '{"a":"text","z":[3,{"a":null,"b":true}]}',
  );
});

test("signers reject invalid inputs", () => {
  assert.throws(() => signProjectParameters({}, ""), CregisClientError);
  assert.throws(
    () => signTeamRequest("relative", 1, "1234567890123456", "{}", "secret"),
    CregisClientError,
  );
  assert.throws(
    () => signTeamRequest("/path", 1, null as never, "{}", "secret"),
    CregisClientError,
  );
  assert.throws(
    () => signTeamRequest("/path", 1, "1234567890123456", null as never, "secret"),
    CregisClientError,
  );
  assert.throws(() => canonicalizeJson({ invalid: Number.NaN }), CregisClientError);
});
