package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import java.util.List;

@Data
@Builder
public class CreateOrderRequest {

    private static final ObjectMapper MAPPER = new ObjectMapper();


    @NonNull
    @JsonProperty("order_id")
    private String orderId;

    @NonNull
    @JsonProperty("order_amount")
    private String orderAmount;

    @NonNull
    @JsonProperty("order_currency")
    private String orderCurrency;

    @NonNull
    @JsonProperty("payer_id")
    private String payerId;

    @JsonProperty("payer_name")
    private String payerName;

    @JsonProperty("payer_email")
    private String payerEmail;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("valid_time")
    private Integer validTime;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @NonNull
    @JsonProperty("success_url")
    private String successUrl;

    @NonNull
    @JsonProperty("cancel_url")
    private String cancelUrl;

    @JsonProperty("language")
    private String language;

    @JsonProperty("stablecoin_realtime_rate")
    private String stablecoinRealtimeRate;

    @JsonProperty("underpaid_tolerance")
    private Float underpaidTolerance;

    @JsonProperty("overpaid_tolerance")
    private Float overpaidTolerance;

    @JsonProperty("accept_partial_payment")
    private String acceptPartialPayment;

    @JsonProperty("accept_over_payment")
    private String acceptOverPayment;

    // OpenAPI defines these values as strings containing JSON, not nested objects.
    @JsonProperty("tokens")
    private String tokens; // JSON array string e.g. "[\"USDT-TRC20\"]"

    @JsonProperty("order_details")
    private String orderDetails;

    @JsonProperty("sub_merchant")
    private String subMerchant;

    /**
     * Set tokens from a list of strings.
     * @param tokensList List of token strings, e.g. ["USDT-TRC20", "USDT-ERC20"]
     */
    public void setTokensList(List<String> tokensList) {
        this.tokens = serializeJsonString(tokensList);
    }

    /**
     * Set order details from an OrderDetails object.
     * @param details OrderDetails object
     */
    public void setOrderDetailsObject(OrderDetails details) {
        this.orderDetails = serializeJsonString(details);
    }

    /**
     * Set sub-merchant details from a SubMerchant object.
     * @param subMerchantObj SubMerchant object
     */
    public void setSubMerchantObject(SubMerchant subMerchantObj) {
        this.subMerchant = serializeJsonString(subMerchantObj);
    }

    private static String serializeJsonString(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize JSON string field", e);
        }
    }
}
