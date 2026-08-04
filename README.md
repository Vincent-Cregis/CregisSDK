# Cregis SDK for Java

Welcome to the official Java SDK for [Cregis](https://cregis.com). This SDK provides a comprehensive interface to interact with the Cregis Payment Engine and WaaS (Wallet as a Service) APIs.

## Features

- **Split Client Architecture**: Dedicated clients for Payment (`CregisPaymentClient`) and WaaS (`CregisWaasClient`).
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
- **Robust Error Handling**: Typed exceptions separating Client vs Server errors.

## Installation

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>com.cregis</groupId>
    <artifactId>cregis-sdk-java</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Quick Start

### 1. WaaS Example (Generate Address)

```java
import com.cregis.sdk.client.CregisWaasClient;
import com.cregis.sdk.domain.waas.*;

// Initialize Client
CregisWaasClient client = CregisWaasClient.builder()
    .credentials("YOUR_PID", "YOUR_API_KEY")
    .endpoint("https://waas.cregis.com") // Optional
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
import java.util.Arrays;

// Initialize Client
CregisPaymentClient client = CregisPaymentClient.builder()
    .credentials("YOUR_PID", "YOUR_API_KEY")
    .build();

// Build Order Details
OrderDetails details = OrderDetails.builder()
    .shoppingCost(5.00)
    .taxCost(2.50)
    .items(Arrays.asList(
        OrderDetails.Item.builder()
            .itemId("ITEM-001")
            .itemName("Premium Subscription")
            .itemPrice(99.00)
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
    .payerEmail("user@example.com")
    .build();

// Set Complex Objects (Automatically Serialized)
request.setOrderDetailsObject(details);
request.setTokensList(Arrays.asList("USDT-TRC20", "USDT-ERC20"));

// Execute
CreateOrderResponse response = client.createOrder(request);
System.out.println("Checkout URL: " + response.getCheckoutUrl());
```

### 3. Handling Callbacks (Webhooks)

The SDK includes handlers to verify signatures and parse callback JSON payloads.

**Payment Callbacks:**

```java
import com.cregis.sdk.client.CregisPaymentCallbackHandler;
import com.cregis.sdk.domain.payment.PaymentCallbackNotification;

CregisPaymentCallbackHandler handler = new CregisPaymentCallbackHandler("YOUR_API_KEY");

String rawJson = "{...}"; // From HTTP Request Body

try {
    // Verifies signature and parses JSON
    PaymentCallbackNotification notification = handler.verifyAndParse(rawJson);
    
    if ("paid".equals(notification.getData().getStatus())) {
        System.out.println("Order Paid: " + notification.getData().getOrderId());
        System.out.println("Amount: " + notification.getData().getPayAmount());
    }
} catch (Exception e) {
    System.err.println("Invalid Signature or Payload: " + e.getMessage());
}
```

**WaaS Callbacks (Deposit, Payout, Withdrawal):**

```java
import com.cregis.sdk.client.CregisWaasCallbackHandler;

CregisWaasCallbackHandler handler = new CregisWaasCallbackHandler("YOUR_API_KEY");

// Example for Deposit
AddressDepositCallbackNotification deposit = handler.handleDepositCallback(rawJson);
System.out.println("Deposit Confirmed: " + deposit.getTxid());
```

## Testing

This project includes Integration Tests that run against the live API.

1. Configure your environment variables (or `.env` file):
   ```bash
   WAAS_PID=...
   WAAS_API_KEY=...
   PAYMENT_PID=...
   PAYMENT_API_KEY=...
   ```
2. Run tests:
   ```bash
   mvn test
   ```

## Documentation

For full API references, please visit the [Cregis Developer Documentation](https://developer-cn.cregis.com).

## License

MIT License
