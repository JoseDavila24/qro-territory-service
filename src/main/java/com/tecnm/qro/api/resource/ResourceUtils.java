package com.tecnm.qro.api.resource;

import jakarta.ws.rs.BadRequestException;

class ResourceUtils {

    private ResourceUtils() {}

    static long parseId(String value) {
        try {
            long id = Long.parseLong(value);
            if (id < 1) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            throw new BadRequestException("El ID debe ser un número entero positivo");
        }
    }
}
