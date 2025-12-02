
package Base;


//import AutomationFramework.DriverManager.DriverManager;
import AutomationFramework.Utilities.ConfigUtils;
import AutomationFramework.Utilities.LogUtils;
import AutomationFramework.drivers.GUIDriver;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class BaseTest {
    protected ThreadLocal<GUIDriver> guiDriver = new ThreadLocal<>();
    protected ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    protected String baseUrl;

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        baseUrl = ConfigUtils.getConfigValue("baseUrlWeb");
        LogUtils.info("Base URL loaded: " + baseUrl);
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        GUIDriver gd = new GUIDriver();
        guiDriver.set(gd);
        driver.set(gd.get());

        driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.get().get(baseUrl);
        LogUtils.info("Navigated to base URL: " + baseUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (guiDriver.get() != null) {
//            guiDriver.get().quit();
//            guiDriver.remove();
        }
        driver.remove();
    }

    public WebDriver getDriver() {
        return driver.get();
    }



}
