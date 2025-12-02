package AutomationFramework.Utilities;

import com.google.common.collect.ImmutableMap;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Parameter;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.jsoup.Jsoup;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static AutomationFramework.Utilities.ConfigUtils.getConfigValue;
import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;

public class AllureUtils {

    // ====== PATHS مهمّة ======
    // ده الفولدر اللي انت كتبته بنفسك في الرسالة: test-outputs\allure-result
    public static final String ALLURE_RESULTS_FOLDER_PATH = "test-outputs/allure-result";    // <-- مهم جداً
    public static final String ALLURE_REPORT_PATH = "test-outputs/reports";
    public static final String FULL_ALLURE_REPORT_PATH = "test-outputs/full-report";

    // هنستخدم الـ user.home من الـ System مباشرة عشان نضمن صحته
    private static final String USER_HOME =
            System.getProperty("user.home", getConfigValue("user.home"));

    // المكان اللي هنفك فيه الـ Allure CLI
    private static final String ALLURE_EXTRACTION_LOCATION =
            USER_HOME + File.separator + ".m2" + File.separator + "repository" + File.separator + "allure" + File.separator;

    private static String ALLURE_VERSION = "";
    private static String ALLURE_BINARY_PATH = "";

    private AllureUtils() {
        super();
    }

    // ====== تحديد مسار الـ allure executable ======
    private static String getAllureExecutable() {
        // لو عندنا Version محفوظة بالفعل
        if (ALLURE_VERSION != null && !ALLURE_VERSION.isEmpty()) {
            ALLURE_BINARY_PATH = ALLURE_EXTRACTION_LOCATION
                    + "allure-" + ALLURE_VERSION
                    + File.separator + "bin" + File.separator + "allure";
        }

        // لو لسه فاضية لأي سبب، نحاول نخمن مكانه (standard locations)
        if (ALLURE_BINARY_PATH == null || ALLURE_BINARY_PATH.isEmpty()) {
            // Windows paths محتملة
            if (OS.getCurrentOS() == OS.WINDOWS) {
                String[] candidates = new String[] {
                        "C:\\allure-commandline\\bin\\allure.bat",
                        "C:\\Program Files\\Allure\\bin\\allure.bat"
                };
                for (String c : candidates) {
                    if (new File(c).exists()) {
                        ALLURE_BINARY_PATH = c;
                        break;
                    }
                }
            }
        }

        // لو لسه برضو فاضي → هنرجع path جوه الـ m2 على أمل إنه موجود من الـ download
        if (ALLURE_BINARY_PATH == null || ALLURE_BINARY_PATH.isEmpty()) {
            ALLURE_BINARY_PATH = ALLURE_EXTRACTION_LOCATION
                    + "allure-" + ALLURE_VERSION
                    + File.separator + "bin" + File.separator + "allure";
        }

        if (OS.getCurrentOS() == OS.WINDOWS) {
            if (!ALLURE_BINARY_PATH.toLowerCase().endsWith(".bat")) {
                ALLURE_BINARY_PATH = ALLURE_BINARY_PATH + ".bat";
            }
        }

        return ALLURE_BINARY_PATH;
    }

    // ====== تشغيل أمر generate ======
    private static void generateReport(String outputFolder, boolean isSingleFile) {
        String allureExecutable = getAllureExecutable();
        File exe = new File(allureExecutable);

        if (!exe.exists()) {
            LogUtils.error("❌ Allure executable not found at: " + exe.getAbsolutePath());
            LogUtils.error("تأكد إن الـ download شغال أو إن الـ Allure CLI متثبت صح.");
            return;
        }

        List<String> command = new ArrayList<>();
        command.add(exe.getAbsolutePath());
        command.add("generate");
        command.add(ALLURE_RESULTS_FOLDER_PATH);
        command.add("-o");
        command.add(outputFolder);
        command.add("--clean");
        if (isSingleFile) {
            command.add("--single-file");
        }

        LogUtils.info("Running Allure command: " + String.join(" ", command));
        TerminalUtils.executeTerminalCommand(command.toArray(new String[0]));
    }

    // ====== إعداد بيئة Allure + environment.properties ======
    public static void setAllureEnvironment() {
        LogUtils.info("Initializing Allure Reporting Environment...");

        String envOutputDir = getConfigValue("user.dir")
                + File.separator
                + ALLURE_RESULTS_FOLDER_PATH
                + File.separator;

        allureEnvironmentWriter(
                ImmutableMap.<String, String>builder()
                        .put("OS", getConfigValue("os.name"))
                        .put("Java version", getConfigValue("java.runtime.version"))
                        .put("Browser", getConfigValue("browserType"))
                        .put("Execution Type", getConfigValue("executionType"))
                        .put("URL", getConfigValue("baseUrlWeb"))
                        .build(),
                envOutputDir
        );

        downloadAndExtractAllureBinaries();
    }

    // ====== Download + Extract Allure CLI ======
    private static void downloadAndExtractAllureBinaries() {
        try {
            String allureZipPath = getAllureZipPath();
            if (allureZipPath == null) {
                LogUtils.error("❌ Could not resolve Allure ZIP path. Skipping Allure binary setup.");
                return;
            }

            String extractionDir = ALLURE_EXTRACTION_LOCATION + "allure-" + ALLURE_VERSION;

            if (new File(extractionDir).exists()) {
                LogUtils.info("Allure binaries already exist.");
                return;
            }

            extractAllureBinaries(allureZipPath);
            LogUtils.info("Allure binaries downloaded and extracted.");

            if (OS.getCurrentOS() != OS.WINDOWS) {
                setUnixExecutePermissions();
            }
        } catch (Exception e) {
            LogUtils.error("Error during Allure binaries extraction: " + e.getMessage());
        }
    }

    private static void extractAllureBinaries(String allureZipPath) {
        if (allureZipPath == null) {
            LogUtils.error("❌ allureZipPath is null, cannot extract binaries.");
            return;
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(allureZipPath))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                File file = new File(ALLURE_EXTRACTION_LOCATION + entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LogUtils.error("Failed to extract Allure binaries: " + e.getMessage());
        }
    }

    private static void setUnixExecutePermissions() {
        String executable = getAllureExecutable();
        if (executable == null || executable.isEmpty()) {
            LogUtils.warn("Allure executable path is empty. Skipping chmod.");
            return;
        }
        TerminalUtils.executeTerminalCommand("chmod", "u+x", executable);
    }

    // ====== Resolve latest Allure version + ZIP path ======
    public static String urlConnection(String url) {
        try {
            String resolvedUrl = Jsoup.connect(url)
                    .followRedirects(true)
                    .execute()
                    .url()
                    .toString();
            LogUtils.info("Resolved URL: " + resolvedUrl);
            return resolvedUrl;
        } catch (IOException e) {
            LogUtils.error("Failed to resolve URL: " + url + " - " + e.getMessage());
            return null;
        }
    }

    private static String getAllureZipPath() {
        try {
            String resolved = urlConnection("https://github.com/allure-framework/allure2/releases/latest");
            if (resolved == null || !resolved.contains("/tag/")) {
                LogUtils.error("❌ Could not parse Allure version from: " + resolved);
                return null;
            }

            ALLURE_VERSION = resolved.split("/tag/")[1];
            LogUtils.info("Allure Version: " + ALLURE_VERSION);

            String allureZipUrl = "https://repo.maven.apache.org/maven2/io/qameta/allure/allure-commandline/"
                    + ALLURE_VERSION
                    + "/allure-commandline-"
                    + ALLURE_VERSION
                    + ".zip";

            String allureZipFile = "src/main/resources/allure/allure-" + ALLURE_VERSION + ".zip";

            File zipFile = new File(allureZipFile);
            if (!zipFile.exists()) {
                downloadAllureZip(allureZipUrl, allureZipFile);
            } else {
                LogUtils.info("Allure ZIP already exists at: " + allureZipFile);
            }

            return allureZipFile;
        } catch (Exception e) {
            LogUtils.error("Error while fetching Allure ZIP path: " + e.getMessage());
            return null;
        }
    }

    private static void downloadAllureZip(String allureZipUrl, String allureZipFile) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(allureZipUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(allureZipFile)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            LogUtils.info("Allure ZIP downloaded to: " + allureZipFile);
        } catch (IOException e) {
            LogUtils.error("Failed to download Allure ZIP from: " + allureZipUrl + " - " + e.getMessage());
        }
    }

    // ====== Public API لإنتاج التقارير ======
    public static void generateAllureReport() {
        generateReport(ALLURE_REPORT_PATH, true);
        LogUtils.info("Allure Report Generated Successfully on " + OS.getCurrentOS());
    }

    public static void generateFullAllureReport() {
        generateReport(FULL_ALLURE_REPORT_PATH, false);
        LogUtils.info("Full Allure Report Generated Successfully on " + OS.getCurrentOS());
    }

    public static String renameAllureReport() {
        String newFileName = "AllureReport" + TimeUtils.getTimestamp() + ".html";
        String oldPath = System.getProperty("user.dir")
                + File.separator
                + ALLURE_REPORT_PATH
                + File.separator
                + "index.html";

        FilesUtils.renameFile(oldPath, newFileName);
        return newFileName;
    }

    // ====== فتح تقرير Allure بعد التنفيذ ======
    public static void openAllureReport(String newFileName) {
        String flag = getConfigValue("OpenAllureReportAfterExecution");
        LogUtils.info("OpenAllureReportAfterExecution = " + flag);

        if (flag == null || !"true".equalsIgnoreCase(flag)) {
            LogUtils.info("Auto open Allure is disabled. Skipping openAllureReport.");
            return;
        }

        try {
            String fullPath = System.getProperty("user.dir")
                    + File.separator
                    + ALLURE_REPORT_PATH
                    + File.separator
                    + newFileName;

            File reportFile = new File(fullPath);

            if (!reportFile.exists()) {
                LogUtils.error("❌ Allure report file not found: " + reportFile.getAbsolutePath());
                return;
            }

            LogUtils.info("✅ Opening Allure report: " + reportFile.getAbsolutePath());

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(reportFile.toURI());
            } else if (OS.getCurrentOS() == OS.WINDOWS) {
                Runtime.getRuntime().exec("cmd /c start \"\" \"" + reportFile.getAbsolutePath() + "\"");
            } else {
                LogUtils.warn("Desktop browse not supported on this OS.");
            }

        } catch (Exception e) {
            LogUtils.error("❌ Failed to open Allure report: " + e.getMessage());
        }
    }

    // ====== History copy ======
    public static void copyHistory() {
        try {
            File historyDir = new File(System.getProperty("user.dir")
                    + File.separator
                    + FULL_ALLURE_REPORT_PATH
                    + File.separator
                    + "history");

            File allureResultsDir = new File(System.getProperty("user.dir")
                    + File.separator
                    + ALLURE_RESULTS_FOLDER_PATH
                    + File.separator
                    + "history");

            if (historyDir.exists()) {
                FileUtils.copyDirectory(historyDir, allureResultsDir);
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage());
        }
    }

    // ====== Attachments إلى Allure ======
    public static void attachScreenshotToAllure(String screenshotName, String screenshotPath) {
        try {
            File screenshotFile = new File(screenshotPath);
            if (!screenshotFile.exists()) {
                LogUtils.error("Screenshot file not found: " + screenshotPath);
                return;
            }
            Allure.addAttachment(screenshotName, java.nio.file.Files.newInputStream(Path.of(screenshotPath)));
            LogUtils.info("Screenshot attached to Allure report: " + screenshotPath);
        } catch (Exception e) {
            LogUtils.error(e.getMessage());
        }
    }

    public static void attachLogsToAllure() {
        try {
            LogManager.shutdown(); // Flush and close appenders
            FilesUtils.getLogFileAfterTest(LogUtils.LOGS_PATH + File.separator + "logs.log");
            File logFile = FilesUtils.getLatestFile(LogUtils.LOGS_PATH);
            ((LoggerContext) LogManager.getContext(false)).reconfigure();

            if (logFile == null || !logFile.exists()) {
                LogUtils.error("Log file not found or does not exist.");
                return;
            }

            String logContent = java.nio.file.Files.readString(Path.of(logFile.getPath()));
            Allure.addAttachment("logs.log", logContent);

        } catch (IOException e) {
            LogUtils.error("Error reading log file: " + e.getMessage());
        } catch (Exception e) {
            LogUtils.error("Unexpected error: " + e.getMessage());
        }
    }

    public static void attachRecordsToAllure() {
        if (getConfigValue("recordTests").equalsIgnoreCase("true")) {
            try {
                File screenRecord = FilesUtils.getLatestFile(ScreenRecorderUtils.RECORDINGS_PATH);
                if (screenRecord != null && screenRecord.getName().contains(".mp4")) {
                    Allure.addAttachment(
                            "Test Execution Video",
                            "video/mp4",
                            java.nio.file.Files.newInputStream(screenRecord.toPath()),
                            ".mp4"
                    );
                }
            } catch (Exception e) {
                LogUtils.error(e.getMessage());
            }
        }
    }

    public static void addStepParameters(String[][] nameValuePairs) {
        List<Parameter> parameters = Arrays.stream(nameValuePairs)
                .map(pair -> new Parameter().setName(pair[0]).setValue(pair[1]))
                .toList();

        Allure.getLifecycle().updateStep(step -> step.setParameters(parameters));
    }

    // ====== OS Enum ======
    public enum OS {
        WINDOWS, MAC, LINUX, OTHER;

        public static OS getCurrentOS() {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("win")) return WINDOWS;
            if (osName.contains("mac")) return MAC;
            if (osName.contains("nix") || osName.contains("nux")) return LINUX;
            return OTHER;
        }
    }
}
