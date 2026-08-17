import type {
  PaymentCallbackEnvelope,
  PaymentCallbackEnvelopeData,
  PaymentCompletedCallbackData,
  PaymentExpiredCallbackData,
  PaymentRefundedCallbackData,
  PaymentRemainingCallbackData,
} from "../generated/payment/index.js";

export type ProjectWebhookEnvelope = Pick<
  PaymentCallbackEnvelope,
  "pid" | "nonce" | "timestamp" | "sign"
>;

export type PaymentEventType = PaymentCallbackEnvelope["event_type"];
export type PaymentStatus = PaymentCompletedCallbackData["status"];
export type PaymentCallbackData = PaymentCallbackEnvelopeData;
export type PaymentSettlementCallbackData =
  | PaymentCompletedCallbackData
  | PaymentRefundedCallbackData
  | PaymentRemainingCallbackData;

type PaymentCallbackNotificationBase<TEvent extends PaymentEventType, TData> =
  Omit<PaymentCallbackEnvelope, "event_type" | "data"> & {
    event_type: TEvent;
    data: TData;
  };

export type PaymentCallbackNotification =
  | PaymentCallbackNotificationBase<
    "paid" | "paid_partial" | "paid_over",
    PaymentCompletedCallbackData
  >
  | PaymentCallbackNotificationBase<"expired", PaymentExpiredCallbackData>
  | PaymentCallbackNotificationBase<"refunded", PaymentRefundedCallbackData>
  | PaymentCallbackNotificationBase<"paid_remain", PaymentRemainingCallbackData>;

export type {
  PaymentCompletedCallbackData,
  PaymentExpiredCallbackData,
  PaymentRefundedCallbackData,
  PaymentRemainingCallbackData,
} from "../generated/payment/index.js";

export type {
  AddressDepositCallbackNotification,
  PayoutCallbackNotification,
  PayoutExternalVerificationCallbackNotification,
  WithdrawalCallbackNotification,
} from "../generated/waas/index.js";
