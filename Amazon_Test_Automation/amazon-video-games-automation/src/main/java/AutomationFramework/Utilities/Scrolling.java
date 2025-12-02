package AutomationFramework.Utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;


    public final class Scrolling {

        //  جعل الكلاس نهائي لمنع التوريث (Best Practice)
        private Scrolling() {
            throw new UnsupportedOperationException("Utility class - cannot be instantiated");
        }

        //  التمرير إلى عنصر محدد
        public static void scrollToElement(WebDriver driver, By locator) {
            WebElement element = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        }

        //  التمرير السلس إلى عنصر معين
        public static void smoothScrollToElement(WebDriver driver, By locator) {
            WebElement element = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({ behavior: 'smooth', block: 'center' });",
                    element
            );
        }

        //  التمرير إلى عنصر معين مع التأكد من وجوده قبل ذلك
        public static void scrollToElementWithWait(WebDriver driver, By locator) {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            scrollToElement(driver, locator);
        }

        //  التمرير إلى أسفل الصفحة
        public static void scrollToBottom(WebDriver driver) {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        }

        //  التمرير إلى أعلى الصفحة
        public static void scrollToTop(WebDriver driver) {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        }

        //  التمرير بمقدار معين من البكسل (إيجابي للأسفل، سلبي للأعلى)
        public static void scrollByPixels(WebDriver driver, int pixels) {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0," + pixels + ");");
        }
    }



