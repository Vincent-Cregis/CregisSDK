package com.cregis.sdk.domain.waas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BatchGenerateAddressResponse {

    // The API returns an array of objects directly in data, or a wrapper?
    // Based on docs: "data": [ { "address": "..." }, ... ]
    // So the response type in `ApiResponse<List<AddressWrapper>>` or similar.
    // However, CregisBaseClient maps data to T.
    // Let's create a Wrapper class if needed, or just use List<GeneratedAddress>

    @Data
    public static class GeneratedAddress {
        @JsonProperty("address")
        private String address;
    }
}
