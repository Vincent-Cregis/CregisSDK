import assert from "node:assert/strict";
import test from "node:test";

import {
  CregisPaymentClient,
  CregisTeamClient,
  CregisWaasClient,
  type FetchLike,
} from "../src/index.js";
import { paymentOperations } from "../src/generated/payment/operations.js";
import { teamOperations } from "../src/generated/team/operations.js";
import { waasOperations } from "../src/generated/waas/operations.js";
import { createOpenApiContractFetch } from "./openapi-contract.js";

type OperationMap = Readonly<Record<string, Readonly<{ method: string; path: string }>>>;

interface ContractProbe {
  readonly fetch: FetchLike;
  assertAllOperationsCovered(): void;
}

function present(name: string): string | undefined {
  const value = process.env[name];
  return value === undefined || value.trim() === "" ? undefined : value;
}

function enabledWith(...names: string[]): boolean {
  return process.env.CREGIS_RUN_SANDBOX_TESTS === "true" && names.every((name) => present(name));
}

function readOnlyEnabledWith(...names: string[]): boolean {
  return process.env.CREGIS_SANDBOX_SUITE !== "all" && enabledWith(...names);
}

function allEnabledWith(...names: string[]): boolean {
  return process.env.CREGIS_SANDBOX_SUITE === "all"
    && process.env.CREGIS_ALLOW_MUTATING_TESTS === "true"
    && enabledWith(...names);
}

function createContractProbe(
  apiName: "payment" | "team" | "waas",
  operations: OperationMap,
): ContractProbe {
  const contractFetch = createOpenApiContractFetch(apiName);
  const expected = new Set(
    Object.values(operations).map(
      (operation) => `${operation.method.toUpperCase()} ${operation.path}`,
    ),
  );
  const covered = new Set<string>();
  return {
    fetch: async (input, init) => {
      const url = new URL(String(input));
      covered.add(`${(init?.method ?? "GET").toUpperCase()} ${url.pathname}`);
      return contractFetch(input, init);
    },
    assertAllOperationsCovered: () => {
      const missing = [...expected].filter((operation) => !covered.has(operation));
      assert.deepEqual(missing, [], `Sandbox did not call: ${missing.join(", ")}`);
    },
  };
}

function valueOrDefault(value: string | undefined, defaultValue: string): string {
  return value === undefined ? defaultValue : value;
}

function uniqueId(prefix: string): string {
  return `${prefix}-${Date.now()}`;
}

function shortAlias(prefix: string): string {
  return `${prefix}-${Date.now().toString().slice(-8)}`;
}

test("Sandbox: WaaS read-only operations", {
  skip: !readOnlyEnabledWith("WAAS_PID", "WAAS_API_KEY", "WAAS_ENDPOINT"),
}, async () => {
  const client = new CregisWaasClient({
    baseUrl: present("WAAS_ENDPOINT")!,
    pid: present("WAAS_PID")!,
    apiKey: present("WAAS_API_KEY")!,
    fetch: createOpenApiContractFetch("waas"),
  });

  const coins = await client.queryProjectCoins();
  assert.ok(Array.isArray(coins.address_coins));
  assert.ok(coins.address_coins.length > 0);
  assert.equal(typeof coins.address_coins[0]?.decimals, "string");

  const trades = await client.queryTradeRecords({ page_num: 1, page_size: 10 });
  assert.equal(typeof trades.pageNum, "number");
  assert.equal(typeof trades.pageSize, "number");
  assert.ok(Array.isArray(trades.rows));
});

test("Sandbox: Payment read-only query", {
  skip: !readOnlyEnabledWith(
    "PAYMENT_PID",
    "PAYMENT_API_KEY",
    "PAYMENT_ENDPOINT",
    "PAYMENT_CREGIS_ID",
  ),
}, async () => {
  const cregisId = present("PAYMENT_CREGIS_ID")!;
  const client = new CregisPaymentClient({
    baseUrl: present("PAYMENT_ENDPOINT")!,
    pid: present("PAYMENT_PID")!,
    apiKey: present("PAYMENT_API_KEY")!,
    fetch: createOpenApiContractFetch("payment"),
  });

  const order = await client.queryOrder({ cregis_id: cregisId });
  assert.equal(order.cregis_id, cregisId);
});

test("Sandbox: all two Payment API operations", {
  skip: !allEnabledWith("PAYMENT_PID", "PAYMENT_API_KEY", "PAYMENT_ENDPOINT"),
}, async () => {
  const probe = createContractProbe("payment", paymentOperations);
  const client = new CregisPaymentClient({
    baseUrl: present("PAYMENT_ENDPOINT")!,
    pid: present("PAYMENT_PID")!,
    apiKey: present("PAYMENT_API_KEY")!,
    fetch: probe.fetch,
  });

  const created = await client.createOrder({
    order_id: uniqueId("ts-sdk-order"),
    order_amount: "1.0",
    order_currency: "USDT",
    payer_id: "ts-sdk-sandbox",
    payer_name: "TypeScript SDK Sandbox",
    callback_url: "https://webhook.site/test",
    success_url: "https://example.com/success",
    cancel_url: "https://example.com/cancel",
    remark: "TypeScript SDK Sandbox test",
    valid_time: 60,
    language: "sc",
    underpaid_tolerance: 0.1,
    overpaid_tolerance: 0.1,
  });
  assert.equal(typeof created.cregis_id, "string");
  assert.ok(created.cregis_id!.length > 0);
  assert.equal(typeof created.checkout_url, "string");

  const queried = await client.queryOrder({ cregis_id: created.cregis_id! });
  assert.equal(queried.cregis_id, created.cregis_id);
  probe.assertAllOperationsCovered();
});

test("Sandbox: all 15 WaaS API operations", {
  skip: !allEnabledWith("WAAS_PID", "WAAS_API_KEY", "WAAS_ENDPOINT", "WITHDRAW_ADDRESS"),
}, async () => {
  const probe = createContractProbe("waas", waasOperations);
  const client = new CregisWaasClient({
    baseUrl: present("WAAS_ENDPOINT")!,
    pid: present("WAAS_PID")!,
    apiKey: present("WAAS_API_KEY")!,
    fetch: probe.fetch,
  });
  const sourceAddress = present("WITHDRAW_ADDRESS")!;
  const amount = valueOrDefault(present("WAAS_TEST_AMOUNT"), "0.001");
  const preferredChainId = valueOrDefault(present("WAAS_CHAIN_ID"), "198");

  const coins = await client.queryProjectCoins();
  const addressCoins = coins.address_coins ?? [];
  const payoutCoins = coins.payout_coins ?? [];
  const selectedCoin = payoutCoins.find((coin) =>
    coin.chain_id === preferredChainId
      && addressCoins.some((addressCoin) => addressCoin.chain_id === coin.chain_id))
    ?? payoutCoins.find((coin) =>
      coin.chain_id !== undefined
        && addressCoins.some((addressCoin) => addressCoin.chain_id === coin.chain_id));
  assert.ok(selectedCoin?.chain_id, "Sandbox needs a chain that supports address creation and payout");
  assert.ok(selectedCoin.token_id, "Selected Sandbox payout coin needs a token ID");
  const chainId = selectedCoin.chain_id;
  const currency = `${chainId}@${selectedCoin.token_id}`;

  const generated = await client.generateAddress({
    chain_id: chainId,
    alias: shortAlias("ts-sdk"),
  });
  assert.equal(typeof generated.address, "string");
  assert.ok(generated.address!.length > 0);
  const generatedAddress = generated.address!;

  const batch = await client.batchGenerateAddress({
    chain_id: chainId,
    number: "2",
    alias: shortAlias("ts-batch"),
  });
  assert.ok(batch.length > 0);
  assert.equal(typeof batch[0]?.address, "string");

  await client.updateAddress({
    address: generatedAddress,
    alias: shortAlias("ts-updated"),
  });

  const internal = await client.validateAddress({ chain_id: chainId, address: generatedAddress });
  assert.equal(internal.result, true);
  const legal = await client.checkAddressLegality({ chain_id: chainId, address: generatedAddress });
  assert.equal(legal.result, true);

  const balanceV1 = await client.queryAddressBalance({
    currency,
    address: sourceAddress,
    page_num: 1,
    page_size: 10,
  });
  assert.ok(Array.isArray(balanceV1.rows));
  const balanceV2 = await client.queryAddressBalanceV2({
    address: sourceAddress,
    currency,
    page_num: 1,
    page_size: 10,
  });
  assert.ok(Array.isArray(balanceV2.rows));
  const trades = await client.queryTradeRecords({ page_num: 1, page_size: 10 });
  assert.ok(Array.isArray(trades.rows));

  const payoutDestination = valueOrDefault(
    present("WAAS_PAYOUT_TO_ADDRESS"),
    generatedAddress,
  );
  const payoutV1 = await client.payoutV1({
    currency,
    address: payoutDestination,
    amount,
    third_party_id: uniqueId("ts-sdk-p1"),
    remark: "TypeScript SDK Sandbox test",
  });
  assert.equal(typeof payoutV1.cid, "number");
  const payout = await client.queryPayout({ cid: payoutV1.cid! });

  const configuredWalletId = present("WAAS_WALLET_ID");
  const walletId = configuredWalletId === undefined ? undefined : Number(configuredWalletId);
  assert.ok(walletId === undefined || Number.isSafeInteger(walletId), "WAAS_WALLET_ID must be an integer");
  const payoutV2 = await client.payoutV2({
    currency,
    to_address: payoutDestination,
    amount,
    third_party_id: uniqueId("ts-sdk-p2"),
    remark: "TypeScript SDK Sandbox test",
    ...(walletId === undefined ? {} : { wallet_id: walletId }),
  });
  assert.equal(typeof payoutV2.cid, "number");

  const withdrawal = await client.withdrawal({
    currency,
    from_address: sourceAddress,
    to_address: valueOrDefault(present("WITHDRAW_TO_ADDRESS"), generatedAddress),
    amount,
    third_party_id: uniqueId("ts-sdk-wd"),
    remark: "TypeScript SDK Sandbox test",
  });
  assert.equal(typeof withdrawal.cid, "number");
  await client.queryWithdrawal({ cid: withdrawal.cid! });

  const collectionDestination = present("WAAS_COLLECTION_TO_ADDRESS") ?? payout.from_address;
  assert.ok(
    collectionDestination,
    "Collection requires WAAS_COLLECTION_TO_ADDRESS or payout query from_address",
  );
  const collection = await client.balanceCollect({
    currency,
    from_address: sourceAddress,
    to_address: collectionDestination,
    amount,
  });
  assert.equal(typeof collection.cid, "number");
  probe.assertAllOperationsCovered();
});

test("Sandbox: all six Team API operations", {
  skip: !enabledWith("TEAM_ACCESS_KEY", "TEAM_ACCESS_SECRET", "TEAM_ENDPOINT"),
}, async () => {
  const probe = createContractProbe("team", teamOperations);
  const client = new CregisTeamClient({
    baseUrl: present("TEAM_ENDPOINT")!,
    accessKey: present("TEAM_ACCESS_KEY")!,
    accessSecret: present("TEAM_ACCESS_SECRET")!,
    fetch: probe.fetch,
  });

  const wallets = await client.listTeamWallets({ page_num: 1, page_size: 10 });
  assert.ok(Array.isArray(wallets.rows));
  assert.ok(wallets.rows.length > 0);
  const wallet = wallets.rows[0]!;
  assert.equal(typeof wallet.wallet_id, "number");
  const token = wallet.tokens?.[0];
  assert.equal(typeof token?.chain_id, "string");
  assert.equal(typeof token?.token_id, "string");
  const walletId = wallet.wallet_id!;
  const chainId = token!.chain_id!;
  const tokenId = token!.token_id!;

  const addresses = await client.listTeamWalletAddresses({
    wallet_id: walletId,
    chain_id: chainId,
    page_num: 1,
    page_size: 10,
  });
  assert.ok(Array.isArray(addresses.rows));

  const balance = await client.queryTeamWalletBalance({
    wallet_id: walletId,
    chain_id: chainId,
    token_id: tokenId,
    page_num: 1,
    page_size: 10,
  });
  assert.ok(Array.isArray(balance.rows));

  const firstAddress = addresses.rows[0]?.address;
  const addressBalance = await client.queryTeamWalletAddressBalance({
    wallet_id: walletId,
    ...(firstAddress === undefined ? {} : { address: firstAddress }),
    chain_id: chainId,
    token_id: tokenId,
    page_num: 1,
    page_size: 10,
  });
  assert.ok(Array.isArray(addressBalance.rows));

  const history = await client.queryTeamWalletHistoryTransactions({
    wallet_id: walletId,
    chain_id: chainId,
    token_id: tokenId,
    page_num: 1,
    page_size: 10,
  });
  assert.ok(Array.isArray(history.rows));

  const processing = await client.queryTeamWalletProcessingTransactions({
    wallet_id: walletId,
    chain_id: chainId,
    token_id: tokenId,
    page_num: 1,
    page_size: 10,
  });
  assert.ok(Array.isArray(processing.rows));
  probe.assertAllOperationsCovered();
});
