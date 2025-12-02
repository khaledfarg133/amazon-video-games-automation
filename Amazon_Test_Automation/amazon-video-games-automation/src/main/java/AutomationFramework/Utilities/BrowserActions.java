package AutomationFramework.Utilities;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import java.util.Set;

public class BrowserActions {

    /**
     * Maximize window
     */
    public static void maximizeWindow(WebDriver driver) {
        driver.manage().window().maximize();
    }

    /**
     * Minimize window
     */
    public static void minimizeWindow(WebDriver driver) {
        driver.manage().window().minimize();
    }

    /**
     * Zoom out the web page
     *
     * @param driver     the WebDriver instance
     * @param zoomFactor the zoom factor e.g. 60 (sent without %)
     */
    public static void zoomOut(WebDriver driver, int zoomFactor) {
        ((JavascriptExecutor) driver).executeScript("document.body.style.zoom = '" + zoomFactor + "%'");
    }

    public static Set<Cookie> getAllCookies(WebDriver driver) {
        return driver.manage().getCookies();
    }

    public static void restoreSession(WebDriver driver, Set<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            driver.manage().addCookie(cookie);
        }
    }

    /**
     * Get current web page's title
     *
     * @return the current title as String
     */
    public static String getPageTitle(WebDriver driver) {
        String title = driver.getTitle();
        LogUtils.info("Get Page Title: " + title);
        return title;
    }

    /**
     * Get current URL from current driver
     *
     * @return the current URL as String
     */
    public static String getCurrentUrl(WebDriver driver) {
        String currentUrl = driver.getCurrentUrl();
        LogUtils.info("Get Current URL: " + currentUrl);
        return currentUrl;
    }

    /**
     * Open new Tab
     */
    public static void openNewTab(WebDriver driver) {
        driver.switchTo().newWindow(WindowType.TAB);
        LogUtils.info("Open new Tab");
    }

    /**
     * Open new Window
     */
    public static void openNewWindow(WebDriver driver) {
        driver.switchTo().newWindow(WindowType.WINDOW);
        LogUtils.info("Open new Window");
    }

    /**
     * Navigate to the specified URL.
     *
     * @param driver the WebDriver instance
     * @param URL    the specified URL
     */
    public static void navigate(WebDriver driver, String URL) {
        driver.get(URL);
        LogUtils.info("Open website with URL: " + URL);
    }

    /**
     * Reload the current web page.
     */
    public static void reloadPage(WebDriver driver) {
        driver.navigate().refresh();
        LogUtils.info("Reloaded page " + driver.getCurrentUrl());
    }

    /**
     * Close current Window
     */
    public static void closeCurrentWindow(WebDriver driver) {
        LogUtils.info("Close current Window." + driver.getCurrentUrl());
        driver.close();
        LogUtils.info("Close current Window");
    }
}
