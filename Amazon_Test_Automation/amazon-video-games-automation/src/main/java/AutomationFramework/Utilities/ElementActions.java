package AutomationFramework.Utilities;

import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.support.ui.Select;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

public class ElementActions {

    public static void selectFromDropDown(WebDriver driver, By locator, String option) {
        new Select(driver.findElement(locator)).selectByVisibleText(option);
    }

    public static void scrollToPosition(WebDriver driver, int x, int y) {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(" + x + ", " + y + ");");
    }

    public static void uploadFileWithLocalForm(WebDriver driver, By uploadButton, String filePath) {
        driver.findElement(uploadButton).click();
        try {
            Robot robot = new Robot();
            StringSelection str = new StringSelection(filePath);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(str, null);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_V);
            robot.delay(2000);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void click(WebDriver driver, By locator) {
        driver.findElement(locator).click();
    }

    public static void uploadFile(WebDriver driver, By locator, String filePath) {
        String fileAbs = System.getProperty("user.dir") + File.separator + filePath;
        driver.findElement(locator).sendKeys(fileAbs);
    }

    public static void type(WebDriver driver, By locator, String data) {
        driver.findElement(locator).sendKeys(data);
    }

    public static String getText(WebDriver driver, By locator) {
        return driver.findElement(locator).getText();
    }

    public static WebElement findElement(WebDriver driver, By locator) {
        return driver.findElement(locator);
    }

    public static List<WebElement> findElements(WebDriver driver, By locator) {
        return driver.findElements(locator);
    }

    public static JavascriptExecutor getJsExecutor(WebDriver driver) {
        return (JavascriptExecutor) driver;
    }

    public static void hoverOnElement(WebDriver driver, By locator) {
        Actions action = new Actions(driver);
        action.moveToElement(driver.findElement(locator)).perform();
    }

    public static void scrollToElementAtBottom(WebDriver driver, By locator) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", driver.findElement(locator));
    }

    public static WebElement highlightElement(WebDriver driver, By locator) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'", driver.findElement(locator));
        return driver.findElement(locator);
    }

    public static void dragAndDrop(WebDriver driver, By fromLocator, By toLocator) {
        Actions action = new Actions(driver);
        WebElement from = driver.findElement(fromLocator);
        WebElement to = driver.findElement(toLocator);
        action.dragAndDrop(from, to).perform();
    }

    public static WebElement getSelectedOptionFromDropDown(WebDriver driver, By locator) {
        return new Select(driver.findElement(locator)).getFirstSelectedOption();
    }

    public static void scrollToElementAtTop(WebDriver driver, By locator) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(locator));
    }

    public static void scrollToElement(WebDriver driver, By locator) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", driver.findElement(locator));
    }

    public static String printPage(WebDriver driver, int endPage) {
        PrintOptions printOptions = new PrintOptions();
        printOptions.setPageRanges("1-" + endPage);
        Pdf pdf = ((PrintsPage) driver).print(printOptions);
        return pdf.getContent();
    }
}
