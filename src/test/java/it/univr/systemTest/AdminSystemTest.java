package it.univr.systemTest;

import it.univr.pageObjects.*;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Gender;
import it.univr.track.entity.enumeration.Role;
import it.univr.track.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class AdminSystemTest extends BaseTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testLoginAdmin() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("dashboard"));

        assertTrue(driver.getCurrentUrl().contains("dashboard"),
                "L'URL finale dovrebbe contenere 'dashboard'");
    }

    @Test
    public void testLogout() {

        DashboardPageObject dashboard = new DashboardPageObject(driver);
        dashboard.navbar.logout();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("signIn"));

        assertTrue(driver.getCurrentUrl().contains("signIn"),
                "L'URL finale dovrebbe contenere 'signIn'");
    }

    @Test
    public void testModificaProfiloAdmin() {

        DashboardPageObject dashboard = new DashboardPageObject(driver);
        dashboard.navbar.goToProfilePage();

        ProfileAdminPageObject profilePage = new ProfileAdminPageObject(driver);
        assertEquals("Admin User", profilePage.getProfileName());

        String newFirstName = "Super";
        String newLastName = "Admin";
        profilePage.editProfile(newFirstName, newLastName, null, null, null, null);

        assertEquals("Super Admin", profilePage.getProfileName(),
                "Il nome del profilo dovrebbe essere aggiornato");
    }

    @Test
    public void assegnaDispositivoUtenteTest() {

        driver.get("http://localhost:8080/signIn");
        LoginPageObject loginPage = new LoginPageObject(driver);
        loginPage.performLogin("admin", "password");

        GestioneGlobalePageObject mappaPage = new GestioneGlobalePageObject(driver);
        mappaPage.navbar.goToGestioneGlobale();

        String deviceName = "Test GPS-001";
        String targetUser = "admin";

        // Wait esplicito per sicurezza (la pagina potrebbe caricare i dati in ritardo)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("unassigned-pool"), deviceName));

        assertTrue(mappaPage.isDeviceInUnassignedPool(deviceName),
                "Il dispositivo GPS-001 deve esistere nel pool");

        mappaPage.assignDeviceToUser(deviceName, targetUser);

        assertTrue(mappaPage.userHasDevice(targetUser, deviceName));
    }

    @Test
    public void creazioneUtente() {
        DashboardPageObject dashboard = new DashboardPageObject(driver);
        dashboard.navbar.goToUserManagementPage();

        GestioneUtentiPageObject utentiPage = new GestioneUtentiPageObject(driver);
        assertTrue(utentiPage.getPageTitle().contains("Gestione Utenti"));

        String newUsername = "newuser";
        String newEmail = "nutente@gmail.com";
        String newPassword = "newpassword";
        String firstName = "Nuovo";
        String lastName = "Utente";
        String city = "CittàTest";
        String address = "Via Test 123";
        String phone = "0123456789";
        String taxId = "TAX987654";
        Gender gender = Gender.OTHER;

        utentiPage.goToRegistrationPage();
        RegistrationPageObject registrationpage = new RegistrationPageObject(driver);
        registrationpage.createNewUser(newUsername, newEmail, newPassword, newPassword,
                firstName, lastName, gender.name(), city, phone, address, taxId);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("admin/users"));
        assertTrue(utentiPage.isUserPresent(newUsername),
                "Il nuovo utente dovrebbe essere presente nella tabella");


    }

    @Test
    public void eliminazioneUtente() {
        // 1. Creazione Utente "Vittima"
        String usernameVittima = "userTest"; // Usiamo questo come ID

        UserRegistered newUser = new UserRegistered(
                "Test", "User", usernameVittima, "testpassword", "testuser@email.it",
                Role.USER, Gender.OTHER, "TestCity", "TestAddress", "1234567890", "TAX123456");

        // Salviamo usando save() semplice (come hai fatto negli altri test)
        userRepository.save(newUser);

        DashboardPageObject dashboard = new DashboardPageObject(driver);
        dashboard.navbar.goToUserManagementPage();

        GestioneUtentiPageObject utentiPage = new GestioneUtentiPageObject(driver);
        assertTrue(utentiPage.getPageTitle().contains("Gestione Utenti"));

        // 2. Verifica esistenza pre-cancellazione (Usa Username)
        assertTrue(utentiPage.isUserPresent(usernameVittima),
                "L'utente dovrebbe esistere prima della cancellazione");

        // 3. CANCELLAZIONE (Il metodo ora include l'attesa interna)
        utentiPage.deleteUser(usernameVittima);

        // 4. Verifica post-cancellazione
        assertFalse(utentiPage.isUserPresent(usernameVittima),
                "L'utente non dovrebbe più essere in tabella");
    }
}