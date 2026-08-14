// Generated from the canonical Cregis OpenAPI specification. Do not edit.
export const waasSchemas = {
  "AddressBalance": {
    "properties": {
      "address": {
        "type": "string"
      },
      "available": {
        "type": "string"
      },
      "currency": {
        "type": "string"
      },
      "pid": {
        "format": "int64",
        "type": "integer"
      },
      "processing": {
        "type": "string"
      },
      "total": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "AddressBalanceRequest": {
    "properties": {
      "address": {
        "type": "string"
      },
      "currency": {
        "type": "string"
      },
      "maximum_balance": {
        "type": "string"
      },
      "minimum_balance": {
        "type": "string"
      },
      "page_num": {
        "format": "int32",
        "type": "integer"
      },
      "page_size": {
        "format": "int32",
        "type": "integer"
      }
    },
    "required": [
      "currency"
    ],
    "type": "object"
  },
  "AddressBalanceResponse": {
    "properties": {
      "pageNum": {
        "format": "int32",
        "type": "integer"
      },
      "pageSize": {
        "format": "int32",
        "type": "integer"
      },
      "rows": {
        "items": {
          "$ref": "#/components/schemas/AddressBalance"
        },
        "type": "array"
      },
      "total": {
        "format": "int64",
        "type": "integer"
      }
    },
    "type": "object"
  },
  "AddressBalanceV2": {
    "properties": {
      "address": {
        "type": "string"
      },
      "available": {
        "type": "string"
      },
      "chain_id": {
        "type": "string"
      },
      "processing": {
        "type": "string"
      },
      "token_id": {
        "type": "string"
      },
      "total": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "AddressBalanceV2Request": {
    "properties": {
      "address": {
        "type": "string"
      },
      "currency": {
        "type": "string"
      },
      "maximum_balance": {
        "type": "string"
      },
      "minimum_balance": {
        "type": "string"
      },
      "page_num": {
        "format": "int32",
        "type": "integer"
      },
      "page_size": {
        "format": "int32",
        "maximum": 100,
        "type": "integer"
      }
    },
    "required": [
      "address"
    ],
    "type": "object"
  },
  "AddressBalanceV2Response": {
    "properties": {
      "pageNum": {
        "format": "int32",
        "type": "integer"
      },
      "pageSize": {
        "format": "int32",
        "type": "integer"
      },
      "rows": {
        "items": {
          "$ref": "#/components/schemas/AddressBalanceV2"
        },
        "type": "array"
      },
      "total": {
        "format": "int64",
        "type": "integer"
      }
    },
    "type": "object"
  },
  "AddressDepositCallbackNotification": {
    "properties": {
      "address": {
        "type": "string"
      },
      "amount": {
        "type": "string"
      },
      "block_height": {
        "type": "string"
      },
      "block_time": {
        "type": "string"
      },
      "chain_id": {
        "type": "string"
      },
      "cid": {
        "format": "int64",
        "type": "integer"
      },
      "currency": {
        "type": "string"
      },
      "memo": {
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
      "status": {
        "enum": [
          "1",
          "2"
        ],
        "type": "string"
      },
      "timestamp": {
        "format": "int64",
        "type": "integer"
      },
      "token_id": {
        "type": "string"
      },
      "txid": {
        "type": "string"
      }
    },
    "required": [
      "pid",
      "cid",
      "chain_id",
      "token_id",
      "currency",
      "address",
      "amount",
      "status",
      "txid",
      "nonce",
      "timestamp",
      "sign"
    ],
    "type": "object"
  },
  "AddressUpdateRequest": {
    "properties": {
      "address": {
        "type": "string"
      },
      "alias": {
        "type": "string"
      },
      "callback_url": {
        "type": "string"
      },
      "status": {
        "enum": [
          "0",
          "1"
        ],
        "type": "string"
      }
    },
    "required": [
      "address"
    ],
    "type": "object",
    "anyOf": [
      {
        "required": [
          "alias"
        ]
      },
      {
        "required": [
          "callback_url"
        ]
      },
      {
        "required": [
          "status"
        ]
      }
    ]
  },
  "BalanceCollectRequest": {
    "properties": {
      "amount": {
        "type": "string"
      },
      "currency": {
        "type": "string"
      },
      "from_address": {
        "type": "string"
      },
      "to_address": {
        "type": "string"
      }
    },
    "required": [
      "currency",
      "from_address",
      "to_address"
    ],
    "type": "object"
  },
  "BalanceCollectResponse": {
    "properties": {
      "cid": {
        "format": "int64",
        "type": "integer"
      }
    },
    "type": "object"
  },
  "BatchGenerateAddressRequest": {
    "properties": {
      "alias": {
        "maxLength": 40,
        "minLength": 1,
        "type": "string"
      },
      "callback_url": {
        "type": "string"
      },
      "chain_id": {
        "type": "string"
      },
      "number": {
        "pattern": "^(?:[1-9]|[1-9][0-9]|100)$",
        "type": "string"
      }
    },
    "required": [
      "chain_id",
      "number"
    ],
    "type": "object"
  },
  "CheckAddressLegalityRequest": {
    "properties": {
      "address": {
        "type": "string"
      },
      "chain_id": {
        "type": "string"
      }
    },
    "required": [
      "address",
      "chain_id"
    ],
    "type": "object"
  },
  "CheckAddressLegalityResponse": {
    "properties": {
      "result": {
        "type": "boolean"
      }
    },
    "type": "object"
  },
  "GenerateAddressRequest": {
    "properties": {
      "alias": {
        "type": "string"
      },
      "callback_url": {
        "type": "string"
      },
      "chain_id": {
        "type": "string"
      }
    },
    "required": [
      "chain_id"
    ],
    "type": "object"
  },
  "GenerateAddressResponse": {
    "properties": {
      "address": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "GeneratedAddress": {
    "properties": {
      "address": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "PayoutCallbackNotification": {
    "properties": {
      "address": {
        "type": "string"
      },
      "amount": {
        "type": "string"
      },
      "block_height": {
        "type": "string"
      },
      "block_time": {
        "format": "int64",
        "type": "integer"
      },
      "chain_id": {
        "type": "string"
      },
      "cid": {
        "format": "int64",
        "type": "integer"
      },
      "currency": {
        "type": "string"
      },
      "memo": {
        "type": "string"
      },
      "nonce": {
        "type": "string"
      },
      "pid": {
        "format": "int64",
        "type": "integer"
      },
      "remark": {
        "type": "string"
      },
      "sign": {
        "type": "string"
      },
      "status": {
        "enum": [
          2,
          4,
          6,
          7
        ],
        "format": "int32",
        "type": "integer"
      },
      "third_party_id": {
        "type": "string"
      },
      "timestamp": {
        "format": "int64",
        "type": "integer"
      },
      "token_id": {
        "type": "string"
      },
      "txid": {
        "type": "string"
      }
    },
    "required": [
      "pid",
      "cid",
      "chain_id",
      "token_id",
      "currency",
      "address",
      "amount",
      "third_party_id",
      "status",
      "nonce",
      "timestamp",
      "sign"
    ],
    "type": "object"
  },
  "PayoutExternalVerificationCallbackNotification": {
    "properties": {
      "amount": {
        "type": "string"
      },
      "chain_id": {
        "type": "string"
      },
      "cid": {
        "format": "int64",
        "type": "integer"
      },
      "from_address": {
        "type": "string"
      },
      "memo": {
        "type": "string"
      },
      "nonce": {
        "type": "string"
      },
      "pid": {
        "format": "int64",
        "type": "integer"
      },
      "remark": {
        "type": "string"
      },
      "sign": {
        "type": "string"
      },
      "third_party_id": {
        "type": "string"
      },
      "timestamp": {
        "format": "int64",
        "type": "integer"
      },
      "to_address": {
        "type": "string"
      },
      "token_id": {
        "type": "string"
      }
    },
    "required": [
      "pid",
      "cid",
      "third_party_id",
      "chain_id",
      "token_id",
      "from_address",
      "to_address",
      "amount",
      "nonce",
      "timestamp",
      "sign"
    ],
    "type": "object"
  },
  "PayoutRequest": {
    "properties": {
      "amount": {
        "type": "string"
      },
      "callback_url": {
        "type": "string"
      },
      "currency": {
        "type": "string"
      },
      "from_address": {
        "type": "string"
      },
      "memo": {
        "type": "string"
      },
      "remark": {
        "type": "string"
      },
      "third_party_id": {
        "type": "string"
      },
      "to_address": {
        "type": "string"
      },
      "wallet_id": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "currency",
      "to_address",
      "amount",
      "third_party_id"
    ],
    "type": "object"
  },
  "PayoutResponse": {
    "properties": {
      "cid": {
        "format": "int64",
        "type": "integer"
      }
    },
    "type": "object"
  },
  "PayoutV1Request": {
    "properties": {
      "address": {
        "type": "string"
      },
      "amount": {
        "type": "string"
      },
      "callback_url": {
        "type": "string"
      },
      "currency": {
        "type": "string"
      },
      "memo": {
        "type": "string"
      },
      "remark": {
        "type": "string"
      },
      "third_party_id": {
        "type": "string"
      }
    },
    "required": [
      "currency",
      "address",
      "amount",
      "third_party_id"
    ],
    "type": "object"
  },
  "ProjectCoin": {
    "properties": {
      "chain_id": {
        "type": "string"
      },
      "coin_name": {
        "type": "string"
      },
      "decimals": {
        "type": "string"
      },
      "token_id": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "ProjectCoinQueryResponse": {
    "properties": {
      "address_coins": {
        "items": {
          "$ref": "#/components/schemas/ProjectCoin"
        },
        "type": "array"
      },
      "order_coins": {
        "items": {
          "$ref": "#/components/schemas/ProjectCoinQueryResponseOrderCoinsItem"
        },
        "nullable": true,
        "type": "array"
      },
      "payout_coins": {
        "items": {
          "$ref": "#/components/schemas/ProjectCoin"
        },
        "type": "array"
      }
    },
    "type": "object"
  },
  "ProjectCoinQueryResponseOrderCoinsItem": {
    "properties": {
      "chain_id": {
        "type": "string"
      },
      "coin_name": {
        "type": "string"
      },
      "decimals": {
        "type": "string"
      },
      "token_id": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "QueryPayoutRequest": {
    "properties": {
      "cid": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "cid"
    ],
    "type": "object"
  },
  "QueryPayoutResponse": {
    "properties": {
      "address": {
        "type": "string"
      },
      "amount": {
        "type": "string"
      },
      "block_height": {
        "nullable": true,
        "type": "string"
      },
      "block_time": {
        "format": "int64",
        "nullable": true,
        "type": "integer"
      },
      "chain_id": {
        "type": "string"
      },
      "currency": {
        "type": "string"
      },
      "from_address": {
        "type": "string"
      },
      "memo": {
        "nullable": true,
        "type": "string"
      },
      "pid": {
        "format": "int64",
        "type": "integer"
      },
      "remark": {
        "type": "string"
      },
      "status": {
        "enum": [
          0,
          1,
          2,
          3,
          4,
          5,
          6,
          7
        ],
        "type": "integer"
      },
      "third_party_id": {
        "type": "string"
      },
      "token_id": {
        "type": "string"
      },
      "txid": {
        "nullable": true,
        "type": "string"
      }
    },
    "type": "object"
  },
  "QueryWithdrawalRequest": {
    "properties": {
      "cid": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "cid"
    ],
    "type": "object"
  },
  "QueryWithdrawalResponse": {
    "properties": {
      "amount": {
        "type": "string"
      },
      "block_height": {
        "nullable": true,
        "type": "string"
      },
      "block_time": {
        "nullable": true,
        "type": "string"
      },
      "chain_id": {
        "type": "string"
      },
      "currency": {
        "type": "string"
      },
      "from_address": {
        "type": "string"
      },
      "memo": {
        "type": "string"
      },
      "pid": {
        "format": "int64",
        "type": "integer"
      },
      "remark": {
        "type": "string"
      },
      "status": {
        "enum": [
          0,
          1,
          2,
          3,
          4,
          5,
          6,
          7
        ],
        "type": "integer"
      },
      "third_party_id": {
        "type": "string"
      },
      "to_address": {
        "type": "string"
      },
      "token_id": {
        "type": "string"
      },
      "txid": {
        "nullable": true,
        "type": "string"
      }
    },
    "type": "object"
  },
  "TradeRecord": {
    "properties": {
      "amount": {
        "type": "string"
      },
      "block_height": {
        "type": "string"
      },
      "block_time": {
        "format": "int64",
        "type": "integer"
      },
      "business_type": {
        "type": "string"
      },
      "chain_id": {
        "type": "string"
      },
      "cid": {
        "format": "int64",
        "type": "integer"
      },
      "currency": {
        "type": "string"
      },
      "fee": {
        "type": "string"
      },
      "from_address": {
        "type": "string"
      },
      "memo": {
        "type": "string"
      },
      "pid": {
        "format": "int64",
        "type": "integer"
      },
      "remark": {
        "type": "string"
      },
      "status": {
        "enum": [
          0,
          1,
          2
        ],
        "format": "int32",
        "type": "integer"
      },
      "to_address": {
        "type": "string"
      },
      "token_id": {
        "type": "string"
      },
      "trade_type": {
        "type": "string"
      },
      "txid": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "TradeRecordQueryRequest": {
    "properties": {
      "blocktime_end": {
        "format": "int64",
        "type": "integer"
      },
      "blocktime_start": {
        "format": "int64",
        "type": "integer"
      },
      "business_type": {
        "enum": [
          0,
          2,
          3,
          4,
          5
        ],
        "format": "int32",
        "type": "integer"
      },
      "chain_id": {
        "type": "string"
      },
      "cid": {
        "format": "int64",
        "type": "integer"
      },
      "page_num": {
        "format": "int32",
        "type": "integer"
      },
      "page_size": {
        "format": "int32",
        "type": "integer"
      },
      "status": {
        "enum": [
          0,
          1,
          2
        ],
        "format": "int32",
        "type": "integer"
      },
      "token_id": {
        "type": "string"
      },
      "trade_type": {
        "enum": [
          1,
          2
        ],
        "format": "int32",
        "type": "integer"
      },
      "tx_id": {
        "type": "string"
      }
    },
    "type": "object"
  },
  "TradeRecordQueryResponse": {
    "properties": {
      "pageNum": {
        "format": "int32",
        "type": "integer"
      },
      "pageSize": {
        "format": "int32",
        "type": "integer"
      },
      "rows": {
        "items": {
          "$ref": "#/components/schemas/TradeRecord"
        },
        "type": "array"
      },
      "total": {
        "format": "int64",
        "type": "integer"
      }
    },
    "type": "object"
  },
  "ValidateAddressRequest": {
    "properties": {
      "address": {
        "type": "string"
      },
      "chain_id": {
        "type": "string"
      }
    },
    "required": [
      "address",
      "chain_id"
    ],
    "type": "object"
  },
  "ValidateAddressResponse": {
    "properties": {
      "result": {
        "type": "boolean"
      }
    },
    "type": "object"
  },
  "WithdrawalCallbackNotification": {
    "properties": {
      "amount": {
        "type": "string"
      },
      "block_height": {
        "type": "string"
      },
      "block_time": {
        "format": "int64",
        "type": "integer"
      },
      "chain_id": {
        "type": "string"
      },
      "cid": {
        "format": "int64",
        "type": "integer"
      },
      "currency": {
        "type": "string"
      },
      "from_address": {
        "type": "string"
      },
      "memo": {
        "type": "string"
      },
      "nonce": {
        "type": "string"
      },
      "pid": {
        "format": "int64",
        "type": "integer"
      },
      "remark": {
        "type": "string"
      },
      "sign": {
        "type": "string"
      },
      "status": {
        "enum": [
          2,
          4,
          6,
          7
        ],
        "format": "int32",
        "type": "integer"
      },
      "third_party_id": {
        "type": "string"
      },
      "timestamp": {
        "format": "int64",
        "type": "integer"
      },
      "to_address": {
        "type": "string"
      },
      "token_id": {
        "type": "string"
      },
      "txid": {
        "type": "string"
      }
    },
    "required": [
      "pid",
      "cid",
      "chain_id",
      "token_id",
      "currency",
      "from_address",
      "to_address",
      "amount",
      "third_party_id",
      "status",
      "nonce",
      "timestamp",
      "sign"
    ],
    "type": "object"
  },
  "WithdrawalRequest": {
    "properties": {
      "amount": {
        "type": "string"
      },
      "callback_url": {
        "type": "string"
      },
      "currency": {
        "type": "string"
      },
      "from_address": {
        "type": "string"
      },
      "memo": {
        "type": "string"
      },
      "remark": {
        "type": "string"
      },
      "third_party_id": {
        "type": "string"
      },
      "to_address": {
        "type": "string"
      }
    },
    "required": [
      "currency",
      "from_address",
      "to_address",
      "amount",
      "third_party_id"
    ],
    "type": "object"
  },
  "WithdrawalResponse": {
    "properties": {
      "cid": {
        "format": "int64",
        "type": "integer"
      }
    },
    "type": "object"
  }
} as const;
