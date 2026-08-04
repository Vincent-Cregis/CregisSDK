package com.cregis.sdk.core.exception;

import lombok.Getter;

/**
 * Exception thrown when the Cregis API returns an error code.
 */
@Getter
public class CregisServerException extends CregisException {

    private final String code;
    private final String msg;

    /**
     * @param code The error code returned by the API (e.g. "40001").
     * @param msg  The error message returned by the API.
     */
    public CregisServerException(String code, String msg) {
        super(String.format("Cregis API Error: [%s] %s", code, msg));
        this.code = code;
        this.msg = msg;
    }
}
