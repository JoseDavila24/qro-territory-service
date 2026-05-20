package com.tecnm.qro.api.service;

import com.tecnm.qro.api.entity.ColoniaEntity;
import com.tecnm.qro.api.entity.DelegacionEntity;
import com.tecnm.qro.api.model.NombreDelegacion;
import com.tecnm.qro.api.model.TipoAsentamiento;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DatosIniciales {

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        if (DelegacionEntity.count() > 0) {
            return;
        }

        DelegacionEntity srj = delegacion("Santa Rosa Jáuregui", "Agapito Pozo s/n, CP 76220", NombreDelegacion.SANTA_ROSA_JAUREGUI);
        DelegacionEntity ch  = delegacion("Centro Histórico", "Calle Madero 81 Pte., CP 76000", NombreDelegacion.CENTRO_HISTORICO);
        DelegacionEntity vcr = delegacion("Villa Cayetano Rubio", "Avenida del Río núm. 1, CP 76209", NombreDelegacion.VILLA_CAYETANO_RUBIO);
        DelegacionEntity jv  = delegacion("Josefa Vergara", "Calle 21 núm. 1000, CP 76086", NombreDelegacion.JOSEFA_VERGARA);
        DelegacionEntity fos = delegacion("Félix Osores Sotomayor", "Avenida de la Luz núm. 602", NombreDelegacion.FELIX_OSORES_SOTOMAYOR);
        DelegacionEntity fcp = delegacion("Felipe Carrillo Puerto", "Calzada Guadalupe núm. 103, CP 76138", NombreDelegacion.FELIPE_CARRILLO_PUERTO);
        DelegacionEntity eg  = delegacion("Epigmenio González", "Calle Tláloc núm. 100, CP 76130", NombreDelegacion.EPIGMENIO_GONZALEZ);

        colonia("Centro",      "76021", TipoAsentamiento.COLONIA,         ch);
        colonia("San José Inn","76021", TipoAsentamiento.COLONIA,         ch);
        colonia("La Cruz",     "76021", TipoAsentamiento.FRACCIONAMIENTO, ch);
        colonia("San Antonio", "76220", TipoAsentamiento.FRACCIONAMIENTO, srj);
    }

    private DelegacionEntity delegacion(String nombre, String sede, NombreDelegacion clave) {
        DelegacionEntity e = new DelegacionEntity();
        e.nombre = nombre;
        e.sede   = sede;
        e.clave  = clave;
        e.persist();
        return e;
    }

    private void colonia(String nombre, String cp, TipoAsentamiento tipo, DelegacionEntity delegacion) {
        ColoniaEntity e = new ColoniaEntity();
        e.nombre           = nombre;
        e.codigoPostal     = cp;
        e.tipoAsentamiento = tipo;
        e.delegacion       = delegacion;
        e.persist();
    }
}
