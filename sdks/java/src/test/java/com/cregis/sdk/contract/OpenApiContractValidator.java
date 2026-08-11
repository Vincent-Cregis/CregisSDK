package com.cregis.sdk.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.Headers;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Strict test-only validator for the OpenAPI wire contract.
 *
 * <p>It intentionally rejects undocumented JSON properties even though JSON
 * Schema permits additional properties by default. An SDK contract test needs
 * to expose new backend fields so the specification and generated models can
 * be reviewed before release.</p>
 */
public final class OpenApiContractValidator {

    private static final BigInteger INT32_MIN = BigInteger.valueOf(Integer.MIN_VALUE);
    private static final BigInteger INT32_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger INT64_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger INT64_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    private final ObjectMapper objectMapper;
    private final JsonNode document;

    private OpenApiContractValidator(ObjectMapper objectMapper, JsonNode document) {
        this.objectMapper = objectMapper;
        this.document = document;
    }

    public static OpenApiContractValidator load(Path specPath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return new OpenApiContractValidator(mapper, mapper.readTree(specPath.toFile()));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read OpenAPI contract: " + specPath, e);
        }
    }

    static OpenApiContractValidator parse(String document) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return new OpenApiContractValidator(mapper, mapper.readTree(document));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid test OpenAPI document", e);
        }
    }

    public Set<String> callableOperationIds() {
        Set<String> operationIds = new LinkedHashSet<>();
        JsonNode paths = document.path("paths");
        paths.properties().forEach(path -> path.getValue().properties().forEach(method -> {
            JsonNode operationId = method.getValue().get("operationId");
            if (operationId != null && operationId.isTextual()) {
                operationIds.add(operationId.textValue());
            }
        }));
        return operationIds;
    }

    public String validateRequest(String method, String path, Headers headers, String rawJson) {
        JsonNode pathItem = pathItem(path);
        JsonNode operation = operation(pathItem, method, path);
        String operationId = requiredOperationId(operation, method, path);

        validateHeaderParameters(pathItem.path("parameters"), headers, operationId);
        validateHeaderParameters(operation.path("parameters"), headers, operationId);

        JsonNode requestBody = resolveComponent(operation.get("requestBody"));
        if (requestBody == null || requestBody.isMissingNode()) {
            if (rawJson != null && !rawJson.trim().isEmpty()) {
                fail(operationId + " request", "$", "OpenAPI does not define a request body");
            }
            return operationId;
        }

        JsonNode schema = jsonSchema(requestBody.path("content"), operationId + " request");
        validateRawJson(rawJson, schema, operationId + " request");
        return operationId;
    }

    public void validateResponse(
            String method,
            String path,
            int statusCode,
            String rawJson) {
        JsonNode operation = operation(pathItem(path), method, path);
        String operationId = requiredOperationId(operation, method, path);
        JsonNode responses = operation.path("responses");
        JsonNode response = responses.get(Integer.toString(statusCode));
        if (response == null) {
            response = responses.get("default");
        }
        response = resolveComponent(response);
        if (response == null || response.isMissingNode()) {
            fail(operationId + " response", "$", "no OpenAPI response for HTTP " + statusCode);
        }

        JsonNode content = response.path("content");
        if (content.isMissingNode() || content.isEmpty()) {
            if (rawJson != null && !rawJson.trim().isEmpty()) {
                fail(operationId + " response", "$", "OpenAPI does not define a response body");
            }
            return;
        }
        JsonNode schema = jsonSchema(content, operationId + " response");
        validateRawJson(rawJson, schema, operationId + " response");
    }

    public void validateWebhook(String webhookName, String rawJson) {
        JsonNode webhook = document.path("webhooks").path(webhookName);
        if (webhook.isMissingNode()) {
            fail(webhookName + " webhook", "$", "webhook is not defined in OpenAPI");
        }
        JsonNode operation = webhook.path("post");
        JsonNode requestBody = resolveComponent(operation.get("requestBody"));
        JsonNode schema = jsonSchema(
                requestBody.path("content"),
                webhookName + " webhook request");
        validateRawJson(rawJson, schema, webhookName + " webhook request");
    }

    private JsonNode pathItem(String path) {
        JsonNode pathItem = document.path("paths").get(path);
        if (pathItem == null) {
            fail("OpenAPI operation", path, "path is not defined");
        }
        return pathItem;
    }

    private JsonNode operation(JsonNode pathItem, String method, String path) {
        String normalizedMethod = method.toLowerCase(Locale.ROOT);
        JsonNode operation = pathItem.get(normalizedMethod);
        if (operation == null || !operation.isObject()) {
            fail("OpenAPI operation", path, method.toUpperCase(Locale.ROOT) + " is not defined");
        }
        return operation;
    }

    private String requiredOperationId(JsonNode operation, String method, String path) {
        JsonNode operationId = operation.get("operationId");
        if (operationId == null || !operationId.isTextual() || operationId.textValue().isEmpty()) {
            fail("OpenAPI operation", path, method.toUpperCase(Locale.ROOT) + " has no operationId");
        }
        return operationId.textValue();
    }

    private void validateHeaderParameters(JsonNode parameters, Headers headers, String operationId) {
        if (!parameters.isArray()) {
            return;
        }
        for (JsonNode unresolved : parameters) {
            JsonNode parameter = expandSchema(unresolved, new LinkedHashSet<>());
            if (!"header".equals(parameter.path("in").asText())) {
                continue;
            }
            String name = parameter.path("name").asText();
            String value = headers.get(name);
            if (value == null) {
                if (parameter.path("required").asBoolean(false)) {
                    fail(operationId + " request headers", name, "required header is missing");
                }
                continue;
            }
            validateHeaderValue(value, parameter.path("schema"), operationId, name);
        }
    }

    private void validateHeaderValue(String value, JsonNode schema, String operationId, String name) {
        String type = schema.path("type").asText("string");
        try {
            switch (type) {
                case "integer":
                    new BigInteger(value);
                    break;
                case "number":
                    new java.math.BigDecimal(value);
                    break;
                case "boolean":
                    if (!"true".equals(value) && !"false".equals(value)) {
                        throw new NumberFormatException("not a boolean");
                    }
                    break;
                case "string":
                    break;
                default:
                    fail(operationId + " request headers", name, "unsupported header schema type " + type);
            }
        } catch (NumberFormatException e) {
            fail(operationId + " request headers", name, "expected " + type);
        }
    }

    private JsonNode jsonSchema(JsonNode content, String context) {
        JsonNode mediaType = content.get("application/json");
        if (mediaType == null) {
            Iterator<Map.Entry<String, JsonNode>> fields = content.properties().iterator();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getKey().startsWith("application/json")) {
                    mediaType = field.getValue();
                    break;
                }
            }
        }
        if (mediaType == null || mediaType.path("schema").isMissingNode()) {
            fail(context, "$", "application/json schema is missing");
        }
        return expandSchema(mediaType.path("schema"), new LinkedHashSet<>());
    }

    private void validateRawJson(String rawJson, JsonNode schema, String context) {
        if (rawJson == null || rawJson.trim().isEmpty()) {
            fail(context, "$", "JSON body is empty");
        }
        JsonNode value;
        try {
            value = objectMapper.readTree(rawJson);
        } catch (JsonProcessingException e) {
            fail(context, "$", "body is not valid JSON");
            return;
        }

        List<String> errors = new ArrayList<>();
        validateValue(value, schema, "$", errors);
        if (!errors.isEmpty()) {
            throw new OpenApiContractViolation(context + " violates OpenAPI: " + String.join("; ", errors));
        }
    }

    private void validateValue(JsonNode value, JsonNode schema, String path, List<String> errors) {
        if (errors.size() >= 20) {
            return;
        }

        if (value == null || value.isNull()) {
            if (!allowsNull(schema)) {
                errors.add(path + " expected " + expectedType(schema) + " but got null");
            }
            return;
        }

        JsonNode alternatives = schema.has("oneOf") ? schema.get("oneOf") : schema.get("anyOf");
        if (alternatives != null && alternatives.isArray()) {
            boolean matched = false;
            List<String> firstFailure = null;
            for (JsonNode alternative : alternatives) {
                List<String> candidateErrors = new ArrayList<>();
                validateValue(value, alternative, path, candidateErrors);
                if (candidateErrors.isEmpty()) {
                    matched = true;
                    break;
                }
                if (firstFailure == null) {
                    firstFailure = candidateErrors;
                }
            }
            if (!matched) {
                errors.add(path + " did not match any documented schema"
                        + (firstFailure == null || firstFailure.isEmpty()
                        ? ""
                        : " (" + firstFailure.get(0) + ")"));
            }
            return;
        }

        String type = schemaType(schema, value);
        if (!matchesType(value, type)) {
            errors.add(path + " expected " + type + " but got " + jsonType(value));
            return;
        }

        if ("integer".equals(type)) {
            validateIntegerFormat(value, schema, path, errors);
        }

        JsonNode enumValues = schema.get("enum");
        if (enumValues != null && enumValues.isArray()) {
            boolean enumMatch = false;
            for (JsonNode enumValue : enumValues) {
                if (enumValue.equals(value)) {
                    enumMatch = true;
                    break;
                }
            }
            if (!enumMatch) {
                errors.add(path + " is outside the documented enum"
                        + (value.isTextual() && value.textValue().trim().isEmpty()
                        ? " (actual value is blank)"
                        : ""));
            }
        }

        if (value.isObject()) {
            validateObject(value, schema, path, errors);
        } else if (value.isArray()) {
            JsonNode items = schema.get("items");
            if (items != null) {
                for (int index = 0; index < value.size(); index++) {
                    validateValue(value.get(index), items, path + "[" + index + "]", errors);
                }
            }
        }
    }

    private void validateObject(JsonNode value, JsonNode schema, String path, List<String> errors) {
        JsonNode properties = schema.path("properties");
        JsonNode required = schema.path("required");
        if (required.isArray()) {
            for (JsonNode field : required) {
                String name = field.asText();
                if (!value.has(name)) {
                    errors.add(childPath(path, name) + " is required but missing");
                }
            }
        }

        Iterator<Map.Entry<String, JsonNode>> fields = value.properties().iterator();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode propertySchema = properties.get(field.getKey());
            if (propertySchema != null) {
                validateValue(field.getValue(), propertySchema, childPath(path, field.getKey()), errors);
                continue;
            }

            JsonNode additionalProperties = schema.get("additionalProperties");
            if (additionalProperties != null && additionalProperties.isObject()) {
                validateValue(
                        field.getValue(),
                        additionalProperties,
                        childPath(path, field.getKey()),
                        errors);
            } else if (additionalProperties == null || !additionalProperties.asBoolean(false)) {
                if (!properties.isMissingNode() && properties.size() > 0) {
                    errors.add(childPath(path, field.getKey())
                            + " is not documented (actual type " + describeType(field.getValue()) + ")");
                }
            } else {
                errors.add(childPath(path, field.getKey())
                        + " is not allowed (actual type " + describeType(field.getValue()) + ")");
            }
        }
    }

    private void validateIntegerFormat(JsonNode value, JsonNode schema, String path, List<String> errors) {
        BigInteger integer = value.bigIntegerValue();
        String format = schema.path("format").asText();
        if ("int32".equals(format)
                && (integer.compareTo(INT32_MIN) < 0 || integer.compareTo(INT32_MAX) > 0)) {
            errors.add(path + " is outside int32 range");
        } else if ("int64".equals(format)
                && (integer.compareTo(INT64_MIN) < 0 || integer.compareTo(INT64_MAX) > 0)) {
            errors.add(path + " is outside int64 range");
        }
    }

    private boolean allowsNull(JsonNode schema) {
        if (!schema.has("type")
                && !schema.has("properties")
                && !schema.has("items")
                && !schema.has("oneOf")
                && !schema.has("anyOf")
                && !schema.has("enum")) {
            return true;
        }
        if (schema.path("nullable").asBoolean(false)) {
            return true;
        }
        JsonNode type = schema.get("type");
        if (type != null && type.isArray()) {
            for (JsonNode candidate : type) {
                if ("null".equals(candidate.asText())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String schemaType(JsonNode schema, JsonNode value) {
        JsonNode type = schema.get("type");
        if (type != null && type.isTextual()) {
            return type.textValue();
        }
        if (type != null && type.isArray()) {
            for (JsonNode candidate : type) {
                if (!"null".equals(candidate.asText())) {
                    return candidate.asText();
                }
            }
        }
        if (schema.has("properties")) {
            return "object";
        }
        if (schema.has("items")) {
            return "array";
        }
        return jsonType(value);
    }

    private String expectedType(JsonNode schema) {
        JsonNode type = schema.get("type");
        if (type != null) {
            return type.isTextual() ? type.textValue() : type.toString();
        }
        if (schema.has("properties")) {
            return "object";
        }
        if (schema.has("items")) {
            return "array";
        }
        return "a non-null value";
    }

    private boolean matchesType(JsonNode value, String type) {
        switch (type) {
            case "object":
                return value.isObject();
            case "array":
                return value.isArray();
            case "string":
                return value.isTextual();
            case "integer":
                return value.isIntegralNumber();
            case "number":
                return value.isNumber();
            case "boolean":
                return value.isBoolean();
            case "null":
                return value.isNull();
            default:
                return true;
        }
    }

    private String jsonType(JsonNode value) {
        if (value == null || value.isNull()) {
            return "null";
        }
        if (value.isObject()) {
            return "object";
        }
        if (value.isArray()) {
            return "array";
        }
        if (value.isTextual()) {
            return "string";
        }
        if (value.isIntegralNumber()) {
            return "integer";
        }
        if (value.isNumber()) {
            return "number";
        }
        if (value.isBoolean()) {
            return "boolean";
        }
        return value.getNodeType().name().toLowerCase(Locale.ROOT);
    }

    private String describeType(JsonNode value) {
        if (value != null && value.isArray()) {
            for (JsonNode item : value) {
                if (!item.isNull()) {
                    return "array<" + describeObjectShape(item) + ">";
                }
            }
            return "array";
        }
        return describeObjectShape(value);
    }

    private String describeObjectShape(JsonNode value) {
        if (value == null || !value.isObject()) {
            return jsonType(value);
        }
        List<String> fields = new ArrayList<>();
        value.properties().forEach(field -> {
            if (fields.size() < 12) {
                fields.add(field.getKey() + ":" + jsonType(field.getValue()));
            }
        });
        return "object{" + String.join(",", fields) + "}";
    }

    private JsonNode expandSchema(JsonNode unresolved, Set<String> referenceStack) {
        if (unresolved == null || !unresolved.isObject()) {
            return unresolved == null ? null : unresolved.deepCopy();
        }

        ObjectNode expanded = objectMapper.createObjectNode();
        JsonNode reference = unresolved.get("$ref");
        if (reference != null && reference.isTextual()) {
            String pointer = reference.textValue();
            if (!pointer.startsWith("#/")) {
                fail("OpenAPI schema", pointer, "only local references are supported");
            }
            if (!referenceStack.add(pointer)) {
                fail("OpenAPI schema", pointer, "cyclic reference is not supported");
            }
            mergeSchemas(expanded, expandSchema(resolvePointer(pointer), referenceStack));
            referenceStack.remove(pointer);
        }

        JsonNode allOf = unresolved.get("allOf");
        if (allOf != null && allOf.isArray()) {
            for (JsonNode part : allOf) {
                mergeSchemas(expanded, expandSchema(part, referenceStack));
            }
        }

        ObjectNode siblings = unresolved.deepCopy();
        siblings.remove("$ref");
        siblings.remove("allOf");
        mergeSchemas(expanded, siblings);

        JsonNode properties = expanded.get("properties");
        if (properties != null && properties.isObject()) {
            ObjectNode expandedProperties = objectMapper.createObjectNode();
            properties.properties().forEach(field -> expandedProperties.set(
                    field.getKey(),
                    expandSchema(field.getValue(), referenceStack)));
            expanded.set("properties", expandedProperties);
        }
        if (expanded.has("items")) {
            expanded.set("items", expandSchema(expanded.get("items"), referenceStack));
        }
        expandAlternatives(expanded, "oneOf", referenceStack);
        expandAlternatives(expanded, "anyOf", referenceStack);
        JsonNode additionalProperties = expanded.get("additionalProperties");
        if (additionalProperties != null && additionalProperties.isObject()) {
            expanded.set("additionalProperties", expandSchema(additionalProperties, referenceStack));
        }
        return expanded;
    }

    private void expandAlternatives(ObjectNode schema, String name, Set<String> referenceStack) {
        JsonNode alternatives = schema.get(name);
        if (alternatives == null || !alternatives.isArray()) {
            return;
        }
        ArrayNode expandedAlternatives = objectMapper.createArrayNode();
        alternatives.forEach(alternative -> expandedAlternatives.add(
                expandSchema(alternative, referenceStack)));
        schema.set(name, expandedAlternatives);
    }

    private void mergeSchemas(ObjectNode target, JsonNode source) {
        if (source == null || !source.isObject()) {
            return;
        }
        source.properties().forEach(field -> {
            String name = field.getKey();
            JsonNode value = field.getValue();
            if ("required".equals(name) && value.isArray()) {
                ArrayNode required = target.withArray("required");
                value.forEach(candidate -> {
                    boolean present = false;
                    for (JsonNode existing : required) {
                        if (existing.equals(candidate)) {
                            present = true;
                            break;
                        }
                    }
                    if (!present) {
                        required.add(candidate.deepCopy());
                    }
                });
            } else if ("properties".equals(name) && value.isObject()) {
                ObjectNode properties = objectProperty(target, "properties");
                value.properties().forEach(property -> {
                    JsonNode existing = properties.get(property.getKey());
                    if (existing != null && existing.isObject() && property.getValue().isObject()) {
                        ObjectNode mergedProperty = existing.deepCopy();
                        mergeSchemas(mergedProperty, property.getValue());
                        properties.set(property.getKey(), mergedProperty);
                    } else {
                        properties.set(property.getKey(), property.getValue().deepCopy());
                    }
                });
            } else {
                target.set(name, value.deepCopy());
            }
        });
    }

    private ObjectNode objectProperty(ObjectNode target, String name) {
        JsonNode existing = target.get(name);
        if (existing == null) {
            ObjectNode created = objectMapper.createObjectNode();
            target.set(name, created);
            return created;
        }
        if (!existing.isObject()) {
            fail("OpenAPI schema", name, "must be an object");
        }
        return (ObjectNode) existing;
    }

    private JsonNode resolveComponent(JsonNode value) {
        if (value == null) {
            return null;
        }
        return expandSchema(value, new LinkedHashSet<>());
    }

    private JsonNode resolvePointer(String reference) {
        String pointer = reference.substring(1);
        JsonNode resolved = document.at(pointer);
        if (resolved.isMissingNode()) {
            fail("OpenAPI schema", reference, "reference cannot be resolved");
        }
        return resolved;
    }

    private String childPath(String parent, String child) {
        return parent + "." + child;
    }

    private void fail(String context, String path, String message) {
        throw new OpenApiContractViolation(context + " violates OpenAPI: " + path + " " + message);
    }
}
