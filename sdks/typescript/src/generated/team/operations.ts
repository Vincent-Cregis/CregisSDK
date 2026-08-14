// Generated from the canonical Cregis OpenAPI specification. Do not edit.
import type { GeneratedOperation } from "../../core/types.js";
import { teamSchemas } from "./schemas.js";

export const teamOperations: Readonly<Record<"listTeamWallets" | "listTeamWalletAddresses" | "queryTeamWalletBalance" | "queryTeamWalletAddressBalance" | "queryTeamWalletHistoryTransactions" | "queryTeamWalletProcessingTransactions", GeneratedOperation>> = {
  listTeamWallets: {
    method: "POST",
    operationId: "listTeamWallets",
    path: "/openapi/v1/wallets",
    requiredRequestFields: [],
    requestSchema: {"$ref":"#/components/schemas/ListTeamWalletsRequest"},
    responseSchema: {"$ref":"#/components/schemas/ListTeamWalletsResponse"},
    schemas: teamSchemas,
  },
  listTeamWalletAddresses: {
    method: "POST",
    operationId: "listTeamWalletAddresses",
    path: "/openapi/v1/wallet_address",
    requiredRequestFields: ["wallet_id","chain_id"],
    requestSchema: {"$ref":"#/components/schemas/ListTeamWalletAddressesRequest"},
    responseSchema: {"$ref":"#/components/schemas/ListTeamWalletAddressesResponse"},
    schemas: teamSchemas,
  },
  queryTeamWalletBalance: {
    method: "POST",
    operationId: "queryTeamWalletBalance",
    path: "/openapi/v1/wallet_balance",
    requiredRequestFields: ["wallet_id"],
    requestSchema: {"$ref":"#/components/schemas/QueryTeamWalletBalanceRequest"},
    responseSchema: {"$ref":"#/components/schemas/QueryTeamWalletBalanceResponse"},
    schemas: teamSchemas,
  },
  queryTeamWalletAddressBalance: {
    method: "POST",
    operationId: "queryTeamWalletAddressBalance",
    path: "/openapi/v1/wallet_address_balance",
    requiredRequestFields: ["wallet_id"],
    requestSchema: {"$ref":"#/components/schemas/QueryTeamWalletAddressBalanceRequest"},
    responseSchema: {"$ref":"#/components/schemas/QueryTeamWalletAddressBalanceResponse"},
    schemas: teamSchemas,
  },
  queryTeamWalletHistoryTransactions: {
    method: "POST",
    operationId: "queryTeamWalletHistoryTransactions",
    path: "/openapi/v1/wallet_history_transaction_info",
    requiredRequestFields: ["wallet_id"],
    requestSchema: {"$ref":"#/components/schemas/QueryTeamWalletHistoryTransactionsRequest"},
    responseSchema: {"$ref":"#/components/schemas/QueryTeamWalletHistoryTransactionsResponse"},
    schemas: teamSchemas,
  },
  queryTeamWalletProcessingTransactions: {
    method: "POST",
    operationId: "queryTeamWalletProcessingTransactions",
    path: "/openapi/v1/wallet_processing_transaction_info",
    requiredRequestFields: ["wallet_id"],
    requestSchema: {"$ref":"#/components/schemas/QueryTeamWalletProcessingTransactionsRequest"},
    responseSchema: {"$ref":"#/components/schemas/QueryTeamWalletProcessingTransactionsResponse"},
    schemas: teamSchemas,
  },
} as const;
