package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * One address returned by the batch address creation operation.
 */
@Data
public class GeneratedAddress {

    @JsonProperty("address")
    private String address;
}
