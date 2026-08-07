# Java SDK Sandbox Test Report — 2026-08-06

## Result

| API | OpenAPI operations | Verified | Blocked | Result |
| --- | ---: | ---: | ---: | --- |
| Payment Engine | 2 | 2 | 0 | Passed |
| WaaS | 15 | 15 | 0 | Passed |
| Team API | 6 | 6 | 0 | Passed |
| Total | 23 | 23 | 0 | Passed |

The Team API credentials initially returned `O0015: API Key does not exist or status is anomalous`. After the credential became active, the same read-only suite was retried and all six operations passed.

## Local verification

- Java runtime: Corretto 11
- Unit and contract tests: 20 passed, 0 failed
- Payment Engine live tests: 2 passed, 0 failed
- WaaS live tests: 15 passed, 0 failed
- Team API live tests: 6 passed, 0 failed
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

Team API checks:

```bash
mvn verify \
  -Pintegration-tests \
  -Dit.test=CregisTeamReadOnlyIntegrationTest
```
