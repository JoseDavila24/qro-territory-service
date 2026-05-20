# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this service does

REST API for querying the catalog of municipal delegations (delegaciones) of Querétaro and their associated settlements (colonias). Used primarily to auto-populate address forms: given a delegation, return its list of colonies. Base path: `/api/v1`.

## Commands

```bash
# Start local environment (MySQL + app)
docker compose up -d

# Enter the app container, then run Maven commands with `mvn` (not ./mvnw — breaks on Windows mounts)
docker compose exec app bash

# Inside the container:
mvn quarkus:dev          # dev mode with live reload
mvn verify -B            # run all tests
mvn test -Dtest=MyTest   # run a single test class
mvn package -DskipTests  # package as JVM JAR
```

Dev UI is available at http://localhost:8080/q/dev/ in dev mode.
Health: `GET /q/health` | Metrics: `GET /metrics`

## Tech stack

- **Quarkus 3.35.3** with Java 21
- **Hibernate ORM with Panache** — use the active record pattern (`entity.persist()`, `Entity.findById()`)
- **Jakarta REST** (JAX-RS) for REST resources
- **Hibernate Validator** for bean validation
- **MySQL 8.0** via JDBC; schema managed by `quarkus.hibernate-orm.database.generation=update`
- **SmallRye Health** + **Micrometer/Prometheus** for observability

## Architecture

Three-layer structure under `com.tecnm.qro.api`:

```
model/      ← JPA entities (Panache active record)
service/    ← Business logic, transactional operations
resource/   ← Jakarta REST endpoints (thin controllers)
```

The **OpenAPI contract is the source of truth** — all endpoints, schemas, and validation rules are defined in `src/main/resources/api/openapi.yaml`. Implement strictly to match it.

## API contract summary

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/v1/delegaciones` | — | List all 7 delegations |
| GET | `/api/v1/delegaciones/{id}` | — | Get delegation by ID |
| GET | `/api/v1/colonias?delegacion=NAME` | — | List colonies by delegation name (enum) |
| GET | `/api/v1/colonias/{id}` | — | Get colony by ID |
| POST | `/api/v1/admin/colonias` | X-API-KEY | Create colony |
| PUT | `/api/v1/admin/colonias/{id}` | X-API-KEY | Update colony |

**Admin endpoints** require `X-API-KEY` header. Return `401` when key is missing/invalid.

**Key validation rules from the spec:**
- `codigo_postal`: exactly 5 digits (`^\d{5}$`)
- `ColoniaInput.nombre`: must start with uppercase (`^[A-ZÁÉÍÓÚÜÑ].*`)
- `delegacion_id` referencing a non-existent delegation → `422 Unprocessable Entity`
- `NombreDelegacion` is a fixed enum of 7 values (e.g. `CENTRO_HISTORICO`, `SANTA_ROSA_JAUREGUI`)

## Data models

- **Delegacion**: `id`, `nombre`, `sede` (nullable address)
- **Colonia**: `id`, `nombre`, `codigo_postal`, `tipo_asentamiento` (enum), `delegacion_id`
- **TipoAsentamiento** enum: `FRACCIONAMIENTO`, `BARRIO`, `COLONIA`, `CONDOMINIO`, `UNIDAD_HABITACIONAL`, `PUEBLO`, `EJIDO`, `ZONA_INDUSTRIAL`, `ZONA_COMERCIAL`, `EQUIPAMIENTO`, `RANCHERIA`

## Testing

Tests use `@QuarkusTest` + REST-Assured. Integration tests (with IT suffix) run against a real running instance. Database credentials for local dev are in `.env` (`qro_user`/`qro_pass`, db `qro_territory`).

## Docker / local setup

`compose.yaml` (not `docker-compose.yaml`) defines MySQL 8.0 + the app container. No `version:` key. The `.env` file holds DB credentials. App exposes port 8080 (API) and 5005 (debug).
