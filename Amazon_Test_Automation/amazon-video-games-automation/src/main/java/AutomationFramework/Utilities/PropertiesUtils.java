package AutomationFramework.Utilities;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

public class PropertiesUtils {

    // ده الفولدر اللي فيه كل ملفات الـ .properties
    private static final String PROPERTIES_DIR = "src/main/resources";

    // هنخزن كل الـ properties هنا بعد ما نحمّلها مرة واحدة
    private static final Properties PROPERTIES = new Properties();
    private static boolean initialized = false;

    /**
     * تحميل كل ملفات الـ .properties من src/main/resources
     * ودمجها مع System.getProperties()
     */
    public static synchronized Properties loadProperties() {
        if (initialized) {
            return PROPERTIES;
        }

        try {
            File baseDir = new File(PROPERTIES_DIR);
            Collection<File> propertiesFilesList =
                    FileUtils.listFiles(baseDir, new String[]{"properties"}, true);

            if (propertiesFilesList.isEmpty()) {
                LogUtils.warn("No .properties files found under: " + baseDir.getAbsolutePath());
            }

            for (File propertyFile : propertiesFilesList) {
                try (FileInputStream fis = new FileInputStream(propertyFile)) {
                    LogUtils.info("Loading properties from: " + propertyFile.getAbsolutePath());
                    PROPERTIES.load(fis);
                } catch (IOException ioe) {
                    LogUtils.error("Error loading properties from "
                            + propertyFile.getAbsolutePath() + " : " + ioe.getMessage());
                }
            }

            // دمج مع system properties (الاتجاهين)
            PROPERTIES.putAll(System.getProperties());
            System.getProperties().putAll(PROPERTIES);

            initialized = true;
            LogUtils.info("Loading Properties File Data. Total keys = " + PROPERTIES.size());

            // دي بس عشان نطمن إن OpenAllureReportAfterExecution تقرأت
            String flag = PROPERTIES.getProperty("OpenAllureReportAfterExecution");
            LogUtils.info("DEBUG -> OpenAllureReportAfterExecution from properties = '" + flag + "'");

            return PROPERTIES;
        } catch (Exception e) {
            LogUtils.error("Failed to Load Properties File Data because: " + e.getMessage());
            return PROPERTIES;
        }
    }

    /**
     * Getter مريح لو حبيت تستخدمه مباشرة
     */
    public static String getProperty(String key) {
        if (!initialized) {
            loadProperties();
        }
        return PROPERTIES.getProperty(key, "");
    }

    /**
     * تعديل / إضافة key في ملف config.properties الرئيسي
     * (تقدر تغيّر اسم الملف لو عندك اسم تاني)
     */
    public static void AddProperty(String key, String value) throws IOException {
        if (!initialized) {
            loadProperties();
        }

        File mainConfigFile = new File(PROPERTIES_DIR, "config.properties");

        // لو الملف مش موجود ننشئه
        if (!mainConfigFile.exists()) {
            if (mainConfigFile.getParentFile() != null &&
                    !mainConfigFile.getParentFile().exists()) {
                mainConfigFile.getParentFile().mkdirs();
            }
            boolean created = mainConfigFile.createNewFile();
            if (!created) {
                LogUtils.warn("Could not create config.properties file at: "
                        + mainConfigFile.getAbsolutePath());
            }
        }

        // نحمّل الموجود في الملف
        Properties fileProps = new Properties();
        try (FileInputStream in = new FileInputStream(mainConfigFile)) {
            fileProps.load(in);
        } catch (IOException e) {
            LogUtils.warn("Could not read existing config.properties, will overwrite. Reason: "
                    + e.getMessage());
        }

        // نعدّل / نضيف القيمة
        fileProps.setProperty(key, value);

        // نكتبها في الملف
        try (FileOutputStream out = new FileOutputStream(mainConfigFile)) {
            fileProps.store(out, "Updated by PropertiesUtils");
        }

        // نحدّث الـ cache والـ System properties
        PROPERTIES.put(key, value);
        System.setProperty(key, value);

        LogUtils.info("Property '" + key + "' updated to '" + value
                + "' in file: " + mainConfigFile.getAbsolutePath());
    }
}
