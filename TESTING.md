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
You must set the following environment variables (defined in your `.env` file):

**For WaaS Tests:**
- `WAAS_PID`
- `WAAS_API_KEY`
- `WAAS_ENDPOINT` (Optional)

**For Payment Tests:**
- `PAYMENT_PID`
- `PAYMENT_API_KEY`
- `PAYMENT_ENDPOINT` (Optional)

### Run specific Integration Test
```bash
# Set Env Vars via .env file or export
export WAAS_PID="your_pid"
export WAAS_API_KEY="your_key"

# Run Maven
mvn -Dtest=CregisWaasIntegrationTest test
```

## 3. Manual Verification (Signatures)
If you want to debug signature issues, run the `SignerVerifier` main class:

```bash
mvn compile exec:java -Dexec.mainClass="com.cregis.sdk.demo.SignerTest"
```
*(Note: You may need to create a Main class entry point if not already present)*
