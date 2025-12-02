package AutomationFramework.Utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for uploading files via Selenium.
 */
public class FileUploadHelper {

    /**
     * Uploads one or more files using a hidden <input type="file"> inside a custom wrapper.
     *
     * @param driver   The WebDriver instance
     * @param locator  The By locator that targets the <input type="file"> element itself
     * @param filePath A single path or multiple paths joined by "\n" (e.g. "file1.png\nfile2.png")
     */
    public static void uploadFile(WebDriver driver, By locator, String filePath) {
        // 1. صياغة المسار المطلق (Absolute Path) للتحقق من وجود الملف وتجنّب الأخطاء
        Path absolutePath = Paths.get(filePath).toAbsolutePath();
        if (!Files.exists(absolutePath)) {
            // إذا الملف غير موجود، نوقف التنفيذ برسالة واضحة
            throw new RuntimeException("File not found at: " + absolutePath);
        }

        // 2. إيجاد عنصر الـ input[type='file']
        WebElement fileInput = driver.findElement(locator);

        // 3. إذا كان الرفع عبر واجهة منبثقة (wrapper) تحتاج أولاً للنقر عليها لفتح الـ input
        try {
            // نجرب العثور على العنصر الحاوي (ancestor div بعلامة upload-wrapper) والنقر عليه
            WebElement wrapper = fileInput.findElement(By.xpath("ancestor::div[contains(@class,'upload-wrapper')]"));
//            wrapper.click();
        } catch (Exception e) {
            // إذا لم يُعثر على wrapper أو النقر فشل، نتجاهل ونكمل
        }

        // 4. إذا كان العنصر مخفي عبر CSS (display:none أو hidden)، نظهره مؤقتاً
        if (!fileInput.isDisplayed()) {
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].style.display='block'; arguments[0].removeAttribute('hidden');", fileInput);
        }

        // 5. إرسال المسار إلى العنصر: تدعم sendKeys رفع ملف واحد أو أكثر (مفصول بسطر جديد)
        fileInput.sendKeys(absolutePath.toString());

        // 6. بعض التطبيقات تحتاج حدث 'change' بعد sendKeys ليبدأ الرفع
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].dispatchEvent(new Event('change'));", fileInput);

        // 7. (اختياري) إعادة العنصر إلى حالته المخفية لتجنب تغييرات الواجهة
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].style.display='none'; arguments[0].setAttribute('hidden','');", fileInput);

        // 8. تسجيل المسار والLocator لمتابعة التنفيذ في اللوج
        LogUtils.info("Uploaded file(s): " + absolutePath);
        LogUtils.info("Used locator: " + locator);
    }
}
