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

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/register")
                .then()
                .statusCode(200); // Rimane 200 come da nuovo controller

        given()
                .auth().preemptive().basic("admin", "password")
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .body("username", hasItem(randomUsername));
    }

    @Test
    void testDeviceProvisioning() {
        Map<String, Object> device = new HashMap<>();
        device.put("name", "Sensor_" + UUID.randomUUID());

        given()
                .contentType(ContentType.JSON)
                .body(device)
                .when()
                .post("/api/devices/provision")
                .then()
                .statusCode(201) // CAMBIATO: Il nuovo controller restituisce 201 Created
                .body("id", notNullValue())
                .body("status", equalTo("ACTIVE"));
    }

    @Test
    void testDeviceLogin() {
        String deviceName = "LoginTestDevice_" + UUID.randomUUID();
        Map<String, Object> device = new HashMap<>();
        device.put("name", deviceName);

        // Effettuiamo il provisioning e ci aspettiamo 201
        Integer deviceId = given()
                .contentType(ContentType.JSON)
                .body(device)
                .when()
                .post("/api/devices/provision")
                .then()
                .statusCode(201) // CAMBIATO: 201 Created
                .extract().path("id");

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

    @Test
    void testListUsers() {
        given()
                .auth().preemptive().basic("admin", "password")
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThanOrEqualTo(1)));
    }
}