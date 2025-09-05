package com.gepardec.lernelamalesen.infrastructure.adapter.llm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record VertexAiResponse(
    @JsonProperty("choices") List<Choice> choices
) {
    public record Choice(
        @JsonProperty("message") Message message,
        @JsonProperty("finish_reason") String finishReason
    ) {}
    
    public record Message(
        @JsonProperty("role") String role,
        @JsonProperty("content") String content
    ) {}
}