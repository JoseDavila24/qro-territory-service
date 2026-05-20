package com.tecnm.qro.api.mapper;

import com.tecnm.qro.api.model.Error;
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
                    // strip method prefix (e.g. "create.input.nombre" → "nombre")
                    int dot = field.lastIndexOf('.');
                    return (dot >= 0 ? field.substring(dot + 1) : field) + ": " + cv.getMessage();
                })
                .collect(Collectors.joining(", "));

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
