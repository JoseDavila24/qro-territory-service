package com.tecnm.qro.api.mapper;

import com.tecnm.qro.api.entity.ColoniaEntity;
import com.tecnm.qro.api.entity.DelegacionEntity;
import com.tecnm.qro.api.model.Colonia;
import com.tecnm.qro.api.model.ColoniaInput;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ColoniaMapper {

    public Colonia toDto(ColoniaEntity entity) {
        return new Colonia(entity.id.intValue())
                .nombre(entity.nombre)
                .codigoPostal(entity.codigoPostal)
                .tipoAsentamiento(entity.tipoAsentamiento)
                .delegacionId(entity.delegacion.id.intValue());
    }

    public ColoniaEntity toNewEntity(ColoniaInput input, DelegacionEntity delegacion) {
        ColoniaEntity entity = new ColoniaEntity();
        entity.nombre = input.getNombre();
        entity.codigoPostal = input.getCodigoPostal();
        entity.tipoAsentamiento = input.getTipoAsentamiento();
        entity.delegacion = delegacion;
        return entity;
    }

    public void updateEntity(ColoniaEntity entity, ColoniaInput input, DelegacionEntity delegacion) {
        entity.nombre = input.getNombre();
        entity.codigoPostal = input.getCodigoPostal();
        entity.tipoAsentamiento = input.getTipoAsentamiento();
        entity.delegacion = delegacion;
    }
}
