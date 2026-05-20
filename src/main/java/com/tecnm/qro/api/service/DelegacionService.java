package com.tecnm.qro.api.service;

import com.tecnm.qro.api.entity.DelegacionEntity;
import com.tecnm.qro.api.model.Delegacion;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class DelegacionService {

    @Transactional
    public List<Delegacion> listAll() {
        return DelegacionEntity.<DelegacionEntity>listAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public Delegacion findById(Long id) {
        DelegacionEntity entity = DelegacionEntity.findById(id);
        if (entity == null) {
            throw new NotFoundException("La delegación con ID " + id + " no existe");
        }
        return toDto(entity);
    }

    private Delegacion toDto(DelegacionEntity entity) {
        return new Delegacion()
                .id(entity.id.intValue())
                .nombre(entity.nombre)
                .sede(entity.sede);
    }
}
