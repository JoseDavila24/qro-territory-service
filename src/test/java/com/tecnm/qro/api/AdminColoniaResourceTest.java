package com.tecnm.qro.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class AdminColoniaResourceTest {

    private static final String API_KEY = "changeme";

    private static final String VALID_BODY = """
            {
              "nombre": "Colonia Nueva",
              "codigo_postal": "76000",
              "tipo_asentamiento": "COLONIA",
              "delegacion_id": 1
            }
            """;

    // --- POST /api/v1/admin/colonias ---

    @Test
    void create_validInput_returns201() {
        given()
            .header("X-API-KEY", API_KEY)
            .contentType(ContentType.JSON)
            .body(VALID_BODY)
            .when().post("/api/v1/admin/colonias")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("nombre", equalTo("Colonia Nueva"))
                .body("codigo_postal", equalTo("76000"))
                .body("tipo_asentamiento", equalTo("COLONIA"))
                .body("delegacion_id", equalTo(1));
    }

    @Test
    void create_missingApiKey_returns401() {
        given()
            .contentType(ContentType.JSON)
            .body(VALID_BODY)
            .when().post("/api/v1/admin/colonias")
            .then()
                .statusCode(401)
                .body("status", equalTo(401));
    }

    @Test
    void create_wrongApiKey_returns401() {
        given()
            .header("X-API-KEY", "clave-incorrecta")
            .contentType(ContentType.JSON)
            .body(VALID_BODY)
            .when().post("/api/v1/admin/colonias")
            .then()
                .statusCode(401)
                .body("status", equalTo(401));
    }

    @Test
    void create_nombreDoesNotStartWithUppercase_returns400() {
        String body = """
                {
                  "nombre": "colonia minuscula",
                  "codigo_postal": "76000",
                  "tipo_asentamiento": "COLONIA",
                  "delegacion_id": 1
                }
                """;
        given()
            .header("X-API-KEY", API_KEY)
            .contentType(ContentType.JSON)
            .body(body)
            .when().post("/api/v1/admin/colonias")
            .then()
                .statusCode(400)
                .body("status", equalTo(400));
    }

    @Test
    void create_invalidCodigoPostal_returns400() {
        String body = """
                {
                  "nombre": "Colonia Valida",
                  "codigo_postal": "7600",
                  "tipo_asentamiento": "COLONIA",
                  "delegacion_id": 1
                }
                """;
        given()
            .header("X-API-KEY", API_KEY)
            .contentType(ContentType.JSON)
            .body(body)
            .when().post("/api/v1/admin/colonias")
            .then()
                .statusCode(400)
                .body("status", equalTo(400));
    }

    @Test
    void create_nonExistentDelegacion_returns422() {
        String body = """
                {
                  "nombre": "Colonia Valida",
                  "codigo_postal": "76000",
                  "tipo_asentamiento": "COLONIA",
                  "delegacion_id": 9999
                }
                """;
        given()
            .header("X-API-KEY", API_KEY)
            .contentType(ContentType.JSON)
            .body(body)
            .when().post("/api/v1/admin/colonias")
            .then()
                .statusCode(422)
                .body("status", equalTo(422));
    }

    // --- PUT /api/v1/admin/colonias/{id} ---

    @Test
    void update_validInput_returns200() {
        String body = """
                {
                  "nombre": "Colonia Actualizada",
                  "codigo_postal": "76010",
                  "tipo_asentamiento": "BARRIO",
                  "delegacion_id": 2
                }
                """;
        given()
            .header("X-API-KEY", API_KEY)
            .contentType(ContentType.JSON)
            .body(body)
            .when().put("/api/v1/admin/colonias/1")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("nombre", equalTo("Colonia Actualizada"))
                .body("delegacion_id", equalTo(2));
    }

    @Test
    void update_nonExistentColonia_returns404() {
        given()
            .header("X-API-KEY", API_KEY)
            .contentType(ContentType.JSON)
            .body(VALID_BODY)
            .when().put("/api/v1/admin/colonias/999999")
            .then()
                .statusCode(404)
                .body("status", equalTo(404));
    }

    @Test
    void update_nonExistentDelegacion_returns422() {
        String body = """
                {
                  "nombre": "Colonia Valida",
                  "codigo_postal": "76000",
                  "tipo_asentamiento": "COLONIA",
                  "delegacion_id": 9999
                }
                """;
        given()
            .header("X-API-KEY", API_KEY)
            .contentType(ContentType.JSON)
            .body(body)
            .when().put("/api/v1/admin/colonias/1")
            .then()
                .statusCode(422)
                .body("status", equalTo(422));
    }

    @Test
    void update_missingApiKey_returns401() {
        given()
            .contentType(ContentType.JSON)
            .body(VALID_BODY)
            .when().put("/api/v1/admin/colonias/1")
            .then()
                .statusCode(401)
                .body("status", equalTo(401));
    }

    @Test
    void update_nonNumericId_returns400() {
        given()
            .header("X-API-KEY", API_KEY)
            .contentType(ContentType.JSON)
            .body(VALID_BODY)
            .when().put("/api/v1/admin/colonias/abc")
            .then()
                .statusCode(400)
                .body("status", equalTo(400));
    }
}
