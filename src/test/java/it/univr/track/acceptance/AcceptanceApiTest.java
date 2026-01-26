package it.univr.track.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    /**
     * Test registrazione nuovo utente (API: /api/register)
     */
    @Test
    void testUserRegistration() {
        String randomUsername = "user_" + UUID.randomUUID().toString().substring(0, 8);
        Map<String, Object> user = new HashMap<>();
        user.put("username", randomUsername);
        user.put("email", randomUsername + "@example.com");
        user.put("password", "Password123!");
        user.put("firstname", "Test");
        user.put("lastname", "User");
        user.put("role", "USER");
        user.put("gender", "OTHER");
        user.put("city", "Verona");
        user.put("address", "Via Test");
        user.put("telephoneNumber", "1234567890");
        user.put("taxIdentificationNumber", "ABCDEF12G34H567I");

        // Esegui POST /api/register
        given()
                // Se necessario auth, ma su register di solito è pubblica o admin
                // Diciamo che qui è pubblica per il test
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/register")
                .then()
                .statusCode(200);

        // Verifica che l'utente sia nella lista (usando GET /api/users)
        given()
                .auth().preemptive().basic("admin", "password") // Assumiamo richieda auth
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .body("username", hasItem(randomUsername));
    }

    /**
     * Test Provisioning Dispositivo (API: /api/devices/provision)
     */
    @Test
    void testDeviceProvisioning() {
        Map<String, Object> device = new HashMap<>();
        device.put("name", "Sensor_" + UUID.randomUUID());
        // Status di default viene messo ACTIVE nel controller se null

        given()
                .contentType(ContentType.JSON)
                .body(device)
                .when()
                .post("/api/devices/provision")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("status", equalTo("ACTIVE"));
    }

    /**
     * Test Login Dispositivo (API: /api/device/login)
     */
    @Test
    void testDeviceLogin() {
        // 1. Provisioning di un dispositivo
        String deviceName = "LoginTestDevice_" + UUID.randomUUID();
        Map<String, Object> device = new HashMap<>();
        device.put("name", deviceName);

        Integer deviceId = given()
                .contentType(ContentType.JSON)
                .body(device)
                .when()
                .post("/api/devices/provision")
                .then()
                .statusCode(200)
                .extract().path("id");

        // 2. Login con dispositivo
        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("deviceId", deviceId);
        loginRequest.put("name", deviceName);

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/device/login")
                .then()
                .statusCode(200)
                .body(containsString("Dispositivo autenticato"));
    }

    /**
     * Test Lista Utenti (API: /api/users)
     */
    @Test
    void testListUsers() {
        given()
                .auth().preemptive().basic("admin", "password")
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThanOrEqualTo(1))); // Almeno admin esiste
    }
}
