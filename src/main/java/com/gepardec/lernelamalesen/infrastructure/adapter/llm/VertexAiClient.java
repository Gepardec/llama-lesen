package com.gepardec.lernelamalesen.infrastructure.adapter.llm;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "vertex-ai")
@RegisterProvider(VertexAiAuthenticationProvider.class)
@Path("/v1/projects/{projectId}/locations/{location}/publishers/google/models/{model}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VertexAiClient {
    
    @POST
    @Path(":generateContent")
    VertexAiResponse generateContent(
        @PathParam("projectId") String projectId,
        @PathParam("location") String location,
        @PathParam("model") String model,
        VertexAiRequest request
    );
}