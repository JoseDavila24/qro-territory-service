package com.tecnm.qro.api.service;

import com.tecnm.qro.api.entity.ColoniaEntity;
import com.tecnm.qro.api.entity.DelegacionEntity;
import com.tecnm.qro.api.mapper.ColoniaMapper;
import com.tecnm.qro.api.model.Colonia;
import com.tecnm.qro.api.model.ColoniaInput;
import com.tecnm.qro.api.model.Error;
import com.tecnm.qro.api.model.NombreDelegacion;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.util.List;

@ApplicationScoped
public class ColoniaService {

    @Inject
    ColoniaMapper mapper;

    @Transactional
    public List<Colonia> findByDelegacion(NombreDelegacion clave) {
        DelegacionEntity delegacion = DelegacionEntity.findByClave(clave);
        if (delegacion == null) {
            throw new NotFoundException("La delegación '" + clave + "' no existe");
        }
        return ColoniaEntity.findByDelegacion(delegacion)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public Colonia findById(Long id) {
        ColoniaEntity entity = ColoniaEntity.findById(id);
        if (entity == null) {
            throw new NotFoundException("La colonia con ID " + id + " no existe");
        }
        return mapper.toDto(entity);
    }

    @Transactional
    public Colonia create(ColoniaInput input) {
        DelegacionEntity delegacion = DelegacionEntity.findById(input.getDelegacionId().longValue());
        if (delegacion == null) {
            throw unprocessable("La delegación con ID " + input.getDelegacionId() + " no existe");
        }
        ColoniaEntity entity = mapper.toNewEntity(input, delegacion);
        entity.persist();
        return mapper.toDto(entity);
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
        mapper.updateEntity(entity, input, delegacion);
        return mapper.toDto(entity);
    }

    private WebApplicationException unprocessable(String message) {
        return new WebApplicationException(
                Response.status(422)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(new Error()
                                .status(422)
                                .error("Unprocessable Entity")
                                .message(message)
                                .timestamp(OffsetDateTime.now()))
                        .build()
        );
    }
}
