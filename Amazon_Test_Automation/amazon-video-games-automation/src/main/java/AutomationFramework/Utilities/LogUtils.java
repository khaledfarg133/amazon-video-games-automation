package AutomationFramework.Utilities; // تحديد الحزمة التي ينتمي إليها الكلاس

import org.apache.logging.log4j.Logger; // استيراد مكتبة Log4j لتسجيل السجلات
import org.apache.logging.log4j.LogManager; // استيراد LogManager للحصول على كائن Logger
import java.util.Objects; // استيراد Objects لتجنب الأخطاء عند التعامل مع القيم الفارغة (null)

public final class LogUtils { // تعديل اسم الكلاس ليطابق النسخة الثانية

    public static  String LOGS_PATH = "test-outputs/Logs"; // تعريف مسار حفظ السجلات

    private LogUtils() {
        // منع إنشاء كائن من هذا الكلاس لأنه Utility Class
    }

    private static Logger getLogger() {
        // الحصول على كائن Logger الخاص بالكلاس الذي استدعى السجل، مما يسمح بتحديد مصدر السجلات ديناميكيًا
        return LogManager.getLogger(Thread.currentThread().getStackTrace()[3].getClassName());
    }

    public static void trace(String ... message) {
        // تسجيل رسالة بمستوى "Trace" (تفاصيل دقيقة جدًا عن التنفيذ، غالبًا للاختبار والتصحيح العميق)
        // getLogger().trace(message);
        getLogger().trace(String.join(" ", message));

    }

    public static void debug(String ... message) {
        // تسجيل رسالة بمستوى "Debug" (معلومات تساعد على تتبع الأخطاء أثناء التطوير)
        // getLogger().debug(message);
        getLogger().debug(String.join(" ", message));

    }

    public static void info(String ... message) {
        // تسجيل رسالة بمستوى "Info" (معلومات عامة عن تنفيذ التطبيق)
        // getLogger().info(message);
        getLogger().info(String.join(" ", message));
    }

    public static void warn(String... message) {
        // تسجيل رسالة بمستوى "Warn" (تحذيرات قد تؤدي إلى مشاكل مستقبلًا)
        //getLogger().warn(message);
        getLogger().warn(String.join(" ", message));
    }

    public static void error(String... message)  {
        // تسجيل رسالة بمستوى "Error" (أخطاء تحتاج إلى معالجة ولكن لا توقف النظام)
//        getLogger().error(message);
        getLogger().error(String.join(" ", message));

    }

    public static void fatal(String message) {
        // تسجيل رسالة بمستوى "Fatal" (أخطاء خطيرة تؤدي إلى فشل التطبيق)
//        getLogger().fatal(message);
        getLogger().fatal(String.join(" ", message));

    }
}
