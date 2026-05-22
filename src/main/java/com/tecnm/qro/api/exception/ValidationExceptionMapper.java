package com.tecnm.qro.api.exception;

import com.tecnm.qro.api.model.Error;
import io.quarkus.logging.Log;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(cv -> {
                    String field = cv.getPropertyPath().toString();
                    int dot = field.lastIndexOf('.');
                    return (dot >= 0 ? field.substring(dot + 1) : field) + ": " + cv.getMessage();
                })
                .collect(Collectors.joining(", "));

        Log.warnf("400 Validation: %s", message);
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(new Error()
                        .status(400)
                        .error("Bad Request")
                        .message(message)
                        .timestamp(OffsetDateTime.now()))
                .build();
    }
}
