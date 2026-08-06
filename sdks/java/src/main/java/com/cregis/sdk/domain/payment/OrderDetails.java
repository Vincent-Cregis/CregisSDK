package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderDetails {

    @JsonProperty("shopping_cost")
    private BigDecimal shoppingCost;

    @JsonProperty("tax_cost")
    private BigDecimal taxCost;

    @JsonProperty("items")
    private List<Item> items;

    @Data
    @Builder
    public static class Item {
        @JsonProperty("item_id")
        private String itemId;

        @JsonProperty("item_name")
        private String itemName;

        @JsonProperty("item_price")
        private BigDecimal itemPrice;

        @JsonProperty("price_currency")
        private String priceCurrency;

        @JsonProperty("item_quantity")
        private Long itemQuantity;
    }
}
