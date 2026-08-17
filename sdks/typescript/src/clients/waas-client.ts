import { CregisProjectClientBase } from "../core/http.js";
import type { ProjectClientOptions, RequestOptions } from "../core/types.js";
import type {
  AddressBalanceRequest,
  AddressBalanceResponse,
  AddressBalanceV2Request,
  AddressBalanceV2Response,
  AddressUpdateRequest,
  BalanceCollectRequest,
  BalanceCollectResponse,
  BatchGenerateAddressRequest,
  CheckAddressLegalityRequest,
  CheckAddressLegalityResponse,
  GenerateAddressRequest,
  GenerateAddressResponse,
  GeneratedAddress,
  PayoutRequest,
  PayoutResponse,
  PayoutV1Request,
  ProjectCoinQueryResponse,
  QueryPayoutRequest,
  QueryPayoutResponse,
  QueryWithdrawalRequest,
  QueryWithdrawalResponse,
  TradeRecordQueryRequest,
  TradeRecordQueryResponse,
  ValidateAddressRequest,
  ValidateAddressResponse,
  WithdrawalRequest,
  WithdrawalResponse,
} from "../generated/waas/index.js";
import { waasOperations } from "../generated/waas/operations.js";

export class CregisWaasClient extends CregisProjectClientBase {
  public constructor(options: ProjectClientOptions) {
    super(options);
  }

  public generateAddress(
    request: GenerateAddressRequest,
    options?: RequestOptions,
  ): Promise<GenerateAddressResponse> {
    return this.post(waasOperations.createAddress, request, options);
  }

  public batchGenerateAddress(
    request: BatchGenerateAddressRequest,
    options?: RequestOptions,
  ): Promise<GeneratedAddress[]> {
    return this.post(waasOperations.batchCreateAddress, request, options);
  }

  public async updateAddress(
    request: AddressUpdateRequest,
    options?: RequestOptions,
  ): Promise<void> {
    await this.post<unknown>(waasOperations.updateAddress, request, options);
  }

  public validateAddress(
    request: ValidateAddressRequest,
    options?: RequestOptions,
  ): Promise<ValidateAddressResponse> {
    return this.post(waasOperations.verifyAddress, request, options);
  }

  public checkAddressLegality(
    request: CheckAddressLegalityRequest,
    options?: RequestOptions,
  ): Promise<CheckAddressLegalityResponse> {
    return this.post(waasOperations.validateAddressFormat, request, options);
  }

  public payoutV1(
    request: PayoutV1Request,
    options?: RequestOptions,
  ): Promise<PayoutResponse> {
    return this.post(waasOperations.initiatePayout, request, options);
  }

  public payoutV2(
    request: PayoutRequest,
    options?: RequestOptions,
  ): Promise<PayoutResponse> {
    return this.post(waasOperations.initiatePayoutV2, request, options);
  }

  /** @deprecated Use {@link payoutV2} to make the API version explicit. */
  public payout(
    request: PayoutRequest,
    options?: RequestOptions,
  ): Promise<PayoutResponse> {
    return this.payoutV2(request, options);
  }

  public withdrawal(
    request: WithdrawalRequest,
    options?: RequestOptions,
  ): Promise<WithdrawalResponse> {
    return this.post(waasOperations.subAddressWithdrawal, request, options);
  }

  public balanceCollect(
    request: BalanceCollectRequest,
    options?: RequestOptions,
  ): Promise<BalanceCollectResponse> {
    return this.post(waasOperations.initiateCollection, request, options);
  }

  public queryProjectCoins(options?: RequestOptions): Promise<ProjectCoinQueryResponse> {
    return this.post(waasOperations.getProjectCoins, {}, options);
  }

  public queryTradeRecords(
    request: TradeRecordQueryRequest,
    options?: RequestOptions,
  ): Promise<TradeRecordQueryResponse> {
    return this.post(waasOperations.tradePage, request, options);
  }

  public queryPayout(
    request: QueryPayoutRequest,
    options?: RequestOptions,
  ): Promise<QueryPayoutResponse> {
    return this.post(waasOperations.queryPayout, request, options);
  }

  public queryWithdrawal(
    request: QueryWithdrawalRequest,
    options?: RequestOptions,
  ): Promise<QueryWithdrawalResponse> {
    return this.post(waasOperations.querySubAddressWithdrawal, request, options);
  }

  public queryAddressBalance(
    request: AddressBalanceRequest,
    options?: RequestOptions,
  ): Promise<AddressBalanceResponse> {
    return this.post(waasOperations.subAddressBalancePage, request, options);
  }

  public queryAddressBalanceV2(
    request: AddressBalanceV2Request,
    options?: RequestOptions,
  ): Promise<AddressBalanceV2Response> {
    return this.post(waasOperations.querySubAddressBalanceV2, request, options);
  }
}
