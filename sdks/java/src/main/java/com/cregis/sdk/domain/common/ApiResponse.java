package com.cregis.sdk.domain.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Standard API Response structure.
 * {
 * "code": "00000",
 * "msg": "ok",
 * "data": { ... }
 * }
 *
 * @param <T> The type of the data field.
 */
@Data
public class ApiResponse<T> {

    @JsonProperty("code")
    private String code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private T data;

    public boolean isSuccess() {
        return "00000".equals(code);
    }
}
