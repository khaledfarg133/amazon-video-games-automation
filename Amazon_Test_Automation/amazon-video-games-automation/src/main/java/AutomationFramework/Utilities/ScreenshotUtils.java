package AutomationFramework.Utilities;

import com.assertthat.selenium_shutterbug.core.Capture;
import com.assertthat.selenium_shutterbug.core.Shutterbug;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static AutomationFramework.Utilities.AllureUtils.attachScreenshotToAllure;
import static AutomationFramework.Utilities.TimeUtils.getTimestamp;

public final class ScreenshotUtils {

    public static final String SCREENSHOTS_PATH = "test-outputs/screenshots/";

    private ScreenshotUtils() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Highlight an element with red border.
     */
    public static void highLightElement(WebDriver driver, By by) {
        try {
            WebElement element = driver.findElement(by);
            if (driver instanceof JavascriptExecutor) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'", element);
                LogUtils.info("Highlighted element: " + by);
            }
        } catch (Exception e) {
            LogUtils.error("Failed to highlight element: " + by, e.getMessage());
        }
    }

    /**
     * Take a general screenshot of the current window.
     */
    public static void takeScreenshot(WebDriver driver, String screenshotName) {
        try {
            File screenshotSrc = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path path = Paths.get(SCREENSHOTS_PATH, screenshotName + "-" + getTimestamp() + ".png");
            FileUtils.copyFile(screenshotSrc, path.toFile());

            attachScreenshotToAllure(screenshotName, path.toString());
            LogUtils.info("Screenshot captured successfully.");
        } catch (Exception e) {
            LogUtils.error("Failed to capture screenshot", e.getMessage());
        }
    }

    /**
     * Take a screenshot with a highlighted element.
     */
    public static void takeHighlightedScreenshot(WebDriver driver, String screenshotName, By locator) {
        try {
            highLightElement(driver, locator);

            File screenshotSrc = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path path = Paths.get(SCREENSHOTS_PATH, screenshotName + "-" + getTimestamp() + ".png");
            FileUtils.copyFile(screenshotSrc, path.toFile());

            attachScreenshotToAllure(screenshotName, path.toString());
            LogUtils.info("Highlighted screenshot captured successfully.");
        } catch (Exception e) {
            LogUtils.error("Failed to capture highlighted screenshot", e.getMessage());
        }
    }

    /**
     * Take a screenshot for a specific element only.
     */
    public static void takeElementScreenshot(WebDriver driver, By locator, String screenshotName) {
        try {
            WebElement element = driver.findElement(locator);
            File screenshotSrc = element.getScreenshotAs(OutputType.FILE);

            Path path = Paths.get(SCREENSHOTS_PATH, screenshotName + "-" + getTimestamp() + ".png");
            FileUtils.copyFile(screenshotSrc, path.toFile());

            attachScreenshotToAllure(screenshotName, path.toString());
            LogUtils.info("Element screenshot captured successfully.");
        } catch (Exception e) {
            LogUtils.error("Failed to capture element screenshot", e.getMessage());
        }
    }

    /**
     * Take a full-page screenshot using Shutterbug.
     */
    public static void takeFullScreenshot(WebDriver driver) {
        try {
            Shutterbug.shootPage(driver, Capture.FULL_SCROLL).save(SCREENSHOTS_PATH);
            LogUtils.info("Full page screenshot captured successfully.");
        } catch (Exception e) {
            LogUtils.error("Failed to capture full page screenshot", e.getMessage());
        }
    }

    /**
     * Take a full screenshot and highlight an element.
     */
    public static void takeFullScreenshotWithHighlighting(WebDriver driver, String screenshotName, By locator) {
        try {
            highLightElement(driver, locator);

            File screenshotSrc = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path path = Paths.get(SCREENSHOTS_PATH, screenshotName + "-" + getTimestamp() + ".png");
            FileUtils.copyFile(screenshotSrc, path.toFile());

            attachScreenshotToAllure(screenshotName, path.toString());
            LogUtils.info("Full screenshot with highlighted element captured successfully.");
        } catch (Exception e) {
            LogUtils.error("Failed to capture full highlighted screenshot", e.getMessage());
        }
    }
}
