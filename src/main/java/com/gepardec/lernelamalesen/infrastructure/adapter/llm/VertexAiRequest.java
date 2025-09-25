package com.gepardec.lernelamalesen.infrastructure.adapter.llm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.ArrayList;

public record VertexAiRequest(
        @JsonProperty("model") String model,
        @JsonProperty("messages") List<Message> messages,
        @JsonProperty("response_format") ResponseFormat responseFormat,
        @JsonProperty("temperature") Double temperature,
        @JsonProperty("max_tokens") Integer maxTokens
) {
    // Constructor for OCR with system and user prompts
    public VertexAiRequest(String model, String systemPrompt, String userPrompt, String base64Image, String mimeType) {
        this(
                model,
                List.of(
                        new Message(
                                "system",
                                systemPrompt
                        ),
                        new Message(
                                "user",
                                List.of(
                                        new Content("text", userPrompt),
                                        new Content("image_url", new ImageUrl("data:" + mimeType + ";base64," + base64Image))
                                )
                        )
                ),
                new ResponseFormat("json_object"),
                0.1,  // Low temperature for OCR accuracy
                4096  // Sufficient tokens for JSON response
        );
    }

    // Constructor with example images for checkbox training
    public VertexAiRequest(String model, String systemPrompt, String userPrompt,
                           String mainImage, String mimeType,
                           List<ExampleImage> exampleImages) {
        this(
                model,
                buildMessagesWithExamples(systemPrompt, userPrompt, mainImage, mimeType, exampleImages),
                new ResponseFormat("json_object"),
                0.1,
                4096
        );
    }

    private static List<Message> buildMessagesWithExamples(String systemPrompt, String userPrompt,
                                                           String mainImage, String mimeType,
                                                           List<ExampleImage> examples) {
        List<Message> messages = new ArrayList<>();

        // Add system message
        messages.add(new Message("system", systemPrompt));

        // Build user message with examples and main image
        List<Content> userContent = new ArrayList<>();

        // Add initial prompt
        userContent.add(new Content("text", userPrompt));

        // Add main document image FIRST (most important)
        userContent.add(new Content("image_url",
                new ImageUrl("data:" + mimeType + ";base64," + mainImage)));

        // Add reference images if they exist
        if (examples != null && !examples.isEmpty()) {
            for (ExampleImage example : examples) {
                if (example.description() != null && !example.description().isEmpty()) {
                    userContent.add(new Content("text", example.description()));
                }
                userContent.add(new Content("image_url",
                        new ImageUrl("data:" + example.mimeType() + ";base64," + example.base64())));
            }
        }

        // Add final reminder (with correct escaping)
        userContent.add(new Content("text", "Return only JSON matching the schema. Remember: any mark = true, empty = \"\", Ja/Nein pairs use true/false/\"\"."));

        messages.add(new Message("user", userContent));
        return messages;
    }

    public record Message(
            @JsonProperty("role") String role,
            @JsonProperty("content") Object content  // Can be String or List<Content>
    ) {
        // Constructor for simple text message (for system role)
        public Message(String role, String text) {
            this(role, (Object) text);
        }

        // Constructor for complex content (for user role with images)
        public Message(String role, List<Content> contentList) {
            this(role, (Object) contentList);
        }
    }

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
    ) {
    }

    public record ResponseFormat(
            @JsonProperty("type") String type
    ) {
    }

    public record ExampleImage(
            String base64,
            String mimeType,
            String description
    ) {
    }
}