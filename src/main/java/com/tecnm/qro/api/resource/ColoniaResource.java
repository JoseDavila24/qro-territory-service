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
    public List<Colonia> listByDelegacion(@QueryParam("delegacion") String delegacionParam) {
        if (delegacionParam == null || delegacionParam.isBlank()) {
            throw new BadRequestException("El parámetro 'delegacion' es obligatorio");
        }
        NombreDelegacion clave;
        try {
            clave = NombreDelegacion.fromValue(delegacionParam);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Valor inválido para 'delegacion': '" + delegacionParam + "'");
        }
        return service.findByDelegacion(clave);
    }

    @GET
    @Path("/{id}")
    public Colonia getById(@PathParam("id") String idParam) {
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
