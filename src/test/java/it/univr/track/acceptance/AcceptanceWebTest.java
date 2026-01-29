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
/**
 * Test di accettazione End-to-End (E2E) che simula l'interazione utente
 * completa via browser.
 * Copre i principali scenari d'uso (User Stories) interagendo con l'interfaccia
 * web reale.
 */
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

    // Scenario 1
    @Test
    public void scenario1_adminCreatesUser() {
        String baseUrl = "http://localhost:" + port;

        driver.get(baseUrl + "/signIn");
        LoginPageObject loginPage = new LoginPageObject(driver);
        loginPage.performLogin("admin", "password");

        NavbarPageObject navbar = new NavbarPageObject(driver);
        navbar.goToUserManagementPage();

        GestioneUtentiPageObject utentiPage = new GestioneUtentiPageObject(driver);

        assertTrue(utentiPage.getPageTitle().contains("Gestione Utenti"),
                "Il titolo della pagina dovrebbe essere 'Gestione Utenti'");

        utentiPage.goToRegistrationPage();

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

        assertTrue(utentiPage.isUserPresent(newUsername), "Il nuovo utente dovrebbe apparire nella tabella.");
    }

    // Scenario 2
    @Test
    public void scenario2_profilePasswordChange() {
        String baseUrl = "http://localhost:" + port;

        String username = "scenario2User";
        String oldPasswordRaw = "Password123!";
        String newPasswordRaw = "NewPassword123!";

        if (userRepository.findByUsername(username).isPresent()) {
            userRepository.delete(userRepository.findByUsername(username).get());
        }

        UserRegistered user = new UserRegistered();
        user.setUsername(username);
        user.setEmail("scenario2@test.it");
        user.setPassword(passwordEncoder.encode(oldPasswordRaw));
        user.setFirstname("User");
        user.setLastname("scenario 2");
        user.setRole(Role.USER);
        user.setGender(it.univr.track.entity.enumeration.Gender.OTHER);
        userRepository.save(user);

        driver.get(baseUrl + "/signIn");
        LoginPageObject loginPage = new LoginPageObject(driver);
        loginPage.performLogin(username, oldPasswordRaw);

        NavbarPageObject navbar = new NavbarPageObject(driver);
        navbar.goToProfilePage();

        ProfileAdminPageObject profilePage = new ProfileAdminPageObject(driver);

        String displayedUsername = profilePage.getUsername();

        assertTrue(displayedUsername.toLowerCase().contains(username.toLowerCase()),
                "Dovremmo essere sul profilo di " + username + " but found '" + displayedUsername + "'");

        profilePage.changePassword(oldPasswordRaw, newPasswordRaw, newPasswordRaw);

        assertTrue(profilePage.isSuccessMessageDisplayed(), "Dovrebbe apparire il messaggio di successo");

        navbar.logout();
        driver.manage().deleteAllCookies();

        driver.get(baseUrl + "/signIn");
        loginPage.performLogin(username, newPasswordRaw);

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/dashboard"),
                "Dopo il login con la nuova password, dovremmo essere in dashboard. URL attuale: " + currentUrl);
    }

    // Scenario 3
    @Test
    public void scenario3_deviceAssignment() {
        String baseUrl = "http://localhost:" + port;

        String username = "userAssign";

        UserRegistered user = new UserRegistered();
        user.setUsername(username);
        user.setEmail("assign@test.it");
        user.setPassword(passwordEncoder.encode("Password123!"));
        user.setFirstname("Assign");
        user.setLastname("User");
        user.setRole(Role.USER);
        user.setGender(it.univr.track.entity.enumeration.Gender.MALE);
        userRepository.save(user);

        String deviceName = "TestSensor-01";
        Device device = new Device(deviceName, DeviceStatus.AVAILABLE, null);
        deviceRepository.save(device);

        try {
            driver.get(baseUrl + "/signIn");
            LoginPageObject loginPage = new LoginPageObject(driver);
            loginPage.performLogin("admin", "password");

            driver.get(baseUrl + "/web/utenti-e-dispositivi");

            DeviceUsersPageObject devicePage = new DeviceUsersPageObject(driver);
            devicePage.assignDeviceToUser(deviceName, username);

            boolean assigned = devicePage.isDeviceAssignedToUser(deviceName, username);
            assertTrue(assigned, "Device '" + deviceName + "' should be assigned to user '" + username + "' in the UI");

        } catch (Exception e) {
            throw e;
        }
    }

    // Scenario 4
    @Test
    public void scenario4_deviceProvisioning() {
        String baseUrl = "http://localhost:" + port;

        String deviceName = "ProvisionDevice-01";
        Device device = new Device(deviceName, DeviceStatus.AVAILABLE, null);
        deviceRepository.save(device);

        try {
            driver.get(baseUrl + "/signIn");
            LoginPageObject loginPage = new LoginPageObject(driver);
            loginPage.performLogin("admin", "password");

            driver.get(baseUrl + "/web/utenti-e-dispositivi");

            DeviceUsersPageObject deviceListPage = new DeviceUsersPageObject(driver);
            deviceListPage.goToDeviceDetails(deviceName);

            DeviceProvisioningPageObject provisionPage = new DeviceProvisioningPageObject(driver);

            provisionPage.toggleProvisioning();

            assertTrue(provisionPage.isProvisioningDataVisible(),
                    "MAC Address and Private Key should be visible after provisioning.");

        } catch (Exception e) {
            throw e;
        }
    }

    // Scenario 5
    @Test
    public void scenario5_securityPermissions() {
        String baseUrl = "http://localhost:" + port;

        String username = "stdUser";
        UserRegistered user = new UserRegistered();
        user.setUsername(username);
        user.setEmail("std@test.it");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstname("Standard");
        user.setLastname("User");
        user.setRole(Role.USER);
        user.setGender(it.univr.track.entity.enumeration.Gender.FEMALE);
        userRepository.save(user);

        String deviceName = "ReadOnlyDevice";
        Device device = new Device(deviceName, DeviceStatus.AVAILABLE, null);
        deviceRepository.save(device);

        driver.get(baseUrl + "/signIn");
        LoginPageObject loginPage = new LoginPageObject(driver);
        loginPage.performLogin(username, "password");

        driver.get(baseUrl + "/admin/users");

        String pageSource = driver.getPageSource();
        boolean isForbidden = pageSource.contains("Forbidden") || pageSource.contains("Access Denied") ||
                driver.getTitle().contains("403") || driver.getCurrentUrl().contains("login");

        assertTrue(isForbidden, "Access to /admin/users should be forbidden for Role USER. Source excerpt: "
                + pageSource.substring(0, Math.min(pageSource.length(), 200)));

        driver.get(baseUrl + "/web/utenti-e-dispositivi");
        DeviceUsersPageObject devicePage = new DeviceUsersPageObject(driver);

        assertTrue(devicePage.isDragAndDropDisabled(), "Drag & Drop should be disabled (class no-drag) for non-admins");

        devicePage.goToDeviceDetails(deviceName);
        DeviceProvisioningPageObject provisionPage = new DeviceProvisioningPageObject(driver);

        assertTrue(!provisionPage.isProvisioningSectionPresent(),
                "Provisioning controls should NOT be visible for standard users");

        driver.get(baseUrl + "/dashboard");

        NavbarPageObject navbar = new NavbarPageObject(driver);
        navbar.logout();
    }
}
