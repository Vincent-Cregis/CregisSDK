package com.cregis.sdk.core.exception;

/**
 * Base exception for all Cregis SDK errors.
 */
public class CregisException extends RuntimeException {

    public CregisException(String message) {
        super(message);
    }

    public CregisException(String message, Throwable cause) {
        super(message, cause);
    }
}
