package com.tecnm.qro.api.mapper;

import com.tecnm.qro.api.model.Error;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.OffsetDateTime;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(BadRequestException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(new Error()
                        .status(400)
                        .error("Bad Request")
                        .message(e.getMessage() != null ? e.getMessage() : "Solicitud inválida")
                        .timestamp(OffsetDateTime.now()))
                .build();
    }
}
