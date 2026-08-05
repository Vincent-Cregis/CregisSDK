package com.cregis.sdk.core.interceptor;

import com.cregis.sdk.core.exception.CregisClientException;
import com.cregis.sdk.core.signer.CregisSigner;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Interceptor that adds Cregis authentication headers/parameters.
 * Note: Cregis requires parameters in the JSON body.
 */
public class CregisAuthInterceptor implements Interceptor {

    private final String pid;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public CregisAuthInterceptor(String pid, String apiKey, ObjectMapper objectMapper) {
        this.pid = pid;
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request original = chain.request();

        if (original.body() == null) {
            // For GET requests or empty body, we might not need to sign logic roughly?
            // Doc says "method is only http post".
            // So we assume POST with JSON.
            throw new CregisClientException("Request body is required for Cregis API signing");
        }

        // 1. Parse existing body
        Map<String, Object> params = parseBodyToMap(original.body());

        if (params == null) {
            params = new HashMap<>();
        }

        // 2. Add System Params
        params.put("pid", pid); // Use String to stay consistent with signer's value.toString()
        params.put("nonce", generateNonce());
        params.put("timestamp", System.currentTimeMillis());

        // 3. Calculate Sign
        String sign = CregisSigner.sign(params, apiKey);
        params.put("sign", sign);

        // 4. Re-construct Request
        String newJson = objectMapper.writeValueAsString(params);
        RequestBody newBody = RequestBody.create(newJson, MediaType.get("application/json; charset=utf-8"));

        Request newRequest = original.newBuilder()
                .post(newBody)
                .build();

        return chain.proceed(newRequest);
    }

    private Map<String, Object> parseBodyToMap(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        if (buffer.size() == 0) {
            return new HashMap<>();
        }
        // Assuming Map<String, Object> is safe for topmost JSON object
        return objectMapper.readValue(buffer.inputStream(), Map.class);
    }

    private String generateNonce() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }
}
