package com.tecnm.qro.api.exception;

import com.tecnm.qro.api.model.Error;
import io.quarkus.logging.Log;
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
        Log.warnf("400 Bad Request: %s", e.getMessage());
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
