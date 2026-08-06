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
| `WAAS_WALLET_ID` | No | Required for Payout test |
| `WAAS_PAYOUT_TO_ADDRESS` | No | Required destination for Payout test |
| `WITHDRAW_ADDRESS` | No | Required source sub-address for Withdrawal test |
| `WITHDRAW_TO_ADDRESS` | No | Required destination for Withdrawal test |

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

For tests that create orders, addresses, payouts, or withdrawals:

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

Signature behavior is covered by deterministic local test vectors. Do not log API keys, Access Secrets, canonical signing strings, full request bodies, or callback payloads in shared environments.
