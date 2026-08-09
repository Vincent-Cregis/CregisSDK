package com.cregis.sdk.domain.payment;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data for an expired order callback.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentExpiredCallbackData extends PaymentCallbackData {
}
