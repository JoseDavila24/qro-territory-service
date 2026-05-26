package com.tecnm.qro.api.exception;

import com.tecnm.qro.api.model.Error;
import io.quarkus.logging.Log;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.OffsetDateTime;

@Provider
public class UnprocessableEntityExceptionMapper implements ExceptionMapper<UnprocessableEntityException> {

    @Override
    public Response toResponse(UnprocessableEntityException e) {
        Log.warnf("422 Unprocessable Entity: %s", e.getMessage());
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(new Error()
                        .status(422)
                        .error("Unprocessable Entity")
                        .message(e.getMessage())
                        .timestamp(OffsetDateTime.now()))
                .build();
    }
}
