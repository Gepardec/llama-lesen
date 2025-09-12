package com.gepardec.lernelamalesen.infrastructure.adapter.llm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record VertexAiRequest(
    @JsonProperty("model") String model,
    @JsonProperty("messages") List<Message> messages,
    @JsonProperty("response_format") ResponseFormat responseFormat
) {
    public VertexAiRequest(String model, String systemPrompt, String userPrompt, String base64Image, String mimeType) {
        this(
            model,
            List.of(
                new Message(
                    "user",
                    List.of(
                        new Content("text", systemPrompt + "\n\n" + userPrompt),
                        new Content("image_url", new ImageUrl("data:" + mimeType + ";base64," + base64Image))
                    )
                )
            ),
            new ResponseFormat("json_object")
        );
    }
    
    public record Message(
        @JsonProperty("role") String role,
        @JsonProperty("content") List<Content> content
    ) {}
    
    public record Content(
        @JsonProperty("type") String type,
        @JsonProperty("text") String text,
        @JsonProperty("image_url") ImageUrl imageUrl
    ) {
        public Content(String type, String text) {
            this(type, text, null);
        }
        
        public Content(String type, ImageUrl imageUrl) {
            this(type, null, imageUrl);
        }
    }
    
    public record ImageUrl(
        @JsonProperty("url") String url
    ) {}
    
    public record ResponseFormat(
        @JsonProperty("type") String type
    ) {}
}