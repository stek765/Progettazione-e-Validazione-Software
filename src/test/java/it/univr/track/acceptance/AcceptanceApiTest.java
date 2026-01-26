package it.univr.track.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "gestione-utenti", "gestione-dispositivi" })
class AcceptanceApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void testLoginEndpoint() {
        // Form Login default Spring Security
        given()
                .contentType(ContentType.URLENC)
                .formParam("username", "admin")
                .formParam("password", "password")
                .when()
                // Configured loginProcessingUrl is /signIn
                .post("/signIn")
                .then()
                .statusCode(302) // Redirects on success
                .header("Location", containsString("dashboard"));
    }

    // --- DEVICE API TESTS ---

    @Test
    void testApiAddDevice() {
        // Verifica API per aggiungere un dispositivo (POST)
        given()
                .auth().preemptive().basic("admin", "password")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/device")
                .then()
                // Status 200 (se ritorna boolean true) o 201 Created
                .statusCode(anyOf(is(200), is(201)));
    }

    @Test
    void testApiGetDevice() {
        // Verifica API per leggere la configurazione di un dispositivo (GET)
        given()
                .auth().preemptive().basic("admin", "password")
                .when()
                .get("/api/device/1") // ID arbitrario
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void testApiUpdateDevice() {
        // Verifica API per aggiornare un dispositivo (PUT)
        given()
                .auth().preemptive().basic("admin", "password")
                .contentType(ContentType.JSON)
                .when()
                .put("/api/device")
                .then()
                .statusCode(200);
    }

    @Test
    void testApiDeleteDevice() {
        // Verifica API per dismettere un dispositivo (DELETE)
        given()
                .auth().preemptive().basic("admin", "password")
                .when()
                .delete("/api/device")
                .then()
                .statusCode(200);
    }

    @Test
    void testApiGetAllDevices() {
        // Verifica API per listare i dispositivi
        given()
                .auth().preemptive().basic("admin", "password")
                .when()
                .get("/api/devices")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThanOrEqualTo(0)));
    }
}
