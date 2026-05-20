package com.tecnm.qro.api.entity;

import com.tecnm.qro.api.model.TipoAsentamiento;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "colonia")
public class ColoniaEntity extends PanacheEntity {

    @Column(nullable = false, length = 150)
    public String nombre;

    @Column(name = "codigo_postal", nullable = false, length = 5)
    public String codigoPostal;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_asentamiento", nullable = false)
    public TipoAsentamiento tipoAsentamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegacion_id", nullable = false)
    public DelegacionEntity delegacion;

    public static java.util.List<ColoniaEntity> findByDelegacion(DelegacionEntity delegacion) {
        return list("delegacion", delegacion);
    }
}
