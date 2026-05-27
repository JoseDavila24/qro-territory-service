# Guía de Despliegue — qro-territory-service

Imagen publicada en Docker Hub: `josedavila784/qro-territory-service:1.1.1`

---

## Opción A — Despliegue local con Docker Compose

### Requisitos

- Docker Desktop instalado y corriendo
- Acceso a internet para descargar las imágenes la primera vez

### Variables de entorno requeridas

`compose.prod.yaml` requiere que `APP_API_KEY` esté definida antes de levantar el servicio. Si no está definida, Docker Compose aborta con un error antes de iniciar cualquier contenedor.

**Opción recomendada — archivo `.env`**

Crea un archivo llamado `.env` en el mismo directorio donde está `compose.prod.yaml` (la raíz del proyecto) con este contenido:

```
APP_API_KEY=tu-clave-secreta-aqui
```

Docker Compose carga este archivo automáticamente. No necesitas hacer nada más.

**Alternativa — variable de entorno en el shell**

En PowerShell (Windows):
```powershell
$env:APP_API_KEY = "tu-clave-secreta-aqui"
docker compose -f compose.prod.yaml up -d
```

En Bash (Linux/macOS):
```bash
export APP_API_KEY=tu-clave-secreta-aqui
docker compose -f compose.prod.yaml up -d
```

> La variable definida en el shell solo dura mientras la sesión esté abierta. El archivo `.env` es persistente.

### Levantar el servicio

```bash
docker compose -f compose.prod.yaml up -d
```

La primera vez descarga las imágenes automáticamente. El flag `-d` corre los contenedores en segundo plano.

### ¿Qué ocurre al arrancar?

1. Se levanta MySQL 8.0 y espera a estar saludable (healthcheck cada 10s)
2. Arranca la aplicación Quarkus con perfil `prod`
3. Hibernate aplica la estrategia `update`: crea las tablas si no existen, aplica cambios incrementales si las hay — **nunca borra datos**
4. La API queda disponible en `http://localhost:8080`

> En el **primer despliegue** (base de datos vacía), `update` crea el esquema completo automáticamente. No se carga `import.sql` en producción — los datos los gestiona la aplicación vía API.

### Verificar que está corriendo

```bash
# Estado de los contenedores
docker compose -f compose.prod.yaml ps

# Health check
curl http://localhost:8080/q/health

# Probar la API
curl http://localhost:8080/api/v1/delegaciones
```

### Detener el servicio

```bash
# Detener sin borrar datos (el volumen MySQL se conserva)
docker compose -f compose.prod.yaml down

# Detener y borrar todos los datos (reseteo completo)
docker compose -f compose.prod.yaml down -v
```

---

## Opción B — Despliegue en Railway

Railway es una plataforma cloud que permite desplegar la imagen directamente desde Docker Hub con base de datos MySQL incluida.

### Paso 1 — Crear proyecto en Railway

1. Ingresa a [railway.app](https://railway.app) y crea una cuenta o inicia sesión
2. Haz clic en **New Project**
3. Selecciona **Empty Project**

### Paso 2 — Agregar base de datos MySQL

1. Dentro del proyecto, haz clic en **+ New Service**
2. Selecciona **Database → MySQL**
3. Railway provisiona MySQL automáticamente y expone sus variables de conexión

> No necesitas copiar las variables de MySQL — en el Paso 4 las referenciarás directamente con la sintaxis `${{MySQL.VARIABLE}}`, que Railway resuelve de forma automática.

### Paso 3 — Agregar el servicio de la app

1. Haz clic en **+ New Service**
2. Selecciona **Docker Image**
3. Ingresa la imagen: `josedavila784/qro-territory-service:1.1.1`
4. Railway detectará el servicio y lo agregará al proyecto

### Paso 4 — Configurar variables de entorno

En el servicio de la app, abre la pestaña **Variables** y agrega:

| Variable | Valor |
|----------|-------|
| `QUARKUS_DATASOURCE_JDBC_URL` | `jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}` |
| `QUARKUS_DATASOURCE_USERNAME` | `${{MySQL.MYSQLUSER}}` |
| `QUARKUS_DATASOURCE_PASSWORD` | `${{MySQL.MYSQLPASSWORD}}` |
| `APP_API_KEY` | tu clave secreta (ej. genera una con `openssl rand -hex 32`) |

> **No agregues `QUARKUS_HTTP_PORT`**: Railway inyecta `PORT` automáticamente en cada contenedor. La app lee esa variable directo (`${PORT:8080}` en el perfil prod). Configurar `QUARKUS_HTTP_PORT=${{PORT}}` en la UI puede resolverse a cadena vacía antes del primer arranque y romper el servicio.

> **Nombre del servicio MySQL**: la sintaxis `${{MySQL.VARIABLE}}` asume que Railway nombró el servicio de base de datos exactamente `MySQL`. Verifica el nombre en la esquina superior izquierda del servicio dentro del proyecto — si es diferente (ej. `mysql`), ajusta el prefijo en todas las referencias.

### Paso 5 — Desplegar

1. En el servicio de la app, haz clic en **Deploy**
2. Railway descarga la imagen de Docker Hub y levanta el contenedor
3. En la pestaña **Deployments** puedes ver los logs en tiempo real

### Paso 6 — Obtener la URL pública

1. En el servicio de la app, abre la pestaña **Settings**
2. En la sección **Networking**, haz clic en **Generate Domain**
3. Railway asigna una URL pública con HTTPS, por ejemplo:
   ```
   https://qro-territory-service-production.up.railway.app
   ```

### Paso 7 — Verificar el despliegue

```bash
# Health check
curl https://<tu-dominio>.up.railway.app/q/health

# Listar delegaciones
curl https://<tu-dominio>.up.railway.app/api/v1/delegaciones
```

### Actualizar a una nueva versión

1. Publica la nueva imagen en Docker Hub (ver `guia-desarrollo.md` sección 8)
2. En Railway, abre el servicio de la app
3. En **Settings → Source**, actualiza el tag de la imagen (ej. `1.1.0`)
4. Haz clic en **Deploy** — Railway hace el reemplazo sin tiempo de inactividad

---

## Endpoints disponibles

| Método | URL | Auth |
|--------|-----|------|
| GET | `/api/v1/delegaciones` | — |
| GET | `/api/v1/delegaciones/{id}` | — |
| GET | `/api/v1/colonias?delegacion=NOMBRE` | — |
| GET | `/api/v1/colonias/{id}` | — |
| POST | `/api/v1/admin/colonias` | X-API-KEY |
| PUT | `/api/v1/admin/colonias/{id}` | X-API-KEY |

Valores válidos para el parámetro `delegacion`:

```
CENTRO_HISTORICO, SANTA_ROSA_JAUREGUI, VILLA_CAYETANO_RUBIO,
JOSEFA_VERGARA, FELIX_OSORES_SOTOMAYOR, FELIPE_CARRILLO_PUERTO,
EPIGMENIO_GONZALEZ
```

### Ejemplo — endpoint admin

```bash
curl -X POST https://<tu-dominio>/api/v1/admin/colonias \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: <tu-clave>" \
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
| `/q/health` | Health check (liveness + readiness) |
| `/q/health/live` | Solo liveness |
| `/q/health/ready` | Solo readiness |
| `/metrics` | Métricas en formato Prometheus |

---

## Estrategia de datos por perfil

| Perfil | Estrategia | Comportamiento |
|--------|-----------|----------------|
| `dev` (local) | `drop-and-create` | Borra y recrea el esquema en cada arranque; carga `import.sql` |
| `prod` | `update` | Crea el esquema en el primer arranque; aplica cambios incrementales; **nunca borra datos** |

Los datos creados vía API en producción persisten entre reinicios gracias al volumen de MySQL (`mysql_data` en Docker Compose, volumen gestionado por Railway en la nube).
