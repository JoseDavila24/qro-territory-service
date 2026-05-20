package com.tecnm.qro.api.resource;

import com.tecnm.qro.api.model.Delegacion;
import com.tecnm.qro.api.service.DelegacionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/v1/delegaciones")
@Produces(MediaType.APPLICATION_JSON)
public class DelegacionResource {

    @Inject
    DelegacionService service;

    @GET
    public List<Delegacion> listAll() {
        return service.listAll();
    }

    @GET
    @Path("/{id}")
    public Delegacion getById(@PathParam("id") String idParam) {
        return service.findById(parseId(idParam));
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
