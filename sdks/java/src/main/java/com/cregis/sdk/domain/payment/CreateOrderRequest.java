package com.cregis.sdk.domain.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import java.util.List;

@Data
@Builder
public class CreateOrderRequest {

    private static final ObjectMapper MAPPER = new ObjectMapper();


    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("order_amount")
    private String orderAmount;

    @JsonProperty("order_currency")
    private String orderCurrency;

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

    @JsonProperty("success_url")
    private String successUrl;

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

    // JSON Strings for complex objects
    // Note: These fields are still defined as String to maintain compatibility or custom serialization logic if needed,
    // but helper methods or external logic should handle object-to-JSON conversion.
    // However, to improve usability, consider accepting objects and serializing them in the client.
    
    // For now, we will keep them as String but add helper methods in a future refactor or leave as is if the user prefers raw JSON string control.
    // Wait, the better approach for SDK users is to pass objects.
    // But since the API expects these as JSON strings *within* the JSON payload (i.e. escaped JSON), 
    // we need to be careful. The documentation says: "JSONString". 
    // This usually means the field value is a string that contains JSON, e.g. "{\"items\":...}".
    // Jackson serialization of an object would produce a nested JSON object, not a string containing JSON.
    // So we need to keep these as String, but maybe provide a setter that takes an object and serializes it?
    
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
    @SneakyThrows
    public void setTokensList(List<String> tokensList) {
        this.tokens = MAPPER.writeValueAsString(tokensList);
    }

    /**
     * Set order details from an OrderDetails object.
     * @param details OrderDetails object
     */
    @SneakyThrows
    public void setOrderDetailsObject(OrderDetails details) {
        this.orderDetails = MAPPER.writeValueAsString(details);
    }

    /**
     * Set sub-merchant details from a SubMerchant object.
     * @param subMerchantObj SubMerchant object
     */
    @SneakyThrows
    public void setSubMerchantObject(SubMerchant subMerchantObj) {
        this.subMerchant = MAPPER.writeValueAsString(subMerchantObj);
    }
}
