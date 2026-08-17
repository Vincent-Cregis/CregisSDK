export type JsonPrimitive = boolean | number | string | null;
export type JsonValue = JsonPrimitive | JsonObject | JsonValue[];
export type JsonObject = { [key: string]: JsonValue | undefined };

export type FetchLike = (
  input: string | URL | Request,
  init?: RequestInit,
) => Promise<Response>;

export interface RequestOptions {
  signal?: AbortSignal;
}

export interface CregisLogEntry {
  method: "POST";
  path: string;
  status?: number;
  durationMs: number;
  outcome: "success" | "http_error" | "network_error";
}

export interface ClientOptions {
  baseUrl: string;
  fetch?: FetchLike;
  timeoutMs?: number;
  debug?: boolean;
  logger?: (entry: CregisLogEntry) => void;
}

export interface ProjectClientOptions extends ClientOptions {
  pid: number | string;
  apiKey: string;
}

export interface TeamClientOptions extends ClientOptions {
  accessKey: string;
  accessSecret: string;
}

export interface GeneratedOperation {
  readonly method: "POST";
  readonly operationId: string;
  readonly path: `/${string}`;
  readonly requiredRequestFields: readonly string[];
  readonly requestSchema: import("./contract.js").RuntimeSchema | null;
  readonly responseSchema: import("./contract.js").RuntimeSchema | null;
  readonly schemas: import("./contract.js").RuntimeSchemaRegistry;
}

export interface GeneratedWebhook {
  readonly operationId: string;
  readonly schema: import("./contract.js").RuntimeSchema;
  readonly schemas: import("./contract.js").RuntimeSchemaRegistry;
}
