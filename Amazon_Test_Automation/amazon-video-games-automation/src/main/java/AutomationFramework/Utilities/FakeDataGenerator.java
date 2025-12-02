package AutomationFramework.Utilities;

import com.github.javafaker.Faker;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

public class FakeDataGenerator {

    private static final Faker faker = new Faker();

    public static String getRandomName() {
        return faker.name().fullName(); // اسم عشوائي كامل
    }

    public static String getRandomEmail() {
        return faker.internet().emailAddress(); // بريد إلكتروني عشوائي
    }
    public static String getFormattedEmail() {
        return faker.name().firstName().toLowerCase() + faker.number().digits(3) + "@test.com";
    }

    public static String getFormattedPhoneNumber() {
        return "+966" + faker.number().digits(9); // رقم سعودي عشوائي
    }

    public static String getRandomPhoneNumber() {
        return faker.phoneNumber().cellPhone(); // رقم هاتف عشوائي
    }

    public static String getRandomBirthDate() {
        return faker.date().birthday().toString(); // تاريخ ميلاد عشوائي
    }

    public static String getRandomPassword() {
        return faker.internet().password(8, 16); // كلمة مرور بين 8 و 16 حرفًا
    }


  /*/////////////////////////////////////////////////////////////////////////////////////////*/
  // Function لملء أي حقل بالاسم العشوائي
  public static void fillFieldWithFakeName(WebDriver driver, By Locator) {
      String fakeName = faker.name().fullName(); // توليد اسم عشوائي

      // البحث عن العنصر بناءً على الـ Locator وتمرير الاسم العشوائي إليه
      WebElement element = driver.findElement(Locator);
      element.clear();
      element.sendKeys(fakeName);

      // طباعة البيانات لمتابعة الاختبار
      System.out.println("Filled field [" + Locator + "] with fake name: " + fakeName);
  }

    // Function لملء أي حقل ببريد إلكتروني عشوائي
    public static void fillFieldWithFake_Email(WebDriver driver, By Locator) {
        String fakeEmail = faker.internet().emailAddress(); // توليد بريد إلكتروني عشوائي

        WebElement element = driver.findElement(Locator);
        element.clear();
        element.sendKeys(fakeEmail);

        System.out.println("Filled field [" + Locator + "] with fake email: " + fakeEmail);
    }

    // ✅ ملء أي حقل برقم هاتف عشوائي
    public static void fillFieldWithFake_Phone(WebDriver driver, By Locator) {
        String fakePhone = "55" + faker.number().digits(7);
        WebElement element = driver.findElement(Locator);
        element.clear();
        element.sendKeys(fakePhone);
        System.out.println("Filled field [" + Locator + "] with fake phone number: " + fakePhone);
    }
    // ✅ ملء أي حقل برقم هاتف عشوائي
    public static void fillFieldWithFakeNational_ID(WebDriver driver, By Locator) {
        String fakePhone =  faker.number().digits(10);
        WebElement element = driver.findElement(Locator);
        element.clear();
        element.sendKeys(fakePhone);
        System.out.println("Filled field [" + Locator + "] with fake National ID: " + fakePhone);
    }


    /**  تنسيق افتراضي للتواريخ "يوم/شهر/سنة"
     * مستخدم فى ال functions الاتية فقط
     * getTwoRelatedDates()
     * getTwoRelatedDatesFormatted()
     * */
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * توليد تاريخين داخل السنة المحددة:
     * - الأول: تاريخ عشوائي داخل السنة
     * - الثاني: تاريخ أحدث من الأول، ولكن ليس في المستقبل
     */
    public static LocalDate[] getTwoRelatedDates(int baseYear) {
        LocalDate startOfYear = LocalDate.of(baseYear, 1, 1);
        LocalDate endOfYear = LocalDate.of(baseYear, 12, 31);

        long startEpochDay = startOfYear.toEpochDay();
        long endEpochDay = endOfYear.toEpochDay();

        LocalDate randomDate = LocalDate.ofEpochDay(ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1));

        LocalDate today = LocalDate.now();
        LocalDate newerDate = randomDate.plusDays(ThreadLocalRandom.current().nextInt(1, (int) ChronoUnit.DAYS.between(randomDate, today) + 1));

        return new LocalDate[]{randomDate, newerDate};
    }

    // نفس الدالة السابقة لكن ترجع التاريخين كـ String بتنسيق معين
    public static String[] getTwoRelatedDatesFormatted(int baseYear) {
        LocalDate[] dates = getTwoRelatedDates(baseYear);
        return new String[]{
                dates[0].format(DEFAULT_FORMATTER),
                dates[1].format(DEFAULT_FORMATTER)
        };
    }

    public static String[] getTwoRelatedDatesAsDigits(int baseYear) {
        DateTimeFormatter digitFormat = DateTimeFormatter.ofPattern("yyyyMMdd");  // تنسيق رقمي
        LocalDate[] dates = getTwoRelatedDates(baseYear);

        return new String[]{
                dates[0].format(digitFormat),
                dates[1].format(digitFormat)
        };
    }



    // توليد تاريخ عشوائي داخل سنة معينة
    public static LocalDate getRandomDateInYear(int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return LocalDate.ofEpochDay(ThreadLocalRandom.current().nextLong(start.toEpochDay(), end.toEpochDay() + 1));
    }

    // توليد تاريخ أحدث من تاريخ معين، بدون تجاوز اليوم الحالي
    public static LocalDate getNewerDate(LocalDate baseDate) {
        LocalDate today = LocalDate.now();
        return baseDate.plusDays(ThreadLocalRandom.current().nextInt(1,
                (int) ChronoUnit.DAYS.between(baseDate, today) + 1));
    }
    // تحويل تاريخ عشوائي داخل سنة معينة إلى نص بصيغة "yyyyMMdd"
    public static String getRandomDateInYearDigits(int year) {
        DateTimeFormatter digitFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        return getRandomDateInYear(year).format(digitFormat);
    }

    public static String convertDateFormat(String dateStr) {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("ddMMyyyy");

        LocalDate date = LocalDate.parse(dateStr, inputFormat);
        return date.format(outputFormat);
    }

    // تحويل تاريخ عشوائي داخل سنة معينة  إلى نص بصيغة "yyyyMMdd" بشرط ألا يكون مستقبلي
    public static String getRandomDateInYearDigits_In_Past(String yearStr) {
        DateTimeFormatter digitFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            return "Invalid: Year format is incorrect";
        }

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        LocalDate today = LocalDate.now();

        // تأكد ألا يتجاوز نهاية السنة التاريخ الحالي
        if (end.isAfter(today)) {
            end = today;
        }

        long startEpoch = start.toEpochDay();
        long endEpoch = end.toEpochDay();

        // إذا كانت السنة كلها في المستقبل، أعد قيمة فارغة أو رسالة تحذير
        if (endEpoch < startEpoch) {
            return "Invalid: Year is entirely in the future";
        }

        LocalDate randomDate = LocalDate.ofEpochDay(
                ThreadLocalRandom.current().nextLong(startEpoch, endEpoch + 1)
        );

        return randomDate.format(digitFormat);
    }


    // تحويل تاريخ عشوائي داخل سنة معينة إلى نص بصيغة "dd/MM/yyyy"
    public static String getRandomDateInYearFormatted(int year) {
        return getRandomDateInYear(year).format(DEFAULT_FORMATTER);
    }
    // تحويل تاريخ أحدث من تاريخ معين إلى صيغة نصية "yyyyMMdd"
    public static String getNewerDateDigits(LocalDate baseDate) {
        DateTimeFormatter digitFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        return getNewerDate(baseDate).format(digitFormat);
    }

    // تحويل تاريخ أحدث من تاريخ معين إلى صيغة نصية
    public static String getNewerDateFormatted(LocalDate baseDate) {
        return getNewerDate(baseDate).format(DEFAULT_FORMATTER);
    }
    //  توليد تاريخ أحدث عشوائي من تاريخ معين ولكن في الماضي فقط
    public static String getNewerDateDigitsInPast(LocalDate baseDate) {
        DateTimeFormatter digitFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDate today = LocalDate.now();

        // حساب عدد الأيام بين التاريخ الأساسي واليوم الحالي
        long daysBetween = ChronoUnit.DAYS.between(baseDate, today);

        // إذا لم يكن هناك مجال لتوليد تاريخ أحدث في الماضي، نعيد نفس التاريخ
        if (daysBetween <= 0) {
            return baseDate.format(digitFormat);  // في حال كان التاريخ المُعطى في المستقبل
        }

        // توليد عدد عشوائي من الأيام بين 1 و daysBetween
        int randomDays = ThreadLocalRandom.current().nextInt(1, (int) daysBetween + 1);

        // توليد تاريخ جديد في الماضي ويجب أن يكون أحدث من التاريخ المعطى (baseDate)
        LocalDate newerDate = baseDate.plusDays(randomDays);

        // التأكد أن التاريخ المولد في الماضي ويكون أحدث من baseDate
        if (newerDate.isAfter(baseDate)) {
            return newerDate.format(digitFormat);
        }

        // في حال لم يكن التاريخ المولد أحدث من baseDate (الذي قد يحدث نادرًا)، نعيد التاريخ نفسه
        return baseDate.format(digitFormat);
    }

    public static String getRandomDateBetween2Dates(String startDateStr, String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        try {
            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            // التأكد أن التاريخ الأول أقدم من أو يساوي الثاني
            if (startDate.isAfter(endDate)) {
                return "Invalid: Start date is after end date";
            }

            long startEpoch = startDate.toEpochDay();
            long endEpoch = endDate.toEpochDay();

            long randomEpochDay = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch + 1);
            LocalDate randomDate = LocalDate.ofEpochDay(randomEpochDay);

            return randomDate.format(formatter);
        } catch (Exception e) {
            return "Invalid: One or both dates are not in yyyyMMdd format";
        }
    }


/*
//////////////////////////////////////////////////////////////////////////////////////////////////

 import com.github.javafaker.Faker;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FakeDataHelper {
    private static final Faker faker = new Faker();

    // ✅ توليد اسم عشوائي
    public static String getRandomName() {
        return faker.name().fullName();
    }

    // ✅ توليد بريد إلكتروني عشوائي
    public static String getRandomEmail() {
        return faker.internet().emailAddress();
    }

    // ✅ توليد بريد إلكتروني بتنسيق معين
    public static String getFormattedEmail() {
        return faker.name().firstName().toLowerCase() + faker.number().digits(3) + "@test.com";
    }

    // ✅ توليد رقم هاتف عشوائي بصيغة سعودية
    public static String getFormattedPhoneNumber() {
        return "+966" + faker.number().digits(9);
    }

    // ✅ توليد رقم هاتف عشوائي
    public static String getRandomPhoneNumber() {
        return faker.phoneNumber().cellPhone();
    }

    // ✅ توليد تاريخ ميلاد عشوائي
    public static String getRandomBirthDate() {
        return faker.date().birthday().toString();
    }

    // ✅ توليد كلمة مرور عشوائية
    public static String getRandomPassword() {
        return faker.internet().password(8, 16);
    }

    // ✅ ملء أي حقل باسم عشوائي
    public static void fillFieldWithFakeName(WebDriver driver, String locator) {
        String fakeName = getRandomName();
        fillField(driver, locator, fakeName);
        System.out.println("Filled field [" + locator + "] with fake name: " + fakeName);
    }

    // ✅ ملء أي حقل ببريد إلكتروني عشوائي
    public static void fillFieldWithFakeEmail(WebDriver driver, String locator) {
        String fakeEmail = getRandomEmail();
        fillField(driver, locator, fakeEmail);
        System.out.println("Filled field [" + locator + "] with fake email: " + fakeEmail);
    }

    // ✅ ملء أي حقل برقم هاتف عشوائي
    public static void fillFieldWithFakePhone(WebDriver driver, String locator) {
        String fakePhone = "5" + faker.number().digits(8);
        fillField(driver, locator, fakePhone);
        System.out.println("Filled field [" + locator + "] with fake phone number: " + fakePhone);
    }

    // ✅ ملء أي حقل بتاريخ ميلاد عشوائي
    public static void fillFieldWithFakeBirthDate(WebDriver driver, String locator) {
        String fakeBirthDate = getRandomBirthDate();
        fillField(driver, locator, fakeBirthDate);
        System.out.println("Filled field [" + locator + "] with fake birth date: " + fakeBirthDate);
    }

    // ✅ ملء أي حقل بكلمة مرور عشوائية
    public static void fillFieldWithFakePassword(WebDriver driver, String locator) {
        String fakePassword = getRandomPassword();
        fillField(driver, locator, fakePassword);
        System.out.println("Filled field [" + locator + "] with fake password: " + fakePassword);
    }

    // ✅ دالة مساعدة لملء أي حقل بالقيمة المطلوبة
    private static void fillField(WebDriver driver, String locator, String value) {
        WebElement element = driver.findElement(By.xpath(locator));
        element.clear();
        element.sendKeys(value);
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////
 */
}
