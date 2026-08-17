import { assertRuntimeContract } from "../core/contract.js";
import { CregisClientError } from "../core/errors.js";
import { paymentWebhooks } from "../generated/payment/webhooks.js";
import type { PaymentCallbackNotification } from "./types.js";
import { verifyProjectWebhook } from "./validation.js";

export class CregisPaymentCallbackHandler {
  public static readonly CALLBACK_SUCCESS = "success";
  readonly #apiKey: string;

  public constructor(apiKey: string) {
    if (typeof apiKey !== "string" || apiKey.trim() === "") {
      throw new CregisClientError("API Key is required");
    }
    this.#apiKey = apiKey;
  }

  public verifyAndParse(rawBody: string): PaymentCallbackNotification {
    const payload = verifyProjectWebhook(rawBody, this.#apiKey);
    const contract = paymentWebhooks.orderCallback;
    assertRuntimeContract(payload, contract.schema, contract.schemas, "Payment callback");
    return payload as unknown as PaymentCallbackNotification;
  }
}
