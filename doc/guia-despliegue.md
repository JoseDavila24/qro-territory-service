# Guía de Despliegue — qro-territory-service

Imagen publicada en Docker Hub: `josedavila784/qro-territory-service:1.0.0-SNAPSHOT`

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

### ¿Qué ocurre al arrancar por primera vez?

1. Se levanta MySQL y espera a estar listo (healthcheck)
2. Arranca la aplicación y detecta que las tablas no existen
3. Hibernate crea el schema (tablas `delegacion` y `colonia`)
4. Se ejecuta `import.sql`: carga 7 delegaciones y 949 colonias
5. La API queda disponible en `http://localhost:8080`

En reinicios posteriores los pasos 3 y 4 se omiten — los datos persisten.

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

Se usa `schema-management.strategy=update`:

- Las tablas **no se borran** en cada reinicio
- El script de datos solo corre en el **primer arranque** (tablas vacías)
- Colonias creadas vía API **persisten** entre reinicios
- Los datos de MySQL se guardan en el volumen Docker `mysql_data`

Para hacer un reseteo completo de datos usa `docker compose -f compose.prod.yaml down -v`.
