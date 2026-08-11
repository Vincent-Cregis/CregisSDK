package com.cregis.sdk.core.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

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

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!enabled) {
            return chain.proceed(request);
        }

        long t1 = System.nanoTime();
        logger.info("Sending {} request to {}", request.method(), request.url());

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        logger.info("Received HTTP {} from {} in {} ms",
                response.code(),
                response.request().url(),
                String.format(Locale.ROOT, "%.1f", (t2 - t1) / 1e6d));

        return response;
    }
}
