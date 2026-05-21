package com.tecnm.qro.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class ColoniaResourceTest {

    @Test
    void listByDelegacion_validEnum_returns200() {
        given()
            .queryParam("delegacion", "CENTRO_HISTORICO")
            .when().get("/api/v1/colonias")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()))
                .body("id", everyItem(notNullValue()))
                .body("nombre", everyItem(not(emptyOrNullString())))
                .body("codigo_postal", everyItem(matchesRegex("\\d{5}")))
                .body("delegacion_id", everyItem(notNullValue()));
    }

    @Test
    void listByDelegacion_missingParam_returns400() {
        given()
            .when().get("/api/v1/colonias")
            .then()
                .statusCode(400)
                .body("status", equalTo(400));
    }

    @Test
    void listByDelegacion_invalidEnum_returns400() {
        given()
            .queryParam("delegacion", "DELEGACION_INVENTADA")
            .when().get("/api/v1/colonias")
            .then()
                .statusCode(400)
                .body("status", equalTo(400));
    }

    @Test
    void getById_existingId_returns200() {
        given()
            .when().get("/api/v1/colonias/1")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("nombre", not(emptyOrNullString()))
                .body("codigo_postal", matchesRegex("\\d{5}"))
                .body("delegacion_id", notNullValue());
    }

    @Test
    void getById_nonExistentId_returns404() {
        given()
            .when().get("/api/v1/colonias/999999")
            .then()
                .statusCode(404)
                .body("status", equalTo(404));
    }

    @Test
    void getById_nonNumericId_returns400() {
        given()
            .when().get("/api/v1/colonias/abc")
            .then()
                .statusCode(400)
                .body("status", equalTo(400));
    }

    @Test
    void getById_negativeId_returns400() {
        given()
            .when().get("/api/v1/colonias/-1")
            .then()
                .statusCode(400)
                .body("status", equalTo(400));
    }
}
