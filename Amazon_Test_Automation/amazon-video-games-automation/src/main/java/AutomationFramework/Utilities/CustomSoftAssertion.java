package AutomationFramework.Utilities;

import io.qameta.allure.Step;
import org.testng.asserts.SoftAssert;

/**
 * Utility class for performing soft assertions using TestNG.
 * Soft assertions allow multiple assertions to be executed before test failure.
 */
public class CustomSoftAssertion extends SoftAssert {

    // Single instance of CustomSoftAssertion
    public static CustomSoftAssertion softAssertion = new CustomSoftAssertion();

    /**
     * Executes all collected soft assertions.
     * If there are assertion failures, they will be reported.
     */
    @Step("Custom Soft Assertion")
    public static void customAssertALL() {
        try {
            softAssertion.assertAll("Custom Soft Assertion");
        } catch (Exception e) {
            System.out.println("Custom Soft Assertion Failed");
        }
    }
}