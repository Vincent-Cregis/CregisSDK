package com.cregis.sdk;

import com.cregis.sdk.domain.payment.CreateOrderResponse;
import com.cregis.sdk.domain.payment.CreateOrderRequest;
import com.cregis.sdk.domain.payment.QueryOrderResponse;
import com.cregis.sdk.domain.team.ListTeamWalletAddressesRequest;
import com.cregis.sdk.domain.waas.AddressBalanceResponse;
import com.cregis.sdk.domain.waas.PayoutV1Request;
import com.cregis.sdk.domain.waas.TradeRecordQueryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                + "\"payment_info\":[{\"token_symbol\":\"USDT\",\"token_decimals\":6}]"
                + "}";

        CreateOrderResponse response = objectMapper.readValue(json, CreateOrderResponse.class);
        assertEquals("Merchant", response.getMerchantName());
        assertEquals(6, response.getPaymentInfo().get(0).getTokenDecimals());
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
        assertEquals("settled", response.getSettlementDetails().getOrderSettlementDetail().getStatus());
    }

    @Test
    void waasPaginationUsesSnakeCaseWireNames() throws Exception {
        String json = "{\"total\":2,\"page_num\":3,\"page_size\":20,\"rows\":[]}";

        AddressBalanceResponse balances = objectMapper.readValue(json, AddressBalanceResponse.class);
        TradeRecordQueryResponse trades = objectMapper.readValue(json, TradeRecordQueryResponse.class);

        assertEquals(3, balances.getPageNum());
        assertEquals(20, balances.getPageSize());
        assertEquals(3, trades.getPageNum());
        assertEquals(20, trades.getPageSize());
        assertNotNull(trades.getRows());
    }

    @Test
    void buildersEnforceOpenApiRequiredBusinessFields() {
        assertThrows(NullPointerException.class, () -> CreateOrderRequest.builder().build());
        assertThrows(NullPointerException.class, () -> PayoutV1Request.builder().build());
        assertThrows(NullPointerException.class, () -> ListTeamWalletAddressesRequest.builder().build());
    }
}
