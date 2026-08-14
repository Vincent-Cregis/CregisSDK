// Generated from the canonical Cregis OpenAPI specification. Do not edit.
import type { GeneratedWebhook } from "../../core/types.js";
import { waasSchemas } from "./schemas.js";

export const waasWebhooks: Readonly<Record<"depositCallback" | "payoutCallback" | "payoutExternalVerificationCallback" | "withdrawalCallback", GeneratedWebhook>> = {
  depositCallback: {
    operationId: "depositCallback",
    schema: {"$ref":"#/components/schemas/AddressDepositCallbackNotification"},
    schemas: waasSchemas,
  },
  payoutCallback: {
    operationId: "payoutCallback",
    schema: {"$ref":"#/components/schemas/PayoutCallbackNotification"},
    schemas: waasSchemas,
  },
  payoutExternalVerificationCallback: {
    operationId: "payoutExternalVerificationCallback",
    schema: {"$ref":"#/components/schemas/PayoutExternalVerificationCallbackNotification"},
    schemas: waasSchemas,
  },
  withdrawalCallback: {
    operationId: "withdrawalCallback",
    schema: {"$ref":"#/components/schemas/WithdrawalCallbackNotification"},
    schemas: waasSchemas,
  },
} as const;
