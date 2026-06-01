package com.example.gazago.gazago.transport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class PythonSuccessResponse {
    @JsonProperty("status")
    private String status;      // "success" 혹은 "fail"

    @JsonProperty("data_count")
    private Integer dataCount;
}