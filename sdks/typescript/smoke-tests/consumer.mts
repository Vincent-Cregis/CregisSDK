import {
  CregisPaymentClient,
  CregisWaasClient,
  type CreateOrderRequest,
} from "@cregis/sdk";

const request: CreateOrderRequest = {
  order_id: "order-1",
  order_amount: "1",
  order_currency: "USD",
  payer_id: "payer-1",
  success_url: "https://merchant.example/success",
  cancel_url: "https://merchant.example/cancel",
};

const payment = new CregisPaymentClient({
  baseUrl: "https://sandbox.example",
  pid: "1382528827416576",
  apiKey: "api-key",
});
void payment.createOrder(request);

const waas = new CregisWaasClient({
  baseUrl: "https://sandbox.example",
  pid: 1382528827416576,
  apiKey: "api-key",
});
void waas.queryProjectCoins();
