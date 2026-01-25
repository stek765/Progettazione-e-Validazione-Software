package it.univr.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPageObject extends PageObject {

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(css = ".alert-error")
    private WebElement errorMessage;

    @FindBy(css = ".alert-success")
    private WebElement successMessage;

    public LoginPageObject(WebDriver driver) {
        super(driver);
    }

    public void performLogin(String username, String password) {
        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        loginButton.click();
    }

    public String getErrorMessage() {
        // Controllo robusto: se non c'è errore, restituisce stringa vuota
        try {
            if (errorMessage.isDisplayed()) {
                return errorMessage.getText();
            }
        } catch (Exception e) {
            // Elemento non trovato o non visibile
        }
        return "";
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return successMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}