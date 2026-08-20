import { readFileSync } from "node:fs";
import { resolve } from "node:path";

import type { FetchLike } from "../src/index.js";

type JsonObject = Record<string, unknown>;

const SPEC_FILES = {
  payment: "payment-engine-api.json",
  team: "team-api.json",
  waas: "waas-api.json",
} as const;

type ApiName = keyof typeof SPEC_FILES;

function isObject(value: unknown): value is JsonObject {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

function resolveReference(spec: JsonObject, reference: string): unknown {
  if (!reference.startsWith("#/")) {
    throw new Error(`Only local OpenAPI references are supported: ${reference}`);
  }
  let value: unknown = spec;
  for (const encodedPart of reference.slice(2).split("/")) {
    const part = encodedPart.replaceAll("~1", "/").replaceAll("~0", "~");
    if (!isObject(value) || !Object.hasOwn(value, part)) {
      throw new Error(`Unresolvable OpenAPI reference: ${reference}`);
    }
    value = value[part];
  }
  return value;
}

function dereference(spec: JsonObject, value: unknown): JsonObject {
  if (!isObject(value)) {
    throw new Error("OpenAPI contract entry must be an object");
  }
  const reference = value.$ref;
  if (typeof reference !== "string") {
    return value;
  }
  const target = resolveReference(spec, reference);
  if (!isObject(target)) {
    throw new Error(`OpenAPI reference does not resolve to an object: ${reference}`);
  }
  const siblings = Object.fromEntries(Object.entries(value).filter(([key]) => key !== "$ref"));
  return { ...target, ...siblings };
}

function jsonType(value: unknown): string {
  if (value === null) return "null";
  if (Array.isArray(value)) return "array";
  if (Number.isInteger(value)) return "integer";
  return typeof value === "object" ? "object" : typeof value;
}

export function validateOpenApiSchema(
  spec: JsonObject,
  rawSchema: unknown,
  value: unknown,
  path: string,
): void {
  const schema = dereference(spec, rawSchema);
  if (value === null && schema.nullable === true) {
    return;
  }

  const allOf = schema.allOf;
  if (Array.isArray(allOf)) {
    for (const part of allOf) {
      validateOpenApiSchema(spec, part, value, path);
    }
  }

  for (const keyword of ["oneOf", "anyOf"] as const) {
    const alternatives = schema[keyword];
    if (Array.isArray(alternatives)) {
      const matches = alternatives.filter((alternative) => {
        try {
          validateOpenApiSchema(spec, alternative, value, path);
          return true;
        } catch {
          return false;
        }
      });
      if (matches.length === 0 || (keyword === "oneOf" && matches.length !== 1)) {
        throw new Error(`${path} does not match the OpenAPI ${keyword} contract`);
      }
    }
  }

  const enumValues = schema.enum;
  if (Array.isArray(enumValues) && !enumValues.some((candidate) => Object.is(candidate, value))) {
    throw new Error(`${path} is outside the documented enum`);
  }

  let expectedType = schema.type;
  if (expectedType === undefined && isObject(schema.properties)) expectedType = "object";
  if (expectedType === undefined && isObject(schema.items)) expectedType = "array";
  if (Array.isArray(expectedType)) {
    if (!expectedType.includes(jsonType(value))) {
      throw new Error(`${path} expected ${expectedType.join(" or ")} but got ${jsonType(value)}`);
    }
  } else if (typeof expectedType === "string") {
    if (expectedType === "integer" && typeof value === "number"
        && Number.isInteger(value) && !Number.isSafeInteger(value)) {
      throw new Error(`${path} expected a JavaScript-safe integer`);
    }
    const matches = expectedType === "integer"
      ? typeof value === "number" && Number.isSafeInteger(value)
      : expectedType === "number"
        ? typeof value === "number" && Number.isFinite(value)
        : expectedType === "object"
          ? isObject(value)
          : expectedType === "array"
            ? Array.isArray(value)
            : expectedType === "null"
              ? value === null
              : typeof value === expectedType;
    if (!matches) {
      throw new Error(`${path} expected ${expectedType} but got ${jsonType(value)}`);
    }
  }

  if (typeof value === "number") {
    if (typeof schema.minimum === "number" && value < schema.minimum) {
      throw new Error(`${path} is below the documented minimum`);
    }
    if (typeof schema.maximum === "number" && value > schema.maximum) {
      throw new Error(`${path} is above the documented maximum`);
    }
  }
  if (typeof value === "string") {
    if (typeof schema.minLength === "number" && value.length < schema.minLength) {
      throw new Error(`${path} is shorter than the documented minimum`);
    }
    if (typeof schema.maxLength === "number" && value.length > schema.maxLength) {
      throw new Error(`${path} is longer than the documented maximum`);
    }
    if (typeof schema.pattern === "string" && !new RegExp(schema.pattern, "u").test(value)) {
      throw new Error(`${path} does not match the documented pattern`);
    }
  }

  if (Array.isArray(value) && isObject(schema.items)) {
    if (typeof schema.minItems === "number" && value.length < schema.minItems) {
      throw new Error(`${path} has fewer items than documented`);
    }
    if (typeof schema.maxItems === "number" && value.length > schema.maxItems) {
      throw new Error(`${path} has more items than documented`);
    }
    value.forEach((item, index) => validateOpenApiSchema(spec, schema.items, item, `${path}[${index}]`));
  }
  if (isObject(value)) {
    const required = schema.required;
    if (Array.isArray(required)) {
      for (const field of required) {
        if (typeof field === "string" && !Object.hasOwn(value, field)) {
          throw new Error(`${path} is missing required field ${field}`);
        }
      }
    }
    const properties = schema.properties;
    if (isObject(properties)) {
      for (const [field, propertySchema] of Object.entries(properties)) {
        if (Object.hasOwn(value, field)) {
          validateOpenApiSchema(spec, propertySchema, value[field], `${path}.${field}`);
        }
      }
      if (schema.additionalProperties === false) {
        const unknown = Object.keys(value).filter((field) => !Object.hasOwn(properties, field));
        if (unknown.length > 0) {
          throw new Error(`${path} contains undocumented fields: ${unknown.join(", ")}`);
        }
      }
    }
  }
}

function loadSpec(apiName: ApiName): JsonObject {
  const configured = process.env.CREGIS_OPENAPI_SPEC_DIR;
  const specDirectory = configured === undefined || configured.trim() === ""
    ? resolve(process.cwd(), "../../../cregis-developer-docs/api-sources/specs")
    : configured;
  const parsed = JSON.parse(readFileSync(resolve(specDirectory, SPEC_FILES[apiName]), "utf8")) as unknown;
  if (!isObject(parsed)) {
    throw new Error(`OpenAPI root for ${apiName} must be an object`);
  }
  return parsed;
}

function operationFor(spec: JsonObject, method: string, path: string): JsonObject {
  const paths = spec.paths;
  const pathItem = isObject(paths) ? paths[path] : undefined;
  const operation = isObject(pathItem) ? pathItem[method.toLowerCase()] : undefined;
  if (!isObject(operation)) {
    throw new Error(`OpenAPI has no ${method} ${path}`);
  }
  return operation;
}

function mediaSchema(spec: JsonObject, owner: unknown): JsonObject | undefined {
  const resolved = dereference(spec, owner);
  const content = resolved.content;
  if (!isObject(content)) return undefined;
  const json = content["application/json"]
    ?? Object.entries(content).find(([name]) => name.startsWith("application/json"))?.[1];
  if (!isObject(json) || !isObject(json.schema)) return undefined;
  return json.schema;
}

function validateHeaders(spec: JsonObject, operation: JsonObject, headers: Headers): void {
  const parameters = operation.parameters;
  if (!Array.isArray(parameters)) return;
  for (const rawParameter of parameters) {
    const parameter = dereference(spec, rawParameter);
    if (parameter.in !== "header" || typeof parameter.name !== "string") continue;
    const value = headers.get(parameter.name);
    if (parameter.required === true && value === null) {
      throw new Error(`Request is missing required header ${parameter.name}`);
    }
    if (value === null || !isObject(parameter.schema)) continue;
    const type = parameter.schema.type;
    if (type === "integer" && !/^-?\d+$/.test(value)) {
      throw new Error(`Header ${parameter.name} expected integer`);
    }
    if (type === "string" && value === "") {
      throw new Error(`Header ${parameter.name} expected non-empty string`);
    }
  }
}

function parseJsonBody(body: BodyInit | null | undefined, label: string): unknown {
  if (typeof body !== "string") {
    throw new Error(`${label} must be a JSON string`);
  }
  try {
    return JSON.parse(body) as unknown;
  } catch (error) {
    throw new Error(`${label} is not valid JSON`, { cause: error });
  }
}

export function createOpenApiContractFetch(apiName: ApiName): FetchLike {
  const spec = loadSpec(apiName);
  return async (input, init) => {
    const url = new URL(String(input));
    const method = init?.method ?? "GET";
    const operation = operationFor(spec, method, url.pathname);
    validateHeaders(spec, operation, new Headers(init?.headers));

    const requestBody = operation.requestBody;
    if (requestBody !== undefined) {
      const schema = mediaSchema(spec, requestBody);
      if (schema !== undefined) {
        validateOpenApiSchema(spec, schema, parseJsonBody(init?.body, "Request body"), "request");
      }
    }

    const response = await globalThis.fetch(input, init);
    const responses = operation.responses;
    const rawResponse = isObject(responses)
      ? responses[String(response.status)] ?? responses.default
      : undefined;
    if (rawResponse !== undefined) {
      const schema = mediaSchema(spec, rawResponse);
      if (schema !== undefined) {
        const rawBody = await response.clone().text();
        validateOpenApiSchema(spec, schema, parseJsonBody(rawBody, "Response body"), "response");
      }
    }
    return response;
  };
}
