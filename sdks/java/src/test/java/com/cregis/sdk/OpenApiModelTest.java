package com.cregis.sdk;

import com.cregis.sdk.generated.payment.model.CreateOrderResponse;
import com.cregis.sdk.generated.payment.model.CreateOrderRequest;
import com.cregis.sdk.generated.payment.model.OrderDetails;
import com.cregis.sdk.generated.payment.model.OrderItem;
import com.cregis.sdk.generated.payment.model.QueryOrderResponse;
import com.cregis.sdk.generated.team.model.ListTeamWalletAddressesRequest;
import com.cregis.sdk.generated.team.model.ListTeamWalletsResponse;
import com.cregis.sdk.generated.waas.model.AddressBalanceResponse;
import com.cregis.sdk.generated.waas.model.PayoutV1Request;
import com.cregis.sdk.generated.waas.model.ProjectCoinQueryResponse;
import com.cregis.sdk.generated.waas.model.TradeRecordQueryResponse;
import com.cregis.sdk.payment.CregisPaymentValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenApiModelTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createOrderResponseIncludesAllOpenApiTopLevelFields() throws Exception {
        String json = "{"
                + "\"cregis_id\":\"po-1\","
                + "\"checkout_url\":\"https://checkout.example\","
                + "\"merchant_name\":\"Merchant\","
                + "\"merchant_logo_url\":\"https://example.com/logo.png\","
                + "\"order_amount\":\"5.00\","
                + "\"order_currency\":\"HKD\","
                + "\"created_time\":1687848653294,"
                + "\"expire_time\":1687850453294,"
                + "\"payment_info\":[{\"token_symbol\":\"USDT\",\"token_decimals\":6,"
                + "\"consolidated_qrcodes\":[{\"wallet_name\":\"Wallet\",\"qrcode\":\"qr\","
                + "\"wallet_icon\":\"https://example.com/wallet.png\"}]}]"
                + "}";

        CreateOrderResponse response = objectMapper.readValue(json, CreateOrderResponse.class);
        assertEquals("Merchant", response.getMerchantName());
        assertEquals(6, response.getPaymentInfo().get(0).getTokenDecimals());
        assertEquals("Wallet", response.getPaymentInfo().get(0)
                .getConsolidatedQrcodes().get(0).getWalletName());
    }

    @Test
    void queryOrderResponseIncludesRefundOrderMerchantAndSettlementObjects() throws Exception {
        String json = "{"
                + "\"refund_data\":{\"refund_id\":\"rf-1\",\"refund_status\":1,\"type\":2},"
                + "\"order_details\":{\"shopping_cost\":10.88,\"items\":[{\"item_id\":\"10001\"}]},"
                + "\"sub_merchant\":{\"sub_merchant_id\":\"sub-1\"},"
                + "\"settlement_details\":{"
                + "\"id\":\"settlement-1\","
                + "\"order_settlement_detail\":{\"status\":\"settled\",\"settlement_amount\":\"10\"}"
                + "}"
                + "}";

        QueryOrderResponse response = objectMapper.readValue(json, QueryOrderResponse.class);
        assertEquals("rf-1", response.getRefundData().getRefundId());
        assertEquals("10001", response.getOrderDetails().getItems().get(0).getItemId());
        assertEquals("sub-1", response.getSubMerchant().getSubMerchantId());
        assertEquals(
                "settled",
                response.getSettlementDetails().getOrderSettlementDetail().getStatus().getValue());
    }

    @Test
    void waasResponseModelsUseActualWireNamesAndTypes() throws Exception {
        String balanceJson = "{\"total\":2,\"pageNum\":3,\"pageSize\":20,\"rows\":[]}";
        String tradeJson = "{\"total\":2,\"pageNum\":3,\"pageSize\":20,\"rows\":[]}";
        String coinsJson = "{\"address_coins\":[{\"coin_name\":\"USDT\",\"decimals\":\"6\"}],"
                + "\"order_coins\":[{\"coin_name\":\"USDT\",\"decimals\":\"6\"}]}";

        AddressBalanceResponse balances = objectMapper.readValue(balanceJson, AddressBalanceResponse.class);
        TradeRecordQueryResponse trades = objectMapper.readValue(tradeJson, TradeRecordQueryResponse.class);
        ProjectCoinQueryResponse coins = objectMapper.readValue(coinsJson, ProjectCoinQueryResponse.class);

        assertEquals(3, balances.getPageNum());
        assertEquals(20, balances.getPageSize());
        assertEquals(3, trades.getPageNum());
        assertEquals(20, trades.getPageSize());
        assertNotNull(trades.getRows());
        assertEquals("6", coins.getAddressCoins().get(0).getDecimals());
        assertEquals("6", coins.getOrderCoins().get(0).getDecimals());
    }

    @Test
    void teamResponseModelsUseActualCamelCaseWireNames() throws Exception {
        String json = "{\"total\":1,\"pageNum\":1,\"pageSize\":10,"
                + "\"rows\":[{\"wallet_id\":1,\"walletType\":\"single_sign\"}]}";

        ListTeamWalletsResponse response = objectMapper.readValue(json, ListTeamWalletsResponse.class);

        assertEquals(1, response.getPageNum());
        assertEquals(10, response.getPageSize());
        assertEquals("single_sign", response.getRows().get(0).getWalletType().getValue());
    }

    @Test
    void generatedModelsRetainOpenApiRequiredFieldMetadata() throws Exception {
        assertTrue(CreateOrderRequest.class.getMethod("getOrderId")
                .getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class).required());
        assertTrue(PayoutV1Request.class.getMethod("getAmount")
                .getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class).required());
        assertTrue(ListTeamWalletAddressesRequest.class.getMethod("getWalletId")
                .getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class).required());
    }

    @Test
    void paymentStructuredValuesAreEncodedAsJsonStrings() throws Exception {
        OrderDetails details = OrderDetails.builder()
                .shoppingCost(new BigDecimal("5.00"))
                .items(Arrays.asList(OrderItem.builder().itemId("ITEM-001").build()))
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .orderId("order-1")
                .orderAmount("5.00")
                .orderCurrency("USD")
                .payerId("payer-1")
                .successUrl("https://merchant.example/success")
                .cancelUrl("https://merchant.example/cancel")
                .orderDetails(CregisPaymentValues.jsonString(details))
                .tokens(CregisPaymentValues.jsonString(Arrays.asList("USDT-TRC20")))
                .build();

        assertEquals("ITEM-001", objectMapper.readTree(request.getOrderDetails())
                .get("items").get(0).get("item_id").asText());
        assertEquals("USDT-TRC20", objectMapper.readTree(request.getTokens()).get(0).asText());
    }
}
