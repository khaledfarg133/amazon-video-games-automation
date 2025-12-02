package AutomationFramework.listeners;

import AutomationFramework.Utilities.*;
import AutomationFramework.drivers.GUIDriver;
import AutomationFramework.validations.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import java.io.File;

import static AutomationFramework.Utilities.AllureUtils.copyHistory;
import static AutomationFramework.Utilities.PropertiesUtils.loadProperties;

public class TestNGListeners implements IExecutionListener, IInvokedMethodListener, ITestListener, ISuiteListener {

    File screenshots = new File("test-outputs/screenshots");
    File recordings = new File("test-outputs/recordings");
    File allure_results = new File(AllureUtils.ALLURE_RESULTS_FOLDER_PATH);
    File reports = new File(AllureUtils.ALLURE_REPORT_PATH);
    File logs = new File("test-outputs/Logs");

    // علشان ما نولّدش التقرير مرتين (في onExecutionFinish + shutdown hook)
    private static volatile boolean reportGenerated = false;

    @Override
    public void onExecutionStart() {
        LogUtils.info("Test Execution started");

        // إنشاء الفولدرات
        createTestOutputDirectories();
        LogUtils.info("Directories created");

        // تحميل الـ properties
        loadProperties();
        LogUtils.info("Properties loaded");

        // نخلي Allure نفسه يكتب النتائج في نفس الفولدر اللي الـ CLI هيقرأ منه
        String resultsDirFullPath = System.getProperty("user.dir")
                + File.separator
                + AllureUtils.ALLURE_RESULTS_FOLDER_PATH;

        System.setProperty("allure.results.directory", resultsDirFullPath);
        LogUtils.info("allure.results.directory = " + System.getProperty("allure.results.directory"));

        // إعداد بيئة Allure (environment.properties + binaries)
        AllureUtils.setAllureEnvironment();
        LogUtils.info("Allure environment set");

        // Shutdown Hook في حالة إن الـ JVM تقفل فجأة (exit 130 أو Stop من IntelliJ)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (reportGenerated) return; // لو already generated من onExecutionFinish

            try {
                LogUtils.info("Shutdown hook: generating Allure report before JVM exit...");

                copyHistory();
                AllureUtils.generateAllureReport();
                AllureUtils.generateFullAllureReport();
                String newFileName = AllureUtils.renameAllureReport();
                AllureUtils.openAllureReport(newFileName);

            } catch (Exception e) {
                LogUtils.error("Error in shutdown hook while generating Allure report: " + e.getMessage());
            }
        }));
    }

    @Override
    public void onExecutionFinish() {
        LogUtils.info("Test Execution Finished");
        reportGenerated = true;  // علشان ما نكررهاش في الـ shutdown hook

        copyHistory();
        LogUtils.info("History copied");

        AllureUtils.generateAllureReport();
        AllureUtils.generateFullAllureReport();
        String newFileName = AllureUtils.renameAllureReport();
        AllureUtils.openAllureReport(newFileName);

        cleanTestOutputDirectories();
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            ScreenRecorderUtils.startRecording();
            LogUtils.info("Test Case " + testResult.getName() + " started");
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (method.isTestMethod()) {
            ScreenRecorderUtils.stopRecording(result.getName());

            GUIDriver guiDriver = GUIDriver.extractDriver(result);
            WebDriver driver = (guiDriver != null) ? guiDriver.get() : null;

            if (driver != null) {
                switch (result.getStatus()) {
                    case ITestResult.FAILURE ->
                            ScreenshotUtils.takeScreenshot(driver, "failed-" + result.getName());
                    case ITestResult.SUCCESS ->
                            ScreenshotUtils.takeScreenshot(driver, "passed-" + result.getName());
                    case ITestResult.SKIP ->
                            ScreenshotUtils.takeScreenshot(driver, "skipped-" + result.getName());
                }
            } else {
                LogUtils.warn("WebDriver is null. Screenshot not taken for test: ", result.getName());
            }

            AllureUtils.attachLogsToAllure();
            AllureUtils.attachRecordsToAllure();

        } else if (method.isConfigurationMethod()) {
            switch (result.getStatus()) {
                case ITestResult.FAILURE ->
                        LogUtils.info("Configuration Method ", result.getName(), "failed");
                case ITestResult.SUCCESS ->
                        LogUtils.info("Configuration Method ", result.getName(), "passed");
                case ITestResult.SKIP ->
                        LogUtils.info("Configuration Method ", result.getName(), "skipped");
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LogUtils.info("Test case", result.getName(), "passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LogUtils.info("Test case", result.getName(), "failed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LogUtils.info("Test case", result.getName(), "skipped");
    }

    @Override
    public void onFinish(ISuite suite) {
        // ممكن تضيف أي منطق إضافي بعد نهاية الـ Suite هنا لو حبيت
    }

    // ====== تنظيف فولدرات بعد التنفيذ ======
    private void cleanTestOutputDirectories() {
        FilesUtils.deleteSpecificFiles(AllureUtils.ALLURE_RESULTS_FOLDER_PATH, "history");

        String cleanHistory = ConfigUtils.getConfigValue("cleanHistory");
        if ("true".equalsIgnoreCase(cleanHistory)) {
            FilesUtils.cleanDirectory(new File(AllureUtils.ALLURE_RESULTS_FOLDER_PATH));
        }

        FilesUtils.cleanDirectory(screenshots);
        FilesUtils.cleanDirectory(recordings);
        FilesUtils.forceDelete(logs);
        // لو حابب تمسح التقارير القديمة كمان:
        // FilesUtils.cleanDirectory(reports);
    }

    // ====== إنشاء فولدرات قبل التنفيذ ======
    private void createTestOutputDirectories() {
        FilesUtils.createDirs(LogUtils.LOGS_PATH);
        FilesUtils.createDirs(ScreenshotUtils.SCREENSHOTS_PATH);
        FilesUtils.createDirs(ScreenRecorderUtils.RECORDINGS_PATH);
        FilesUtils.createDirs(AllureUtils.ALLURE_REPORT_PATH);
        FilesUtils.createDirs(AllureUtils.ALLURE_RESULTS_FOLDER_PATH);
    }
}
