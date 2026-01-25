package it.univr.pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class GestioneUtentiPageObject extends PageObject {

    public NavbarPageObject navbar;

    @FindBy(tagName = "h1")
    private WebElement pageTitle;

    @FindBy(className = "btn-hero")
    private WebElement registerNewUserButton;

    public GestioneUtentiPageObject(WebDriver driver) {
        super(driver);
        this.navbar = new NavbarPageObject(driver);
    }

    public String getPageTitle() {
        // FIX: Aspetta che il titolo sia visibile
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
        return pageTitle.getText();
    }

    public void deleteUser(String username) {
        // Usa lo Username per trovare la riga, è più sicuro dell'email
        String xpath = String.format("//tr[.//span[text()='%s']]//button[contains(@class, 'delete')]", username);

        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        deleteButton.click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        // FIX CRITICO: Aspetta che l'utente sparisca dalla tabella!
        // Altrimenti il test controlla troppo presto.
        waitForUserToDisappear(username);
    }

    public boolean isUserPresent(String username) {
        try {
            String xpath = String.format("//tr[.//span[text()='%s']]", username);
            // Usiamo un wait breve (o findElements) per verificare la presenza
            return !driver.findElements(By.xpath(xpath)).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // Metodo helper per aspettare la cancellazione
    public void waitForUserToDisappear(String username) {
        String xpath = String.format("//tr[.//span[text()='%s']]", username);
        // Aspetta finché l'elemento NON è più visibile (invisibilityOfElementLocated)
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
    }

    public void goToRegistrationPage() {
        wait.until(ExpectedConditions.elementToBeClickable(registerNewUserButton));
        registerNewUserButton.click();
    }
}