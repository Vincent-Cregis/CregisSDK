import assert from "node:assert/strict";
import test from "node:test";

import { validateOpenApiSchema } from "./openapi-contract.js";

const spec = {
  components: {
    schemas: {
      Page: {
        type: "object",
        required: ["rows", "active"],
        properties: {
          rows: {
            type: "array",
            items: {
              type: "object",
              required: ["id"],
              properties: { id: { type: "integer", format: "int64" } },
            },
          },
          active: { type: "boolean" },
          status: { type: "string", enum: ["ok", "failed"] },
        },
      },
    },
  },
};

test("OpenAPI test validator accepts nested fields with exact JSON types", () => {
  assert.doesNotThrow(() => validateOpenApiSchema(
    spec,
    { $ref: "#/components/schemas/Page" },
    { rows: [{ id: 1 }], active: true, status: "ok" },
    "response",
  ));
});

test("OpenAPI test validator rejects missing, mistyped, and invalid enum fields", () => {
  assert.throws(
    () => validateOpenApiSchema(spec, { $ref: "#/components/schemas/Page" }, {
      rows: [{ id: 1 }],
      active: "true",
    }, "response"),
    /response.active expected boolean but got string/,
  );
  assert.throws(
    () => validateOpenApiSchema(spec, { $ref: "#/components/schemas/Page" }, {
      rows: [{}],
      active: true,
    }, "response"),
    /response.rows\[0\] is missing required field id/,
  );
  assert.throws(
    () => validateOpenApiSchema(spec, { $ref: "#/components/schemas/Page" }, {
      rows: [],
      active: true,
      status: "unknown",
    }, "response"),
    /outside the documented enum/,
  );
  assert.throws(
    () => validateOpenApiSchema(spec, { type: "integer", format: "int64" },
      Number.MAX_SAFE_INTEGER + 1, "response.id"),
    /JavaScript-safe integer/,
  );
});
