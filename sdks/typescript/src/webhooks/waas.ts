import { assertRuntimeContract } from "../core/contract.js";
import { CregisClientError } from "../core/errors.js";
import { waasWebhooks } from "../generated/waas/webhooks.js";
import type {
  AddressDepositCallbackNotification,
  PayoutCallbackNotification,
  PayoutExternalVerificationCallbackNotification,
  WithdrawalCallbackNotification,
} from "./types.js";
import { verifyProjectWebhook } from "./validation.js";

export class CregisWaasCallbackHandler {
  public static readonly CALLBACK_SUCCESS = "success";
  public static readonly EXTERNAL_VERIFICATION_APPROVE = "ok";
  public static readonly EXTERNAL_VERIFICATION_DENY = "deny";
  readonly #apiKey: string;

  public constructor(apiKey: string) {
    if (typeof apiKey !== "string" || apiKey.trim() === "") {
      throw new CregisClientError("API Key is required");
    }
    this.#apiKey = apiKey;
  }

  public handleDepositCallback(rawBody: string): AddressDepositCallbackNotification {
    return this.#verify<AddressDepositCallbackNotification>(
      rawBody,
      waasWebhooks.depositCallback,
      "WaaS deposit callback",
    );
  }

  public handlePayoutCallback(rawBody: string): PayoutCallbackNotification {
    return this.#verify<PayoutCallbackNotification>(
      rawBody,
      waasWebhooks.payoutCallback,
      "WaaS payout callback",
    );
  }

  public handlePayoutExternalVerificationCallback(
    rawBody: string,
  ): PayoutExternalVerificationCallbackNotification {
    return this.#verify<PayoutExternalVerificationCallbackNotification>(
      rawBody,
      waasWebhooks.payoutExternalVerificationCallback,
      "WaaS payout external verification callback",
    );
  }

  public handleWithdrawalCallback(rawBody: string): WithdrawalCallbackNotification {
    return this.#verify<WithdrawalCallbackNotification>(
      rawBody,
      waasWebhooks.withdrawalCallback,
      "WaaS withdrawal callback",
    );
  }

  #verify<T>(
    rawBody: string,
    contract: (typeof waasWebhooks)[keyof typeof waasWebhooks],
    name: string,
  ): T {
    const payload = verifyProjectWebhook(rawBody, this.#apiKey);
    assertRuntimeContract(payload, contract.schema, contract.schemas, name);
    return payload as T;
  }
}
