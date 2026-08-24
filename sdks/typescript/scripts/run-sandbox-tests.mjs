import { spawnSync } from "node:child_process";

const allOperations = process.argv.includes("--all");
const requiredGroups = allOperations
  ? {
      "WaaS full suite": [
        "WAAS_PID",
        "WAAS_API_KEY",
        "WAAS_ENDPOINT",
        "WITHDRAW_ADDRESS",
      ],
      "Payment full suite": ["PAYMENT_PID", "PAYMENT_API_KEY", "PAYMENT_ENDPOINT"],
      "Team API suite": ["TEAM_ACCESS_KEY", "TEAM_ACCESS_SECRET", "TEAM_ENDPOINT"],
    }
  : {
      "WaaS read-only suite": ["WAAS_PID", "WAAS_API_KEY", "WAAS_ENDPOINT"],
      "Payment read-only suite": [
        "PAYMENT_PID",
        "PAYMENT_API_KEY",
        "PAYMENT_ENDPOINT",
        "PAYMENT_CREGIS_ID",
      ],
      "Team API suite": ["TEAM_ACCESS_KEY", "TEAM_ACCESS_SECRET", "TEAM_ENDPOINT"],
    };

if (allOperations && process.env.CREGIS_ALLOW_MUTATING_TESTS !== "true") {
  console.error(
    "Full Sandbox tests require CREGIS_ALLOW_MUTATING_TESTS=true because they create "
      + "orders and addresses and submit payout, withdrawal, and collection requests.",
  );
  process.exit(2);
}

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

if (allOperations) {
  const unsafeEndpoints = ["WAAS_ENDPOINT", "PAYMENT_ENDPOINT", "TEAM_ENDPOINT"]
    .filter((name) => {
      try {
        const url = new URL(process.env[name]);
        return url.protocol !== "https:"
          || !url.hostname.startsWith("t-")
          || !url.hostname.endsWith(".cregis.dev");
      } catch {
        return true;
      }
    });
  if (unsafeEndpoints.length > 0) {
    console.error(
      `Full Sandbox tests refused non-Sandbox endpoints: ${unsafeEndpoints.join(", ")}`,
    );
    process.exit(2);
  }
}

const result = spawnSync(
  process.execPath,
  ["--test", ".test-dist/test/sandbox.test.js"],
  {
    env: {
      ...process.env,
      CREGIS_RUN_SANDBOX_TESTS: "true",
      CREGIS_SANDBOX_SUITE: allOperations ? "all" : "readonly",
    },
    stdio: "inherit",
  },
);

if (result.error !== undefined) throw result.error;
process.exit(result.status ?? 1);
