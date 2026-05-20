package com.tecnm.qro.api.resource;

import com.tecnm.qro.api.model.Delegacion;
import com.tecnm.qro.api.service.DelegacionService;
import jakarta.inject.Inject;
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
    public Delegacion getById(@PathParam("id") Long id) {
        return service.findById(id);
    }
}
