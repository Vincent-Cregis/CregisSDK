package com.cregis.sdk.domain.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TeamPagedResponse<T> {

    @JsonProperty("total")
    private Long total;

    @JsonProperty("page_num")
    private Integer pageNum;

    @JsonProperty("page_size")
    private Integer pageSize;

    @JsonProperty("rows")
    private List<T> rows;
}
