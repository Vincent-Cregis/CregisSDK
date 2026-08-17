import { CregisProjectClientBase } from "../core/http.js";
import type { ProjectClientOptions, RequestOptions } from "../core/types.js";
import type {
  CreateOrderRequest,
  CreateOrderResponse,
  QueryOrderRequest,
  QueryOrderResponse,
} from "../generated/payment/index.js";
import { paymentOperations } from "../generated/payment/operations.js";

export class CregisPaymentClient extends CregisProjectClientBase {
  public constructor(options: ProjectClientOptions) {
    super(options);
  }

  public createOrder(
    request: CreateOrderRequest,
    options?: RequestOptions,
  ): Promise<CreateOrderResponse> {
    return this.post(paymentOperations.createOrder, request, options);
  }

  public queryOrder(
    request: QueryOrderRequest,
    options?: RequestOptions,
  ): Promise<QueryOrderResponse> {
    return this.post(paymentOperations.queryOrder, request, options);
  }
}
