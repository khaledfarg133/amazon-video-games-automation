package AutomationFramework.drivers;

import AutomationFramework.Utilities.ConfigUtils;
import AutomationFramework.Utilities.LogUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.testng.Assert.fail;

public class GUIDriver {

    private final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private final WebDriver driver;

    public GUIDriver() {
        String browserName = ConfigUtils.getConfigValue("browserType");
        this.driver = getDriver(browserName).startDriver();
        setDriver(driver);
    }

    public WebDriver get() {
        if (driverThreadLocal.get() == null) {
            LogUtils.error("Driver is null");
            fail("Driver is null");
            return null;
        }
        return driverThreadLocal.get();
    }

    public void quit() {
        if (get() != null) {
            get().quit();
        }
    }

    private void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    private AbstractDriver getDriver(String browserName) {
        return switch (browserName.toLowerCase()) {
            case "chrome" -> new ChromeFactory();
            case "firefox" -> new FirefoxFactory();
            case "edge" -> new EdgeFactory();
            default -> throw new IllegalArgumentException("Invalid browser name: " + browserName);
        };
    }

    public static GUIDriver extractDriver(ITestResult result) {
        Object testInstance = result.getInstance();
        Class<?> clazz = testInstance.getClass();

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object value = Modifier.isStatic(field.getModifiers()) ? field.get(null) : field.get(testInstance);

                    if (value instanceof ThreadLocal<?> threadLocal) {
                        Object driverObj = threadLocal.get();
                        if (driverObj instanceof GUIDriver driver) return driver;
                    }

                    if (value instanceof GUIDriver driver) {
                        return driver;
                    }

                } catch (IllegalAccessException e) {
                    LogUtils.error("Unable to access field '", field.getName(), "'", e.getMessage());
                }
            }

            clazz = clazz.getSuperclass();
        }

        LogUtils.warn("GUIDriver instance not found in test class: ", testInstance.getClass().getSimpleName());
        return null;
    }
}
