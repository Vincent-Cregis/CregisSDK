package com.cregis.sdk.generated;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Minimal model support required by OpenAPI Generator's Java model template.
 *
 * <p>This class intentionally contains no HTTP behavior. Cregis transport and
 * authentication remain in the handwritten runtime.</p>
 */
public final class ApiClient {

    private ApiClient() {
    }

    public static String urlEncode(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public static String valueToString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
