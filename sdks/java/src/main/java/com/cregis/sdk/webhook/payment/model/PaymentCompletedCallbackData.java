package com.cregis.sdk.webhook.payment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data for paid, partially paid, and overpaid order callbacks.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentCompletedCallbackData extends PaymentSettlementCallbackData {
}
