package AutomationFramework.Utilities;

import com.google.gson.*;
import com.jayway.jsonpath.JsonPath;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class DataUtils {
    public final static String CONFIG_PATH = "src/test/resources/config/";

    public final static String TEST_DATA_PATH = "src/test/resources/test-data/";
    public final static String ENVIRONMENT_PATH = "src/test/resources/test-data/environment.properties";

    private DataUtils() {
    }

    //TODO: Read any field from json file
    public static String getJsonValue(String jsonFilename, String field) {
        try {
            FileReader reader = new FileReader(TEST_DATA_PATH + jsonFilename + ".json");
            Object jsonData = new Gson().fromJson(reader, Object.class);
            return JsonPath.read(jsonData, "$." + field);
        } catch (Exception e) {
            LogUtils.error(e.getMessage());
            return ""; // Return empty string in case of any exception
        }
    }

    //TODO: Read data from json file
    public static String getJsonData(String jsonFilename, String field) {
        try {
            // Define object of file Reader
            FileReader reader = new FileReader(TEST_DATA_PATH + jsonFilename + ".json");
            // Parse the JSON directly into a JsonElement
            JsonElement jsonElement = JsonParser.parseReader(reader);
            return jsonElement.getAsJsonObject().get(field).getAsString();
        } catch (Exception e) {
            LogUtils.error(e.getMessage());
        }
        return "";
    }

    // TODO: Read Data From Excel Sheet
    public static String getExcelData(String excelFilename, String sheetName, int rowNum, int colNum) {
        XSSFWorkbook workBook;
        XSSFSheet sheet;

        String cellData;
        try {
            workBook = new XSSFWorkbook(TEST_DATA_PATH + excelFilename);
            sheet = workBook.getSheet(sheetName);
            cellData = sheet.getRow(rowNum).getCell(colNum).getStringCellValue();
            return cellData;

        } catch (IOException e) {
            LogUtils.error(e.getMessage());
            return "";
        }

    }

    //TODO: get properties from .properties file
    public static String getEnvironmentPropertyValue(String key) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(ENVIRONMENT_PATH));
            return properties.getProperty(key);
        } catch (Exception e) {
            LogUtils.error(e.getMessage());
            return "";
        }

    }

    //TODO: get properties from any .properties file
    public static String getPropertyValue(String fileName, String key) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(TEST_DATA_PATH + fileName + ".properties"));
            return properties.getProperty(key);
        } catch (Exception e) {
            LogUtils.error(e.getMessage());
            return "";
        }

    }

    //TODO: get properties from any .properties file
    public static String getConfigValue(String fileName, String key) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(CONFIG_PATH + fileName + ".properties"));
            return properties.getProperty(key);
        } catch (Exception e) {
            LogUtils.error(e.getMessage());
            return "";
        }
    }
    public static String MYgetJsonValue(String jsonFilename, String field) {
        try {
            FileReader reader = new FileReader(TEST_DATA_PATH + jsonFilename + ".json");
            JsonParser parser = new JsonParser();
            JsonObject jsonData = parser.parse(reader).getAsJsonObject();

            if (field.contains("[")) {  // إذا كان الحقل يشير إلى مصفوفة
                String arrayName = field.substring(0, field.indexOf("["));
                int index = Integer.parseInt(field.substring(field.indexOf("[") + 1, field.indexOf("]")));

                JsonArray jsonArray = jsonData.getAsJsonArray(arrayName);
                if (index < jsonArray.size()) {
                    return jsonArray.get(index).getAsString();
                } else {
                    throw new IndexOutOfBoundsException("Index out of range for " + arrayName);
                }
            } else {
                return jsonData.get(field).getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error : NO DATA"; // إرجاع قيمة فارغة في حالة حدوث خطأ
        }
    }
}
