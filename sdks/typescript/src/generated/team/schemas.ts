// Generated from the canonical Cregis OpenAPI specification. Do not edit.
export const teamSchemas = {
  "ListTeamWalletAddressesRequest": {
    "properties": {
      "chain_id": {
        "type": "string"
      },
      "page_num": {
        "format": "int32",
        "minimum": 1,
        "type": "integer"
      },
      "page_size": {
        "format": "int32",
        "maximum": 100,
        "minimum": 1,
        "type": "integer"
      },
      "wallet_id": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "wallet_id",
      "chain_id"
    ],
    "type": "object"
  },
  "ListTeamWalletAddressesResponse": {
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
          "$ref": "#/components/schemas/TeamWalletAddress"
        },
        "type": "array"
      },
      "total": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "pageNum",
      "pageSize",
      "total",
      "rows"
    ],
    "type": "object"
  },
  "ListTeamWalletsRequest": {
    "properties": {
      "page_num": {
        "format": "int32",
        "minimum": 1,
        "type": "integer"
      },
      "page_size": {
        "format": "int32",
        "maximum": 100,
        "minimum": 1,
        "type": "integer"
      },
      "wallet_type": {
        "enum": [
          "single_sign",
          "multi_sign"
        ],
        "type": "string"
      }
    },
    "type": "object"
  },
  "ListTeamWalletsResponse": {
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
          "$ref": "#/components/schemas/TeamWallet"
        },
        "type": "array"
      },
      "total": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "pageNum",
      "pageSize",
      "total",
      "rows"
    ],
    "type": "object"
  },
  "QueryTeamWalletAddressBalanceRequest": {
    "properties": {
      "address": {
        "type": "string"
      },
      "chain_id": {
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
        "minimum": 1,
        "type": "integer"
      },
      "page_size": {
        "format": "int32",
        "maximum": 100,
        "minimum": 1,
        "type": "integer"
      },
      "token_id": {
        "type": "string"
      },
      "wallet_id": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "wallet_id"
    ],
    "type": "object"
  },
  "QueryTeamWalletAddressBalanceResponse": {
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
          "$ref": "#/components/schemas/TeamWalletAddressBalance"
        },
        "type": "array"
      },
      "total": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "pageNum",
      "pageSize",
      "total",
      "rows"
    ],
    "type": "object"
  },
  "QueryTeamWalletBalanceRequest": {
    "properties": {
      "chain_id": {
        "type": "string"
      },
      "page_num": {
        "format": "int32",
        "minimum": 1,
        "type": "integer"
      },
      "page_size": {
        "format": "int32",
        "maximum": 100,
        "minimum": 1,
        "type": "integer"
      },
      "token_id": {
        "type": "string"
      },
      "wallet_id": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "wallet_id"
    ],
    "type": "object"
  },
  "QueryTeamWalletBalanceResponse": {
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
          "$ref": "#/components/schemas/TeamWalletBalance"
        },
        "type": "array"
      },
      "total": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "pageNum",
      "pageSize",
      "total",
      "rows"
    ],
    "type": "object"
  },
  "QueryTeamWalletHistoryTransactionsRequest": {
    "properties": {
      "blocktime_end": {
        "format": "int64",
        "type": "integer"
      },
      "blocktime_start": {
        "format": "int64",
        "type": "integer"
      },
      "chain_id": {
        "type": "string"
      },
      "page_num": {
        "format": "int32",
        "minimum": 1,
        "type": "integer"
      },
      "page_size": {
        "format": "int32",
        "maximum": 100,
        "minimum": 1,
        "type": "integer"
      },
      "token_id": {
        "type": "string"
      },
      "transaction_status": {
        "enum": [
          1,
          2
        ],
        "format": "int32",
        "type": "integer"
      },
      "transaction_type": {
        "enum": [
          1,
          2
        ],
        "format": "int32",
        "type": "integer"
      },
      "txid": {
        "type": "string"
      },
      "wallet_id": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "wallet_id"
    ],
    "type": "object"
  },
  "QueryTeamWalletHistoryTransactionsResponse": {
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
          "$ref": "#/components/schemas/TeamWalletTransaction"
        },
        "type": "array"
      },
      "total": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "pageNum",
      "pageSize",
      "total",
      "rows"
    ],
    "type": "object"
  },
  "QueryTeamWalletProcessingTransactionsRequest": {
    "properties": {
      "chain_id": {
        "type": "string"
      },
      "page_num": {
        "format": "int32",
        "minimum": 1,
        "type": "integer"
      },
      "page_size": {
        "format": "int32",
        "maximum": 100,
        "minimum": 1,
        "type": "integer"
      },
      "token_id": {
        "type": "string"
      },
      "txid": {
        "type": "string"
      },
      "wallet_id": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "wallet_id"
    ],
    "type": "object"
  },
  "QueryTeamWalletProcessingTransactionsResponse": {
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
          "$ref": "#/components/schemas/TeamWalletProcessingTransaction"
        },
        "type": "array"
      },
      "total": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "pageNum",
      "pageSize",
      "total",
      "rows"
    ],
    "type": "object"
  },
  "TeamWallet": {
    "properties": {
      "alias": {
        "type": "string"
      },
      "create_time": {
        "format": "int64",
        "type": "integer"
      },
      "tokens": {
        "items": {
          "$ref": "#/components/schemas/TeamWalletToken"
        },
        "type": "array"
      },
      "walletType": {
        "enum": [
          "single_sign",
          "multi_sign"
        ],
        "type": "string"
      },
      "wallet_id": {
        "format": "int64",
        "type": "integer"
      },
      "wallet_status": {
        "enum": [
          "normal"
        ],
        "type": "string"
      }
    },
    "required": [
      "wallet_id",
      "walletType"
    ],
    "type": "object"
  },
  "TeamWalletAddress": {
    "properties": {
      "address": {
        "type": "string"
      },
      "address_status": {
        "enum": [
          "enable",
          "disable"
        ],
        "type": "string"
      },
      "alias": {
        "type": "string"
      },
      "create_time": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "address"
    ],
    "type": "object"
  },
  "TeamWalletAddressBalance": {
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
    "required": [
      "address",
      "chain_id",
      "token_id",
      "total",
      "available",
      "processing"
    ],
    "type": "object"
  },
  "TeamWalletBalance": {
    "properties": {
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
    "required": [
      "chain_id",
      "token_id",
      "total",
      "available",
      "processing"
    ],
    "type": "object"
  },
  "TeamWalletProcessingTransaction": {
    "properties": {
      "amount": {
        "type": "string"
      },
      "chain_id": {
        "type": "string"
      },
      "from_address": {
        "type": "string"
      },
      "status": {
        "enum": [
          0
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
      "transaction_type": {
        "enum": [
          1
        ],
        "format": "int32",
        "type": "integer"
      },
      "txid": {
        "type": "string"
      },
      "wallet_id": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "wallet_id",
      "transaction_type",
      "status",
      "chain_id",
      "token_id",
      "amount"
    ],
    "type": "object"
  },
  "TeamWalletToken": {
    "properties": {
      "chain_id": {
        "type": "string"
      },
      "chain_name": {
        "type": "string"
      },
      "token_id": {
        "type": "string"
      },
      "token_name": {
        "type": "string"
      }
    },
    "required": [
      "chain_id",
      "token_id"
    ],
    "type": "object"
  },
  "TeamWalletTransaction": {
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
      "fee": {
        "type": "string"
      },
      "from_address": {
        "type": "string"
      },
      "to_address": {
        "type": "string"
      },
      "token_id": {
        "type": "string"
      },
      "transaction_status": {
        "enum": [
          1,
          2
        ],
        "format": "int32",
        "type": "integer"
      },
      "transaction_type": {
        "enum": [
          1,
          2
        ],
        "format": "int32",
        "type": "integer"
      },
      "txid": {
        "type": "string"
      },
      "wallet_id": {
        "format": "int64",
        "type": "integer"
      }
    },
    "required": [
      "wallet_id",
      "transaction_type",
      "transaction_status",
      "chain_id",
      "token_id",
      "amount"
    ],
    "type": "object"
  }
} as const;
