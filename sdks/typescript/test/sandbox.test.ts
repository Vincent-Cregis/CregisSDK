import assert from "node:assert/strict";
import test from "node:test";

import {
  CregisPaymentClient,
  CregisTeamClient,
  CregisWaasClient,
} from "../src/index.js";
import { createOpenApiContractFetch } from "./openapi-contract.js";

function present(name: string): string | undefined {
  const value = process.env[name];
  return value === undefined || value.trim() === "" ? undefined : value;
}

function enabledWith(...names: string[]): boolean {
  return process.env.CREGIS_RUN_SANDBOX_TESTS === "true" && names.every((name) => present(name));
}

test("Sandbox: WaaS read-only operations", {
  skip: !enabledWith("WAAS_PID", "WAAS_API_KEY", "WAAS_ENDPOINT"),
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
  skip: !enabledWith("PAYMENT_PID", "PAYMENT_API_KEY", "PAYMENT_ENDPOINT", "PAYMENT_CREGIS_ID"),
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

test("Sandbox: all six Team API operations", {
  skip: !enabledWith("TEAM_ACCESS_KEY", "TEAM_ACCESS_SECRET", "TEAM_ENDPOINT"),
}, async () => {
  const client = new CregisTeamClient({
    baseUrl: present("TEAM_ENDPOINT")!,
    accessKey: present("TEAM_ACCESS_KEY")!,
    accessSecret: present("TEAM_ACCESS_SECRET")!,
    fetch: createOpenApiContractFetch("team"),
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
});
