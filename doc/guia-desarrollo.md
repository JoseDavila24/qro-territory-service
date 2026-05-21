# Guía de Desarrollo — qro-territory-service

## 1. Levantar el entorno

Desde la raíz del proyecto, levanta MySQL y el contenedor de la aplicación:

```bash
docker compose up -d
```

Esto inicia dos contenedores:
- `qro-mysql` — base de datos MySQL 8.0
- `qro-app-dev` — entorno Maven/Java 21 con el código montado como volumen

Verifica que ambos estén corriendo:

```bash
docker compose ps
```

## 2. Ingresar al contenedor de la app

```bash
docker compose exec app bash
```

Ahora estás dentro del contenedor con acceso al proyecto en `/workspace`.

## 3. Iniciar el modo de desarrollo

Dentro del contenedor, ejecuta Quarkus en modo dev (live reload activo):

```bash
mvn quarkus:dev
```

La app quedará disponible en:

| Recurso | URL |
|---------|-----|
| API REST | http://localhost:8080/api/v1 |
| Dev UI (Quarkus) | http://localhost:8080/q/dev/ |
| Health checks | http://localhost:8080/q/health |
| Métricas (Prometheus) | http://localhost:8080/metrics |
| Debug remoto | puerto `5005` |

> El modo dev detecta cambios en el código y recarga automáticamente sin reiniciar el contenedor.

## 4. Comandos útiles dentro del contenedor

### Pruebas

```bash
# Ejecutar todos los tests
mvn verify -B

# Ejecutar una sola clase de test
mvn test -Dtest=NombreDeLaClaseTest

# Ejecutar un método de test específico
mvn test -Dtest=NombreDeLaClaseTest#nombreDelMetodo
```

### Build

```bash
# Compilar el proyecto
mvn compile

# Empaquetar como JAR (sin ejecutar tests)
mvn package -DskipTests

# Limpiar artefactos generados
mvn clean
```

### Generar imagen de producción

Estos comandos se ejecutan **desde el host** (fuera del contenedor), en la raíz del proyecto.

```bash
# 1. Empaquetar el JAR dentro del contenedor de desarrollo
docker compose exec app mvn clean package -DskipTests

# 2. Construir la imagen JVM
docker build -f src/main/docker/Dockerfile.jvm -t qro-territory-service:1.0.0 .

# 3. Taggear con el usuario de Docker Hub
docker image tag qro-territory-service:1.0.0 josedavila784/qro-territory-service:1.0.0

# 4. Subir a Docker Hub (requiere docker login previo)
docker login
docker push josedavila784/qro-territory-service:1.0.0
```

> El JAR generado en el paso 1 queda en `target/quarkus-app/`. El `Dockerfile.jvm` lo copia al construir la imagen.

## 5. Comandos útiles de Docker Compose (desde el host)

```bash
# Ver logs en tiempo real de todos los servicios
docker compose logs -f

# Ver logs solo de la app
docker compose logs -f app

# Ver logs solo de MySQL
docker compose logs -f mysql

# Detener todos los contenedores (conserva datos)
docker compose stop

# Detener y eliminar contenedores (conserva volúmenes)
docker compose down

# Detener y eliminar contenedores + volúmenes (borra la base de datos)
docker compose down -v

# Reconstruir la imagen de la app (tras cambios en Dockerfile.dev)
docker compose build app
docker compose up -d
```

## 6. Acceso directo a MySQL

Desde el host:

```bash
docker compose exec mysql mysql -u qro_user -pqro_pass qro_territory
```

Credenciales del entorno local (definidas en `.env`):

| Variable | Valor |
|----------|-------|
| Base de datos | `qro_territory` |
| Usuario | `qro_user` |
| Contraseña | `qro_pass` |
| Puerto | `3306` |
