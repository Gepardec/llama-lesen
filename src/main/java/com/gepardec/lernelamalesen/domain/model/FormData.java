package com.gepardec.lernelamalesen.domain.model;

import java.time.LocalDateTime;
import java.util.Map;

public record FormData(
    String id,
    String originalFilename,
    Map<String, Object> extractedFields,
    ProcessingStatus status,
    LocalDateTime processedAt,
    String errorMessage
) {
    public enum ProcessingStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
    
    public static FormData create(String filename) {
        return new FormData(
            java.util.UUID.randomUUID().toString(),
            filename,
            Map.of(),
            ProcessingStatus.PENDING,
            LocalDateTime.now(),
            null
        );
    }
    
    public FormData withExtractedFields(Map<String, Object> fields) {
        return new FormData(id, originalFilename, fields, ProcessingStatus.COMPLETED, processedAt, errorMessage);
    }
    
    public FormData withError(String error) {
        return new FormData(id, originalFilename, extractedFields, ProcessingStatus.FAILED, processedAt, error);
    }
    
    public FormData withStatus(ProcessingStatus newStatus) {
        return new FormData(id, originalFilename, extractedFields, newStatus, processedAt, errorMessage);
    }
}