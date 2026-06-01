package com.example.gazago.gazago.transport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FastApiChatRequest {
    @JsonProperty("user_input")
    private String userInput;
}