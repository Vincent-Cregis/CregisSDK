import { timingSafeEqual } from "node:crypto";

import { CregisClientError } from "../core/errors.js";
import { signProjectParameters } from "../signing/project.js";

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

function parseJsonObject(rawBody: string): Record<string, unknown> {
  if (typeof rawBody !== "string" || rawBody.trim() === "") {
    throw new CregisClientError("Callback body is required");
  }
  let parsed: unknown;
  try {
    parsed = JSON.parse(rawBody) as unknown;
  } catch (error) {
    throw new CregisClientError("Failed to parse callback JSON for signature verification", {
      cause: error,
    });
  }
  if (!isRecord(parsed)) {
    throw new CregisClientError("Callback body must be a JSON object");
  }
  return parsed;
}

function assertPositiveInteger(value: unknown, field: string): asserts value is number {
  if (typeof value !== "number" || !Number.isSafeInteger(value) || value <= 0) {
    throw new CregisClientError(`Callback ${field} must be a positive integer`);
  }
}

export function verifyProjectWebhook(
  rawBody: string,
  apiKey: string,
): Record<string, unknown> {
  if (typeof apiKey !== "string" || apiKey.trim() === "") {
    throw new CregisClientError("API Key is required");
  }
  const parsed = parseJsonObject(rawBody);
  const incomingSign = parsed.sign;
  if (typeof incomingSign !== "string" || !/^[0-9a-f]{32}$/i.test(incomingSign)) {
    throw new CregisClientError("Callback signature must be a 32-character hexadecimal string");
  }
  assertPositiveInteger(parsed.pid, "pid");
  if (typeof parsed.nonce !== "string" || parsed.nonce.trim() === "") {
    throw new CregisClientError("Callback nonce must be a non-empty string");
  }
  assertPositiveInteger(parsed.timestamp, "timestamp");

  const unsigned = { ...parsed };
  delete unsigned.sign;
  const calculated = signProjectParameters(unsigned, apiKey);
  const incomingBuffer = Buffer.from(incomingSign.toLowerCase(), "ascii");
  const calculatedBuffer = Buffer.from(calculated, "ascii");
  if (!timingSafeEqual(calculatedBuffer, incomingBuffer)) {
    throw new CregisClientError("Callback signature verification failed");
  }
  return parsed;
}
