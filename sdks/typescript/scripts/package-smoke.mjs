import assert from "node:assert/strict";
import { createRequire } from "node:module";

const esm = await import("../dist/esm/index.js");
const require = createRequire(import.meta.url);
const cjs = require("../dist/cjs/index.js");

for (const sdk of [esm, cjs]) {
  assert.equal(typeof sdk.CregisPaymentClient, "function");
  assert.equal(typeof sdk.CregisWaasClient, "function");
  assert.equal(typeof sdk.CregisTeamClient, "function");
  assert.equal(typeof sdk.signProjectParameters, "function");
  assert.equal(typeof sdk.signTeamRequest, "function");
}
