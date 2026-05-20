-- Datos iniciales para modo dev/test
-- Ejecutado automáticamente por Quarkus cuando quarkus.hibernate-orm.database.generation=update

-- Delegaciones (clave = valor del enum NombreDelegacion)
insert into delegacion (id, nombre, sede, clave) values (1, 'Centro Histórico', 'Calle Madero 81 Pte., CP 76000', 'CENTRO_HISTORICO');
insert into delegacion (id, nombre, sede, clave) values (2, 'Santa Rosa Jáuregui', 'Agapito Pozo s/n, CP 76220', 'SANTA_ROSA_JAUREGUI');
-- agregar las 5 delegaciones restantes...

-- Colonias (delegacion_id referencia el id de delegacion)
insert into colonia (id, nombre, codigo_postal, tipo_asentamiento, delegacion_id) values (1, 'Centro', '76000', 'COLONIA', 1);
insert into colonia (id, nombre, codigo_postal, tipo_asentamiento, delegacion_id) values (2, 'San Antonio', '76010', 'FRACCIONAMIENTO', 1);
-- agregar más colonias...

-- Reiniciar secuencias para que el autoincremento no colisione con los ids insertados
alter table delegacion auto_increment = 10;
alter table colonia auto_increment = 100;
