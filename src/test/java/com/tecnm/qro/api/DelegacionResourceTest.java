package com.tecnm.qro.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class DelegacionResourceTest {

    @Test
    void listAll_returns7Delegaciones() {
        given()
            .when().get("/api/v1/delegaciones")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(7));
    }

    @Test
    void listAll_eachItemHasRequiredFields() {
        given()
            .when().get("/api/v1/delegaciones")
            .then()
                .statusCode(200)
                .body("id", everyItem(notNullValue()))
                .body("nombre", everyItem(not(emptyOrNullString())));
    }

    @Test
    void getById_existingId_returns200() {
        given()
            .when().get("/api/v1/delegaciones/1")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("nombre", not(emptyOrNullString()));
    }

    @Test
    void getById_nonExistentId_returns404() {
        given()
            .when().get("/api/v1/delegaciones/9999")
            .then()
                .statusCode(404)
                .body("status", equalTo(404));
    }

    @Test
    void getById_nonNumericId_returns400() {
        given()
            .when().get("/api/v1/delegaciones/abc")
            .then()
                .statusCode(400)
                .body("status", equalTo(400));
    }

    @Test
    void getById_negativeId_returns400() {
        given()
            .when().get("/api/v1/delegaciones/-1")
            .then()
                .statusCode(400)
                .body("status", equalTo(400));
    }

    @Test
    void getById_zeroId_returns400() {
        given()
            .when().get("/api/v1/delegaciones/0")
            .then()
                .statusCode(400)
                .body("status", equalTo(400));
    }
}
