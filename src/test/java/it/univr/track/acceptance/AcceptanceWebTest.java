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

    @Test
    public void scenario3_deviceAssignment() {
        String baseUrl = "http://localhost:" + port;

        // 1. Create User and Device in DB
        String username = "userAssign";
        // Note: @Sql cleans DB before test, so we are good.

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
            // 2. Login as Admin
            driver.get(baseUrl + "/signIn");
            LoginPageObject loginPage = new LoginPageObject(driver);
            loginPage.performLogin("admin", "password");

            // 3. Go to deviceAndUsers
            // Using explicit URL navigation as per prompt instruction "vai su
            // deviceAndUsers.html"
            driver.get(baseUrl + "/web/utenti-e-dispositivi");

            // 4. Assign
            DeviceUsersPageObject devicePage = new DeviceUsersPageObject(driver);
            devicePage.assignDeviceToUser(deviceName, username);

            // 5. Verify
            boolean assigned = devicePage.isDeviceAssignedToUser(deviceName, username);
            assertTrue(assigned, "Device '" + deviceName + "' should be assigned to user '" + username + "' in the UI");

        } catch (Exception e) {
            // Screenshot/Source dump handled by framework or manually here if needed
            throw e;
        }
    }

    @Test
    public void scenario4_deviceProvisioning() {
        String baseUrl = "http://localhost:" + port;

        // 1. Create Device in DB
        String deviceName = "ProvisionDevice-01";
        Device device = new Device(deviceName, DeviceStatus.AVAILABLE, null);
        deviceRepository.save(device);

        try {
            // 2. Login as Admin
            driver.get(baseUrl + "/signIn");
            LoginPageObject loginPage = new LoginPageObject(driver);
            loginPage.performLogin("admin", "password");

            // 3. Go to deviceAndUsers
            driver.get(baseUrl + "/web/utenti-e-dispositivi");

            // 4. Click detail link
            DeviceUsersPageObject deviceListPage = new DeviceUsersPageObject(driver);
            deviceListPage.goToDeviceDetails(deviceName);

            // 5. Provisioning Page
            DeviceProvisioningPageObject provisionPage = new DeviceProvisioningPageObject(driver);

            // 6. Act: Toggle Provisioning
            provisionPage.toggleProvisioning();

            // 7. Assert: Check MAC and Key visibility
            assertTrue(provisionPage.isProvisioningDataVisible(),
                    "MAC Address and Private Key should be visible after provisioning.");

            // Optional: Log values
            System.out.println("DEBUG: Generated MAC: " + provisionPage.getMacAddress());
            System.out.println("DEBUG: Generated Key: " + provisionPage.getPrivateKey().substring(0, 20) + "...");

        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void scenario5_securityPermissions() {
        String baseUrl = "http://localhost:" + port;

        // 1. Create Standard User
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

        // Create a device for detail check
        String deviceName = "ReadOnlyDevice";
        Device device = new Device(deviceName, DeviceStatus.AVAILABLE, null);
        deviceRepository.save(device);

        // 2. Login
        driver.get(baseUrl + "/signIn");
        LoginPageObject loginPage = new LoginPageObject(driver);
        loginPage.performLogin(username, "password");

        // 3. Test Blocked URL (/admin/users)
        driver.get(baseUrl + "/admin/users");
        // Expecting 403 Forbidden or Redirect to Login or Error Page
        // Spring Security usually returns 403 status, browser shows a default error
        // page or custom one.
        // We can check the title or page content. Default Spring Boot 403 page often
        // has "Forbidden" text.
        String pageSource = driver.getPageSource();
        boolean isForbidden = pageSource.contains("Forbidden") || pageSource.contains("Access Denied") ||
                driver.getTitle().contains("403") || driver.getCurrentUrl().contains("login");

        // Note: Our SecurityConfig uses .anyRequest().authenticated() and restricted
        // /admin/**
        // It's likely returning a WhiteLabel Error Page with 403 status.
        assertTrue(isForbidden, "Access to /admin/users should be forbidden for Role USER. Source excerpt: "
                + pageSource.substring(0, Math.min(pageSource.length(), 200)));

        // 4. Test UI Permissions (Device Page)
        driver.get(baseUrl + "/web/utenti-e-dispositivi");
        DeviceUsersPageObject devicePage = new DeviceUsersPageObject(driver);

        // Check Drag & Drop disabled
        assertTrue(devicePage.isDragAndDropDisabled(), "Drag & Drop should be disabled (class no-drag) for non-admins");

        // 5. Test UI Detail Page
        devicePage.goToDeviceDetails(deviceName);
        DeviceProvisioningPageObject provisionPage = new DeviceProvisioningPageObject(driver);

        // Check Provisioning Section Hidden
        assertTrue(!provisionPage.isProvisioningSectionPresent(),
                "Provisioning controls should NOT be visible for standard users");

        // Navigate to Dashboard to access Navbar for logout (provisioning page has no
        // navbar)
        driver.get(baseUrl + "/dashboard");

        // Logout
        NavbarPageObject navbar = new NavbarPageObject(driver);
        navbar.logout();
    }
}
