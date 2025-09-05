package com.gepardec.lernelamalesen.domain.model;

public record LlmRequest(
    DocumentImage image,
    String systemPrompt,
    String userPrompt
) {
    public LlmRequest {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        if (systemPrompt == null || systemPrompt.trim().isEmpty()) {
            throw new IllegalArgumentException("System prompt cannot be null or empty");
        }
    }
}