package Pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static AutomationFramework.Utilities.Utility.clickElement;

public class P2_HomePage {
    private final WebDriver driver;
    WebDriverWait wait;


    public P2_HomePage(WebDriver driver) {
        this.driver = driver;

        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    }


    //Web Elements
    By open_All_menu = By.xpath("(//i[@class='hm-icon nav-sprite'])[1]");
    By Click_On_SeeAll = By.cssSelector("a[aria-label='See All Categories'] div");
    By Click_On_VideoGames = By.xpath("//a[@role='button']//div[contains(text(),'Video Games')]");
    By Chose_All_Video_Games = By.xpath("(//a[normalize-space()='All Video Games'])[1]");

    By Filter_FreeShipping = By.xpath("(//i[@class='a-icon a-icon-checkbox'])[1]");
    By Filter_New = By.cssSelector("a[aria-label='Apply the filter New to narrow results'] span[class='a-size-base a-color-base']");


    By Button_SortBy = By.cssSelector("#a-autoid-0-announce");
    By HighToLow_Selected = By.cssSelector("#s-result-sort-select_2");


    //Methods



    @Step("Navigate from home page to 'All Video Games' listing")
    public void Navigate_To_All_VideoGame() {
        clickElement(driver,open_All_menu);
        clickElement(driver,Click_On_SeeAll);
        clickElement(driver,Click_On_VideoGames);
        clickElement(driver,Chose_All_Video_Games);


//        sendData(driver,PhoneFiled,phoneFiled);

    }

    @Step("Apply filters: Free Shipping, New condition, sort by Price: High to Low")
    public void Select_Filter() {
        clickElement(driver,Filter_FreeShipping);
        clickElement(driver,Filter_New);
        clickElement(driver,Button_SortBy);
        clickElement(driver,HighToLow_Selected);



    }
}
