package it.univr.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class NavbarPageObject extends PageObject {

    @FindBy(css = "a[href*='/dashboard']")
    private WebElement dashboardLink;

    @FindBy(css = "button.btn-logout")
    private WebElement logoutButton;

    @FindBy(css = "a[href*='/admin/users']")
    private WebElement userManagementLink;

    @FindBy(css = "a[href*='/web/utenti-e-dispositivi']")
    private WebElement gestioneGlobaleLink;

    @FindBy(css = "a[href*='/profile']")
    private WebElement profileLink;


    public NavbarPageObject(WebDriver driver) {
        super(driver); // InitElements automatico anche qui!
    }

    public void logout() {
        logoutButton.click();
    }

    public void goToDashboard() {
        dashboardLink.click();
    }

    public void goToUserManagementPage() {
        userManagementLink.click();
    }

    public void goToProfilePage() {
        profileLink.click();
    }

    public void goToGestioneGlobale() {
        gestioneGlobaleLink.click();
    }
}