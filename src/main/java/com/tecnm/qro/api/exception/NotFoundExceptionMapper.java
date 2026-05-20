package com.tecnm.qro.api.exception;

import com.tecnm.qro.api.model.Error;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.OffsetDateTime;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(new Error()
                        .status(404)
                        .error("Not Found")
                        .message(e.getMessage() != null ? e.getMessage() : "Recurso no encontrado")
                        .timestamp(OffsetDateTime.now()))
                .build();
    }
}
