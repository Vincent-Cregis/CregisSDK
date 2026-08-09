package com.cregis.sdk.domain.payment;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data for paid, partially paid, and overpaid order callbacks.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentCompletedCallbackData extends PaymentSettlementCallbackData {
}
