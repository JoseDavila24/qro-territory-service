package com.tecnm.qro.api.entity;

import com.tecnm.qro.api.model.NombreDelegacion;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "delegacion")
public class DelegacionEntity extends PanacheEntity {

    @Column(nullable = false, length = 100)
    public String nombre;

    @Column(length = 255)
    public String sede;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 30)
    public NombreDelegacion clave;

    public static DelegacionEntity findByClave(NombreDelegacion clave) {
        return find("clave", clave).firstResult();
    }
}
