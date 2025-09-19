package com.gepardec.lernelamalesen.application.service;

import com.gepardec.lernelamalesen.domain.model.ExampleImage;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

@ApplicationScoped
public class PromptService {

    @ConfigProperty(name = "llm.prompt.system",
            defaultValue = "Sie sind ein KI-Assistent, der darauf spezialisiert ist, handschriftlich ausgefüllte Formulare zu analysieren und strukturierte Daten zu extrahieren.")
    String systemPrompt;

    @ConfigProperty(name = "llm.prompt.user",
            defaultValue = "Analysieren Sie dieses handschriftlich ausgefüllte Formular und extrahieren Sie alle lesbaren Informationen in einem strukturierten JSON-Format. Geben Sie nur güliges JSON zurück.")
    String userPrompt;

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public String getUserPrompt() {
        return userPrompt;
    }

    public List<ExampleImage> loadExampleImages(File examplesPath) {
        List<ExampleImage> exampleImages = new ArrayList<>();

        if (examplesPath == null || !examplesPath.exists() || !examplesPath.isDirectory()) {
            return exampleImages;
        }

        try {
            Properties descriptions = loadDescriptions(examplesPath);

            File[] imageFiles = examplesPath.listFiles((dir, name) -> {
                String lowerName = name.toLowerCase();
                return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png");
            });

            if (imageFiles != null) {
                for (File imageFile : imageFiles) {
                    try {
                        String fileName = imageFile.getName();
                        String description = descriptions.getProperty(fileName, "");

                        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
                        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                        String mimeType = getMimeType(fileName);

                        exampleImages.add(new ExampleImage(base64Image, mimeType, description));

                    } catch (IOException e) {
                        System.err.println("Error reading image file: " + imageFile.getName() + " - " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading example images: " + e.getMessage());
        }

        return exampleImages;
    }

    private Properties loadDescriptions(File examplesPath) {
        Properties descriptions = new Properties();
        File propsFile = new File(examplesPath, "descriptions.prop");

        if (propsFile.exists() && propsFile.isFile()) {
            try (InputStreamReader reader = new InputStreamReader(
                    Files.newInputStream(propsFile.toPath()), 
                    StandardCharsets.UTF_8)) {
                descriptions.load(reader);
            } catch (IOException e) {
                System.err.println("Error loading descriptions.prop: " + e.getMessage());
            }
        }

        return descriptions;
    }

    private String getMimeType(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerName.endsWith(".png")) {
            return "image/png";
        }
        return "application/octet-stream";
    }
}