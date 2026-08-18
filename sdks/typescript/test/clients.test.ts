import assert from "node:assert/strict";
import test from "node:test";

import {
  CregisApiError,
  CregisClientError,
  CregisHttpError,
  CregisPaymentClient,
  CregisTeamClient,
  CregisWaasClient,
  signProjectParameters,
  signTeamRequest,
} from "../src/index.js";
import type { CregisLogEntry, FetchLike } from "../src/index.js";
import type { RuntimeSchema, RuntimeSchemaRegistry } from "../src/index.js";
import type { GeneratedOperation } from "../src/core/types.js";
import { paymentOperations } from "../src/generated/payment/operations.js";
import { teamOperations } from "../src/generated/team/operations.js";
import { waasOperations } from "../src/generated/waas/operations.js";

const PID = 1382528827416576;
const API_KEY = "test-api-key";
const ACCESS_KEY = "test-access-key";
const ACCESS_SECRET = "test-access-secret";

interface CapturedRequest {
  url: string;
  init: RequestInit;
}

const operationsByPath = new Map<string, GeneratedOperation>(
  [...Object.values(paymentOperations), ...Object.values(waasOperations), ...Object.values(teamOperations)]
    .map((operation) => [operation.path, operation as GeneratedOperation]),
);

function minimalContractValue(
  schema: RuntimeSchema,
  schemas: RuntimeSchemaRegistry,
): unknown {
  if (schema.$ref !== undefined) {
    const name = schema.$ref.replace("#/components/schemas/", "");
    const target = schemas[name];
    assert.ok(target, `Missing test schema ${name}`);
    return minimalContractValue(target, schemas);
  }
  if (schema.const !== undefined) return schema.const;
  if (schema.enum?.[0] !== undefined) return schema.enum[0];
  const alternatives = schema.oneOf ?? schema.anyOf;
  if (alternatives?.[0] !== undefined) return minimalContractValue(alternatives[0], schemas);
  if (schema.allOf !== undefined) {
    return Object.assign(
      {},
      ...schema.allOf.map((part) => minimalContractValue(part, schemas)),
    );
  }
  if (schema.type === "array" || schema.items !== undefined) return [];
  if (schema.type === "integer" || schema.type === "number") return 1;
  if (schema.type === "boolean") return true;
  if (schema.type === "null") return null;
  if (schema.type === "object" || schema.properties !== undefined) {
    const value: Record<string, unknown> = {};
    for (const field of schema.required ?? []) {
      const fieldSchema = schema.properties?.[field];
      assert.ok(fieldSchema, `Missing test property schema ${field}`);
      value[field] = minimalContractValue(fieldSchema, schemas);
    }
    return value;
  }
  return "test";
}

const GENERATED_DATA = Symbol("generated response data");

function successfulFetch(
  requests: CapturedRequest[],
  data: unknown | typeof GENERATED_DATA = GENERATED_DATA,
): FetchLike {
  return async (input, init) => {
    const url = String(input);
    requests.push({ url, init: init ?? {} });
    const operation = operationsByPath.get(new URL(url).pathname);
    assert.ok(operation, `Missing generated operation for ${url}`);
    const responseData = data === GENERATED_DATA
      ? operation.responseSchema === null
        ? null
        : minimalContractValue(operation.responseSchema, operation.schemas)
      : data;
    return new Response(JSON.stringify({ code: "00000", msg: "ok", data: responseData }), {
      status: 200,
      headers: { "Content-Type": "application/json" },
    });
  };
}

function pathOf(request: CapturedRequest): string {
  return new URL(request.url).pathname;
}

function bodyOf(request: CapturedRequest): Record<string, unknown> {
  const body = request.init.body;
  assert.equal(typeof body, "string");
  return JSON.parse(body as string) as Record<string, unknown>;
}

test("Payment and WaaS clients cover all 17 project API paths and signatures", async () => {
  const requests: CapturedRequest[] = [];
  const fetch = successfulFetch(requests);
  const common = { baseUrl: "https://sandbox.example", pid: PID, apiKey: API_KEY, fetch };
  const payment = new CregisPaymentClient(common);
  const waas = new CregisWaasClient(common);

  await payment.createOrder({
    order_id: "order-1",
    order_amount: "1",
    order_currency: "USD",
    payer_id: "payer-1",
    success_url: "https://merchant.example/success",
    cancel_url: "https://merchant.example/cancel",
  });
  await payment.queryOrder({ cregis_id: "po-1" });
  await waas.generateAddress({ chain_id: "195" });
  await waas.batchGenerateAddress({ chain_id: "195", number: "2" });
  await waas.updateAddress({ address: "T-test", alias: "updated" });
  await waas.validateAddress({ chain_id: "195", address: "T-test" });
  await waas.checkAddressLegality({ chain_id: "195", address: "T-test" });
  await waas.payoutV1({
    currency: "195@195",
    address: "T-to",
    amount: "1",
    third_party_id: "payout-v1-1",
  });
  await waas.payoutV2({
    currency: "195@195",
    to_address: "T-to",
    amount: "1",
    third_party_id: "payout-v2-1",
  });
  await waas.withdrawal({
    currency: "195@195",
    from_address: "T-from",
    to_address: "T-to",
    amount: "1",
    third_party_id: "withdrawal-1",
  });
  await waas.balanceCollect({
    currency: "195@195",
    from_address: "T-from",
    to_address: "T-to",
  });
  await waas.queryProjectCoins();
  await waas.queryTradeRecords({});
  await waas.queryPayout({ cid: 1 });
  await waas.queryWithdrawal({ cid: 1 });
  await waas.queryAddressBalance({ currency: "195@195" });
  await waas.queryAddressBalanceV2({ address: "T-test" });

  assert.deepEqual(requests.map(pathOf), [
    "/api/v2/checkout",
    "/api/v2/order/info",
    "/api/v1/address/create",
    "/api/v1/batch/address/create",
    "/api/v1/address/update",
    "/api/v1/address/inner",
    "/api/v1/address/legal",
    "/api/v1/payout",
    "/api/v2/payout",
    "/api/v1/sub_address_withdrawal",
    "/api/v1/collection",
    "/api/v1/coins",
    "/api/v1/trade/page",
    "/api/v1/payout/query",
    "/api/v1/sub_address_withdrawal/info",
    "/api/v1/sub_address_balance",
    "/api/v2/sub_address_balance",
  ]);

  for (const request of requests) {
    const signed = bodyOf(request);
    assert.equal(signed.pid, PID);
    assert.match(String(signed.nonce), /^[0-9a-f]{6}$/);
    assert.equal(typeof signed.timestamp, "number");
    assert.match(String(signed.sign), /^[0-9a-f]{32}$/);
    const unsigned = { ...signed };
    delete unsigned.sign;
    assert.equal(signed.sign, signProjectParameters(unsigned, API_KEY));
    assert.equal(request.init.redirect, "manual");
  }
});

test("Team client covers all six paths, canonical bodies, and Access headers", async () => {
  const requests: CapturedRequest[] = [];
  const client = new CregisTeamClient({
    baseUrl: "https://sandbox.example",
    accessKey: ACCESS_KEY,
    accessSecret: ACCESS_SECRET,
    fetch: successfulFetch(requests),
  });

  await client.listTeamWallets({ page_size: 10, page_num: 1, wallet_type: "single_sign" });
  await client.listTeamWalletAddresses({ wallet_id: 1, chain_id: "195" });
  await client.queryTeamWalletBalance({ wallet_id: 1 });
  await client.queryTeamWalletAddressBalance({ wallet_id: 1 });
  await client.queryTeamWalletHistoryTransactions({ wallet_id: 1 });
  await client.queryTeamWalletProcessingTransactions({ wallet_id: 1 });

  assert.deepEqual(requests.map(pathOf), [
    "/openapi/v1/wallets",
    "/openapi/v1/wallet_address",
    "/openapi/v1/wallet_balance",
    "/openapi/v1/wallet_address_balance",
    "/openapi/v1/wallet_history_transaction_info",
    "/openapi/v1/wallet_processing_transaction_info",
  ]);
  assert.equal(requests[0]?.init.body, '{"page_num":1,"page_size":10,"wallet_type":"single_sign"}');

  for (const request of requests) {
    const headers = new Headers(request.init.headers);
    const timestamp = Number(headers.get("Access-Timestamp"));
    const nonce = headers.get("Access-Nonce");
    const signature = headers.get("Access-Signature");
    assert.equal(headers.get("Access-Key"), ACCESS_KEY);
    assert.ok(nonce);
    assert.ok(signature);
    assert.equal(
      signature,
      signTeamRequest(pathOf(request), timestamp, nonce, String(request.init.body), ACCESS_SECRET),
    );
  }
});

test("required OpenAPI request fields are checked before sending", async () => {
  let calls = 0;
  const client = new CregisPaymentClient({
    baseUrl: "https://sandbox.example",
    pid: PID,
    apiKey: API_KEY,
    fetch: async () => {
      calls += 1;
      return new Response();
    },
  });

  await assert.rejects(
    client.createOrder({} as never),
    (error: unknown) => error instanceof CregisClientError
      && error.message.startsWith("Missing required request field:"),
  );
  assert.equal(calls, 0);
});

test("OpenAPI request types, limits, enums, and cross-field rules are checked before sending", async () => {
  let calls = 0;
  const fetch: FetchLike = async () => {
    calls += 1;
    return new Response();
  };
  const project = { baseUrl: "https://sandbox.example", pid: PID, apiKey: API_KEY, fetch };
  const payment = new CregisPaymentClient(project);
  const waas = new CregisWaasClient(project);
  const team = new CregisTeamClient({
    baseUrl: "https://sandbox.example",
    accessKey: ACCESS_KEY,
    accessSecret: ACCESS_SECRET,
    fetch,
  });

  await assert.rejects(payment.createOrder({
    order_id: "order-1",
    order_amount: 1,
    order_currency: "USD",
    payer_id: "payer-1",
    success_url: "https://merchant.example/success",
    cancel_url: "https://merchant.example/cancel",
  } as never), /order_amount must be string/);
  await assert.rejects(waas.updateAddress({ address: "T-test" }), /OpenAPI anyOf contract/);
  await assert.rejects(waas.batchGenerateAddress({
    chain_id: "195",
    number: "101",
  }), /number does not match the documented format/);
  await assert.rejects(team.listTeamWallets({
    page_num: 1,
    page_size: 101,
  }), /page_size must be at most 100/);
  await assert.rejects(team.queryTeamWalletBalance({
    wallet_id: Number.MAX_SAFE_INTEGER + 1,
  }), /wallet_id must be a JavaScript-safe integer/);
  assert.equal(calls, 0);
});

test("successful API responses are validated before typed data is returned", async () => {
  const project = { baseUrl: "https://sandbox.example", pid: PID, apiKey: API_KEY };
  const payment = new CregisPaymentClient({
    ...project,
    fetch: successfulFetch([], { cregis_id: 123 }),
  });
  await assert.rejects(
    payment.queryOrder({ cregis_id: "po-1" }),
    /queryOrder response data\.cregis_id must be string/,
  );

  const team = new CregisTeamClient({
    baseUrl: "https://sandbox.example",
    accessKey: ACCESS_KEY,
    accessSecret: ACCESS_SECRET,
    fetch: successfulFetch([], { pageNum: 1, pageSize: 10, total: 1 }),
  });
  await assert.rejects(
    team.listTeamWallets({ page_num: 1, page_size: 10 }),
    /listTeamWallets response data is missing required field rows/,
  );

  const nullablePayment = new CregisPaymentClient({
    ...project,
    fetch: successfulFetch([], { cregis_id: "po-1", settlement_details: null }),
  });
  const nullableResult = await nullablePayment.queryOrder({ cregis_id: "po-1" });
  assert.equal(nullableResult.settlement_details, null);
});

test("response envelopes require code, msg, and data", async () => {
  const bodies = [
    { code: "00000", data: {} },
    { code: "00000", msg: "ok" },
  ];
  const client = new CregisWaasClient({
    baseUrl: "https://sandbox.example",
    pid: PID,
    apiKey: API_KEY,
    fetch: async () => new Response(JSON.stringify(bodies.shift())),
  });

  await assert.rejects(client.queryProjectCoins(), /required string field: msg/);
  await assert.rejects(client.queryProjectCoins(), /required field: data/);
});

test("HTTP, API, and malformed response errors preserve their category", async () => {
  const responses = [
    new Response("rate limited", { status: 429, statusText: "Too Many Requests" }),
    new Response(JSON.stringify({ code: "B0001", msg: "Signature Error", data: null })),
    new Response("not-json"),
  ];
  const client = new CregisWaasClient({
    baseUrl: "https://sandbox.example",
    pid: PID,
    apiKey: API_KEY,
    fetch: async () => responses.shift() ?? new Response(),
  });

  await assert.rejects(client.queryProjectCoins(), (error: unknown) => {
    assert.ok(error instanceof CregisHttpError);
    assert.equal(error.status, 429);
    assert.equal(error.responseBody, "rate limited");
    return true;
  });
  await assert.rejects(client.queryProjectCoins(), (error: unknown) => {
    assert.ok(error instanceof CregisApiError);
    assert.equal(error.code, "B0001");
    assert.equal(error.apiMessage, "Signature Error");
    return true;
  });
  await assert.rejects(client.queryProjectCoins(), CregisClientError);
});

test("POST failures are not retried and signed redirects are manual", async () => {
  let attempts = 0;
  const client = new CregisWaasClient({
    baseUrl: "https://sandbox.example",
    pid: PID,
    apiKey: API_KEY,
    fetch: async () => {
      attempts += 1;
      throw new Error("connection lost");
    },
  });

  await assert.rejects(client.queryProjectCoins(), CregisClientError);
  assert.equal(attempts, 1);
});

test("timeouts abort the request and debug logs never contain credentials", async () => {
  const entries: CregisLogEntry[] = [];
  const client = new CregisWaasClient({
    baseUrl: "https://sandbox.example",
    pid: PID,
    apiKey: API_KEY,
    timeoutMs: 10,
    debug: true,
    logger: (entry) => entries.push(entry),
    fetch: async (_input, init) => await new Promise<Response>((_resolve, reject) => {
      const holdOpen = setTimeout(() => reject(new Error("test timeout guard")), 1_000);
      init?.signal?.addEventListener("abort", () => {
        clearTimeout(holdOpen);
        reject(init.signal?.reason);
      }, { once: true });
    }),
  });

  await assert.rejects(
    client.queryProjectCoins(),
    (error: unknown) => error instanceof CregisClientError && error.message.includes("timed out"),
  );
  assert.equal(entries.length, 1);
  const logText = JSON.stringify(entries);
  assert.doesNotMatch(logText, /test-api-key|sign|nonce|timestamp|1382528827416576/i);
});

test("base URL and credentials are validated", () => {
  assert.throws(() => new CregisWaasClient({
    baseUrl: "http://api.example",
    pid: PID,
    apiKey: API_KEY,
  }), CregisClientError);
  assert.doesNotThrow(() => new CregisWaasClient({
    baseUrl: "http://127.0.0.1:8080",
    pid: String(PID),
    apiKey: API_KEY,
  }));
  assert.throws(() => new CregisTeamClient({
    baseUrl: "https://sandbox.example",
    accessKey: "",
    accessSecret: ACCESS_SECRET,
  }), CregisClientError);
  assert.throws(() => new CregisWaasClient({
    baseUrl: "https://sandbox.example",
    pid: null as never,
    apiKey: API_KEY,
  }), CregisClientError);
});
