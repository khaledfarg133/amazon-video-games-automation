package AutomationFramework.Utilities;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import static AutomationFramework.Utilities.Waits.*;


//import static AutomationFramework.DriverManager.DriverManager.getDriver;
import static AutomationFramework.Utilities.DataUtils.getConfigValue;
import static AutomationFramework.Utilities.Waits.*;
import static java.sql.DriverManager.getDriver;


public class Utility {


    //TODO: Clicking on element after checking clickability
    //click

    @Step("Clicking on the element: {locator}")
    public static void clickElement(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

        // Scroll to element
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);

        try {
            element.click();
            LogUtils.info("Clicked on the element normally: " + locator.toString());
        } catch (ElementClickInterceptedException e) {
            LogUtils.warn("Normal click failed due to interception, using JavaScript click for: " + locator.toString());
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }
//    @Step("Clicking on the element: {locator}") // 1 usage
//   public static void clickElement(WebDriver driver, By locator) {
//        //code
//        Waits.explicitlyWaitForVisibility(driver, locator);
//        Waits.explicitlyWaitForClickability(driver, locator);
//        Scrolling.scrollToElement(driver, locator);
//        findElement(driver, locator).click();
//        LogUtils.info("Clicked on the element: ", locator.toString());
//    }

//    public static void clicking(WebDriver driver, By locator) {
//        explicitlyWaitForClickability(driver, locator);
//        findWebElement(driver, locator).click();
//    }

    //TODO: Send data to element after checking visibility
    //send keys
    @Step("Sending data: {data} to the element: {locator}") // 2 usages
    public static void sendData(WebDriver driver, By locator, String data) {
        //code
        Waits.explicitlyWaitForClickability(driver, locator);
        Waits.explicitlyWaitForVisibility(driver, locator);
        Scrolling.scrollToElement(driver, locator);
        findElement(driver, locator).sendKeys(data);
        LogUtils.info("Data entered: ", data, " in the field: ", locator.toString());
    }
//    public static void sendData(WebDriver driver, By locator, String data) {
//        explicitlyWaitForVisibility(driver, locator);
//        findWebElement(driver, locator).sendKeys(data);
//    }

    //TODO: get text from element after checking visibility
    @Step("Getting text from the element: {locator}") // 1 usage
    public static String getText(WebDriver driver, By locator) {
        Waits.explicitlyWaitForVisibility(driver, locator);
        Scrolling.scrollToElement(driver, locator);
        LogUtils.info("Getting text from the element: ", locator.toString(), " Text: ", findElement(driver, locator).getText());
        return findElement(driver, locator).getText();
    }

//    public static String getText(WebDriver driver, By locator) {
//        new WebDriverWait(driver, Duration.ofSeconds(5))
//                .until(ExpectedConditions.visibilityOfElementLocated(locator));
//        return findWebElement(driver, locator).getText();
//
//    }

    //find element
    public static WebElement findElement(WebDriver driver, By locator) {
        LogUtils.info("Finding element: ", locator.toString());
        return driver.findElement(locator);
    }


    public static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd_h-m-ssa").format(new Date());
    }

    public static String getSimpleTimestamp() {
        return new SimpleDateFormat("h-m-ssa").format(new Date());
    }


    //TODO:  Scroll to specific element
    public static void scrollToElement(WebDriver driver, By locator) {
        ((JavascriptExecutor) (driver)).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", findWebElement(driver, locator));
    }

    public static void scrollToElementAtTop(WebDriver driver, By locator) {
        ((JavascriptExecutor) (driver)).executeScript("arguments[0].scrollIntoView(true);", findWebElement(driver, locator));
    }

    public static void scrollToElementAtBottom(WebDriver driver, By locator) {
        ((JavascriptExecutor) (driver)).executeScript("arguments[0].scrollIntoView(false);", findWebElement(driver, locator));
    }

    //TODO:  Scroll to specific position
    public static void scrollToPosition(WebDriver driver, int x, int y) {
        ((JavascriptExecutor) (driver)).executeScript("window.scrollTo(" + x + ", " + y + ");");
    }

    // Method to zoom out using JavaScript
    public static void zoomOut(WebDriver driver, int zoomFactor) {
        ((JavascriptExecutor) driver).executeScript("document.body.style.zoom = '" + zoomFactor + "%'");
    }



    //Convert Locator to Web Element
    public static WebElement findWebElement(WebDriver driver, By locator) {
        return driver.findElement(locator);
    }

    /**
     * Find multiple elements with the locator By object
     *
     * @param by     is an element of type By
     * @param driver is an element of Web driver
     * @return Returns a List of WebElement objects
     */
    public static List<WebElement> findWebElements(WebDriver driver, By by) {
        return driver.findElements(by);
    }

    //TODO: Function for getting selected option from drop down
    public static WebElement getSelectedOptionFromDropDown(WebDriver driver, By locator) {
        return new Select(Utility.findWebElement(driver, locator)).getFirstSelectedOption();
    }

    //TODO: Function for selecting from drop down
    public static void selectFromDropDown(WebDriver driver, By locator, String option) {
        new Select(Utility.findWebElement(driver, locator)).selectByVisibleText(option);
    }

    /**
     * اختيار عنصر عشوائي من قائمة منسدلة عادية (`<select>`)
     *
     * @param driver  كائن WebDriver للتحكم في المتصفح
     * @param locator محدد (Locator) للعنصر `<select>`
     */
    //  اختيار عنصر عشوائي من قائمة `Single-select`
    // الاختيارات RadioButton  داخل ال ddl
    public static void selectRandomOptionFromDropDown(WebDriver driver, By locator) {
        // العثور على القائمة المنسدلة باستخدام Selenium
        Select dropdown = new Select(driver.findElement(locator));

        // الحصول على جميع الخيارات المتاحة داخل القائمة
        List<WebElement> options = dropdown.getOptions();

        // التأكد من أن هناك خيارات للاختيار منها
        if (options.size() > 1) {
            // إنشاء رقم عشوائي ضمن عدد الخيارات المتاحة
            int randomIndex = generateRandomNumber(options.size());

            // اختيار العنصر العشوائي بناءً على الفهرس العشوائي
            dropdown.selectByIndex(randomIndex);

            // طباعة العنصر المحدد لاختبار صحة التنفيذ
            System.out.println("تم اختيار العنصر: " + options.get(randomIndex).getText());
        } else {
            System.out.println("القائمة تحتوي على خيار واحد فقط أو فارغة!");
        }
    }


    public static void SearchAndSelect_From_DDl(WebDriver driver, By clickSelector, By inputSelector, By confirmSelector, String text) {
        // الضغط على أول عنصر و هو ال ddl
        clickElement(driver,clickSelector);
        // إدخال البيانات في العنصر الثاني الخاص بالبحث
        sendData(driver,inputSelector,text);
        // الضغط على العنصر الثالث (مثلاً اختيار أو تأكيد)
        clickElement(driver,confirmSelector);
    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////*/



    /**
     * checkDropdownSelectedText(WebDriver driver, By optionsLocator, String selectedItemText)
     * هى دالة تستخدم فى اختيار عنصر محدد من ال ddl
     * تأخذ ال driver + ال selector  الخاص بال ddl + نص اسم العنصر المراد اختيارة من ال ddl
     * */
    public static String checkDropdownSelectedText(WebDriver driver, By optionsLocator, String selectedItemText) {
        // انتظار ظهور العناصر في القائمة المنسدلة
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(optionsLocator));
//        options.forEach(option -> System.out.print(option.getText().trim() + " | "));


        if (!options.isEmpty()) {
            // البحث عن العنصر الذي يحتوي على النص المحدد
            for (WebElement option : options) {
                String optionText = option.getText().trim();
                if (optionText.equals(selectedItemText)) {
                    // إذا كان النص مطابقًا، النقر على العنصر
                    option.click();
                    System.out.println("تم اختيار العنصر: " + optionText);
                    return optionText; // إرجاع النص المحدد
                }
            }
            // في حالة عدم العثور على العنصر
            System.out.println("العنصر " + selectedItemText + " غير موجود في القائمة!");
            return "Unknown"; // قيمة افتراضية إذا لم يتم العثور على العنصر
        } else {
            System.out.println("القائمة فارغة أو لم يتم تحميل الخيارات!");
            return "Unknown"; // قيمة افتراضية في حالة عدم توفر الخيارات
        }
    }

    public static String checkDropdownSelectedText(WebDriver driver, By optionsLocator) {
        // انتظار ظهور العناصر في القائمة المنسدلة
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(optionsLocator));

        if (!options.isEmpty()) {
            // اختيار عنصر عشوائي
            int randomIndex = new Random().nextInt(options.size());
            WebElement selectedOption = wait.until(ExpectedConditions.elementToBeClickable(options.get(randomIndex)));

            // التأكد من أن العنصر مرئي وقابل للنقر
            String selectedText = selectedOption.getText().trim();
            selectedOption.click(); // الضغط على العنصر

            // طباعة العنصر الذي تم تحديده
            System.out.println("تم اختيار العنصر: " + selectedText);

            // إرجاع النص المحدد فقط
            return selectedText;
        } else {
            System.out.println("القائمة فارغة أو لم يتم تحميل الخيارات!");
            return "Unknown"; // قيمة افتراضية في حالة عدم توفر الخيارات
        }
    }

    public static void selectRandomOptionFromCustomDropDown(WebDriver driver, By optionsLocator) {
        // انتظار ظهور القائمة المنسدلة
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(optionsLocator));

        // التأكد من أن هناك خيارات متاحة
        if (!options.isEmpty()) {
            // اختيار عنصر عشوائي
            int randomIndex = new Random().nextInt(options.size());
            WebElement selectedOption = options.get(randomIndex);

            // الضغط على العنصر المحدد
            selectedOption.click();

            // طباعة العنصر الذي تم تحديده
            System.out.println("تم اختيار العنصر: " + selectedOption.getText());
        } else {
            System.out.println("القائمة فارغة أو لم يتم تحميل الخيارات!");
        }
    }
    public static void selectRandomOptionFromCustomDropDown(WebDriver driver, By dropdownButton, By optionsLocator) {
        // النقر على زر القائمة المنسدلة لعرض الخيارات
        WebElement dropdown = driver.findElement(dropdownButton);
        dropdown.click();

        // الانتظار حتى تظهر العناصر
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(optionsLocator));

        // التأكد من أن هناك خيارات للاختيار منها
        if (!options.isEmpty()) {
            // إنشاء رقم عشوائي ضمن عدد الخيارات المتاحة
            int randomIndex = new Random().nextInt(options.size());

            // اختيار العنصر العشوائي والنقر عليه
            WebElement selectedOption = options.get(randomIndex);
            selectedOption.click();

            // طباعة العنصر المحدد لاختبار صحة التنفيذ
            System.out.println("تم اختيار العنصر: " + selectedOption.getText());
        } else {
            System.out.println("القائمة فارغة أو لم يتم تحميل الخيارات!");
        }
    }
    public static void selectRandomOptionFromDropDownWithNoData(WebDriver driver, By dropdownButton, By optionsLocator, By noDataLocator) {
        // النقر على زر القائمة المنسدلة لعرض الخيارات
        WebElement dropdown = driver.findElement(dropdownButton);
        dropdown.click();

        // الانتظار حتى ظهور القائمة
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // التحقق مما إذا كانت هناك رسالة "لا توجد إدارات"
        List<WebElement> noDataMessage = driver.findElements(noDataLocator);
        if (!noDataMessage.isEmpty()) {
            System.out.println("القائمة فارغة: " + noDataMessage.get(0).getText());
            return; // لا يوجد عناصر للاختيار منها
        }

        // العثور على جميع الخيارات المتاحة
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(optionsLocator));
        for (WebElement option : options) {
            System.out.println("🔹 " + option.getText()); // طباعة كل عنصر في القائمة
        }
        // التأكد من وجود خيارات للاختيار منها
        if (!options.isEmpty()) {
            int randomIndex = new Random().nextInt(options.size());
            WebElement selectedOption = options.get(randomIndex);
            selectedOption.click();
            System.out.println("تم اختيار العنصر: " + selectedOption.getText());
        } else {
            System.out.println("القائمة فارغة أو لم يتم تحميل الخيارات!");
        }
    }



    /*//////////////////////////////////////////////////////////////////////////////////////////////////*/
    public static void selectOptionFromDropDown(WebDriver driver, By dropdownButton, By optionLocator) {
        // النقر على زر القائمة المنسدلة لعرض الخيارات
        clickElement(driver,dropdownButton);

        clickElement(driver,optionLocator);



    }

    /*//////////////////////////////////////////////////////////////////////////////////////////////////*/

    /**
     * اختيار عنصر عشوائي من قائمة مخصصة (`multi-select`)
     *
     * @param driver          كائن WebDriver للتحكم في المتصفح
     * @param dropdownLocator محدد الزر الرئيسي لفتح القائمة
     * @param optionsLocator  محدد عناصر القائمة الداخلية
     */
    //  اختيار عنصر عشوائي من قائمة `multi-select`
    // الاختيارات checkbox  داخل ال ddl
    public static void selectRandomMultiSelectOptionFromCustomDropDown(WebDriver driver, By dropdownLocator, By optionsLocator) {
        // العثور على زر القائمة المنسدلة والضغط عليه لفتح القائمة
        WebElement dropdown = driver.findElement(dropdownLocator);
        dropdown.click();

        // الحصول على جميع الخيارات المتاحة داخل القائمة
        List<WebElement> options = driver.findElements(optionsLocator);

        // التأكد من وجود خيارات متاحة داخل القائمة
        if (!options.isEmpty()) {
            // إنشاء رقم عشوائي لاختيار عنصر من القائمة
            int randomIndex = generateRandomNumber(options.size());

            // اختيار العنصر العشوائي والضغط عليه
            options.get(randomIndex).click();

            // طباعة العنصر المحدد لاختبار صحة التنفيذ
            System.out.println("تم اختيار العنصر: " + options.get(randomIndex).getText());
        } else {
            System.out.println("القائمة فارغة ولا تحتوي على خيارات!");
        }
    }

//    /**
//     * إنشاء رقم عشوائي بين 0 و (الحد الأقصى - 1)
//     *
//     * @param upperBound الحد الأقصى للرقم العشوائي (حصري)
//     * @return رقم عشوائي بين 0 و upperBound-1
//     */
//    public static int generateRandomNumber(int upperBound) {
//        return new Random().nextInt(upperBound);
//    }


    public static int generateRandomNumber(int upperBound) { //0 >> upper-1  > 5
        return new Random().nextInt(upperBound) + 1;
    }

    public static Set<Integer> generateUniqueNumber(int numberNeeded, int totalNumbers) {
        Set<Integer> generatedNumbers = new HashSet<>();
        while (generatedNumbers.size() < numberNeeded) {
            int randomNumber = generateRandomNumber(totalNumbers);
            generatedNumbers.add(randomNumber);
        }
        return generatedNumbers;
    }

    public static boolean VerifyURL(WebDriver driver, String expectedURL) {
        try {
            generalWait(driver, Integer.parseInt(getConfigValue("config", "WAIT_EXPLICIT")))
                    .until(ExpectedConditions.urlToBe(expectedURL));
            LogUtils.info("Expected URL: " + expectedURL);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public static File getLatestFile(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        assert files != null;
        if (files.length == 0)
            return null;
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files[0];
    }



// في AutomationFramework.Utilities.Utility

    // في Utility class
    public static String buildProductKey(String rawName, double price) {
        if (rawName == null) {
            rawName = "";
        }

        // نطبع الاسم بطريقة موحدة
        String normalizedName = rawName
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .trim();

        // نوحّد شكل السعر (مثلاً 2 رقم عشري)
        String normalizedPrice = String.format(java.util.Locale.US, "%.2f", price);

        return normalizedName + " | " + normalizedPrice;
    }

    /**
     * نفس فكرة Get_CardPrice ولكن عامة (بدون driver)
     * تشتغل مع أي text زي "EGP 9,999.00"
     */
    public static double parsePriceTextSafely(String priceText) {
        if (priceText == null) return 0.0;

        System.out.println("Raw price text is : " + priceText.replace("\n", "\\n"));

        if (priceText.trim().isEmpty()) {
            return 0.0;
        }

        // ناخد أول سطر فقط
        String firstLine = priceText.split("\\R")[0].trim();

        // نخلي بس الأرقام والكوما والنقطة
        String cleaned = firstLine.replaceAll("[^0-9.,]", "");

        if (cleaned.isEmpty()) {
            return 0.0;
        }

        cleaned = cleaned.replace(",", "");

        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            System.out.println("Failed to parse price from text: " + priceText + " | cleaned: " + cleaned);
            return 0.0;
        }
    }

}
