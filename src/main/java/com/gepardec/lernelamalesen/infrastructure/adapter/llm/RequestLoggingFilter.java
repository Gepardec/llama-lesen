package com.gepardec.lernelamalesen.infrastructure.adapter.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.io.IOException;

@ApplicationScoped
public class RequestLoggingFilter implements ClientRequestFilter {
    private static final Logger LOG = Logger.getLogger(RequestLoggingFilter.class);

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (requestContext.hasEntity() && 
            requestContext.getMediaType() != null && 
            requestContext.getMediaType().isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            
            try {
                Object entity = requestContext.getEntity();
                String jsonString = objectMapper.writeValueAsString(entity);
                LOG.infof("Actual JSON being sent: %s", jsonString);
            } catch (Exception e) {
                LOG.warnf("Could not serialize request entity to JSON: %s", e.getMessage());
            }
        }
    }
}