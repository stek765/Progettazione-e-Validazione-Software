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
    @FindBy(css = "#unassigned-pool .device-chip")
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
        return unassignedDevices.stream()
                .anyMatch(d -> d.getText().contains(deviceName));
    }

    /**
     * Verifica se un dispositivo è presente nella lista di uno specifico utente
     */
    public boolean userHasDevice(String username, String deviceName) {
        try {
            // Trova la lista specifica dell'utente: id="user-{username}"
            WebElement userList = driver.findElement(By.id("user-" + username));
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
                .orElseThrow(() -> new RuntimeException("Device '" + deviceName + "' non trovato nei non assegnati."));

        // 2. Trova l'elemento destinazione (la lista dell'utente)
        WebElement targetList = driver.findElement(By.id("user-" + targetUsername));

        // 3. Esegui simulazione JS
        simulateDragAndDrop(sourceDevice, targetList);
    }

    // Helper per simulare Drag & Drop HTML5
    private void simulateDragAndDrop(WebElement source, WebElement target) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "function createEvent(typeOfEvent) {" +
                        "var event =document.createEvent(\"CustomEvent\");" +
                        "event.initCustomEvent(typeOfEvent,true, true, null);" +
                        "event.dataTransfer = {" +
                        "data: {}," +
                        "setData: function (key, value) {" +
                        "this.data[key] = value;" +
                        "}," +
                        "getData: function (key) {" +
                        "return this.data[key];" +
                        "}" +
                        "};" +
                        "return event;" +
                        "}" +
                        "function dispatchEvent(element, event,transferData) {" +
                        "if (transferData !== undefined) {" +
                        "event.dataTransfer = transferData;" +
                        "}" +
                        "if (element.dispatchEvent) {" +
                        "element.dispatchEvent(event);" +
                        "} else if (element.fireEvent) {" +
                        "element.fireEvent(\"on\" + event.type, event);" +
                        "}" +
                        "}" +
                        "function simulateHTML5DragAndDrop(element, destination) {" +
                        "var dragStartEvent =createEvent('dragstart');" +
                        "dispatchEvent(element, dragStartEvent);" +
                        "var dropEvent = createEvent('drop');" +
                        "dispatchEvent(destination, dropEvent,dragStartEvent.dataTransfer);" +
                        "var dragEndEvent = createEvent('dragend');" +
                        "dispatchEvent(element, dragEndEvent,dropEvent.dataTransfer);" +
                        "}" +
                        "simulateHTML5DragAndDrop(arguments[0], arguments[1])",
                source, target);
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