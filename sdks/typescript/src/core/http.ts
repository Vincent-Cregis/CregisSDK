import { randomBytes, randomUUID } from "node:crypto";

import { signProjectParameters } from "../signing/project.js";
import { canonicalizeJson, signTeamRequest } from "../signing/team.js";
import { assertRuntimeContract } from "./contract.js";
import { CregisApiError, CregisClientError, CregisError, CregisHttpError } from "./errors.js";
import type {
  ClientOptions,
  CregisLogEntry,
  FetchLike,
  GeneratedOperation,
  ProjectClientOptions,
  RequestOptions,
  TeamClientOptions,
} from "./types.js";

interface SignedRequest {
  body: string;
  headers?: Readonly<Record<string, string>>;
}

type RequestAuthenticator = (path: string, payload: Record<string, unknown>) => SignedRequest;

interface ApiEnvelope {
  code: string;
  msg: string;
  data: unknown;
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

function requireNonBlank(value: string, name: string): string {
  if (typeof value !== "string" || value.trim() === "") {
    throw new CregisClientError(`${name} is required`);
  }
  return value;
}

function normalizePid(value: number | string): number {
  if (typeof value !== "number" && typeof value !== "string") {
    throw new CregisClientError("PID must be a positive JavaScript-safe int64 value");
  }
  const parsed = typeof value === "number"
    ? value
    : /^\d+$/.test(value.trim())
      ? Number(value)
      : Number.NaN;
  if (!Number.isSafeInteger(parsed) || parsed <= 0) {
    throw new CregisClientError("PID must be a positive JavaScript-safe int64 value");
  }
  return parsed;
}

function normalizeBaseUrl(value: string): string {
  requireNonBlank(value, "Base URL");
  let parsed: URL;
  try {
    parsed = new URL(value.trim());
  } catch (error) {
    throw new CregisClientError("Base URL must be a valid HTTP(S) URL", { cause: error });
  }
  if (parsed.username !== "" || parsed.password !== "") {
    throw new CregisClientError("Base URL must not contain user information");
  }
  if (parsed.search !== "" || parsed.hash !== "") {
    throw new CregisClientError("Base URL must not contain a query or fragment");
  }
  const loopback = parsed.hostname === "localhost"
    || parsed.hostname === "127.0.0.1"
    || parsed.hostname === "[::1]";
  if (parsed.protocol !== "https:" && !(parsed.protocol === "http:" && loopback)) {
    throw new CregisClientError("Base URL must use HTTPS");
  }
  return parsed.href.replace(/\/+$/, "");
}

function serializePayload(payload: object): Record<string, unknown> {
  if (!isRecord(payload)) {
    throw new CregisClientError("Request payload must be a JSON object");
  }
  try {
    const serialized = JSON.stringify(payload);
    const normalized = JSON.parse(serialized) as unknown;
    if (!isRecord(normalized)) {
      throw new TypeError("Serialized request is not an object");
    }
    return normalized;
  } catch (error) {
    throw new CregisClientError("Failed to serialize request payload", { cause: error });
  }
}

function validateRequiredFields(
  payload: Record<string, unknown>,
  operation: GeneratedOperation,
): void {
  for (const field of operation.requiredRequestFields) {
    if (!Object.hasOwn(payload, field) || payload[field] === null || payload[field] === undefined) {
      throw new CregisClientError(`Missing required request field: ${field}`);
    }
  }
}

function parseEnvelope(rawBody: string, path: string): ApiEnvelope {
  let value: unknown;
  try {
    value = JSON.parse(rawBody) as unknown;
  } catch (error) {
    throw new CregisClientError(`Failed to parse Cregis response for POST ${path}`, { cause: error });
  }
  if (!isRecord(value)) {
    throw new CregisClientError("Cregis response must be a JSON object");
  }
  if (typeof value.code !== "string" || value.code.trim() === "") {
    throw new CregisClientError("Cregis response is missing required field: code");
  }
  if (typeof value.msg !== "string") {
    throw new CregisClientError("Cregis response is missing required string field: msg");
  }
  if (!Object.hasOwn(value, "data")) {
    throw new CregisClientError("Cregis response is missing required field: data");
  }
  return {
    code: value.code,
    msg: value.msg,
    data: value.data,
  };
}

export abstract class CregisBaseClient {
  readonly #baseUrl: string;
  readonly #fetch: FetchLike;
  readonly #timeoutMs: number;
  readonly #logger: ((entry: CregisLogEntry) => void) | undefined;
  readonly #authenticate: RequestAuthenticator;

  protected constructor(options: ClientOptions, authenticate: RequestAuthenticator) {
    this.#baseUrl = normalizeBaseUrl(options.baseUrl);
    this.#fetch = options.fetch ?? globalThis.fetch.bind(globalThis);
    this.#timeoutMs = options.timeoutMs ?? 30_000;
    if (!Number.isFinite(this.#timeoutMs) || this.#timeoutMs <= 0) {
      throw new CregisClientError("timeoutMs must be greater than zero");
    }
    this.#logger = options.logger ?? (options.debug === true
      ? (entry) => console.debug("[Cregis SDK]", entry)
      : undefined);
    this.#authenticate = authenticate;
  }

  protected async post<TResponse>(
    operation: GeneratedOperation,
    request: object,
    options?: RequestOptions,
  ): Promise<TResponse> {
    if (operation.method !== "POST") {
      throw new CregisClientError(`Unsupported HTTP method: ${operation.method}`);
    }
    const payload = serializePayload(request);
    validateRequiredFields(payload, operation);
    if (operation.requestSchema !== null) {
      assertRuntimeContract(
        payload,
        operation.requestSchema,
        operation.schemas,
        `${operation.operationId} request`,
      );
    }
    const signed = this.#authenticate(operation.path, payload);
    const timeoutSignal = AbortSignal.timeout(this.#timeoutMs);
    const signal = options?.signal === undefined
      ? timeoutSignal
      : AbortSignal.any([options.signal, timeoutSignal]);
    const startedAt = Date.now();

    let response: Response;
    try {
      response = await this.#fetch(`${this.#baseUrl}${operation.path}`, {
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json; charset=utf-8",
          ...signed.headers,
        },
        body: signed.body,
        redirect: "manual",
        signal,
      });
    } catch (error) {
      this.#log({
        method: "POST",
        path: operation.path,
        durationMs: Date.now() - startedAt,
        outcome: "network_error",
      });
      const message = timeoutSignal.aborted
        ? `Cregis request timed out after ${this.#timeoutMs}ms`
        : `Network error executing POST ${operation.path}`;
      throw new CregisClientError(message, { cause: error });
    }

    let rawBody: string;
    try {
      rawBody = await response.text();
    } catch (error) {
      throw new CregisClientError(`Failed to read Cregis response for POST ${operation.path}`, {
        cause: error,
      });
    }

    if (!response.ok) {
      this.#log({
        method: "POST",
        path: operation.path,
        status: response.status,
        durationMs: Date.now() - startedAt,
        outcome: "http_error",
      });
      throw new CregisHttpError(response.status, response.statusText, rawBody);
    }

    this.#log({
      method: "POST",
      path: operation.path,
      status: response.status,
      durationMs: Date.now() - startedAt,
      outcome: "success",
    });
    const envelope = parseEnvelope(rawBody, operation.path);
    if (envelope.code !== "00000") {
      throw new CregisApiError(envelope.code, envelope.msg);
    }
    if (operation.responseSchema !== null) {
      assertRuntimeContract(
        envelope.data,
        operation.responseSchema,
        operation.schemas,
        `${operation.operationId} response data`,
      );
    }
    return envelope.data as TResponse;
  }

  #log(entry: CregisLogEntry): void {
    try {
      this.#logger?.(entry);
    } catch {
      // Consumer logging must never change request behavior.
    }
  }
}

export abstract class CregisProjectClientBase extends CregisBaseClient {
  protected constructor(options: ProjectClientOptions) {
    const pid = normalizePid(options.pid);
    const apiKey = requireNonBlank(options.apiKey, "API Key");
    super(options, (_path, payload) => {
      const parameters: Record<string, unknown> = {
        ...payload,
        pid,
        nonce: randomBytes(3).toString("hex"),
        timestamp: Date.now(),
      };
      parameters.sign = signProjectParameters(parameters, apiKey);
      return { body: JSON.stringify(parameters) };
    });
  }
}

export abstract class CregisTeamClientBase extends CregisBaseClient {
  protected constructor(options: TeamClientOptions) {
    const accessKey = requireNonBlank(options.accessKey, "Access Key");
    const accessSecret = requireNonBlank(options.accessSecret, "Access Secret");
    super(options, (path, payload) => {
      const body = canonicalizeJson(payload);
      const timestamp = Date.now();
      const nonce = randomUUID().replaceAll("-", "");
      return {
        body,
        headers: {
          "Access-Key": accessKey,
          "Access-Timestamp": String(timestamp),
          "Access-Nonce": nonce,
          "Access-Signature": signTeamRequest(path, timestamp, nonce, body, accessSecret),
        },
      };
    });
  }
}

export function isCregisError(error: unknown): error is CregisError {
  return error instanceof CregisError;
}
