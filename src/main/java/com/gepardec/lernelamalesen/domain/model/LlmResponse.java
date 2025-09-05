package com.gepardec.lernelamalesen.domain.model;

import java.util.Map;

public record LlmResponse(
    Map<String, Object> structuredData,
    String rawResponse,
    boolean success,
    String errorMessage
) {
    public static LlmResponse success(Map<String, Object> data, String raw) {
        return new LlmResponse(data, raw, true, null);
    }
    
    public static LlmResponse failure(String error, String raw) {
        return new LlmResponse(Map.of(), raw, false, error);
    }
}