package com.tecnm.qro.api.service;

import com.tecnm.qro.api.entity.ColoniaEntity;
import com.tecnm.qro.api.entity.DelegacionEntity;
import com.tecnm.qro.api.model.Colonia;
import com.tecnm.qro.api.model.ColoniaInput;
import com.tecnm.qro.api.model.NombreDelegacion;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ColoniaService {

    @Transactional
    public List<Colonia> findByDelegacion(NombreDelegacion clave) {
        DelegacionEntity delegacion = DelegacionEntity.findByClave(clave);
        if (delegacion == null) {
            throw new NotFoundException("La delegación '" + clave + "' no existe");
        }
        return ColoniaEntity.findByDelegacion(delegacion)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public Colonia findById(Long id) {
        ColoniaEntity entity = ColoniaEntity.findById(id);
        if (entity == null) {
            throw new NotFoundException("La colonia con ID " + id + " no existe");
        }
        return toDto(entity);
    }

    @Transactional
    public Colonia create(ColoniaInput input) {
        DelegacionEntity delegacion = DelegacionEntity.findById(input.getDelegacionId().longValue());
        if (delegacion == null) {
            throw unprocessable("La delegación con ID " + input.getDelegacionId() + " no existe");
        }
        ColoniaEntity entity = new ColoniaEntity();
        entity.nombre = input.getNombre();
        entity.codigoPostal = input.getCodigoPostal();
        entity.tipoAsentamiento = input.getTipoAsentamiento();
        entity.delegacion = delegacion;
        entity.persist();
        return toDto(entity);
    }

    @Transactional
    public Colonia update(Long id, ColoniaInput input) {
        ColoniaEntity entity = ColoniaEntity.findById(id);
        if (entity == null) {
            throw new NotFoundException("La colonia con ID " + id + " no existe");
        }
        DelegacionEntity delegacion = DelegacionEntity.findById(input.getDelegacionId().longValue());
        if (delegacion == null) {
            throw unprocessable("La delegación con ID " + input.getDelegacionId() + " no existe");
        }
        entity.nombre = input.getNombre();
        entity.codigoPostal = input.getCodigoPostal();
        entity.tipoAsentamiento = input.getTipoAsentamiento();
        entity.delegacion = delegacion;
        return toDto(entity);
    }

    private Colonia toDto(ColoniaEntity entity) {
        return new Colonia(entity.id.intValue())
                .nombre(entity.nombre)
                .codigoPostal(entity.codigoPostal)
                .tipoAsentamiento(entity.tipoAsentamiento)
                .delegacionId(entity.delegacion.id.intValue());
    }

    private WebApplicationException unprocessable(String message) {
        return new WebApplicationException(
                Response.status(422)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(Map.of(
                                "status", 422,
                                "error", "Unprocessable Entity",
                                "message", message
                        ))
                        .build()
        );
    }
}
