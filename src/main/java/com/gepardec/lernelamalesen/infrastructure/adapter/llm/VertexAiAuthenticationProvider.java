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
import java.util.List;

@ApplicationScoped
public class VertexAiAuthenticationProvider implements ClientRequestFilter {
    private static final Logger LOG = Logger.getLogger(VertexAiAuthenticationProvider.class);

    @ConfigProperty(name = "vertex.ai.credentials.path", defaultValue = "")
    String credentialsPath;

    @ConfigProperty(name = "vertex.ai.billing.project", defaultValue = "")
    String billingProject; // optional, only if SA != billing project

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        try {
            GoogleCredentials creds = (credentialsPath != null && !credentialsPath.trim().isEmpty())
                    ? GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
                    : GoogleCredentials.getApplicationDefault();

            // Vertex AI only needs cloud-platform
            creds = creds.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            // Ensure we actually have a token on first use
            if (creds.getAccessToken() == null) {
                creds.refresh();
            } else {
                creds.refreshIfExpired();
            }

            AccessToken token = creds.getAccessToken();
            if (token == null || token.getTokenValue() == null || token.getTokenValue().isBlank()) {
                throw new IOException("No OAuth access token available for Vertex AI");
            }

            requestContext.getHeaders().putSingle("Authorization", "Bearer " + token.getTokenValue());
            requestContext.getHeaders().putSingle("Content-Type", "application/json");
        } catch (Exception e) {
            LOG.errorf(e, "Failed to obtain Google OAuth token (credentialsPath=%s)", credentialsPath);
            throw new IOException("Authentication failed", e);
        }
    }
}