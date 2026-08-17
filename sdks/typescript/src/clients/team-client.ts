import { CregisTeamClientBase } from "../core/http.js";
import type { RequestOptions, TeamClientOptions } from "../core/types.js";
import type {
  ListTeamWalletAddressesRequest,
  ListTeamWalletAddressesResponse,
  ListTeamWalletsRequest,
  ListTeamWalletsResponse,
  QueryTeamWalletAddressBalanceRequest,
  QueryTeamWalletAddressBalanceResponse,
  QueryTeamWalletBalanceRequest,
  QueryTeamWalletBalanceResponse,
  QueryTeamWalletHistoryTransactionsRequest,
  QueryTeamWalletHistoryTransactionsResponse,
  QueryTeamWalletProcessingTransactionsRequest,
  QueryTeamWalletProcessingTransactionsResponse,
} from "../generated/team/index.js";
import { teamOperations } from "../generated/team/operations.js";

export class CregisTeamClient extends CregisTeamClientBase {
  public constructor(options: TeamClientOptions) {
    super(options);
  }

  public listTeamWallets(
    request: ListTeamWalletsRequest,
    options?: RequestOptions,
  ): Promise<ListTeamWalletsResponse> {
    return this.post(teamOperations.listTeamWallets, request, options);
  }

  public listTeamWalletAddresses(
    request: ListTeamWalletAddressesRequest,
    options?: RequestOptions,
  ): Promise<ListTeamWalletAddressesResponse> {
    return this.post(teamOperations.listTeamWalletAddresses, request, options);
  }

  public queryTeamWalletBalance(
    request: QueryTeamWalletBalanceRequest,
    options?: RequestOptions,
  ): Promise<QueryTeamWalletBalanceResponse> {
    return this.post(teamOperations.queryTeamWalletBalance, request, options);
  }

  public queryTeamWalletAddressBalance(
    request: QueryTeamWalletAddressBalanceRequest,
    options?: RequestOptions,
  ): Promise<QueryTeamWalletAddressBalanceResponse> {
    return this.post(teamOperations.queryTeamWalletAddressBalance, request, options);
  }

  public queryTeamWalletHistoryTransactions(
    request: QueryTeamWalletHistoryTransactionsRequest,
    options?: RequestOptions,
  ): Promise<QueryTeamWalletHistoryTransactionsResponse> {
    return this.post(teamOperations.queryTeamWalletHistoryTransactions, request, options);
  }

  public queryTeamWalletProcessingTransactions(
    request: QueryTeamWalletProcessingTransactionsRequest,
    options?: RequestOptions,
  ): Promise<QueryTeamWalletProcessingTransactionsResponse> {
    return this.post(teamOperations.queryTeamWalletProcessingTransactions, request, options);
  }
}
