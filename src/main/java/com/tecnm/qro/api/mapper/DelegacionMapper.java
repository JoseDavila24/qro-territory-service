package com.tecnm.qro.api.mapper;

import com.tecnm.qro.api.entity.DelegacionEntity;
import com.tecnm.qro.api.model.Delegacion;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DelegacionMapper {

    public Delegacion toDto(DelegacionEntity entity) {
        return new Delegacion()
                .id(entity.id.intValue())
                .nombre(entity.nombre)
                .sede(entity.sede);
    }
}
