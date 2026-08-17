import { createHash } from "node:crypto";
import canonicalizeModule from "canonicalize";

import { CregisClientError } from "../core/errors.js";

type SignableValue = unknown;
const canonicalize = canonicalizeModule as unknown as (input: unknown) => string | undefined;

function canonicalizeNestedValue(value: SignableValue): string {
  try {
    const result = canonicalize(value);
    if (typeof result !== "string") {
      throw new TypeError("Value is not JSON serializable");
    }
    return result;
  } catch (error) {
    throw new CregisClientError("Failed to serialize a nested signature value", {
      cause: error,
    });
  }
}

function stringifySignatureValue(value: SignableValue): string {
  if (typeof value === "object" && value !== null) {
    return canonicalizeNestedValue(value);
  }
  return String(value);
}

export function signProjectParameters(
  parameters: Readonly<Record<string, SignableValue>>,
  apiKey: string,
): string {
  if (parameters === null || typeof parameters !== "object" || Array.isArray(parameters)) {
    throw new CregisClientError("Signature parameters must be a JSON object");
  }
  if (typeof apiKey !== "string" || apiKey.trim() === "") {
    throw new CregisClientError("API Key is required");
  }

  let signingText = apiKey;
  for (const key of Object.keys(parameters).sort()) {
    const value = parameters[key];
    if (key === "sign" || value === null || value === undefined) {
      continue;
    }
    const stringValue = stringifySignatureValue(value);
    if (stringValue === "") {
      continue;
    }
    signingText += key + stringValue;
  }

  return createHash("md5").update(signingText, "utf8").digest("hex");
}
