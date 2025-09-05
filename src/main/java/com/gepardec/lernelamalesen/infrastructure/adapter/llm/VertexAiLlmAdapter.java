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
    
    @ConfigProperty(name = "vertex.ai.model", defaultValue = "gemini-1.5-pro-vision-001")
    String model;
    
    @Override
    public LlmResponse analyzeDocument(LlmRequest request) {
        try {
            LOG.infof("Sending request to Vertex AI for image: %s", request.image().filename());
            
            String base64Image = Base64.getEncoder().encodeToString(request.image().imageData());
            
            VertexAiRequest vertexRequest = new VertexAiRequest(
                request.systemPrompt(),
                request.userPrompt(),
                base64Image,
                request.image().mimeType()
            );
            
            VertexAiResponse response = vertexAiClient.generateContent(
                projectId, 
                location, 
                model, 
                vertexRequest
            );
            
            if (response.candidates() != null && !response.candidates().isEmpty()) {
                String rawResponse = response.candidates().get(0).content().parts().get(0).text();
                
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
            
        } catch (Exception e) {
            LOG.errorf(e, "Error calling Vertex AI");
            return LlmResponse.failure("Vertex AI error: " + e.getMessage(), "");
        }
    }
}