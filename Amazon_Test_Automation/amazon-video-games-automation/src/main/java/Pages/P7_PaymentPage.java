package Pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class P7_PaymentPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // عدّل الـ locator ده حسب النص اللي ظاهر عندك في صفحة الدفع
    private final By paymentHeader = By.xpath("//h1[contains(.,'Select a payment method')]");

    public P7_PaymentPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Step("Verify user is on payment page")
    public void verifyOnPaymentPage() {
        wait.until(visibilityOfElementLocated(paymentHeader));
        System.out.println("User is on Payment Page.");
    }

    // هنا بعدين تقدر تزود:
    // - اختيار كارت
    // - اختيار طريقة الدفع
    // - Confirm etc.
}
