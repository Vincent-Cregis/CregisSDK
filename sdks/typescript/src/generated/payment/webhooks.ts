// Generated from the canonical Cregis OpenAPI specification. Do not edit.
import type { GeneratedWebhook } from "../../core/types.js";
import { paymentSchemas } from "./schemas.js";

export const paymentWebhooks: Readonly<Record<"orderCallback", GeneratedWebhook>> = {
  orderCallback: {
    operationId: "orderCallback",
    schema: {"$ref":"#/components/schemas/PaymentCallbackEnvelope"},
    schemas: paymentSchemas,
  },
} as const;
