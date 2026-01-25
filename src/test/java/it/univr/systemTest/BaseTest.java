package it.univr.systemTest;

import io.github.bonigarcia.wdm.WebDriverManager;
import it.univr.track.SmartTrackApplication;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static it.univr.track.TestUtils.effettuaLoginAdmin;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import com.gargoylesoftware.htmlunit.WebClient;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = SmartTrackApplication.class)
public abstract class BaseTest {

    protected WebDriver driver;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected DeviceRepository deviceRepository;

    @BeforeEach
    @Sql(scripts = { "../../../../resources/script/adminSIT.sql" })
    public void setUp() {

        driver = new HtmlUnitDriver(true) {
            @Override
            protected WebClient modifyWebClient(WebClient client) {
                WebClient webClient = super.modifyWebClient(client);
                webClient.getOptions().setThrowExceptionOnScriptError(false);
                webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
                webClient.getOptions().setCssEnabled(false);
                return webClient;
            }
        };
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

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