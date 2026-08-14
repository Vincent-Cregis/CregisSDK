// Generated from the canonical Cregis OpenAPI specification. Do not edit.
import type { GeneratedOperation } from "../../core/types.js";
import { paymentSchemas } from "./schemas.js";

export const paymentOperations: Readonly<Record<"createOrder" | "queryOrder", GeneratedOperation>> = {
  createOrder: {
    method: "POST",
    operationId: "createOrder",
    path: "/api/v2/checkout",
    requiredRequestFields: ["order_amount","order_currency","order_id","payer_id","success_url","cancel_url"],
    requestSchema: {"$ref":"#/components/schemas/CreateOrderRequest"},
    responseSchema: {"$ref":"#/components/schemas/CreateOrderResponse"},
    schemas: paymentSchemas,
  },
  queryOrder: {
    method: "POST",
    operationId: "queryOrder",
    path: "/api/v2/order/info",
    requiredRequestFields: ["cregis_id"],
    requestSchema: {"$ref":"#/components/schemas/QueryOrderRequest"},
    responseSchema: {"$ref":"#/components/schemas/QueryOrderResponse"},
    schemas: paymentSchemas,
  },
} as const;
