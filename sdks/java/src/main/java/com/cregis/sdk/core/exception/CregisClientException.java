package com.cregis.sdk.core.exception;

/**
 * Exception thrown when a client-side error occurs, such as invalid parameters or network issues.
 */
public class CregisClientException extends CregisException {

    public CregisClientException(String message) {
        super(message);
    }

    public CregisClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
