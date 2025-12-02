package AutomationFramework.Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtils {

    public final static String CONFIGS_PATH = "src/main/resources/";

    private static final Properties props = new Properties();

    static {
        try {
            // تحميل ملفات properties
            FileInputStream envFile = new FileInputStream(CONFIGS_PATH + "environment.properties");
            props.load(envFile);
            envFile.close();

            FileInputStream webAppFile = new FileInputStream(CONFIGS_PATH + "webApp.properties");
            props.load(webAppFile);
            webAppFile.close();

        } catch (IOException e) {
            LogUtils.error("Error loading properties files: " + e.getMessage());
        }
    }

    private ConfigUtils() {
        super();
    }

    public static String getConfigValue(String key) {
        // أولًا حاول تقرأ من system properties (للسيطرة عبر JVM args)
        String sysValue = System.getProperty(key);
        if (sysValue != null && !sysValue.isEmpty()) {
            return sysValue;
        }

        // لو مش موجودة في system properties، اقراها من ملفات الـ properties
        String value = props.getProperty(key);
        return value != null ? value : "";
    }
}
