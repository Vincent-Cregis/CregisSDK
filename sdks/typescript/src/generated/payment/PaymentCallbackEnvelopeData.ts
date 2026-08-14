// Generated from the canonical Cregis OpenAPI specification. Do not edit.
import type { PaymentCompletedCallbackData } from './PaymentCompletedCallbackData.js';
import type { PaymentExpiredCallbackData } from './PaymentExpiredCallbackData.js';
import type { PaymentRefundedCallbackData } from './PaymentRefundedCallbackData.js';
import type { PaymentRemainingCallbackData } from './PaymentRemainingCallbackData.js';

export type PaymentCallbackEnvelopeData = PaymentCompletedCallbackData | PaymentExpiredCallbackData | PaymentRefundedCallbackData | PaymentRemainingCallbackData;
