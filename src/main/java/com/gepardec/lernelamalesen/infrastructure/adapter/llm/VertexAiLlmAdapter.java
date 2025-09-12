package com.gepardec.lernelamalesen.infrastructure.adapter.llm;

import com.gepardec.lernelamalesen.domain.model.LlmRequest;
import com.gepardec.lernelamalesen.domain.model.LlmResponse;
import com.gepardec.lernelamalesen.domain.port.LlmPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Map;

@ApplicationScoped
public class VertexAiLlmAdapter implements LlmPort {
    
    private static final Logger LOG = Logger.getLogger(VertexAiLlmAdapter.class);
    
    @Inject
    @RestClient
    VertexAiClient vertexAiClient;
    
    @Inject
    ObjectMapper objectMapper;
    
    @ConfigProperty(name = "vertex.ai.project-id")
    String projectId;
    
    @ConfigProperty(name = "vertex.ai.location", defaultValue = "us-central1")
    String location;

    @ConfigProperty(name = "vertex.ai.publisher", defaultValue = "google")
    String publisher;
    
    @ConfigProperty(name = "vertex.ai.model", defaultValue = "gemini-1.5-pro-vision-001")
    String model;
    
    @Override
    public LlmResponse analyzeDocument(LlmRequest request) {
        try {
            LOG.infof("Sending request to Vertex AI for image: %s", request.image().filename());
            LOG.infof("Project ID: %s, Location: %s, Model: %s", projectId, location, model);
            
            String base64Image = Base64.getEncoder().encodeToString(request.image().imageData());
            LOG.debugf("Base64 image length: %d characters", base64Image.length());
            
            VertexAiRequest vertexRequest = new VertexAiRequest(
                publisher + "/" + model,
                request.systemPrompt(),
                request.userPrompt(),
                base64Image,
                request.image().mimeType()
            );
            
            // Log the request structure (without the full base64 image)
            LOG.infof("Request URL: /v1/projects/%s/locations/%s/endpoints/openapi/chat/completions", projectId, location);
            LOG.infof("Full model name: %s", publisher + "/" + model);
            LOG.infof("Image MIME type: %s", request.image().mimeType());
            LOG.infof("System prompt length: %d", request.systemPrompt().length());
            LOG.infof("User prompt length: %d", request.userPrompt().length());
            
            try {
                // Log actual JSON that will be sent
                String jsonRequest = objectMapper.writeValueAsString(vertexRequest);
                LOG.infof("Request JSON (first 1000 chars): %s", 
                    jsonRequest.length() > 1000 ? jsonRequest.substring(0, 1000) + "..." : jsonRequest);
            } catch (Exception e) {
                LOG.warnf("Could not serialize request to JSON: %s", e.getMessage());
            }
            
            VertexAiResponse response = vertexAiClient.generateContent(
                projectId, 
                location, 
                vertexRequest
            );
            
            if (response.choices() != null && !response.choices().isEmpty()) {
                String rawResponse = response.choices().get(0).message().content();
                
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> structuredData = objectMapper.readValue(rawResponse, Map.class);
                    return LlmResponse.success(structuredData, rawResponse);
                } catch (Exception e) {
                    LOG.warnf("Failed to parse response as JSON, returning as text: %s", e.getMessage());
                    return LlmResponse.success(Map.of("raw_text", rawResponse), rawResponse);
                }
            } else {
                return LlmResponse.failure("No response from Vertex AI", "");
            }
            
        } catch (jakarta.ws.rs.WebApplicationException e) {
            String responseBody = "";
            try {
                if (e.getResponse() != null) {
                    responseBody = e.getResponse().readEntity(String.class);
                    LOG.errorf("Vertex AI HTTP %d error response: %s", e.getResponse().getStatus(), responseBody);
                }
            } catch (Exception readException) {
                LOG.warnf("Could not read error response body: %s", readException.getMessage());
            }
            LOG.errorf(e, "Error calling Vertex AI: HTTP %d", e.getResponse() != null ? e.getResponse().getStatus() : -1);
            return LlmResponse.failure("Vertex AI HTTP error: " + e.getMessage() + " - " + responseBody, "");
        } catch (Exception e) {
            LOG.errorf(e, "Error calling Vertex AI");
            return LlmResponse.failure("Vertex AI error: " + e.getMessage(), "");
        }
    }
}