package AutomationFramework.validations;

import AutomationFramework.Utilities.ElementActions;
import AutomationFramework.Utilities.LogUtils;
import AutomationFramework.Utilities.Waits;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public final class Assertions extends BaseAssertions {

    public Assertions(ElementActions elementActions, Waits wait, WebDriver driver) {
        super(elementActions, wait, driver);
    }

    @Override
    protected void assertTrue(boolean condition, String message) {
        try {
            Assert.assertTrue(condition, message);
            LogUtils.info("Assert True passed: " + message);
        } catch (AssertionError e) {
            LogUtils.error("Assert True failed: " + message, e.getMessage());
            throw e;
        }
    }

    @Override
    protected void assertFalse(boolean condition, String message) {
        try {
            Assert.assertFalse(condition, message);
            LogUtils.info("Assert False passed: " + message);
        } catch (AssertionError e) {
            LogUtils.error("Assert False failed: " + message, e.getMessage());
            throw e;
        }
    }

    @Override
    protected void assertEquals(String actual, String expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
            LogUtils.info("Assert Equals passed: " + message + " | Expected: " + expected + ", Actual: " + actual);
        } catch (AssertionError e) {
            LogUtils.error("Assert Equals failed: " + message + " | Expected: " + expected + ", Actual: " + actual, e.getMessage());
            throw e;
        }
    }
}
