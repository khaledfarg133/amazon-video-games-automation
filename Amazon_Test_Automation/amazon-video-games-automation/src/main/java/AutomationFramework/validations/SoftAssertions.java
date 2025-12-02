package AutomationFramework.validations;

import AutomationFramework.Utilities.ElementActions;
import AutomationFramework.Utilities.LogUtils;
import AutomationFramework.Utilities.Waits;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

public class SoftAssertions extends BaseAssertions {

    private static boolean used = false; // Flag to track usage
    private static SoftAssert softAssert = new SoftAssert();

    public SoftAssertions(ElementActions elementActions, Waits wait, WebDriver driver) {
        super(elementActions, wait, driver);
    }

    /**
     * Call this after all soft assertions to validate them.
     */
    public static void assertIfUsed() {
        if (used) {
            assertAll();
        }
    }

    @Step("Assert all collected soft assertions")
    private static void assertAll() {
        try {
            softAssert.assertAll();
            LogUtils.info("All soft assertions passed successfully.");
        } catch (AssertionError e) {
            LogUtils.error("Soft assertions failed: " + e.getMessage());
            throw e;
        } finally {
            resetSoftAssert();
        }
    }

    private static void resetSoftAssert() {
        softAssert = new SoftAssert();
        used = false;
    }

    @Override
    @Step("Soft assert true: {message}")
    protected void assertTrue(boolean condition, String message) {
        used = true;
        softAssert.assertTrue(condition, message);
        LogUtils.info("Soft assertTrue executed: " + message);
    }

    @Override
    @Step("Soft assert false: {message}")
    protected void assertFalse(boolean condition, String message) {
        used = true;
        softAssert.assertFalse(condition, message);
        LogUtils.info("Soft assertFalse executed: " + message);
    }

    @Override
    @Step("Soft assert equals: expected = {expected}, actual = {actual}, message = {message}")
    protected void assertEquals(String actual, String expected, String message) {
        used = true;
        softAssert.assertEquals(actual, expected, message);
        LogUtils.info("Soft assertEquals executed: " + message);
    }
}
