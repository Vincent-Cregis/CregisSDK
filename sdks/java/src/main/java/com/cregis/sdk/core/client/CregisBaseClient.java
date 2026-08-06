package com.cregis.sdk.core.client;

import com.cregis.sdk.core.exception.CregisClientException;
import com.cregis.sdk.core.exception.CregisHttpException;
import com.cregis.sdk.core.exception.CregisServerException;
import com.cregis.sdk.core.interceptor.CregisLoggingInterceptor;
import com.cregis.sdk.domain.common.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class CregisBaseClient {

    protected final OkHttpClient httpClient;
    protected final ObjectMapper objectMapper;
    protected final String baseUrl;

    protected CregisBaseClient(
            String baseUrl,
            boolean debug,
            Function<ObjectMapper, Interceptor> authenticationInterceptorFactory) {
        this.baseUrl = normalizeBaseUrl(baseUrl);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        if (authenticationInterceptorFactory != null) {
            builder.addInterceptor(authenticationInterceptorFactory.apply(objectMapper));
        }
        builder.addInterceptor(new CregisLoggingInterceptor(debug));

        this.httpClient = builder.build();
    }

    /**
     * Executes a request and returns the parsed response data.
     *
     * @param request       The OkHttp request to execute.
     * @param typeReference The Jackson TypeReference for the response data type.
     * @param <T>           The type of the response data.
     * @return The data object from the API response.
     */
    protected <T> T execute(Request request, TypeReference<ApiResponse<T>> typeReference) {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() == null ? null : response.body().string();
                throw new CregisHttpException(response.code(), response.message(), errorBody);
            }

            if (response.body() == null) {
                throw new CregisClientException("Empty response body");
            }

            String bodyString = response.body().string();
            // Parse into ApiResponse
            ApiResponse<T> apiResponse = objectMapper.readValue(bodyString, typeReference);

            if (!apiResponse.isSuccess()) {
                throw new CregisServerException(apiResponse.getCode(), apiResponse.getMsg());
            }

            return apiResponse.getData();

        } catch (IOException e) {
            throw new CregisClientException("IO Exception executing request", e);
        }
    }

    protected Request.Builder post(String path, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
            return new Request.Builder()
                    .url(baseUrl + path)
                    .post(body);
        } catch (JsonProcessingException e) {
            throw new CregisClientException("Failed to serialize request body", e);
        }
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Base URL is required");
        }

        String normalized = baseUrl.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        HttpUrl parsed = HttpUrl.parse(normalized);
        if (parsed == null) {
            throw new IllegalArgumentException("Base URL must be a valid HTTP(S) URL");
        }
        if (!parsed.username().isEmpty() || !parsed.password().isEmpty()) {
            throw new IllegalArgumentException("Base URL must not contain user information");
        }
        if (parsed.query() != null || parsed.fragment() != null) {
            throw new IllegalArgumentException("Base URL must not contain a query or fragment");
        }

        boolean loopback = "localhost".equalsIgnoreCase(parsed.host())
                || "127.0.0.1".equals(parsed.host())
                || "::1".equals(parsed.host());
        if (!parsed.isHttps() && !loopback) {
            throw new IllegalArgumentException("Base URL must use HTTPS");
        }

        return normalized;
    }
}
