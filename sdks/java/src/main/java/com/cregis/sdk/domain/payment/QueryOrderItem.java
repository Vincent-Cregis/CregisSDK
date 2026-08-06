package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QueryOrderItem {

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
