package com.tecnm.qro.api.service;

import com.tecnm.qro.api.entity.DelegacionEntity;
import com.tecnm.qro.api.mapper.DelegacionMapper;
import com.tecnm.qro.api.model.Delegacion;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class DelegacionService {

    @Inject
    DelegacionMapper mapper;

    @Transactional
    public List<Delegacion> listAll() {
        return DelegacionEntity.<DelegacionEntity>listAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public Delegacion findById(Long id) {
        DelegacionEntity entity = DelegacionEntity.findById(id);
        if (entity == null) {
            throw new NotFoundException("La delegación con ID " + id + " no existe");
        }
        return mapper.toDto(entity);
    }
}
