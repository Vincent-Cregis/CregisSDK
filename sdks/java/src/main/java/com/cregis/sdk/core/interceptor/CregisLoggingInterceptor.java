package com.cregis.sdk.core.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Interceptor to log requests and responses.
 * Recommend turning this off in production.
 */
public class CregisLoggingInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(CregisLoggingInterceptor.class);
    private final boolean enabled;

    public CregisLoggingInterceptor(boolean enabled) {
        this.enabled = enabled;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        if (!enabled) {
            return chain.proceed(request);
        }

        long t1 = System.nanoTime();
        String requestBody = bodyToString(request);
        logger.info("Sending request {} on {}\n{}", request.url(), chain.connection(), requestBody);

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        String responseBody = "";
        ResponseBody body = response.body();
        // Clone the body content to log it without consuming it
        if (body != null) {
            okio.BufferedSource source = body.source();
            source.request(Long.MAX_VALUE); // Buffer entire body.
            Buffer buffer = source.getBuffer();
            responseBody = buffer.clone().readString(StandardCharsets.UTF_8);
        }

        logger.info("Received response for {} in {:.1f}ms\nResponse Code: {}\nResponse Body: {}",
                response.request().url(), (t2 - t1) / 1e6d, response.code(), responseBody);

        return response;
    }

    private String bodyToString(Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            if (copy.body() != null) {
                copy.body().writeTo(buffer);
            }
            return buffer.readString(StandardCharsets.UTF_8);
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
