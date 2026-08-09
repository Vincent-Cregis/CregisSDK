# Cregis SDK for Java

Welcome to the official Java SDK for [Cregis](https://cregis.com). This SDK provides clients for the Cregis Payment Engine, WaaS (Wallet as a Service), and Team APIs.

## Features

- **Split Client Architecture**: Dedicated clients for Payment (`CregisPaymentClient`), WaaS (`CregisWaasClient`), and Team API (`CregisTeamClient`).
- **Auto-Signing**: Automatically generates MD5 signatures using `pid`, `api_key`, `nonce`, and `timestamp`.
- **WaaS Module**: 
  - Manage Deposit Addresses (Generate, Batch, Update, Validate)
  - Payouts & Sub-Address Withdrawals
  - Funds Collection (Sweeping)
  - Balance & Trade Queries
- **Payment Engine**:
  - Create & Query Orders
  - Supports advanced order details (Items, Tax, Shipping)
  - Sub-merchant support
  - Webhook Notification Handling (Payment, Refund, Replenishment)
- **Team API**:
  - Query wallets, addresses, balances, history, and processing transactions
  - RFC 8785 request-body canonicalization and HMAC-SHA256 header signing
- **Robust Error Handling**: Typed exceptions separating Client vs Server errors.

## Installation

The current release candidate uses the following Maven coordinates:

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>com.cregis</groupId>
    <artifactId>cregis-sdk-java</artifactId>
    <version>1.0.0-rc.1</version>
</dependency>
```

Gradle:

```kotlin
implementation("com.cregis:cregis-sdk-java:1.0.0-rc.1")
```

Before the release candidate is available from Maven Central, run `mvn install` in `sdks/java` to install it into Maven Local.

## Quick Start

### 1. WaaS Example (Generate Address)

```java
import com.cregis.sdk.client.CregisWaasClient;
import com.cregis.sdk.domain.waas.*;

// Initialize Client
CregisWaasClient client = CregisWaasClient.builder()
    .credentials("YOUR_PID", "YOUR_API_KEY")
    .endpoint("YOUR_PROJECT_SPECIFIC_BASE_URL")
    .build();

// Create Request
GenerateAddressRequest request = GenerateAddressRequest.builder()
    .chainId("195") // TRON
    .alias("my-wallet")
    .callbackUrl("https://your-webhook.com/deposit")
    .build();

// Execute
try {
    GenerateAddressResponse response = client.generateAddress(request);
    System.out.println("Generated Address: " + response.getAddress());
} catch (Exception e) {
    e.printStackTrace();
}
```

### 2. Payment Example (Create Order)

The SDK provides helper methods to easily handle complex objects like Order Details and Sub-Merchants.

```java
import com.cregis.sdk.client.CregisPaymentClient;
import com.cregis.sdk.domain.payment.*;
import java.math.BigDecimal;
import java.util.Arrays;

// Initialize Client
CregisPaymentClient client = CregisPaymentClient.builder()
    .credentials("YOUR_PID", "YOUR_API_KEY")
    .endpoint("YOUR_PROJECT_SPECIFIC_BASE_URL")
    .build();

// Build Order Details
OrderDetails details = OrderDetails.builder()
    .shoppingCost(new BigDecimal("5.00"))
    .taxCost(new BigDecimal("2.50"))
    .items(Arrays.asList(
        OrderDetails.Item.builder()
            .itemId("ITEM-001")
            .itemName("Premium Subscription")
            .itemPrice(new BigDecimal("99.00"))
            .itemQuantity(1L)
            .priceCurrency("USD")
            .build()
    ))
    .build();

// Create Request
CreateOrderRequest request = CreateOrderRequest.builder()
    .orderId("ORDER-" + System.currentTimeMillis())
    .orderAmount("106.50") // Total = Item + Tax + Shipping
    .orderCurrency("USD")
    .payerId("customer-001")
    .payerEmail("user@example.com")
    .successUrl("https://merchant.example/payment/success")
    .cancelUrl("https://merchant.example/payment/cancel")
    .build();

// Set Complex Objects (Automatically Serialized)
request.setOrderDetailsObject(details);
request.setTokensList(Arrays.asList("USDT-TRC20", "USDT-ERC20"));

// Execute
CreateOrderResponse response = client.createOrder(request);
System.out.println("Checkout URL: " + response.getCheckoutUrl());
```

### 3. Team API Example (List Wallets)

```java
import com.cregis.sdk.client.CregisTeamClient;
import com.cregis.sdk.domain.team.*;

CregisTeamClient teamClient = CregisTeamClient.builder()
    .endpoint("YOUR_TEAM_SPECIFIC_BASE_URL")
    .credentials("YOUR_ACCESS_KEY", "YOUR_ACCESS_SECRET")
    .build();

TeamPagedResponse<TeamWallet> wallets = teamClient.listTeamWallets(
    ListTeamWalletsRequest.builder()
        .pageNum(1)
        .pageSize(10)
        .build()
);
```

### HTTP transport configuration

All clients use 30-second connect, read, and write timeouts by default. Because
Cregis operations use POST and some create financial state, connection-failure
retry is disabled by default. Redirects are always disabled so a signed request
cannot be forwarded to another origin.

Applications that need a proxy, shared connection pool, monitoring interceptor,
or custom timeout can provide an OkHttp-based configuration:

```java
import com.cregis.sdk.core.client.CregisHttpConfig;
import java.time.Duration;

CregisHttpConfig httpConfig = CregisHttpConfig.builder()
    .connectTimeout(Duration.ofSeconds(10))
    .readTimeout(Duration.ofSeconds(20))
    .writeTimeout(Duration.ofSeconds(20))
    .build();

CregisWaasClient client = CregisWaasClient.builder()
    .endpoint("YOUR_PROJECT_SPECIFIC_BASE_URL")
    .credentials("YOUR_PID", "YOUR_API_KEY")
    .httpConfig(httpConfig)
    .build();
```

`retryOnConnectionFailure(true)` is available as an explicit opt-in. Enable it
only when the application reconciles ambiguous results using `order_id`,
`third_party_id`, or `cid`.

### 4. Handling Callbacks (Webhooks)

The SDK includes handlers to verify signatures and parse callback JSON payloads.

**Payment Callbacks:**

```java
import com.cregis.sdk.client.CregisPaymentCallbackHandler;
import com.cregis.sdk.domain.payment.*;

CregisPaymentCallbackHandler handler = new CregisPaymentCallbackHandler("YOUR_API_KEY");

String rawJson = "{...}"; // From HTTP Request Body

try {
    PaymentCallbackNotification<? extends PaymentCallbackData> notification =
        handler.verifyAndParse(rawJson);

    if (notification.getData() instanceof PaymentCompletedCallbackData) {
        PaymentCompletedCallbackData payment =
            (PaymentCompletedCallbackData) notification.getData();
        System.out.println("Order Paid: " + payment.getOrderId());
        System.out.println("Amount: " + payment.getPayAmount());
    } else if (notification.getData() instanceof PaymentRefundedCallbackData) {
        PaymentRefundedCallbackData refund =
            (PaymentRefundedCallbackData) notification.getData();
        System.out.println("Refund: " + refund.getRefundId());
    }
} catch (Exception e) {
    System.err.println("Invalid Signature or Payload: " + e.getMessage());
}
```

**WaaS Callbacks (Deposit, Payout, External Verification, Withdrawal):**

```java
import com.cregis.sdk.client.CregisWaasCallbackHandler;
import com.cregis.sdk.domain.waas.*;

CregisWaasCallbackHandler handler = new CregisWaasCallbackHandler("YOUR_API_KEY");

// Example for Deposit
AddressDepositCallbackNotification deposit = handler.handleDepositCallback(rawJson);
System.out.println("Deposit Confirmed: " + deposit.getTxid());

PayoutExternalVerificationCallbackNotification verification =
    handler.handlePayoutExternalVerificationCallback(rawJson);
// Return exactly "ok" to approve or "deny" to reject.
```

Signature verification must run before business processing. Cregis may retry a
valid callback, so applications must also enforce business idempotency using
stable identifiers such as `cregis_id`, `cid`, or `txid` before returning
`success`.

## Testing

The default test command runs local unit tests only:

```bash
mvn test
```

Sandbox integration tests are opt-in. Read-only tests can run with Sandbox credentials. Tests that create orders, addresses, payouts, or withdrawals remain skipped unless `CREGIS_ALLOW_MUTATING_TESTS=true` is explicitly set.

1. Configure your environment variables (or `.env` file):
   ```bash
   WAAS_PID=...
   WAAS_API_KEY=...
   WAAS_ENDPOINT=... # project-specific Sandbox Base URL
   PAYMENT_PID=...
   PAYMENT_API_KEY=...
   PAYMENT_ENDPOINT=... # project-specific Sandbox Base URL
   ```
2. Run Sandbox integration tests explicitly:
   ```bash
   mvn verify -Pintegration-tests
   ```

See [TESTING.md](TESTING.md) for individual integration-test commands and safety notes.

## Documentation

For full API references, please visit the [Cregis Developer Documentation](https://developer-cn.cregis.com).

## Release status

Version `1.0.0-rc.1` is the first public release candidate. It targets Java 11 and is tested on Java 11, 17, and 21. The project is licensed under Apache-2.0.
