package com.cregis.sdk.webhook.payment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data for an expired order callback.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentExpiredCallbackData extends PaymentCallbackData {
}
