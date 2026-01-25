package it.univr.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DashboardPageObject extends PageObject {


    public NavbarPageObject navbar;

    @FindBy(css = ".hero h1")
    private WebElement welcomeMessage;

    @FindBy(css = ".stat-card:nth-child(1) .stat-value")
    private WebElement firstStatValue;

    public DashboardPageObject(WebDriver driver) {
        super(driver); // Chiama il costruttore di PageObject che fa: this.driver = driver + initElements
        this.navbar = new NavbarPageObject(driver); // Inizializza il componente navbar
    }

    public String getWelcomeMessage() {
        return welcomeMessage.getText();
    }

    public String getFirstStatValue() {
        return firstStatValue.getText();
    }
}