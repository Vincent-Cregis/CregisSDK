package com.cregis.sdk.domain.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetSupportedCurrenciesRequest {
    // Empty request body as per documentation convention for list queries usually
    // But we need to check if parameters are required.
    // Documentation payment-pay-currency.md says:
    // "如tokens不传，将默认支持所有可支持的加密货币币种进行支付" -> This refers to Create Order tokens
    // param.
    // The endpoint itself might be a GET or POST.
    // Wait, the doc payment-pay-currency.md is just a list reference?
    // "Request APIs" -> "payment-pay-currency"
    // Let's assume there is an API to fetch this dynamic list if it changes.
    // Actually the doc link says "payment-pay-currency" which implies there might
    // be an API.
    // However, if it's static, we already have the Enum.
}
