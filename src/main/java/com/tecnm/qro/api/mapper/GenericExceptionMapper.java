package com.tecnm.qro.api.mapper;

import com.tecnm.qro.api.model.Error;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.OffsetDateTime;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        // WebApplicationException ya lleva una Response construida (ej. 422)
        if (e instanceof WebApplicationException wae) {
            return wae.getResponse();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(new Error()
                        .status(500)
                        .error("Internal Server Error")
                        .message("Error inesperado del servidor.")
                        .timestamp(OffsetDateTime.now()))
                .build();
    }
}
