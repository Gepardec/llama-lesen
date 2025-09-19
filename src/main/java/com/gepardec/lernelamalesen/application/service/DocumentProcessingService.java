package com.gepardec.lernelamalesen.application.service;

import com.gepardec.lernelamalesen.domain.model.DocumentImage;
import com.gepardec.lernelamalesen.domain.model.ExampleImage;
import com.gepardec.lernelamalesen.domain.model.FormData;
import com.gepardec.lernelamalesen.domain.model.LlmRequest;
import com.gepardec.lernelamalesen.domain.model.LlmResponse;
import com.gepardec.lernelamalesen.domain.port.FormDataRepositoryPort;
import com.gepardec.lernelamalesen.domain.port.LlmPort;
import com.gepardec.lernelamalesen.domain.port.PdfProcessorPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.util.List;

@ApplicationScoped
public class DocumentProcessingService {
    
    private static final Logger LOG = Logger.getLogger(DocumentProcessingService.class);
    
    @Inject
    PdfProcessorPort pdfProcessor;
    
    @Inject
    LlmPort llmService;
    
    @Inject
    FormDataRepositoryPort repository;
    
    @Inject
    PromptService promptService;
    
    public FormData processDocument(InputStream pdfStream, String filename) {
        return processDocument(pdfStream, filename, List.of());
    }
    
    public FormData processDocument(InputStream pdfStream, String filename, List<ExampleImage> exampleImages) {
        FormData formData = FormData.create(filename);
        formData = repository.save(formData.withStatus(FormData.ProcessingStatus.PROCESSING));
        
        try {
            LOG.infof("Starting document processing for file: %s", filename);
            
            List<DocumentImage> images = pdfProcessor.extractImagesFromPdf(pdfStream, filename);
            if (images.isEmpty()) {
                String error = "No images found in PDF document";
                LOG.error(error);
                return repository.save(formData.withError(error));
            }
            
            DocumentImage firstImage = images.get(0);
            
            LlmRequest request = new LlmRequest(
                firstImage,
                promptService.getSystemPrompt(),
                promptService.getUserPrompt(),
                exampleImages
            );
            
            LlmResponse response = llmService.analyzeDocument(request);
            
            if (response.success()) {
                LOG.infof("Successfully processed document: %s", filename);
                return repository.save(formData.withExtractedFields(response.structuredData()));
            } else {
                LOG.errorf("LLM processing failed: %s", response.errorMessage());
                return repository.save(formData.withError(response.errorMessage()));
            }
            
        } catch (Exception e) {
            LOG.errorf(e, "Error processing document: %s", filename);
            return repository.save(formData.withError("Processing failed: " + e.getMessage()));
        }
    }
}