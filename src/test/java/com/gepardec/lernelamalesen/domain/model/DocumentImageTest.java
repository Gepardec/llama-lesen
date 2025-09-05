package com.gepardec.lernelamalesen.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DocumentImageTest {
    
    @Test
    void shouldCreateValidDocumentImage() {
        byte[] imageData = "fake image data".getBytes();
        
        DocumentImage image = new DocumentImage(imageData, "image/png", "test.png");
        
        assertArrayEquals(imageData, image.imageData());
        assertEquals("image/png", image.mimeType());
        assertEquals("test.png", image.filename());
    }
    
    @Test
    void shouldThrowExceptionForNullImageData() {
        assertThrows(IllegalArgumentException.class, () -> 
            new DocumentImage(null, "image/png", "test.png")
        );
    }
    
    @Test
    void shouldThrowExceptionForEmptyImageData() {
        assertThrows(IllegalArgumentException.class, () -> 
            new DocumentImage(new byte[0], "image/png", "test.png")
        );
    }
    
    @Test
    void shouldThrowExceptionForNullFilename() {
        byte[] imageData = "fake image data".getBytes();
        
        assertThrows(IllegalArgumentException.class, () -> 
            new DocumentImage(imageData, "image/png", null)
        );
    }
    
    @Test
    void shouldThrowExceptionForEmptyFilename() {
        byte[] imageData = "fake image data".getBytes();
        
        assertThrows(IllegalArgumentException.class, () -> 
            new DocumentImage(imageData, "image/png", "")
        );
    }
}