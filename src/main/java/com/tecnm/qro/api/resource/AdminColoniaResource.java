package com.tecnm.qro.api.resource;

import com.tecnm.qro.api.model.Colonia;
import com.tecnm.qro.api.model.ColoniaInput;
import com.tecnm.qro.api.service.ColoniaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/api/v1/admin/colonias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminColoniaResource {

    @Inject
    ColoniaService service;

    @POST
    public Response create(@Valid ColoniaInput input) {
        Colonia created = service.create(input);
        URI location = URI.create("/api/v1/colonias/" + created.getId());
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Colonia update(@PathParam("id") String idParam, @Valid ColoniaInput input) {
        return service.update(parseId(idParam), input);
    }

    private static long parseId(String value) {
        try {
            long id = Long.parseLong(value);
            if (id < 1) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            throw new BadRequestException("El ID debe ser un número entero positivo");
        }
    }
}
