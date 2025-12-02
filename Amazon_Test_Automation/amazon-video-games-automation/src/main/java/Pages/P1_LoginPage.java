package Pages;

import AutomationFramework.Utilities.Waits;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

import static AutomationFramework.Utilities.DataUtils.getJsonValue;
import static AutomationFramework.Utilities.Utility.clickElement;
import static AutomationFramework.Utilities.Utility.sendData;

public class P1_LoginPage {
    private final WebDriver driver;
    public P1_LoginPage(WebDriver driver) {

        this.driver = driver;
    }

    //Web Elements
    By loginButton = By.cssSelector("div[id='nav-link-accountList'] a:nth-child(1)");

    By PhoneFiled = By.cssSelector("#ap_email_login");
    By Continue_Phone_Button = By.cssSelector("input[type='submit']");
    By Cuntory_ddl_Button = By.cssSelector(".a-button-text.a-declarative");
    By Cuntory_Button = By.xpath("//span[normalize-space()='+20']");


    By PasswordFiled =By.cssSelector("#ap_password");
    By Sign_in_Button = By.cssSelector("#signInSubmit");



    //Methods



    @Step("Login to Amazon account with phone [{0}]")
    public void loginAccount(String phoneFiled  , String passwordFiled) {

        clickElement(driver,loginButton);
        sendData(driver,PhoneFiled,phoneFiled);
        System.out.println("phoneFiled = " + phoneFiled); // التحقق من قيمة j
        clickElement(driver,Cuntory_ddl_Button);
        clickElement(driver,Cuntory_Button);

        clickElement(driver,Continue_Phone_Button);
        sendData(driver,PasswordFiled,passwordFiled);
        System.out.println("passwordFiled = " + passwordFiled); // التحقق من قيمة j

        clickElement(driver,Sign_in_Button);

    }


}
