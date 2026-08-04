# Testing Guide

## 1. Unit Tests
These tests run in isolation and do not require API credentials. They verify the internal logic of the SDK (e.g., Signature generation, JSON parsing).

**Run Command:**
```bash
mvn test
```

## 2. Integration Tests
These tests connect to the **REAL** Cregis API. You should use a Sandbox/Test project for these.

### Prerequisites
1.  Copy `.env.example` to `.env` and fill in your credentials.
2.  Set environment variables for the tests you want to run.

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

### Run All Integration Tests
```bash
mvn test -Dgroups=integration
```

### Run WaaS Integration Tests
```bash
# Run all WaaS tests
mvn test -Dgroups=integration -Dtest=CregisWaasIntegrationTest

# Run a specific WaaS test method
mvn test -Dgroups=integration -Dtest=CregisWaasIntegrationTest#testProjectCoinQuery
```

### Run Payment Engine Integration Tests
```bash
# Run all Payment Engine tests
mvn test -Dgroups=integration -Dtest=CregisPaymentIntegrationTest

# Run a specific Payment Engine test method
mvn test -Dgroups=integration -Dtest=CregisPaymentIntegrationTest#testCreateOrder
```

## 3. Manual Verification (Signatures)
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
