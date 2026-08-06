package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class QueryOrderDetails {

    @JsonProperty("shopping_cost")
    private BigDecimal shoppingCost;

    @JsonProperty("tax_cost")
    private BigDecimal taxCost;

    @JsonProperty("items")
    private List<QueryOrderItem> items;
}
