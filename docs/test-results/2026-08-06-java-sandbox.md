# Java SDK Sandbox Test Report — 2026-08-06

## Result

| API | OpenAPI operations | Verified | Blocked | Result |
| --- | ---: | ---: | ---: | --- |
| Payment Engine | 2 | 2 | 0 | Passed |
| WaaS | 15 | 15 | 0 | Passed |
| Team API | 6 | 0 | 6 | Blocked by Team Access Key |
| Total | 23 | 17 | 6 | Partial |

The Team Base URL is reachable and maps to the expected API. The first Team operation returns `O0015: API Key does not exist or status is anomalous`. The other five Team operations depend on the wallet returned by that first operation and were therefore skipped.

## Local verification

- Java runtime: Corretto 11
- Unit and contract tests: 20 passed, 0 failed
- Payment Engine live tests: 2 passed, 0 failed
- WaaS live tests: 15 passed, 0 failed
- Team API live tests: 1 authentication error, 5 dependency skips
- Callback contracts: covered locally; callbacks are inbound and cannot be actively invoked against Sandbox

## Sandbox changes created by the test

- One Payment Engine test order
- One WaaS address and one batch of two WaaS addresses
- Payout V1 and Payout V2 requests using the minimum configured test amount
- One sub-address withdrawal request using the minimum configured test amount
- One balance collection request using the minimum configured test amount

No credential, address, order identifier, transaction identifier, or callback payload is stored in this report.

## Commands

Read-only checks:

```bash
mvn verify -Pintegration-tests -Dit.test='*ReadOnlyIntegrationTest'
```

State-changing Payment Engine checks:

```bash
CREGIS_ALLOW_MUTATING_TESTS=true mvn verify \
  -Pintegration-tests \
  -Dit.test=CregisPaymentIntegrationTest
```

All WaaS operations:

```bash
CREGIS_ALLOW_MUTATING_TESTS=true mvn verify \
  -Pintegration-tests \
  -Dit.test=CregisWaasIntegrationTest
```

Team API checks after replacing the inactive credentials:

```bash
mvn verify \
  -Pintegration-tests \
  -Dit.test=CregisTeamReadOnlyIntegrationTest
```
