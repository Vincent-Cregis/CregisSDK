# Testing Guide

## Unit tests

Unit tests run locally without API credentials. They verify SDK behavior such as signature generation and HTTP request handling.

```bash
mvn test
```

The default Maven build explicitly excludes classes named `*IntegrationTest`. CI also uses this safe default.

## Sandbox integration tests

Integration tests connect to Cregis APIs. Use only Sandbox credentials and the project-specific Sandbox Base URL. Read-only checks are separate from state-changing tests. State-changing tests require an additional explicit opt-in flag.

### Prerequisites

1. Copy `.env.example` to `.env` and fill in Sandbox credentials.
2. Set the environment variables required by the tests you intend to run.

**For WaaS Tests:**
| Variable | Required | Description |
| --- | --- | --- |
| `WAAS_PID` | Yes | Your WaaS Project ID |
| `WAAS_API_KEY` | Yes | Your WaaS API Key |
| `WAAS_ENDPOINT` | Yes | Project-specific WaaS Sandbox Base URL |
| `WAAS_CHAIN_ID` | No | Chain ID used by state-changing address tests; defaults to Shasta `198` |
| `WAAS_TEST_AMOUNT` | No | Amount used by funds-moving tests; defaults to `0.001` |
| `WAAS_WALLET_ID` | No | Optional wallet override for Payout V2 |
| `WAAS_PAYOUT_TO_ADDRESS` | No | Optional payout destination; defaults to an address created by the test |
| `WAAS_COLLECTION_TO_ADDRESS` | No | Optional collection destination; otherwise derived from the payout query |
| `WITHDRAW_ADDRESS` | For funds-moving tests | Existing project sub-address used as withdrawal and collection source |
| `WITHDRAW_TO_ADDRESS` | No | Optional withdrawal destination; defaults to an address created by the test |

**For Payment Engine Tests:**
| Variable | Required | Description |
| --- | --- | --- |
| `PAYMENT_PID` | Yes | Your Payment Engine Project ID |
| `PAYMENT_API_KEY` | Yes | Your Payment Engine API Key |
| `PAYMENT_ENDPOINT` | Yes | Project-specific Payment Sandbox Base URL |
| `PAYMENT_CREGIS_ID` | For read-only test | An existing Sandbox order ID to query |

**For Team API Tests:**

| Variable | Required | Description |
| --- | --- | --- |
| `TEAM_ACCESS_KEY` | Yes | Your Team API Access Key |
| `TEAM_ACCESS_SECRET` | Yes | Your Team API Access Secret |
| `TEAM_ENDPOINT` | Yes | Team Sandbox Base URL |

For tests that create orders, addresses, payouts, withdrawals, or collections:

| Variable | Required | Description |
| --- | --- | --- |
| `CREGIS_ALLOW_MUTATING_TESTS` | Yes | Must be exactly `true` to enable state-changing tests |

### Run all read-only integration tests

```bash
mvn verify -Pintegration-tests -Dit.test='*ReadOnlyIntegrationTest'
```

This runs the WaaS, Payment Engine, and Team API read-only checks. A check is skipped when its required Sandbox variables are absent. It never creates orders, addresses, payouts, or withdrawals.

### Run all integration tests

```bash
mvn verify -Pintegration-tests
```

Without `CREGIS_ALLOW_MUTATING_TESTS=true`, state-changing tests are skipped.

The full callable OpenAPI coverage is:

| API | Sandbox operations |
| --- | ---: |
| Payment Engine | 2 |
| WaaS | 15 |
| Team API | 6 |
| Total | 23 |

Webhook definitions are inbound notifications rather than callable operations. They are covered by local callback contract tests. The repository includes a stable synthetic nested Payment fixture; a sanitized callback captured from the backend, or an independent backend signature vector, is still required before GA to prove nested-value encoding against the real sender.

### Run WaaS integration tests

```bash
mvn verify -Pintegration-tests -Dit.test=CregisWaasReadOnlyIntegrationTest
```

### Run Payment Engine integration tests

```bash
mvn verify -Pintegration-tests -Dit.test=CregisPaymentReadOnlyIntegrationTest
```

### Run Team API read-only integration tests

```bash
mvn verify -Pintegration-tests -Dit.test=CregisTeamReadOnlyIntegrationTest
```

### Run state-changing integration tests

Set `CREGIS_ALLOW_MUTATING_TESTS=true`, then target the required suite explicitly:

```bash
mvn verify -Pintegration-tests -Dit.test=CregisPaymentIntegrationTest

mvn verify -Pintegration-tests -Dit.test=CregisWaasIntegrationTest
```

The WaaS state-changing suite first queries supported coins and creates an internal Sandbox address. It then reuses that address for the dependent operations and never prints addresses, order IDs, transaction IDs, or credentials.

### Run all callable OpenAPI operations

```bash
CREGIS_ALLOW_MUTATING_TESTS=true mvn verify -Pintegration-tests
```

Run this only with `.dev` Sandbox Base URLs. The command creates Sandbox data and submits funds-moving Sandbox operations.

Signature behavior is covered by deterministic local test vectors. Do not log API keys, Access Secrets, canonical signing strings, full request bodies, or callback payloads in shared environments.

## OpenAPI operation drift

From the repository root, compare the Java clients with the canonical specs in
the local documentation repository:

```bash
./codegen/scripts/check-java-openapi.py \
  --spec-dir ../cregis-developer-docs/api-sources/specs
```

This check does not use Sandbox credentials and does not copy the specs into the
SDK repository.
