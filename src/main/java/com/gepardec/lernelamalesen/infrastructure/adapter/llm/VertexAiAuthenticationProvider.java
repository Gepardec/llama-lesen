package com.gepardec.lernelamalesen.infrastructure.adapter.llm;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.FileInputStream;
import java.io.IOException;

@ApplicationScoped
public class VertexAiAuthenticationProvider implements ClientRequestFilter {
    
    private static final Logger LOG = Logger.getLogger(VertexAiAuthenticationProvider.class);
    
    @ConfigProperty(name = "vertex.ai.credentials.path", defaultValue = "")
    String credentialsPath;
    
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        try {
            GoogleCredentials credentials;
            
            if (credentialsPath != null && !credentialsPath.trim().isEmpty()) {
                // Use service account key file
                credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
            } else {
                // Use Application Default Credentials (for Google Cloud environments)
                credentials = GoogleCredentials.getApplicationDefault();
            }
            
            credentials = credentials.createScoped(java.util.List.of(
                "https://www.googleapis.com/auth/cloud-platform",
                "https://www.googleapis.com/auth/generative-language"
            ));
            credentials.refreshIfExpired();
            AccessToken token = credentials.getAccessToken();
            String accessToken = token.getTokenValue();
            if (accessToken != null && !accessToken.trim().isEmpty()) {
                requestContext.getHeaders().add("Authorization", "Bearer " + accessToken);
                LOG.debug("Added Bearer token to Vertex AI request");
            } else {
                LOG.warn("No access token configured for Vertex AI");
            }
        } catch (Exception e) {
            LOG.errorf("Failed to load Google credentials from path: %s", credentialsPath);
            throw new IOException("Authentication failed", e);
        }
        
        requestContext.getHeaders().add("Content-Type", "application/json");
    }
}