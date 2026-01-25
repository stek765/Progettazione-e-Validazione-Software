package it.univr.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class RegistrationPageObject extends PageObject {

    // --- SEZIONE ACCOUNT ---
    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "confirmPassword")
    private WebElement confirmPasswordInput;

    // --- SEZIONE DATI PERSONALI ---
    @FindBy(id = "firstname")
    private WebElement firstNameInput;

    @FindBy(id = "lastname")
    private WebElement lastNameInput;

    @FindBy(id = "gender")
    private WebElement genderSelect;

    // --- SEZIONE CONTATTI ---
    @FindBy(id = "city")
    private WebElement cityInput;

    @FindBy(id = "telephoneNumber")
    private WebElement telephoneInput;

    @FindBy(id = "address")
    private WebElement addressInput;

    @FindBy(id = "taxIdentificationNumber")
    private WebElement taxIdInput;

    // --- BOTTONI E MESSAGGI ---
    @FindBy(id = "submitBtn")
    private WebElement submitButton;

    @FindBy(css = ".alert-error")
    private WebElement errorMessage;

    public RegistrationPageObject(WebDriver driver) {
        super(driver);
    }

    public void createNewUser(String username, String email, String password, String confirmPassword,
                              String firstName, String lastName, String gender,
                              String city, String phone, String address, String taxId) {

        // 1. Aspetta visibilità del form (Standard Selenium)
        wait.until(ExpectedConditions.visibilityOf(usernameInput));

        // 2. Compila i campi
        usernameInput.sendKeys(username);
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        confirmPasswordInput.sendKeys(confirmPassword);

        firstNameInput.sendKeys(firstName);
        lastNameInput.sendKeys(lastName);

        // Dropdown
        if (gender != null) {
            Select select = new Select(genderSelect);
            select.selectByValue(gender);
        }

        // Opzionali
        if (city != null) cityInput.sendKeys(city);
        if (phone != null) telephoneInput.sendKeys(phone);
        if (address != null) addressInput.sendKeys(address);
        if (taxId != null) taxIdInput.sendKeys(taxId);

        // 3. Click sicuro (Aspetta che sia cliccabile)
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        submitButton.click();
    }

    public String getErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return errorMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }
}