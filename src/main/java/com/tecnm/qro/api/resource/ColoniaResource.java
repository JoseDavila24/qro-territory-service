package com.tecnm.qro.api.resource;

import com.tecnm.qro.api.model.Colonia;
import com.tecnm.qro.api.model.NombreDelegacion;
import com.tecnm.qro.api.service.ColoniaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/v1/colonias")
@Produces(MediaType.APPLICATION_JSON)
public class ColoniaResource {

    @Inject
    ColoniaService service;

    @GET
    public List<Colonia> listByDelegacion(@QueryParam("delegacion") NombreDelegacion clave) {
        if (clave == null) {
            throw new BadRequestException("El parámetro 'delegacion' es obligatorio");
        }
        return service.findByDelegacion(clave);
    }

    @GET
    @Path("/{id}")
    public Colonia getById(@PathParam("id") Long id) {
        return service.findById(id);
    }
}
