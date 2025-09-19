package com.gepardec.lernelamalesen.domain.model;

import java.util.Collections;
import java.util.List;

public record LlmRequest(
    DocumentImage image,
    String systemPrompt,
    String userPrompt,
    List<ExampleImage> exampleImages
) {
    public LlmRequest {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        if (systemPrompt == null || systemPrompt.trim().isEmpty()) {
            throw new IllegalArgumentException("System prompt cannot be null or empty");
        }
        if (exampleImages == null) {
            exampleImages = Collections.emptyList();
        }
    }
    
    public LlmRequest(DocumentImage image, String systemPrompt, String userPrompt) {
        this(image, systemPrompt, userPrompt, Collections.emptyList());
    }
}