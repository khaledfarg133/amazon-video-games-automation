package AutomationFramework.validations;

import AutomationFramework.Utilities.AllureUtils;
import AutomationFramework.Utilities.ElementActions;
import AutomationFramework.Utilities.LogUtils;
import AutomationFramework.Utilities.Waits;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.Objects;

public abstract class BaseAssertions {
    protected ElementActions elementActions;
    protected Waits wait;
    protected WebDriver driver;

    public BaseAssertions(ElementActions elementActions, Waits wait, WebDriver driver) {
        this.elementActions = elementActions;
        this.wait = wait;
        this.driver = driver;
    }

    protected abstract void assertTrue(boolean condition, String message);

    protected abstract void assertFalse(boolean condition, String message);

    protected abstract void assertEquals(String actual, String expected, String message);





    @Step("Verify condition is true")
    public void validateTrue(boolean condition) {
        assertTrue(condition, "Expected condition to be true, but it was false");
    }

    @Step("Verify condition is false")
    public void validateFalse(boolean condition) {
        assertFalse(condition, "Expected condition to be false, but it was true");
    }

    @Step("Verify text equality: actual = \"{actualText}\", expected = \"{expectedText}\"")
    public void validateEquals(String expectedText, String actualText) {
        assertEquals(actualText, expectedText, "Texts are not equal");
    }

    @Step("Verify text inequality: actual != expected")
    public void validateNotEquals(String expectedText, String actualText) {
        assertFalse(actualText.equals(expectedText), "Texts should not be equal");
    }



    @Step("Verify page title is: {pageTitle}")
    public void validatePageTitle(String pageTitle) {
        boolean matched = wait.waitForPageTitle(pageTitle);
        assertTrue(matched, "Page title does not match: " + pageTitle);
    }

    @Step("Verify page contains text: {text}")
    public void validatePageContainsText(String text) {
        try {
            LogUtils.info("Verifying page contains text: " + text);
            boolean contains = Objects.requireNonNull(driver.getPageSource()).contains(text);
            assertTrue(contains, "Text not found in page source");
        } catch (Exception e) {
            LogUtils.error("Failed to verify text in page", e.getMessage());
            throw e;
        }
    }

    @Step("Verify page URL is: {expectedURL}")
    public void validatePageURL(String expectedURL) {
        boolean matched = wait.waitForPageURL(expectedURL);
        assertTrue(matched, "Page URL does not match: " + expectedURL);
    }

    @Step("Verify alert is present")
    public void validateAlertPresent() {
        boolean alertPresent = wait.waitForAlertPresent();
        assertTrue(alertPresent, "Expected alert to be present, but it wasn't");
    }
}
