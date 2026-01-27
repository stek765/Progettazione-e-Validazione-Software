package it.univr.track.acceptance;

import io.github.bonigarcia.wdm.WebDriverManager;
import it.univr.pageObjects.*;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.Device;
import it.univr.track.entity.enumeration.Role;
import it.univr.track.entity.enumeration.DeviceStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import it.univr.track.SmartTrackApplication;
import it.univr.track.user.UserRepository;
import it.univr.track.device.DeviceRepository;
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

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        assertTrue(utentiPage.isUserPresent(newUsername), "Il nuovo utente dovrebbe apparire nella tabella.");
    }

    @Test
    public void scenario2_profilePasswordChange() {
        System.out.println("DEBUG: Executing scenario2_profilePasswordChange");
        String baseUrl = "http://localhost:" + port;

        // 1. Setup Utente "Scenario 2" Programmaticamente
        String username = "scenario2User";
        String oldPasswordRaw = "Password123!";
        String newPasswordRaw = "NewPassword123!";

        // Ensure user does not exist
        if (userRepository.findByUsername(username).isPresent()) {
            System.out.println("DEBUG: User " + username + " already exists. Deleting...");
            userRepository.delete(userRepository.findByUsername(username).get());
        }

        UserRegistered user = new UserRegistered();
        user.setUsername(username);
        user.setEmail("scenario2@test.it");
        user.setPassword(passwordEncoder.encode(oldPasswordRaw));
        user.setFirstname("Scenario");
        user.setLastname("Two");
        user.setRole(Role.USER);
        user.setGender(it.univr.track.entity.enumeration.Gender.OTHER);
        userRepository.save(user);
        System.out.println("DEBUG: Created user " + username + " with ID " + user.getId());

        // 2. Login
        driver.get(baseUrl + "/signIn");
        LoginPageObject loginPage = new LoginPageObject(driver);
        loginPage.performLogin(username, oldPasswordRaw);

        // Navigazione al Profilo
        NavbarPageObject navbar = new NavbarPageObject(driver);
        navbar.goToProfilePage();

        ProfileAdminPageObject profilePage = new ProfileAdminPageObject(driver);

        // Verifica che siamo sul profilo giusto
        System.out.println("CURRENT URL: " + driver.getCurrentUrl());
        String displayedUsername = profilePage.getUsername();
        System.out.println("DISPLAYED USERNAME: '" + displayedUsername + "'");

        assertTrue(displayedUsername.toLowerCase().contains(username.toLowerCase()),
                "Dovremmo essere sul profilo di " + username + " but found '" + displayedUsername + "'");

        // 3. Cambio Password
        profilePage.changePassword(oldPasswordRaw, newPasswordRaw, newPasswordRaw);

        // Verifica messaggio successo (opzionale ma consigliato)
        assertTrue(profilePage.isSuccessMessageDisplayed(), "Dovrebbe apparire il messaggio di successo");

        // 4. Logout e pulizia
        navbar.logout();
        driver.manage().deleteAllCookies(); // Pulizia esplicita

        // 5. Login con NUOVA Password
        driver.get(baseUrl + "/signIn");
        loginPage.performLogin(username, newPasswordRaw);

        // 6. Verifica successo finale (Dashboard)
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/dashboard"),
                "Dopo il login con la nuova password, dovremmo essere in dashboard. URL attuale: " + currentUrl);
    }
}
