package it.univr.systemTest;

import io.github.bonigarcia.wdm.WebDriverManager;
import it.univr.track.SmartTrackApplication;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static it.univr.track.TestUtils.effettuaLoginAdmin;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = SmartTrackApplication.class)
public abstract class BaseTest {

    protected WebDriver driver;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected DeviceRepository deviceRepository;

    @BeforeEach
    @Sql(scripts = {"../../../../resources/script/adminSIT.sql"})
    public void setUp() {

        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        driver = new FirefoxDriver(options);

        effettuaLoginAdmin(driver);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}