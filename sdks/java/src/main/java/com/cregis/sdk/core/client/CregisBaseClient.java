package com.cregis.sdk.core.client;

import com.cregis.sdk.core.exception.CregisClientException;
import com.cregis.sdk.core.exception.CregisServerException;
import com.cregis.sdk.core.interceptor.CregisAuthInterceptor;
import com.cregis.sdk.core.interceptor.CregisLoggingInterceptor;
import com.cregis.sdk.domain.common.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class CregisBaseClient {

    protected final OkHttpClient httpClient;
    protected final ObjectMapper objectMapper;
    protected final String baseUrl;

    protected CregisBaseClient(String baseUrl, String pid, String apiKey, boolean debug) {
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        // Add Interceptors
        builder.addInterceptor(new CregisAuthInterceptor(pid, apiKey, objectMapper));
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
                throw new CregisClientException("Network Error: " + response.code() + " " + response.message());
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
}
