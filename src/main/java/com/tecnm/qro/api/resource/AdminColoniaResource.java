package com.tecnm.qro.api.resource;

import com.tecnm.qro.api.model.Colonia;
import com.tecnm.qro.api.model.ColoniaInput;
import com.tecnm.qro.api.service.ColoniaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/api/v1/admin/colonias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminColoniaResource {

    @Inject
    ColoniaService service;

    @ConfigProperty(name = "app.api-key")
    String apiKey;

    @POST
    public Response create(@HeaderParam("X-API-KEY") String key, @Valid ColoniaInput input) {
        validateApiKey(key);
        Colonia created = service.create(input);
        URI location = URI.create("/api/v1/colonias/" + created.getId());
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Colonia update(@HeaderParam("X-API-KEY") String key,
                          @PathParam("id") Long id,
                          @Valid ColoniaInput input) {
        validateApiKey(key);
        return service.update(id, input);
    }

    private void validateApiKey(String key) {
        if (key == null || !key.equals(apiKey)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(Map.of(
                                    "status", 401,
                                    "error", "Unauthorized",
                                    "message", "API Key inválida o no proporcionada"
                            ))
                            .build()
            );
        }
    }
}
