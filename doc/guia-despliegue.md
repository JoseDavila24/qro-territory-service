# Guía de Despliegue — qro-territory-service

Imagen publicada en Docker Hub: `josedavila784/qro-territory-service:1.0.0`

---

## Requisitos

- Docker Desktop instalado y corriendo
- Acceso a internet (para descargar las imágenes la primera vez)

---

## Levantar el servicio

Desde cualquier carpeta que tenga el archivo `compose.prod.yaml`:

```bash
docker compose -f compose.prod.yaml up -d
```

La primera vez descarga las imágenes automáticamente. El flag `-d` corre los contenedores en segundo plano.

### ¿Qué ocurre al arrancar?

1. Se levanta MySQL y espera a estar listo (healthcheck)
2. Arranca la aplicación
3. Hibernate elimina las tablas existentes y las recrea desde las entidades JPA
4. Se ejecuta `import.sql`: carga 7 delegaciones y sus colonias
5. La API queda disponible en `http://localhost:8080`

> Esto ocurre **en cada arranque** porque la estrategia es `drop-and-create`. Los datos siempre parten del estado inicial definido en `import.sql`. Cualquier colonia creada vía API se pierde al reiniciar.

---

## Verificar que está corriendo

```bash
# Ver estado de los contenedores
docker compose -f compose.prod.yaml ps

# Probar la API
curl http://localhost:8080/api/v1/delegaciones
```

---

## Endpoints disponibles

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/v1/delegaciones` | Lista las 7 delegaciones |
| GET | `/api/v1/delegaciones/{id}` | Detalle de una delegación |
| GET | `/api/v1/colonias?delegacion=NOMBRE` | Colonias por delegación |
| GET | `/api/v1/colonias/{id}` | Detalle de una colonia |
| POST | `/api/v1/admin/colonias` | Crear colonia (requiere API Key) |
| PUT | `/api/v1/admin/colonias/{id}` | Actualizar colonia (requiere API Key) |

Valores válidos para el parámetro `delegacion`:

```
CENTRO_HISTORICO, SANTA_ROSA_JAUREGUI, VILLA_CAYETANO_RUBIO,
JOSEFA_VERGARA, FELIX_OSORES_SOTOMAYOR, FELIPE_CARRILLO_PUERTO,
EPIGMENIO_GONZALEZ
```

### Endpoints admin

Requieren el header `X-API-KEY: changeme`:

```bash
curl -X POST http://localhost:8080/api/v1/admin/colonias \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: changeme" \
  -d '{
    "nombre": "Nueva Colonia",
    "codigo_postal": "76000",
    "tipo_asentamiento": "COLONIA",
    "delegacion_id": 1
  }'
```

---

## Detener el servicio

```bash
# Detener sin borrar datos
docker compose -f compose.prod.yaml down

# Detener y borrar todos los datos (reseteo completo)
docker compose -f compose.prod.yaml down -v
```

El flag `-v` elimina el volumen de MySQL. La próxima vez que levantes el servicio volverá a sembrar los datos desde cero.

---

## Observabilidad

| URL | Descripción |
|-----|-------------|
| `http://localhost:8080/q/health` | Health check (liveness + readiness) |
| `http://localhost:8080/metrics` | Métricas Prometheus |

---

## Estrategia de datos

Se usa `schema-management.strategy=drop-and-create`:

- Las tablas se **eliminan y recrean en cada arranque** del contenedor de la app
- `import.sql` corre **siempre** al iniciar — el catálogo queda en su estado original
- Colonias creadas vía API **no persisten** entre reinicios
- El volumen `mysql_prod_data` guarda los archivos de MySQL, pero los datos de las tablas se reinician con la app

Para un reseteo completo (incluyendo el volumen de MySQL) usa `docker compose -f compose.prod.yaml down -v`.
