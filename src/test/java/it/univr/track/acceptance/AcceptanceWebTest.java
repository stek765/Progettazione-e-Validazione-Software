package it.univr.track.acceptance;

import io.github.bonigarcia.wdm.WebDriverManager;
import it.univr.pageObjects.GestioneUtentiPageObject;
import it.univr.pageObjects.LoginPageObject;
import it.univr.pageObjects.NavbarPageObject;
import it.univr.pageObjects.RegistrationPageObject;
import it.univr.track.SmartTrackApplication;
import it.univr.track.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SmartTrackApplication.class)
@Sql(scripts = "/script/adminSIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AcceptanceWebTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        // Setup WebDriver per Firefox (NON headless per vederlo in azione)
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();

        // Timeout implicito per dare tempo al rendering
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void scenario1_adminCreatesUser() {
        String baseUrl = "http://localhost:" + port;

        // 1. Login come Admin
        driver.get(baseUrl + "/signIn");
        LoginPageObject loginPage = new LoginPageObject(driver);
        loginPage.performLogin("admin", "password");

        // 2. Navigazione a Gestione Utenti
        NavbarPageObject navbar = new NavbarPageObject(driver);
        navbar.goToUserManagementPage();

        GestioneUtentiPageObject utentiPage = new GestioneUtentiPageObject(driver);

        // Verifico di essere sulla pagina giusta (opzionale ma utile)
        assertTrue(utentiPage.getPageTitle().contains("Gestione Utenti"),
                "Il titolo della pagina dovrebbe essere 'Gestione Utenti'");

        // 3. Click su "Nuovo Utente"
        utentiPage.goToRegistrationPage();

        // 4. Compilazione Form
        RegistrationPageObject registrationPage = new RegistrationPageObject(driver);
        String newUsername = "nuovoutente";
        registrationPage.createNewUser(
                newUsername,
                "nuovo@example.com",
                "Password123!",
                "Password123!",
                "Mario",
                "Rossi",
                "MALE",
                "Verona",
                "3331234567",
                "Via Roma 1",
                "RSSMRA80A01H501U");

        // 5. Verifica creazione
        // Dopo il submit, dovremmo essere reindirizzati alla lista utenti
        assertTrue(utentiPage.isUserPresent(newUsername), "Il nuovo utente dovrebbe apparire nella tabella.");
    }
}
