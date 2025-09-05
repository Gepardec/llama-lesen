package com.gepardec.lernelamalesen.domain.model;

public record DocumentImage(
    byte[] imageData,
    String mimeType,
    String filename
) {
    public DocumentImage {
        if (imageData == null || imageData.length == 0) {
            throw new IllegalArgumentException("Image data cannot be null or empty");
        }
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
    }
}