package it.univr.pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class DeviceUsersPageObject extends PageObject {

    public DeviceUsersPageObject(WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "unassigned-pool")
    private WebElement unassignedPool;

    public void assignDeviceToUser(String deviceName, String username) {
        // Find device chip by text (name)
        // Check unassigned pool
        WebElement deviceChip = unassignedPool.findElement(By.xpath(
                ".//div[contains(@class,'device-chip')][descendant::div[contains(text(),'" + deviceName + "')]]"));

        // Find user target zone
        WebElement userZone = driver.findElement(By.id("user-" + username));

        // Perform Drag and Drop
        Actions builder = new Actions(driver);
        // Sometimes clickAndHold + moveTo + release works better for SortableJS
        builder.clickAndHold(deviceChip)
                .moveToElement(userZone)
                .moveByOffset(10, 10) // small nudge
                .release()
                .build()
                .perform();

        // Brief pause for animation/AJAX
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    public boolean isDeviceAssignedToUser(String deviceName, String username) {
        try {
            WebElement userZone = driver.findElement(By.id("user-" + username));
            List<WebElement> devices = userZone.findElements(By.className("device-chip"));
            for (WebElement dev : devices) {
                if (dev.getText().contains(deviceName))
                    return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
