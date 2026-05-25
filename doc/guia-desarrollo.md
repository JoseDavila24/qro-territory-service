# Guía de Desarrollo — qro-territory-service

## Requisitos previos

- Docker Desktop instalado y corriendo
- Git configurado localmente
- Puerto `8080` y `5005` libres en el host

---

## 1. Clonar y configurar el entorno

```bash
git clone <url-del-repositorio>
cd qro-territory-service
```

El archivo `.env` en la raíz define las credenciales locales. Ya está incluido con valores por defecto:

```
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=qro_territory
MYSQL_USER=qro_user
MYSQL_PASSWORD=qro_pass
APP_API_KEY=changeme
```

> `.env` está en `.gitignore` — nunca se sube al repositorio.

---

## 2. Levantar el entorno

```bash
docker compose up -d
```

Esto inicia dos contenedores:

| Contenedor | Descripción |
|------------|-------------|
| `qro-mysql` | MySQL 8.0 con la base `qro_territory` |
| `qro-app-dev` | Maven + Java 21, código montado como volumen |

Verifica que ambos estén corriendo:

```bash
docker compose ps
```

---

## 3. Ingresar al contenedor de la app

```bash
docker compose exec app bash
```

A partir de aquí todos los comandos `mvn` se ejecutan **dentro del contenedor**.

---

## 4. Iniciar el modo de desarrollo

```bash
mvn quarkus:dev
```

Quarkus arranca en modo dev con **live reload**: detecta cambios en el código y recarga sin reiniciar el contenedor.

Al arrancar, Hibernate aplica la estrategia `drop-and-create`:
1. Elimina las tablas existentes (si las hay)
2. Las recrea desde las entidades JPA
3. Ejecuta `import.sql`: carga 7 delegaciones y colonias de ejemplo

Recursos disponibles tras el arranque:

| Recurso | URL |
|---------|-----|
| API REST — delegaciones | http://localhost:8080/api/v1/delegaciones |
| API REST — colonias | http://localhost:8080/api/v1/colonias |
| Dev UI (Quarkus) | http://localhost:8080/q/dev/ |
| Health checks | http://localhost:8080/q/health |
| Métricas Prometheus | http://localhost:8080/metrics |
| Debug remoto (JDWP) | puerto `5005` |

> `/api/v1` es el prefijo base de la API, no una ruta válida por sí misma. Llamarla directamente devuelve `404`.

---

## 5. Flujo de trabajo habitual

### Modificar una entidad o servicio

1. Edita el archivo en tu editor (fuera del contenedor — el código está montado como volumen)
2. Quarkus detecta el cambio y recarga automáticamente
3. Si modificas una entidad JPA, el contenedor reinicia el contexto de Hibernate

### Agregar una dependencia

1. Edita `pom.xml`
2. Quarkus en modo dev recargará las dependencias automáticamente en la mayoría de los casos
3. Si no recarga, reinicia manualmente: `Ctrl+C` → `mvn quarkus:dev`

### Cambiar la configuración

Edita `src/main/resources/application.properties`. El cambio se aplica en el siguiente reinicio del modo dev.

---

## 6. Pruebas

Ejecuta los tests dentro del contenedor:

```bash
# Todos los tests
mvn verify -B

# Una sola clase
mvn test -Dtest=DelegacionResourceTest

# Un método específico
mvn test -Dtest=DelegacionResourceTest#debeListarTodasLasDelegaciones

# Solo tests de integración (sufijo IT)
mvn verify -Dit.test=ColoniaResourceIT
```

Los tests usan `@QuarkusTest` + REST-Assured contra la base de datos real del contenedor.

---

## 7. Build

```bash
# Compilar (sin empaquetar)
mvn compile

# Empaquetar como JAR JVM (sin tests)
mvn package -DskipTests

# Limpiar artefactos
mvn clean
```

El JAR generado queda en `target/quarkus-app/quarkus-run.jar`.

---

## 8. Generar y publicar la imagen de producción

Estos comandos se ejecutan **desde el host** (fuera del contenedor).

```bash
# 1. Empaquetar el JAR dentro del contenedor de desarrollo
docker compose exec app mvn clean package -DskipTests

# 2. Construir la imagen JVM
docker build -f src/main/docker/Dockerfile.jvm -t qro-territory-service:1.1.0 .

# 3. Taggear con el usuario de Docker Hub
docker image tag qro-territory-service:1.1.0 josedavila784/qro-territory-service:1.1.0

# 4. Subir a Docker Hub
docker login
docker push josedavila784/qro-territory-service:1.1.0
```

Para una nueva versión, reemplaza `1.1.0` por el tag correspondiente en los pasos 2, 3 y 4, y actualiza también `compose.prod.yaml`.

---

## 9. Comandos Docker Compose (desde el host)

```bash
# Ver logs en tiempo real
docker compose logs -f

# Ver logs solo de la app
docker compose logs -f app

# Ver logs solo de MySQL
docker compose logs -f mysql

# Detener contenedores (conserva datos)
docker compose stop

# Detener y eliminar contenedores (conserva el volumen de MySQL)
docker compose down

# Reseteo completo — elimina contenedores y la base de datos
docker compose down -v

# Reconstruir la imagen dev tras cambios en Dockerfile.dev
docker compose build app && docker compose up -d
```

---

## 10. Acceso directo a MySQL

```bash
docker compose exec mysql mysql -u qro_user -pqro_pass qro_territory
```

Credenciales locales (definidas en `.env`):

| Variable | Valor |
|----------|-------|
| Base de datos | `qro_territory` |
| Usuario | `qro_user` |
| Contraseña | `qro_pass` |
| Puerto | `3306` |

---

## 11. Probar la API manualmente

```bash
# Listar delegaciones
curl http://localhost:8080/api/v1/delegaciones

# Obtener delegación por ID
curl http://localhost:8080/api/v1/delegaciones/1

# Colonias por delegación
curl "http://localhost:8080/api/v1/colonias?delegacion=CENTRO_HISTORICO"

# Crear colonia (requiere API Key)
curl -X POST http://localhost:8080/api/v1/admin/colonias \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: changeme" \
  -d '{
    "nombre": "Colonia de Prueba",
    "codigo_postal": "76000",
    "tipo_asentamiento": "COLONIA",
    "delegacion_id": 2
  }'
```

---

## 12. Solución de problemas comunes

### La app no arranca — error de conexión a MySQL

```
Unable to acquire JDBC Connection
```

MySQL aún no está listo. Espera unos segundos y reinicia:

```bash
docker compose restart app
```

O verifica el healthcheck de MySQL:

```bash
docker compose ps mysql
```

### Puerto 8080 ocupado

Detén el proceso que lo usa o cambia el puerto en `application.properties`:

```properties
quarkus.http.port=8081
```

### Cambios de entidad no se reflejan

Fuerza un reinicio del modo dev: `Ctrl+C` → `mvn quarkus:dev`. Si el problema persiste, resetea la base de datos:

```bash
docker compose down -v && docker compose up -d
```

### `APP_API_KEY` no definida — error al arrancar

El `compose.yaml` inyecta `APP_API_KEY` al contenedor usando el valor del `.env` (default `changeme`). Si ves este error, verifica que el contenedor fue recreado tras el último cambio al `.env`:

```bash
docker compose down && docker compose up -d
```
