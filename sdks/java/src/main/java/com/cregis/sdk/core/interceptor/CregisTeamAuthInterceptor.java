package com.cregis.sdk.core.interceptor;

import com.cregis.sdk.core.exception.CregisClientException;
import com.cregis.sdk.core.signer.CregisTeamSigner;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Adds the four Access-* authentication headers required by Team API.
 */
public class CregisTeamAuthInterceptor implements Interceptor {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final String accessKey;
    private final String accessSecret;
    private final LongSupplier timestampSupplier;
    private final Supplier<String> nonceSupplier;

    public CregisTeamAuthInterceptor(String accessKey, String accessSecret) {
        this(
                accessKey,
                accessSecret,
                System::currentTimeMillis,
                () -> UUID.randomUUID().toString().replace("-", ""));
    }

    CregisTeamAuthInterceptor(
            String accessKey,
            String accessSecret,
            LongSupplier timestampSupplier,
            Supplier<String> nonceSupplier) {
        if (accessKey == null || accessKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Access Key is required");
        }
        if (accessSecret == null || accessSecret.trim().isEmpty()) {
            throw new IllegalArgumentException("Access Secret is required");
        }
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
        this.timestampSupplier = timestampSupplier;
        this.nonceSupplier = nonceSupplier;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request original = chain.request();
        String rawBody = readBody(original.body());
        String canonicalBody;
        try {
            canonicalBody = CregisTeamSigner.canonicalizeBody(rawBody);
        } catch (IllegalArgumentException e) {
            throw new CregisClientException("Failed to canonicalize Team API request body", e);
        }

        long timestamp = timestampSupplier.getAsLong();
        String nonce = nonceSupplier.get();
        String path = original.url().encodedPath();
        String signature = CregisTeamSigner.sign(path, timestamp, nonce, canonicalBody, accessSecret);

        RequestBody canonicalRequestBody = canonicalBody.isEmpty()
                ? original.body()
                : RequestBody.create(canonicalBody, original.body() == null || original.body().contentType() == null
                        ? JSON
                        : original.body().contentType());

        Request signed = original.newBuilder()
                .method(original.method(), canonicalRequestBody)
                .header("Access-Key", accessKey)
                .header("Access-Timestamp", Long.toString(timestamp))
                .header("Access-Nonce", nonce)
                .header("Access-Signature", signature)
                .build();
        return chain.proceed(signed);
    }

    private static String readBody(RequestBody body) throws IOException {
        if (body == null) {
            return "";
        }
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readUtf8();
    }
}
