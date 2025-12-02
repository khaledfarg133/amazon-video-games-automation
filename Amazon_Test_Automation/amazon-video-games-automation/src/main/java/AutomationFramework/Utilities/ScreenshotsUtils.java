package AutomationFramework.Utilities;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import static AutomationFramework.Utilities.Waits.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScreenshotsUtils
{
    private static Path path ;
    private static String SCREENSHOTS_PATH = "test-outputs/screenshots";

    public Path getPath()
    {
        return path;
    }

    public void setPath(Path path)
    {
        this.path = path;
    }
    public static String getScreenshotsPath()
    {
        return SCREENSHOTS_PATH;
    }
    public static void setScreenshotsPath(String screenshotsPath)
    {
        SCREENSHOTS_PATH = screenshotsPath;
    }
    //create method to Take screenshot
    public static File captureScreenshot(WebDriver driver, String screenshotName) {
         path = Paths.get(SCREENSHOTS_PATH,screenshotName+"_"+ Utility.getTimestamp()+".png");
        try {
            Files.createDirectories(path.getParent());
            File screenshotSrc= ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File screenshotFile = new File(path.toString());
            FileUtils.copyFile(screenshotSrc,screenshotFile);
            return screenshotFile;
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage());
            return null;
        }
    }
    //create method to Take screenshot for element
    public static File captureScreenshotForElement(WebDriver driver, String screenshotName,By locator) {
        path = Paths.get(SCREENSHOTS_PATH,screenshotName+"_"+ Utility.getTimestamp()+".png");
        try {
            Files.createDirectories(path.getParent());
            File screenshotSrc= ((TakesScreenshot) Utility.findWebElement(driver,locator)).getScreenshotAs(OutputType.FILE);
            File screenshotFile = new File(path.toString());
            FileUtils.copyFile(screenshotSrc,screenshotFile);
            return screenshotFile;
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage());
            return null;
        }
    }
    //create method to Take screenshot with highlighting element
    public static File takeScreenshotWithHighlighting(WebDriver driver, String screenshotName, By locator) {
        path = Paths.get(SCREENSHOTS_PATH,screenshotName+"_"+ Utility.getTimestamp()+".png");
         try {
            highLightElement(driver,locator);
            Files.createDirectories(path.getParent());
             File screenshotSrc= ((TakesScreenshot) Utility.findWebElement(driver,locator)).getScreenshotAs(OutputType.FILE);
             File screenshotFile = new File(path.toString());
             FileUtils.copyFile(screenshotSrc,screenshotFile);
            return screenshotFile;
        }
        catch (Exception e) {
            LogUtils.error(e.getMessage());
            return null;
        }
    }
    public static void highLightElement(WebDriver driver,By by) {
        // draw a border around the found element
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'", driver, by);
            LogUtils.info("Highlight on element " + by);
        }
    }
}