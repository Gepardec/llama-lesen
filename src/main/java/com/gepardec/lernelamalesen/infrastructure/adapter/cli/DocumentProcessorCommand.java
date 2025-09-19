package com.gepardec.lernelamalesen.infrastructure.adapter.cli;

import com.gepardec.lernelamalesen.application.service.DocumentProcessingService;
import com.gepardec.lernelamalesen.application.service.PromptService;
import com.gepardec.lernelamalesen.domain.model.ExampleImage;
import com.gepardec.lernelamalesen.domain.model.FormData;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@TopCommand
@CommandLine.Command(
    name = "lerne-lama-lesen",
    description = "Process handwritten PDF forms using LLM analysis",
    mixinStandardHelpOptions = true,
    version = "1.0.0"
)
public class DocumentProcessorCommand implements Runnable {
    
    @CommandLine.Option(
        names = {"-f", "--file"}, 
        description = "PDF file to process", 
        required = true
    )
    File pdfFile;
    
    @CommandLine.Option(
        names = {"-v", "--verbose"}, 
        description = "Enable verbose output"
    )
    boolean verbose;
    
    @CommandLine.Option(
        names = {"-e", "--examples"}, 
        description = "Path to directory containing example images (JPG/PNG) and descriptions.prop file"
    )
    File examplesPath;
    
    @Inject
    DocumentProcessingService documentProcessingService;
    
    @Inject
    PromptService promptService;
    
    @Override
    public void run() {
        if (!pdfFile.exists()) {
            System.err.println("Error: File does not exist: " + pdfFile.getAbsolutePath());
            System.exit(1);
        }
        
        if (!pdfFile.getName().toLowerCase().endsWith(".pdf")) {
            System.err.println("Error: File must be a PDF: " + pdfFile.getName());
            System.exit(1);
        }
        
        try (InputStream fileStream = new FileInputStream(pdfFile)) {
            if (verbose) {
                System.out.println("Processing file: " + pdfFile.getAbsolutePath());
            }
            
            // Load example images if path is provided
            List<ExampleImage> exampleImages = List.of();
            if (examplesPath != null) {
                if (verbose) {
                    System.out.println("Loading example images from: " + examplesPath.getAbsolutePath());
                }
                exampleImages = promptService.loadExampleImages(examplesPath);
                if (verbose) {
                    System.out.println("Loaded " + exampleImages.size() + " example images");
                }
            }
            
            FormData result = documentProcessingService.processDocument(
                fileStream, 
                pdfFile.getName(),
                exampleImages
            );
            
            printResult(result);
            
        } catch (Exception e) {
            System.err.println("Error processing file: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
    
    private void printResult(FormData result) {
        System.out.println("Processing completed!");
        System.out.println("ID: " + result.id());
        System.out.println("Status: " + result.status());
        System.out.println("Processed at: " + result.processedAt());
        
        if (result.status() == FormData.ProcessingStatus.COMPLETED) {
            System.out.println("Extracted data:");
            result.extractedFields().forEach((key, value) -> 
                System.out.println("  " + key + ": " + value)
            );
        } else if (result.status() == FormData.ProcessingStatus.FAILED) {
            System.err.println("Error: " + result.errorMessage());
        }
    }
}