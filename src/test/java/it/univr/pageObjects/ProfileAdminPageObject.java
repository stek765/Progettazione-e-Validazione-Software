package it.univr.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProfileAdminPageObject extends PageObject {

    public NavbarPageObject navbar;

    @FindBy(className = "profile-name")
    private WebElement profileNameDisplay;

    @FindBy(css = ".profile-role span")
    private WebElement profileRoleDisplay;

    @FindBy(css = ".info-grid .info-item:nth-of-type(1) .info-value")
    private WebElement usernameDisplay;

    @FindBy(css = ".info-grid .info-item:nth-of-type(2) .info-value")
    private WebElement emailDisplay;

    // --- FORM DI MODIFICA ---
    @FindBy(id = "firstname")
    private WebElement firstNameInput;

    @FindBy(id = "lastname")
    private WebElement lastNameInput;

    @FindBy(id = "city")
    private WebElement cityInput;

    @FindBy(id = "telephoneNumber")
    private WebElement phoneInput;

    @FindBy(id = "address")
    private WebElement addressInput;

    @FindBy(id = "taxIdentificationNumber")
    private WebElement taxIdInput;

    @FindBy(css = "#profileForm button[type='submit']")
    private WebElement saveButton;

    @FindBy(className = "alert-success")
    private WebElement successMessage;

    // --- CAMBIO PASSWORD ---
    @FindBy(id = "oldPassword")
    private WebElement oldPasswordInput;

    @FindBy(id = "newPassword")
    private WebElement newPasswordInput;

    @FindBy(id = "confirmPassword")
    private WebElement confirmPasswordInput;

    @FindBy(css = "#passwordForm button[type='submit']")
    private WebElement changePasswordButton;

    public ProfileAdminPageObject(WebDriver driver) {
        super(driver);
        this.navbar = new NavbarPageObject(driver);
    }

    // --- METODI DI LETTURA ---
    // SOLUZIONE: innerText legge il contenuto HTML ignorando la visibilità CSS
    // (animazioni)
    public String getProfileName() {
        return profileNameDisplay.getAttribute("innerText").trim();
    }

    public String getRole() {
        return profileRoleDisplay.getAttribute("innerText").trim();
    }

    public String getUsername() {
        return usernameDisplay.getAttribute("innerText").trim();
    }

    public String getEmail() {
        return emailDisplay.getAttribute("innerText").trim();
    }

    // --- METODI DI SCRITTURA ---
    public void editProfile(String firstName, String lastName, String city, String phone, String address,
            String taxId) {
        // Aspettiamo che il primo campo sia visibile per essere sicuri che il form sia
        // pronto
        // Nota: 'wait' è ereditato da PageObject (se protected)
        wait.until(ExpectedConditions.visibilityOf(firstNameInput));

        if (firstName != null) {
            firstNameInput.clear();
            firstNameInput.sendKeys(firstName);
        }
        if (lastName != null) {
            lastNameInput.clear();
            lastNameInput.sendKeys(lastName);
        }
        if (city != null) {
            cityInput.clear();
            cityInput.sendKeys(city);
        }
        if (phone != null) {
            phoneInput.clear();
            phoneInput.sendKeys(phone);
        }
        if (address != null) {
            addressInput.clear();
            addressInput.sendKeys(address);
        }
        if (taxId != null) {
            taxIdInput.clear();
            taxIdInput.sendKeys(taxId);
        }

        // Click sicuro
        wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        saveButton.click();
    }

    public void updateContactInfo(String city, String phone) {
        editProfile(null, null, city, phone, null, null);
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(successMessage));
            return successMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getSuccessMessageText() {
        if (isSuccessMessageDisplayed()) {
            return successMessage.getText();
        }
        return "";
    }

    public void changePassword(String oldPassword, String newPassword, String confirmPassword) {
        wait.until(ExpectedConditions.visibilityOf(oldPasswordInput));
        oldPasswordInput.clear();
        oldPasswordInput.sendKeys(oldPassword);

        newPasswordInput.clear();
        newPasswordInput.sendKeys(newPassword);

        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys(confirmPassword);

        wait.until(ExpectedConditions.elementToBeClickable(changePasswordButton));
        changePasswordButton.click();
    }
}