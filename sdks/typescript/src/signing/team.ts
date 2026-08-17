import { createHmac } from "node:crypto";
import canonicalizeModule from "canonicalize";

import { CregisClientError } from "../core/errors.js";

const canonicalize = canonicalizeModule as unknown as (input: unknown) => string | undefined;

export function canonicalizeJson(value: unknown): string {
  try {
    const jsonValue = typeof value === "string" ? JSON.parse(value) : value;
    const result = canonicalize(jsonValue);
    if (typeof result !== "string") {
      throw new TypeError("Value is not JSON serializable");
    }
    return result;
  } catch (error) {
    throw new CregisClientError("JSON value cannot be canonicalized", { cause: error });
  }
}

export function signTeamRequest(
  path: string,
  timestamp: number,
  nonce: string,
  canonicalBody: string,
  accessSecret: string,
): string {
  if (typeof path !== "string" || !path.startsWith("/")) {
    throw new CregisClientError("Team API signing path must start with '/'");
  }
  if (!Number.isSafeInteger(timestamp) || timestamp <= 0) {
    throw new CregisClientError("Team API timestamp must be a positive integer");
  }
  if (typeof nonce !== "string" || nonce.length < 16 || nonce.length > 64) {
    throw new CregisClientError("Team API nonce must contain 16 to 64 characters");
  }
  if (typeof canonicalBody !== "string") {
    throw new CregisClientError("Team API canonical body must be a string");
  }
  if (typeof accessSecret !== "string" || accessSecret.trim() === "") {
    throw new CregisClientError("Access Secret is required");
  }

  const signingText = `${path}\n${timestamp}\n${nonce}\n${canonicalBody}`;
  return createHmac("sha256", accessSecret).update(signingText, "utf8").digest("hex");
}
