package com.gepardec.lernelamalesen.domain.model;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class FormDataTest {
    
    @Test
    void shouldCreateFormDataWithDefaults() {
        FormData formData = FormData.create("test.pdf");
        
        assertNotNull(formData.id());
        assertEquals("test.pdf", formData.originalFilename());
        assertEquals(FormData.ProcessingStatus.PENDING, formData.status());
        assertTrue(formData.extractedFields().isEmpty());
        assertNotNull(formData.processedAt());
        assertNull(formData.errorMessage());
    }
    
    @Test
    void shouldUpdateWithExtractedFields() {
        FormData original = FormData.create("test.pdf");
        Map<String, Object> fields = Map.of("name", "John Doe", "age", 30);
        
        FormData updated = original.withExtractedFields(fields);
        
        assertEquals(original.id(), updated.id());
        assertEquals(FormData.ProcessingStatus.COMPLETED, updated.status());
        assertEquals(fields, updated.extractedFields());
    }
    
    @Test
    void shouldUpdateWithError() {
        FormData original = FormData.create("test.pdf");
        String error = "Processing failed";
        
        FormData updated = original.withError(error);
        
        assertEquals(original.id(), updated.id());
        assertEquals(FormData.ProcessingStatus.FAILED, updated.status());
        assertEquals(error, updated.errorMessage());
    }
    
    @Test
    void shouldUpdateStatus() {
        FormData original = FormData.create("test.pdf");
        
        FormData updated = original.withStatus(FormData.ProcessingStatus.PROCESSING);
        
        assertEquals(original.id(), updated.id());
        assertEquals(FormData.ProcessingStatus.PROCESSING, updated.status());
    }
}