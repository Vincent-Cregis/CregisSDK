// Generated from the canonical Cregis OpenAPI specification. Do not edit.
export const paymentSchemas = {
  "CreateOrderRequest": {
    "properties": {
      "accept_over_payment": {
        "enum": [
          "true",
          "false"
        ],
        "type": "string"
      },
      "accept_partial_payment": {
        "enum": [
          "true",
          "false"
        ],
        "type": "string"
      },
      "callback_url": {
        "type": "string"
      },
      "cancel_url": {
        "type": "string"
      },
      "language": {
        "enum": [
          "en",
          "tc",
          "sc"
        ],
        "type": "string"
      },
      "order_amount": {
        "type": "string"
      },
      "order_currency": {
        "type": "string"
      },
      "order_details": {
        "type": "string"
      },
      "order_id": {
        "type": "string"
      },
      "overpaid_tolerance": {
        "format": "float",
        "type": "number"
      },
      "payer_email": {
        "type": "string"
      },
      "payer_id": {
        "type": "string"
      },
      "payer_name": {
        "type": "string"
      },
      "remark": {
        "type": "string"
      },
      "stablecoin_realtime_rate": {
        "enum": [
          "true",
          "false"
        ],
        "type": "string"
      },
      "sub_merchant": {
        "type": "string"
      },
      "success_url": {
        "type": "string"
      },
      "tokens": {
        "type": "string"
      },
      "underpaid_tolerance": {
        "format": "float",
        "type": "number"
      },
      "valid_time": {
        "type": "integer"
      }
    },
    "required": [
      "order_amount",
      "order_currency",
      "order_id",
      "payer_id",
      "success_url",
      "cancel_url"
    ],
    "type": "object"
  },
  "CreateOrderResponse": {
    "properties": {
      "checkout_url": {
        "type": "string"
      },
      "created_time": {
        "format": "int64",
        "type": "integer"
      },
      "cregis_id": {
        "type": "string"
      },
      "expire_time": {
        "format": "int64",
        "type": "integer"
      },
      "merchant_logo_url": {
        "type": "string"
      },
      "merchant_name": {
        "type": "string"
      },
      "order_amount": {
        "type": "string"
      },
      "order_currency": {
        "type": "string"
      },
      "payment_info": {
        "items": {
          "$ref": "#/components/schemas/PaymentInfo"
        },
        "type": "array"
      }
    },
    "type": "object"
  },
  "OrderDetails": {
    "properties": {
      "items": {
        "items": {
          "$ref": "#/components/schemas/OrderItem"
        },
        "type": "array"
      },
      "shopping_cost": {
        "type": "number"
      },
      "tax_cost": {
        "type": "number"
      }
    },
    "type": "object"
  },
  "OrderItem": {
    "properties": {
      "item_id": {
        "type": "string"
      },
      "item_name": {
        "type": "string"
      },
      "item_price": {
        "type": "number"
      },
      "item_quantity": {
        "format": "int64",
        "type": "integer"
      },
      "price_currency": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "OrderSettlementDetail": {
    "properties": {
      "actual_settlement_amount": {
        "type": "string"
      },
      "settlement_amount": {
        "type": "string"
      },
      "settlement_fee": {
        "type": "string"
      },
      "status": {
        "enum": [
          "unsettled",
          "settling",
          "settled"
        ],
        "type": "string"
      }
    },
    "type": "object"
  },
  "PaymentCallbackEnvelope": {
    "properties": {
      "data": {
        "oneOf": [
          {
            "$ref": "#/components/schemas/PaymentCompletedCallbackData"
          },
          {
            "$ref": "#/components/schemas/PaymentExpiredCallbackData"
          },
          {
            "$ref": "#/components/schemas/PaymentRefundedCallbackData"
          },
          {
            "$ref": "#/components/schemas/PaymentRemainingCallbackData"
          }
        ]
      },
      "event_name": {
        "enum": [
          "order"
        ],
        "type": "string"
      },
      "event_type": {
        "enum": [
          "expired",
          "paid",
          "paid_partial",
          "paid_over",
          "refunded",
          "paid_remain"
        ],
        "type": "string"
      },
      "nonce": {
        "type": "string"
      },
      "pid": {
        "format": "int64",
        "type": "integer"
      },
      "sign": {
        "type": "string"
      },
      "timestamp": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "pid",
      "nonce",
      "timestamp",
      "sign",
      "event_name",
      "event_type",
      "data"
    ],
    "type": "object",
    "x-cregis-event-payload": {
      "eventProperty": "event_type",
      "payloadProperty": "data",
      "mapping": {
        "expired": "#/components/schemas/PaymentExpiredCallbackData",
        "paid": "#/components/schemas/PaymentCompletedCallbackData",
        "paid_over": "#/components/schemas/PaymentCompletedCallbackData",
        "paid_partial": "#/components/schemas/PaymentCompletedCallbackData",
        "paid_remain": "#/components/schemas/PaymentRemainingCallbackData",
        "refunded": "#/components/schemas/PaymentRefundedCallbackData"
      }
    }
  },
  "PaymentCompletedCallbackData": {
    "properties": {
      "cancel_time": {
        "format": "int64",
        "type": "integer"
      },
      "created_time": {
        "format": "int64",
        "type": "integer"
      },
      "cregis_id": {
        "type": "string"
      },
      "exchange_rate": {
        "type": "string"
      },
      "order_amount": {
        "type": "string"
      },
      "order_currency": {
        "type": "string"
      },
      "order_id": {
        "type": "string"
      },
      "pay_amount": {
        "type": "string"
      },
      "pay_currency": {
        "type": "string"
      },
      "payer_email": {
        "type": "string"
      },
      "payer_id": {
        "type": "string"
      },
      "payer_name": {
        "type": "string"
      },
      "payment_address": {
        "type": "string"
      },
      "receive_amount": {
        "type": "string"
      },
      "receive_currency": {
        "type": "string"
      },
      "remark": {
        "type": "string"
      },
      "status": {
        "enum": [
          "new",
          "paid",
          "expired",
          "paid_over",
          "paid_partial",
          "canceled"
        ],
        "type": "string"
      },
      "transact_time": {
        "format": "int64",
        "type": "integer"
      },
      "tx_id": {
        "type": "string"
      },
      "valid_time": {
        "format": "int32",
        "type": "integer"
      }
    },
    "required": [
      "cregis_id",
      "order_id",
      "status",
      "payment_address",
      "receive_amount",
      "receive_currency",
      "pay_amount",
      "pay_currency",
      "exchange_rate",
      "tx_id",
      "transact_time"
    ],
    "type": "object"
  },
  "PaymentDetail": {
    "properties": {
      "blockchain": {
        "type": "string"
      },
      "exchange_rate": {
        "type": "string"
      },
      "from_address": {
        "type": "string"
      },
      "pay_amount": {
        "type": "string"
      },
      "pay_currency": {
        "type": "string"
      },
      "payment_address": {
        "type": "string"
      },
      "receive_amount": {
        "type": "string"
      },
      "receive_currency": {
        "type": "string"
      },
      "token_name": {
        "type": "string"
      },
      "tx_id": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "PaymentExpiredCallbackData": {
    "properties": {
      "cancel_time": {
        "format": "int64",
        "type": "integer"
      },
      "created_time": {
        "format": "int64",
        "type": "integer"
      },
      "cregis_id": {
        "type": "string"
      },
      "order_amount": {
        "type": "string"
      },
      "order_currency": {
        "type": "string"
      },
      "order_id": {
        "type": "string"
      },
      "payer_email": {
        "type": "string"
      },
      "payer_id": {
        "type": "string"
      },
      "payer_name": {
        "type": "string"
      },
      "remark": {
        "type": "string"
      },
      "status": {
        "enum": [
          "new",
          "paid",
          "expired",
          "paid_over",
          "paid_partial",
          "canceled"
        ],
        "type": "string"
      },
      "valid_time": {
        "format": "int32",
        "type": "integer"
      }
    },
    "required": [
      "cregis_id",
      "order_id",
      "status"
    ],
    "type": "object"
  },
  "PaymentInfo": {
    "properties": {
      "asset_logo": {
        "type": "string"
      },
      "blockchain": {
        "type": "string"
      },
      "consolidated_qrcodes": {
        "items": {
          "$ref": "#/components/schemas/PaymentInfoConsolidatedQrcodesItem"
        },
        "nullable": true,
        "type": "array"
      },
      "exchange_rate": {
        "type": "string"
      },
      "logo_url": {
        "type": "string"
      },
      "payment_address": {
        "type": "string"
      },
      "receive_amount": {
        "type": "string"
      },
      "receive_currency": {
        "type": "string"
      },
      "token_decimals": {
        "type": "integer"
      },
      "token_name": {
        "type": "string"
      },
      "token_symbol": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "PaymentInfoConsolidatedQrcodesItem": {
    "properties": {
      "qrcode": {
        "type": "string"
      },
      "wallet_icon": {
        "type": "string"
      },
      "wallet_name": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "PaymentRefundedCallbackData": {
    "properties": {
      "actual_refund_amount": {
        "type": "string"
      },
      "cancel_time": {
        "format": "int64",
        "type": "integer"
      },
      "created_time": {
        "format": "int64",
        "type": "integer"
      },
      "cregis_id": {
        "type": "string"
      },
      "exchange_rate": {
        "type": "string"
      },
      "order_amount": {
        "type": "string"
      },
      "order_currency": {
        "type": "string"
      },
      "order_id": {
        "type": "string"
      },
      "pay_amount": {
        "type": "string"
      },
      "pay_currency": {
        "type": "string"
      },
      "payer_email": {
        "type": "string"
      },
      "payer_id": {
        "type": "string"
      },
      "payer_name": {
        "type": "string"
      },
      "payment_address": {
        "type": "string"
      },
      "receive_amount": {
        "type": "string"
      },
      "receive_currency": {
        "type": "string"
      },
      "refund_address": {
        "type": "string"
      },
      "refund_amount": {
        "type": "string"
      },
      "refund_created_time": {
        "format": "int64",
        "type": "integer"
      },
      "refund_currency": {
        "type": "string"
      },
      "refund_fee": {
        "type": "string"
      },
      "refund_id": {
        "type": "string"
      },
      "refund_requested": {
        "enum": [
          "yes",
          "no"
        ],
        "type": "string"
      },
      "refund_status": {
        "enum": [
          0,
          1,
          2
        ],
        "type": "integer"
      },
      "refund_transact_time": {
        "format": "int64",
        "type": "integer"
      },
      "refund_tx_id": {
        "type": "string"
      },
      "remark": {
        "type": "string"
      },
      "status": {
        "enum": [
          "new",
          "paid",
          "expired",
          "paid_over",
          "paid_partial",
          "canceled"
        ],
        "type": "string"
      },
      "transact_time": {
        "format": "int64",
        "type": "integer"
      },
      "tx_id": {
        "type": "string"
      },
      "type": {
        "enum": [
          0,
          1
        ],
        "type": "integer"
      },
      "valid_time": {
        "format": "int32",
        "type": "integer"
      }
    },
    "required": [
      "cregis_id",
      "order_id",
      "status",
      "payment_address",
      "receive_amount",
      "receive_currency",
      "pay_amount",
      "pay_currency",
      "exchange_rate",
      "tx_id",
      "transact_time"
    ],
    "type": "object"
  },
  "PaymentRemainingCallbackData": {
    "properties": {
      "additional_pay_amount": {
        "type": "string"
      },
      "additional_pay_currency": {
        "type": "string"
      },
      "additional_payment_address": {
        "type": "string"
      },
      "additional_payment_transact_time": {
        "format": "int64",
        "type": "integer"
      },
      "additional_payment_tx_id": {
        "type": "string"
      },
      "cancel_time": {
        "format": "int64",
        "type": "integer"
      },
      "created_time": {
        "format": "int64",
        "type": "integer"
      },
      "cregis_id": {
        "type": "string"
      },
      "exchange_rate": {
        "type": "string"
      },
      "order_amount": {
        "type": "string"
      },
      "order_currency": {
        "type": "string"
      },
      "order_id": {
        "type": "string"
      },
      "pay_amount": {
        "type": "string"
      },
      "pay_currency": {
        "type": "string"
      },
      "payer_email": {
        "type": "string"
      },
      "payer_id": {
        "type": "string"
      },
      "payer_name": {
        "type": "string"
      },
      "payment_address": {
        "type": "string"
      },
      "receive_amount": {
        "type": "string"
      },
      "receive_currency": {
        "type": "string"
      },
      "remark": {
        "type": "string"
      },
      "status": {
        "enum": [
          "new",
          "paid",
          "expired",
          "paid_over",
          "paid_partial",
          "canceled"
        ],
        "type": "string"
      },
      "transact_time": {
        "format": "int64",
        "type": "integer"
      },
      "tx_id": {
        "type": "string"
      },
      "valid_time": {
        "format": "int32",
        "type": "integer"
      }
    },
    "required": [
      "cregis_id",
      "order_id",
      "status",
      "payment_address",
      "receive_amount",
      "receive_currency",
      "pay_amount",
      "pay_currency",
      "exchange_rate",
      "tx_id",
      "transact_time"
    ],
    "type": "object"
  },
  "QueryOrderDetails": {
    "properties": {
      "items": {
        "items": {
          "$ref": "#/components/schemas/QueryOrderItem"
        },
        "nullable": true,
        "type": "array"
      },
      "shopping_cost": {
        "nullable": true,
        "type": "number"
      },
      "tax_cost": {
        "nullable": true,
        "type": "number"
      }
    },
    "type": "object"
  },
  "QueryOrderItem": {
    "properties": {
      "item_id": {
        "type": "string"
      },
      "item_name": {
        "type": "string"
      },
      "item_price": {
        "type": "number"
      },
      "item_quantity": {
        "format": "int64",
        "type": "integer"
      },
      "price_currency": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "QueryOrderRequest": {
    "properties": {
      "cregis_id": {
        "type": "string"
      }
    },
    "required": [
      "cregis_id"
    ],
    "type": "object"
  },
  "QueryOrderResponse": {
    "properties": {
      "cancel_time": {
        "format": "int64",
        "nullable": true,
        "type": "integer"
      },
      "created_time": {
        "format": "int64",
        "type": "integer"
      },
      "cregis_id": {
        "type": "string"
      },
      "order_amount": {
        "type": "string"
      },
      "order_currency": {
        "type": "string"
      },
      "order_details": {
        "$ref": "#/components/schemas/QueryOrderDetails"
      },
      "order_id": {
        "type": "string"
      },
      "payer_email": {
        "nullable": true,
        "type": "string"
      },
      "payer_id": {
        "type": "string"
      },
      "payer_name": {
        "type": "string"
      },
      "payment_detail": {
        "items": {
          "$ref": "#/components/schemas/PaymentDetail"
        },
        "nullable": true,
        "type": "array"
      },
      "payment_info": {
        "items": {
          "$ref": "#/components/schemas/PaymentInfo"
        },
        "type": "array"
      },
      "refund_data": {
        "$ref": "#/components/schemas/RefundData"
      },
      "refund_requested": {
        "enum": [
          "yes",
          "no"
        ],
        "type": "string"
      },
      "remark": {
        "type": "string"
      },
      "settlement_details": {
        "$ref": "#/components/schemas/SettlementDetails",
        "nullable": true
      },
      "settlement_status": {
        "enum": [
          "unsettled",
          "settling",
          "settled"
        ],
        "type": "string"
      },
      "settlement_type": {
        "enum": [
          "",
          "system",
          "manual"
        ],
        "type": "string"
      },
      "status": {
        "enum": [
          "new",
          "paid",
          "expired",
          "paid_over",
          "paid_partial",
          "canceled"
        ],
        "type": "string"
      },
      "sub_merchant": {
        "$ref": "#/components/schemas/QuerySubMerchant"
      },
      "transact_time": {
        "format": "int64",
        "nullable": true,
        "type": "integer"
      },
      "valid_time": {
        "format": "int32",
        "type": "integer"
      }
    },
    "type": "object"
  },
  "QuerySubMerchant": {
    "properties": {
      "sub_merchant_id": {
        "nullable": true,
        "type": "string"
      },
      "sub_merchant_name": {
        "nullable": true,
        "type": "string"
      }
    },
    "type": "object"
  },
  "RefundData": {
    "nullable": true,
    "properties": {
      "actual_refund_amount": {
        "type": "string"
      },
      "cregis_id": {
        "type": "string"
      },
      "network": {
        "type": "string"
      },
      "recipient_address": {
        "type": "string"
      },
      "recipient_email": {
        "type": "string"
      },
      "recipient_id": {
        "type": "string"
      },
      "recipient_name": {
        "type": "string"
      },
      "reference_id": {
        "type": "string"
      },
      "refund_amount": {
        "type": "string"
      },
      "refund_created_time": {
        "format": "int64",
        "type": "integer"
      },
      "refund_fee": {
        "type": "string"
      },
      "refund_id": {
        "type": "string"
      },
      "refund_status": {
        "enum": [
          0,
          1,
          2
        ],
        "type": "integer"
      },
      "refund_transact_time": {
        "format": "int64",
        "type": "integer"
      },
      "refund_tx_id": {
        "type": "string"
      },
      "token": {
        "type": "string"
      },
      "type": {
        "enum": [
          1,
          2
        ],
        "type": "integer"
      }
    },
    "type": "object"
  },
  "SettlementDetails": {
    "properties": {
      "created_time": {
        "format": "int64",
        "type": "integer"
      },
      "from_address": {
        "type": "string"
      },
      "id": {
        "type": "string"
      },
      "order_count": {
        "format": "int64",
        "type": "integer"
      },
      "order_settlement_detail": {
        "$ref": "#/components/schemas/OrderSettlementDetail"
      },
      "settlement_currency": {
        "type": "string"
      },
      "to_address": {
        "type": "string"
      },
      "total_actual_settlement_amount": {
        "type": "string"
      },
      "total_settlement_amount": {
        "type": "string"
      },
      "total_settlement_fee": {
        "type": "string"
      },
      "tx_id": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "SubMerchant": {
    "properties": {
      "sub_merchant_id": {
        "type": "string"
      },
      "sub_merchant_name": {
        "type": "string"
      }
    },
    "type": "object"
  }
} as const;
