package com.cregis.sdk.contract;

import com.cregis.sdk.core.client.CregisHttpConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/** Validates final signed Sandbox traffic against a canonical OpenAPI file. */
public final class SandboxContractProbe implements Interceptor {

    private static final long MAX_RESPONSE_BYTES = 16L * 1024L * 1024L;

    private final OpenApiContractValidator contract;
    private final Set<String> coveredOperationIds = Collections.synchronizedSet(new LinkedHashSet<>());

    private SandboxContractProbe(OpenApiContractValidator contract) {
        this.contract = contract;
    }

    public static SandboxContractProbe forApi(String apiName) {
        return new SandboxContractProbe(OpenApiContractValidator.load(
                OpenApiSpecLocator.locateSpec(apiName)));
    }

    public CregisHttpConfig httpConfig() {
        OkHttpClient observingClient = new OkHttpClient.Builder()
                .addInterceptor(this)
                .build();
        return CregisHttpConfig.builder().httpClient(observingClient).build();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        Request sentRequest = response.request();
        String path = sentRequest.url().encodedPath();
        String requestBody = readRequestBody(sentRequest.body());

        String operationId = contract.validateRequest(
                sentRequest.method(),
                path,
                sentRequest.headers(),
                requestBody);
        String responseBody = response.peekBody(MAX_RESPONSE_BYTES).string();
        contract.validateResponse(
                sentRequest.method(),
                path,
                response.code(),
                responseBody);
        coveredOperationIds.add(operationId);
        return response;
    }

    public void assertAllCallableOperationsCovered() {
        Set<String> missing = contract.callableOperationIds();
        synchronized (coveredOperationIds) {
            missing.removeAll(coveredOperationIds);
        }
        if (!missing.isEmpty()) {
            throw new AssertionError("OpenAPI operations were not contract-validated: " + missing);
        }
    }

    public Set<String> coveredOperationIds() {
        synchronized (coveredOperationIds) {
            return new LinkedHashSet<>(coveredOperationIds);
        }
    }

    private String readRequestBody(RequestBody body) throws IOException {
        if (body == null) {
            return null;
        }
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readUtf8();
    }
}
