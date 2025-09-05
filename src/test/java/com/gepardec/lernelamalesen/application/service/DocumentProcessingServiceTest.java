package com.gepardec.lernelamalesen.application.service;

import com.gepardec.lernelamalesen.domain.model.DocumentImage;
import com.gepardec.lernelamalesen.domain.model.FormData;
import com.gepardec.lernelamalesen.domain.model.LlmRequest;
import com.gepardec.lernelamalesen.domain.model.LlmResponse;
import com.gepardec.lernelamalesen.domain.port.FormDataRepositoryPort;
import com.gepardec.lernelamalesen.domain.port.LlmPort;
import com.gepardec.lernelamalesen.domain.port.PdfProcessorPort;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
class DocumentProcessingServiceTest {
    
    @Inject
    DocumentProcessingService service;
    
    @InjectMock
    PdfProcessorPort pdfProcessor;
    
    @InjectMock
    LlmPort llmService;
    
    @InjectMock
    FormDataRepositoryPort repository;
    
    @InjectMock
    PromptService promptService;
    
    private DocumentImage testImage;
    private FormData testFormData;
    
    @BeforeEach
    void setUp() {
        testImage = new DocumentImage("test image".getBytes(), "image/png", "test.png");
        testFormData = FormData.create("test.pdf");
        
        when(repository.save(any(FormData.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(promptService.getSystemPrompt()).thenReturn("System prompt");
        when(promptService.getUserPrompt()).thenReturn("User prompt");
    }
    
    @Test
    void shouldProcessDocumentSuccessfully() {
        InputStream inputStream = new ByteArrayInputStream("pdf content".getBytes());
        
        when(pdfProcessor.extractImagesFromPdf(any(InputStream.class), anyString()))
            .thenReturn(List.of(testImage));
        
        Map<String, Object> extractedData = Map.of("name", "John Doe", "age", 30);
        LlmResponse llmResponse = LlmResponse.success(extractedData, "raw response");
        when(llmService.analyzeDocument(any(LlmRequest.class))).thenReturn(llmResponse);
        
        FormData result = service.processDocument(inputStream, "test.pdf");
        
        assertEquals(FormData.ProcessingStatus.COMPLETED, result.status());
        assertEquals(extractedData, result.extractedFields());
        assertNull(result.errorMessage());
        
        verify(pdfProcessor).extractImagesFromPdf(any(InputStream.class), eq("test.pdf"));
        verify(llmService).analyzeDocument(any(LlmRequest.class));
        verify(repository, times(2)).save(any(FormData.class));
    }
    
    @Test
    void shouldHandleNoImagesFound() {
        InputStream inputStream = new ByteArrayInputStream("pdf content".getBytes());
        
        when(pdfProcessor.extractImagesFromPdf(any(InputStream.class), anyString()))
            .thenReturn(List.of());
        
        FormData result = service.processDocument(inputStream, "test.pdf");
        
        assertEquals(FormData.ProcessingStatus.FAILED, result.status());
        assertEquals("No images found in PDF document", result.errorMessage());
        
        verify(llmService, never()).analyzeDocument(any(LlmRequest.class));
    }
    
    @Test
    void shouldHandleLlmFailure() {
        InputStream inputStream = new ByteArrayInputStream("pdf content".getBytes());
        
        when(pdfProcessor.extractImagesFromPdf(any(InputStream.class), anyString()))
            .thenReturn(List.of(testImage));
        
        LlmResponse llmResponse = LlmResponse.failure("LLM error", "");
        when(llmService.analyzeDocument(any(LlmRequest.class))).thenReturn(llmResponse);
        
        FormData result = service.processDocument(inputStream, "test.pdf");
        
        assertEquals(FormData.ProcessingStatus.FAILED, result.status());
        assertEquals("LLM error", result.errorMessage());
    }
    
    @Test
    void shouldHandleProcessingException() {
        InputStream inputStream = new ByteArrayInputStream("pdf content".getBytes());
        
        when(pdfProcessor.extractImagesFromPdf(any(InputStream.class), anyString()))
            .thenThrow(new RuntimeException("PDF processing failed"));
        
        FormData result = service.processDocument(inputStream, "test.pdf");
        
        assertEquals(FormData.ProcessingStatus.FAILED, result.status());
        assertTrue(result.errorMessage().contains("Processing failed"));
    }
}