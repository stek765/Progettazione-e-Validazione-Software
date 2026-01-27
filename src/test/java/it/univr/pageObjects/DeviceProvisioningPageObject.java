package it.univr.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DeviceProvisioningPageObject extends PageObject {

    @FindBy(id = "provisionSwitch")
    private WebElement provisionSwitch;

    @FindBy(className = "slider")
    private WebElement slider;

    @FindBy(id = "macOutput")
    private WebElement macOutputContainer;

    @FindBy(id = "macValue")
    private WebElement macValue;

    @FindBy(id = "keyOutput")
    private WebElement keyOutputContainer;

    @FindBy(id = "privateKeyValue")
    private WebElement privateKeyValue;

    @FindBy(id = "provisionGroup")
    private WebElement provisionGroup;

    public DeviceProvisioningPageObject(WebDriver driver) {
        super(driver);
    }

    public void toggleProvisioning() {
        // The input 'provisionSwitch' is hidden (opacity: 0), so we can't wait for it
        // to be clickable.
        // We wait for the visible slider instead.
        wait.until(ExpectedConditions.visibilityOf(slider));

        // We check the state using the hidden input (it's in the DOM)
        // If device is not already provisioned (checkbox not selected), we click.
        if (!provisionSwitch.isSelected()) {
            slider.click();
        }
    }

    public boolean isProvisioningDataVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOf(macOutputContainer));
            wait.until(ExpectedConditions.visibilityOf(keyOutputContainer));

            return macValue.isDisplayed() && !macValue.getText().trim().isEmpty() &&
                    privateKeyValue.isDisplayed() && !privateKeyValue.getText().trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public String getMacAddress() {
        return macValue.getText();
    }

    public String getPrivateKey() {
        return privateKeyValue.getText();
    }

    public boolean isProvisioningSectionPresent() {
        try {
            // Using By.id directly to avoid proxy wait issues if element is missing
            return !driver.findElements(org.openqa.selenium.By.id("provisionGroup")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
