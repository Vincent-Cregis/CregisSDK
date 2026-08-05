# Testing Guide

## Unit tests

Unit tests run locally without API credentials. They verify SDK behavior such as signature generation and HTTP request handling.

```bash
mvn test
```

The default Maven build explicitly excludes classes named `*IntegrationTest`. CI also uses this safe default.

## Sandbox integration tests

Integration tests connect to Cregis APIs and can create orders, addresses, payouts, or withdrawals. Use Sandbox credentials and run them only when these external changes are intended.

### Prerequisites

1. Copy `.env.example` to `.env` and fill in Sandbox credentials.
2. Set the environment variables required by the tests you intend to run.

**For WaaS Tests:**
| Variable | Required | Description |
| --- | --- | --- |
| `WAAS_PID` | Yes | Your WaaS Project ID |
| `WAAS_API_KEY` | Yes | Your WaaS API Key |
| `WAAS_ENDPOINT` | No | Defaults to `https://waas.cregis.com` |
| `WAAS_WALLET_ID` | No | Required for Payout test |
| `WITHDRAW_ADDRESS` | No | Required for Withdrawal test |

**For Payment Engine Tests:**
| Variable | Required | Description |
| --- | --- | --- |
| `PAYMENT_PID` | Yes | Your Payment Engine Project ID |
| `PAYMENT_API_KEY` | Yes | Your Payment Engine API Key |
| `PAYMENT_ENDPOINT` | No | Defaults to `https://payment.cregis.com` |

### Run all integration tests

```bash
mvn verify -Pintegration-tests
```

This command first runs unit tests, then runs `*IntegrationTest` classes with Maven Failsafe.

### Run WaaS integration tests

```bash
mvn verify -Pintegration-tests -Dit.test=CregisWaasIntegrationTest

mvn verify -Pintegration-tests -Dit.test=CregisWaasIntegrationTest#testProjectCoinQuery
```

### Run Payment Engine integration tests

```bash
mvn verify -Pintegration-tests -Dit.test=CregisPaymentIntegrationTest

mvn verify -Pintegration-tests -Dit.test=CregisPaymentIntegrationTest#testCreateOrder
```

## Manual signature verification

If you want to debug signature issues, you can add logging to `CregisSigner.java`:

```java
// Inside CregisSigner.sign() method
String toSign = sb.toString();
System.out.println("DEBUG: Signer input: [" + toSign + "]");
String sign = md5(toSign).toLowerCase();
System.out.println("DEBUG: Signer output: [" + sign + "]");
return sign;
```

Then run your tests to see the exact string being signed and its MD5 hash.
