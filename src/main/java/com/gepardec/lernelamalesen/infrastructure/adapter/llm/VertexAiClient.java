package com.gepardec.lernelamalesen.infrastructure.adapter.llm;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "vertex-ai")
@RegisterProvider(VertexAiAuthenticationProvider.class)
@Path("/v1beta1/projects/{projectId}/locations/{location}/endpoints/openapi/chat/completions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VertexAiClient {
    
    @POST
    @Path("")
    VertexAiResponse generateContent(
        @PathParam("projectId") String projectId,
        @PathParam("location") String location,
        VertexAiRequest request
    );
}