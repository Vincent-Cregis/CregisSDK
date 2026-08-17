import { CregisClientError } from "./errors.js";

export interface RuntimeEventPayload {
  readonly eventProperty: string;
  readonly payloadProperty: string;
  readonly mapping: Readonly<Record<string, string>>;
}

export interface RuntimeSchema {
  readonly $ref?: string;
  readonly type?: string | readonly string[];
  readonly nullable?: boolean;
  readonly const?: unknown;
  readonly enum?: readonly unknown[];
  readonly required?: readonly string[];
  readonly properties?: Readonly<Record<string, RuntimeSchema>>;
  readonly items?: RuntimeSchema;
  readonly allOf?: readonly RuntimeSchema[];
  readonly anyOf?: readonly RuntimeSchema[];
  readonly oneOf?: readonly RuntimeSchema[];
  readonly additionalProperties?: boolean | RuntimeSchema;
  readonly minimum?: number;
  readonly maximum?: number;
  readonly minLength?: number;
  readonly maxLength?: number;
  readonly pattern?: string;
  readonly minItems?: number;
  readonly maxItems?: number;
  readonly "x-cregis-event-payload"?: RuntimeEventPayload;
}

export type RuntimeSchemaRegistry = Readonly<Record<string, RuntimeSchema>>;

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

function jsonType(value: unknown): string {
  if (value === null) return "null";
  if (Array.isArray(value)) return "array";
  if (typeof value === "number" && Number.isInteger(value)) return "integer";
  return typeof value;
}

function fail(path: string, message: string): never {
  throw new CregisClientError(`${path} ${message}`);
}

function referenceName(reference: string, path: string): string {
  const prefix = "#/components/schemas/";
  if (!reference.startsWith(prefix) || reference.length === prefix.length) {
    return fail(path, `uses unsupported schema reference: ${reference}`);
  }
  return reference.slice(prefix.length);
}

function assertAlternatives(
  value: unknown,
  alternatives: readonly RuntimeSchema[],
  schemas: RuntimeSchemaRegistry,
  path: string,
  keyword: "anyOf" | "oneOf",
): void {
  let matches = 0;
  for (const alternative of alternatives) {
    try {
      assertRuntimeContract(value, alternative, schemas, path);
      matches += 1;
    } catch (error) {
      if (!(error instanceof CregisClientError)) throw error;
    }
  }
  if (matches === 0 || (keyword === "oneOf" && matches !== 1)) {
    fail(path, `does not match the OpenAPI ${keyword} contract`);
  }
}

function assertExpectedType(value: unknown, expected: string, path: string): void {
  if (expected === "integer") {
    if (typeof value !== "number" || !Number.isInteger(value)) {
      fail(path, `must be integer; received ${jsonType(value)}`);
    }
    if (!Number.isSafeInteger(value)) {
      fail(path, "must be a JavaScript-safe integer");
    }
    return;
  }

  const valid = expected === "number"
      ? typeof value === "number" && Number.isFinite(value)
      : expected === "object"
        ? isRecord(value)
        : expected === "array"
          ? Array.isArray(value)
          : expected === "null"
            ? value === null
            : typeof value === expected;
  if (!valid) {
    fail(path, `must be ${expected}; received ${jsonType(value)}`);
  }
}

export function assertRuntimeContract(
  value: unknown,
  schema: RuntimeSchema,
  schemas: RuntimeSchemaRegistry,
  path: string,
): void {
  if (value === null && schema.nullable === true) return;

  if (schema.$ref !== undefined) {
    const name = referenceName(schema.$ref, path);
    const target = schemas[name];
    if (target === undefined) fail(path, `references missing schema ${name}`);
    assertRuntimeContract(value, target, schemas, path);
    const siblings = Object.fromEntries(
      Object.entries(schema).filter(([key]) => key !== "$ref"),
    ) as RuntimeSchema;
    if (Object.keys(siblings).length > 0) {
      assertRuntimeContract(value, siblings, schemas, path);
    }
    return;
  }

  for (const part of schema.allOf ?? []) {
    assertRuntimeContract(value, part, schemas, path);
  }
  if (schema.anyOf !== undefined) {
    assertAlternatives(value, schema.anyOf, schemas, path, "anyOf");
  }
  if (schema.oneOf !== undefined && schema["x-cregis-event-payload"] === undefined) {
    assertAlternatives(value, schema.oneOf, schemas, path, "oneOf");
  }

  if (schema.const !== undefined && !Object.is(value, schema.const)) {
    fail(path, "does not equal the documented constant");
  }
  const expected = schema.type;
  if (Array.isArray(expected)) {
    const matches = expected.some((item) => {
      try {
        assertExpectedType(value, item, path);
        return true;
      } catch {
        return false;
      }
    });
    if (!matches) fail(path, `must be ${expected.join(" or ")}; received ${jsonType(value)}`);
  } else if (typeof expected === "string") {
    assertExpectedType(value, expected, path);
  } else if (schema.properties !== undefined) {
    assertExpectedType(value, "object", path);
  } else if (schema.items !== undefined) {
    assertExpectedType(value, "array", path);
  }

  if (schema.enum !== undefined && !schema.enum.some((candidate) => Object.is(candidate, value))) {
    fail(path, "is outside the documented enum");
  }

  if (typeof value === "number") {
    if (schema.minimum !== undefined && value < schema.minimum) {
      fail(path, `must be at least ${schema.minimum}`);
    }
    if (schema.maximum !== undefined && value > schema.maximum) {
      fail(path, `must be at most ${schema.maximum}`);
    }
  }

  if (typeof value === "string") {
    if (schema.minLength !== undefined && value.length < schema.minLength) {
      fail(path, `must contain at least ${schema.minLength} characters`);
    }
    if (schema.maxLength !== undefined && value.length > schema.maxLength) {
      fail(path, `must contain at most ${schema.maxLength} characters`);
    }
    if (schema.pattern !== undefined && !new RegExp(schema.pattern, "u").test(value)) {
      fail(path, "does not match the documented format");
    }
  }

  if (Array.isArray(value)) {
    if (schema.minItems !== undefined && value.length < schema.minItems) {
      fail(path, `must contain at least ${schema.minItems} items`);
    }
    if (schema.maxItems !== undefined && value.length > schema.maxItems) {
      fail(path, `must contain at most ${schema.maxItems} items`);
    }
    if (schema.items !== undefined) {
      value.forEach((item, index) => {
        assertRuntimeContract(item, schema.items!, schemas, `${path}[${index}]`);
      });
    }
  }

  if (!isRecord(value)) return;

  for (const field of schema.required ?? []) {
    if (!Object.hasOwn(value, field)) fail(path, `is missing required field ${field}`);
  }

  const eventPayload = schema["x-cregis-event-payload"];
  const properties = schema.properties ?? {};
  for (const [field, fieldSchema] of Object.entries(properties)) {
    if (!Object.hasOwn(value, field)) continue;
    if (eventPayload !== undefined && field === eventPayload.payloadProperty) {
      const eventValue = value[eventPayload.eventProperty];
      if (typeof eventValue !== "string") {
        fail(`${path}.${eventPayload.eventProperty}`, "must select a payload contract");
      }
      const mappedReference = eventPayload.mapping[eventValue];
      if (mappedReference === undefined) {
        fail(`${path}.${eventPayload.eventProperty}`, "has no payload contract mapping");
      }
      assertRuntimeContract(
        value[field],
        { $ref: mappedReference },
        schemas,
        `${path}.${field}`,
      );
      continue;
    }
    assertRuntimeContract(value[field], fieldSchema, schemas, `${path}.${field}`);
  }

  if (schema.additionalProperties === false) {
    const unknown = Object.keys(value).filter((field) => !Object.hasOwn(properties, field));
    if (unknown.length > 0) fail(path, `contains undocumented fields: ${unknown.join(", ")}`);
  } else if (isRecord(schema.additionalProperties)) {
    for (const [field, fieldValue] of Object.entries(value)) {
      if (!Object.hasOwn(properties, field)) {
        assertRuntimeContract(fieldValue, schema.additionalProperties, schemas, `${path}.${field}`);
      }
    }
  }
}
