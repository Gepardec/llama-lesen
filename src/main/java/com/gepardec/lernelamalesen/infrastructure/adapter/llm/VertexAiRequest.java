package com.gepardec.lernelamalesen.infrastructure.adapter.llm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record VertexAiRequest(
    @JsonProperty("contents") List<Content> contents,
    @JsonProperty("generationConfig") GenerationConfig generationConfig
) {
    public VertexAiRequest(String systemPrompt, String userPrompt, String base64Image, String mimeType) {
        this(
            List.of(
                new Content(
                    "user",
                    List.of(
                        new Part(systemPrompt + "\n\n" + userPrompt),
                        new Part(new InlineData(mimeType, base64Image))
                    )
                )
            ),
            new GenerationConfig(0.7, 1024, 0.8, 40)
        );
    }
    
    public record Content(
        @JsonProperty("role") String role,
        @JsonProperty("parts") List<Part> parts
    ) {}
    
    public record Part(
        @JsonProperty("text") String text,
        @JsonProperty("inlineData") InlineData inlineData
    ) {
        public Part(String text) {
            this(text, null);
        }
        
        public Part(InlineData inlineData) {
            this(null, inlineData);
        }
    }
    
    public record InlineData(
        @JsonProperty("mimeType") String mimeType,
        @JsonProperty("data") String data
    ) {}
    
    public record GenerationConfig(
        @JsonProperty("temperature") double temperature,
        @JsonProperty("maxOutputTokens") int maxOutputTokens,
        @JsonProperty("topP") double topP,
        @JsonProperty("topK") int topK
    ) {}
}