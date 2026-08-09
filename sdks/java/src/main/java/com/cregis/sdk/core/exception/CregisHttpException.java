package com.cregis.sdk.core.exception;

import lombok.Getter;

/**
 * Represents a non-successful HTTP response from a Cregis endpoint.
 */
@Getter
public class CregisHttpException extends CregisException {

    private final int statusCode;
    private final String responseBody;

    public CregisHttpException(int statusCode, String message, String responseBody) {
        super("Cregis HTTP error: [" + statusCode + "] " + message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}
