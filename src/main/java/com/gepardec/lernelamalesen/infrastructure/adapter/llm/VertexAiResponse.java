package com.gepardec.lernelamalesen.infrastructure.adapter.llm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record VertexAiResponse(
    @JsonProperty("candidates") List<Candidate> candidates
) {
    public record Candidate(
        @JsonProperty("content") Content content,
        @JsonProperty("finishReason") String finishReason
    ) {}
    
    public record Content(
        @JsonProperty("role") String role,
        @JsonProperty("parts") List<Part> parts
    ) {}
    
    public record Part(
        @JsonProperty("text") String text
    ) {}
}