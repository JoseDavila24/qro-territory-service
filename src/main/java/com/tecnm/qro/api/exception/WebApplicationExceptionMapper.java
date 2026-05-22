package com.tecnm.qro.api.exception;

import io.quarkus.logging.Log;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException e) {
        int status = e.getResponse().getStatus();
        Throwable cause = e.getCause() != null ? e.getCause() : e;
        String detail = cause.getMessage() != null ? cause.getMessage() : e.getMessage();
        if (status >= 500) {
            Log.errorf(e, "%d: %s", status, detail);
        } else {
            Log.warnf("%d: %s", status, detail);
        }
        return e.getResponse();
    }
}
