package com.gepardec.lernelamalesen.infrastructure.adapter.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "form_data")
public class FormDataEntity {
    
    @Id
    @Column(name = "id", length = 36)
    public String id;
    
    @Column(name = "original_filename", nullable = false)
    public String originalFilename;
    
    @Column(name = "extracted_fields", columnDefinition = "TEXT")
    public String extractedFields;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public ProcessingStatus status;
    
    @Column(name = "processed_at", nullable = false)
    public LocalDateTime processedAt;
    
    @Column(name = "error_message")
    public String errorMessage;
    
    public enum ProcessingStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}