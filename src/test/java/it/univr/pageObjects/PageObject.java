package it.univr.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PageObject {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public PageObject(WebDriver driver) {
        this.driver = driver;

        // Inizializza il wait con 10 secondi di timeout
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Inizializza i @FindBy
        PageFactory.initElements(driver, this);
    }


}
