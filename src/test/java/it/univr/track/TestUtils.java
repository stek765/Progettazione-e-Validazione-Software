package it.univr.track;

import it.univr.pageObjects.LoginPageObject;
import org.openqa.selenium.WebDriver;

public class TestUtils {

    public static void effettuaLoginAdmin(WebDriver driver) {
        driver.get("http://localhost:8080/signIn");
        LoginPageObject loginPage = new LoginPageObject(driver);
        loginPage.performLogin("admin", "password");
    }
}
