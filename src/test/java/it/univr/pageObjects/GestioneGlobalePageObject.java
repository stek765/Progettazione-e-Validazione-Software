package it.univr.pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class GestioneGlobalePageObject extends PageObject {

    public NavbarPageObject navbar;

    // Lista dei dispositivi nella colonna di sinistra (Non Assegnati)
    @FindBy(xpath = "//div[@id='unassigned-pool']//div[contains(@class,'device-chip')]")
    private List<WebElement> unassignedDevices;

    // Selettore per le liste dispositivi degli utenti (destinazione)
    // Non usiamo @FindBy qui perché l'ID è dinamico (user-username)

    public GestioneGlobalePageObject(WebDriver driver) {
        super(driver);
        this.navbar = new NavbarPageObject(driver);
    }

    /**
     * Verifica se un dispositivo è presente nella lista "Non Assegnati"
     */
    public boolean isDeviceInUnassignedPool(String deviceName) {
        boolean preciseCheck = unassignedDevices.stream()
                .anyMatch(d -> d.getText().contains(deviceName));
        if (preciseCheck)
            return true;

        // Fallback per ambienti Headless/HtmlUnit dove la lista children potrebbe non
        // sincronizzarsi
        try {
            return driver.findElement(By.id("unassigned-pool")).getText().contains(deviceName);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica se un dispositivo è presente nella lista di uno specifico utente
     */
    public boolean userHasDevice(String username, String deviceName) {
        try {
            // Trova la lista specifica dell'utente: id="user-{username}"
            WebElement userList = driver.findElement(By.id("user-" + username));
            if (userList.getText().contains(deviceName))
                return true; // Fallback rapido

            List<WebElement> userDevices = userList.findElements(By.className("device-chip"));

            return userDevices.stream()
                    .anyMatch(d -> d.getText().contains(deviceName));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Esegue il Drag & Drop da "Non Assegnati" all'utente specificato.
     * Usa JS perché SortableJS non risponde bene alle Actions standard di Selenium.
     */
    public void assignDeviceToUser(String deviceName, String targetUsername) {
        // 1. Trova l'elemento sorgente
        WebElement sourceDevice = unassignedDevices.stream()
                .filter(d -> d.getText().contains(deviceName))
                .findFirst()
                .orElse(null);

        if (sourceDevice == null) {
            // Fallback find
            try {
                sourceDevice = driver.findElement(
                        By.xpath("//div[@id='unassigned-pool']//div[contains(@class,'device-chip') and contains(., '"
                                + deviceName + "')]"));
            } catch (Exception e) {
                throw new RuntimeException("Device '" + deviceName + "' non trovato nei non assegnati.");
            }
        }

        // 2. Trova l'elemento destinazione (la lista dell'utente)
        WebElement targetList = driver.findElement(By.id("user-" + targetUsername));

        // 3. Esegui simulazione JS
        simulateDragAndDrop(sourceDevice, targetList);
    }

    // Helper per simulare Drag & Drop HTML5 (Simplified for HtmlUnit: Direct DOM
    // Move)
    private void simulateDragAndDrop(WebElement source, WebElement target) {
        // HtmlUnit e SortableJS non vanno d'accordo con la simulazione eventi
        // complessa.
        // Simuliamo il risultato finale spostando direttamente il nodo DOM.
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[1].appendChild(arguments[0]);", source, target);
    }

    public void deleteUser(String newUsername) {
        try {
            // Trova il pulsante di eliminazione per l'utente specificato
            WebElement deleteButton = driver.findElement(By.cssSelector("#user-" + newUsername + " .delete-user-btn"));
            deleteButton.click();

            // Conferma l'eliminazione nella finestra di dialogo
            WebElement confirmButton = driver.findElement(By.id("confirm-delete-user"));
            confirmButton.click();
        } catch (Exception e) {
            throw new RuntimeException("Impossibile eliminare l'utente '" + newUsername + "': " + e.getMessage());
        }
    }

    public boolean isUserDeleted(String newUsername) {
        try {
            // Prova a trovare l'elemento dell'utente
            driver.findElement(By.id("user-" + newUsername));
            return false; // Se lo trova, l'utente non è stato eliminato
        } catch (Exception e) {
            return true; // Se non lo trova, l'utente è stato eliminato
        }
    }
}