package Pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static AutomationFramework.Utilities.Utility.clickElement;
import static AutomationFramework.Utilities.Utility.sendData;

public class P6_AddressPage  {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public P6_AddressPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // 1) زر إضافة عنوان جديد
    private final By addNewAddressButton = By.cssSelector(
            "a[data-csa-c-slot-id='add-new-address-non-mobile-tango-sasp-zero-address']"
    );

    // 3) الحقول الأساسية
    private final By fullNameInput = By.id("address-ui-widgets-enterAddressFullName");
    private final By phoneInput = By.id("address-ui-widgets-enterAddressPhoneNumber");
    private final By streetInput = By.id("address-ui-widgets-enterAddressLine1");
    private final By buildingInput = By.id("address-ui-widgets-enter-building-name-or-number");
    private final By cityInput = By.id("address-ui-widgets-enterAddressCity");
    private final By landmarkInput = By.id("address-ui-widgets-landmark");

    private final By homeAddressRadio = By.id("address-ui-widgets-addr-details-res-radio-input");
    private final By officeAddressRadio = By.id("address-ui-widgets-addr-details-com-radio-input");

    private final By useAsDefaultCheckbox = By.id("address-ui-widgets-use-as-my-default");

    private final By useThisAddressButton = By.cssSelector(
            "#checkout-primary-continue-button-id input.a-button-input"
    );

    // --------- Actions ---------

    @Step("Open 'Add new address' popup")
    public P6_AddressPage openAddNewAddressPopup() {
        clickElement(driver, addNewAddressButton);
        return this;
    }

    @Step("Enter full name: {fullName}")
    public P6_AddressPage enterFullName(String fullName) {
        sendData(driver, fullNameInput, fullName);
        return this;
    }

    @Step("Enter phone number: {phone}")
    public P6_AddressPage enterPhone(String phone) {
        sendData(driver, phoneInput, phone);
        return this;
    }

    @Step("Enter street: {street}")
    public P6_AddressPage enterStreet(String street) {
        sendData(driver, streetInput, street);
        return this;
    }

    @Step("Enter building: {building}")
    public P6_AddressPage enterBuilding(String building) {
        sendData(driver, buildingInput, building);
        return this;
    }

    @Step("Enter city: {city}")
    public P6_AddressPage enterCity(String city) {
        sendData(driver, cityInput, city);
        return this;
    }

    @Step("Enter landmark: {landmark}")
    public P6_AddressPage enterLandmark(String landmark) {
        sendData(driver, landmarkInput, landmark);
        return this;
    }

    @Step("Select address type: Home")
    public P6_AddressPage selectHomeAddressType() {
        clickElement(driver, homeAddressRadio);
        return this;
    }

    @Step("Select address type: Office")
    public P6_AddressPage selectOfficeAddressType() {
        clickElement(driver, officeAddressRadio);
        return this;
    }

    @Step("Mark this address as default")
    public P6_AddressPage markAsDefaultAddress() {
        clickElement(driver, useAsDefaultCheckbox);
        return this;
    }

    @Step("Click 'Use this address' and go to payment page")
    public P7_PaymentPage clickUseThisAddress() {
        clickElement(driver, useThisAddressButton);
        return new P7_PaymentPage(driver);
    }

    // Helper method لتجميع كل الخطوات في Method واحدة
    @Step("Add new address and use it for checkout")
    public P7_PaymentPage addNewAddress(
            String fullName,
            String phone,
            String street,
            String building,
            String city,
            String landmark,
            boolean isHome,
            boolean setAsDefault
    ) {
        openAddNewAddressPopup();
        enterFullName(fullName);
        enterPhone(phone);
        enterStreet(street);
        enterBuilding(building);
        enterCity(city);
        enterLandmark(landmark);

        if (isHome) {
            selectHomeAddressType();
        } else {
            selectOfficeAddressType();
        }

        if (setAsDefault) {
            markAsDefaultAddress();
        }

        clickUseThisAddress();   // بعده المفروض يفتح صفحة الدفع

        return new P7_PaymentPage(driver);
    }

}
