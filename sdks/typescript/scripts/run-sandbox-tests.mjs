import { spawnSync } from "node:child_process";

const requiredGroups = {
  "WaaS read-only suite": ["WAAS_PID", "WAAS_API_KEY", "WAAS_ENDPOINT"],
  "Payment read-only suite": [
    "PAYMENT_PID",
    "PAYMENT_API_KEY",
    "PAYMENT_ENDPOINT",
    "PAYMENT_CREGIS_ID",
  ],
  "Team API suite": ["TEAM_ACCESS_KEY", "TEAM_ACCESS_SECRET", "TEAM_ENDPOINT"],
};

const missing = Object.entries(requiredGroups)
  .map(([suite, names]) => [
    suite,
    names.filter((name) => process.env[name]?.trim() === "" || process.env[name] === undefined),
  ])
  .filter(([, names]) => names.length > 0);

if (missing.length > 0) {
  console.error("Sandbox tests did not run because required environment variables are missing:");
  for (const [suite, names] of missing) {
    console.error(`- ${suite}: ${names.join(", ")}`);
  }
  process.exit(2);
}

const result = spawnSync(
  process.execPath,
  ["--test", ".test-dist/test/sandbox.test.js"],
  {
    env: { ...process.env, CREGIS_RUN_SANDBOX_TESTS: "true" },
    stdio: "inherit",
  },
);

if (result.error !== undefined) throw result.error;
process.exit(result.status ?? 1);
