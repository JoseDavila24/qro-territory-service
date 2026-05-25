# qro-territory-service

API REST para consultar el catálogo de delegaciones municipales de Querétaro y sus colonias. Diseñada para autocompletar formularios de captura de direcciones: dada una delegación, devuelve su lista de colonias.

Imagen Docker Hub: `josedavila784/qro-territory-service:1.1.0`

---

## Requisitos

- Docker Desktop instalado y corriendo
- Acceso a internet (para descargar las imágenes la primera vez)

---

## Configuración inicial

Antes de levantar el servicio define `APP_API_KEY` — es requerida para los endpoints de administración. Si no está definida, Docker Compose abortará al iniciar.

Crea un archivo `.env` en la raíz del proyecto con el siguiente contenido:

```
APP_API_KEY=tu-clave-secreta-aqui
```

Docker Compose lo carga automáticamente.

---

## Levantar el servicio

```bash
docker compose -f compose.prod.yaml up -d
```

Al arrancar, Hibernate crea el esquema y carga automáticamente el catálogo completo (7 delegaciones y sus colonias) desde el script embebido en la imagen.

### Verificar que está corriendo

```bash
docker compose -f compose.prod.yaml ps
curl http://localhost:8080/api/v1/delegaciones
```

### Detener

```bash
# Detener sin borrar datos
docker compose -f compose.prod.yaml down

# Reseteo completo (borra el volumen MySQL)
docker compose -f compose.prod.yaml down -v
```

---

## Endpoints

| Método | URL | Auth | Descripción |
|--------|-----|------|-------------|
| GET | `/api/v1/delegaciones` | — | Lista las 7 delegaciones |
| GET | `/api/v1/delegaciones/{delegacionId}` | — | Detalle de una delegación |
| GET | `/api/v1/colonias?delegacion=NOMBRE` | — | Colonias por delegación |
| GET | `/api/v1/colonias/{coloniaId}` | — | Detalle de una colonia |
| POST | `/api/v1/admin/colonias` | X-API-KEY | Crear colonia |
| PUT | `/api/v1/admin/colonias/{id}` | X-API-KEY | Actualizar colonia |

### Valores válidos para `delegacion`

```
CENTRO_HISTORICO, SANTA_ROSA_JAUREGUI, VILLA_CAYETANO_RUBIO,
JOSEFA_VERGARA, FELIX_OSORES_SOTOMAYOR, FELIPE_CARRILLO_PUERTO,
EPIGMENIO_GONZALEZ
```

### Ejemplo — consultar colonias

```bash
curl "http://localhost:8080/api/v1/colonias?delegacion=CENTRO_HISTORICO"
```

### Ejemplo — crear colonia (admin)

```bash
curl -X POST http://localhost:8080/api/v1/admin/colonias \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: tu-clave-secreta-aqui" \
  -d '{
    "nombre": "Nueva Colonia",
    "codigo_postal": "76000",
    "tipo_asentamiento": "COLONIA",
    "delegacion_id": 1
  }'
```

---

## Observabilidad

| URL | Descripción |
|-----|-------------|
| `http://localhost:8080/q/health` | Health check |
| `http://localhost:8080/metrics` | Métricas Prometheus |

---

## Contrato OpenAPI

El archivo `openapi.yaml` contiene la especificación completa del API (OAS 3.0).
