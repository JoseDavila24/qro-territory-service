package com.tecnm.qro.api.mapper;

import com.tecnm.qro.api.model.Error;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.time.OffsetDateTime;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
@ApplicationScoped
public class ApiKeyFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "app.api-key")
    String apiKey;

    @Override
    public void filter(ContainerRequestContext ctx) {
        if (!ctx.getUriInfo().getPath().contains("admin")) {
            return;
        }
        String key = ctx.getHeaderString("X-API-KEY");
        if (key == null || !key.equals(apiKey)) {
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new Error()
                            .status(401)
                            .error("Unauthorized")
                            .message("API Key inválida o no proporcionada")
                            .timestamp(OffsetDateTime.now()))
                    .build());
        }
    }
}
