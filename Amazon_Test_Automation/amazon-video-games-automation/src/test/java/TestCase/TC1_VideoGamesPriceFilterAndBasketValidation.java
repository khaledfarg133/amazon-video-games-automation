package TestCase;

import Base.BaseTest;
import Pages.*;
import Pages.P3_ProductListingPage.ListingResult;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static AutomationFramework.Utilities.DataUtils.getJsonValue;

public class TC1_VideoGamesPriceFilterAndBasketValidation extends BaseTest {

    @Test
    public void userCanFilterVideoGames_AddEligibleProductsToCart_AndCompleteCheckoutFlow() throws IOException {
        // 1) Login
        loginWithValidCredentials();

        // 2) Navigate + Filter
        navigateToVideoGamesAndApplyFilter();

        // 3) Listing + Add to Cart
        ListingResult listingResult = collectEligibleProductsAndAddToCart();

        // 4) Basket + Address + Payment
        proceedFromBasketToAddressAndPayment(listingResult);
    }

    // ======================
    // 1) Login Step
    // ======================
    @Step("Login with valid Amazon credentials")
    private void loginWithValidCredentials() throws IOException {
        P1_LoginPage loginPage = new P1_LoginPage(getDriver());


        String phone    = getJsonValue("LoginData", "LoginData.Phone");
        String password = getJsonValue("LoginData", "LoginData.Password");

        // لو أنت عامل getJsonValue يتعامل مع path كامل زي "LoginData.Phone"
        // يبقى رجّعها زى ما كانت:
        // String phone    = getJsonValue("LoginData", "LoginData.Phone");
        // String password = getJsonValue("LoginData", "LoginData.Password");

        loginPage.loginAccount(phone, password);
    }

    // ======================
    // 2) Navigation + Filter
    // ======================
    @Step("Navigate to All Video Games and apply listing filters")
    private void navigateToVideoGamesAndApplyFilter() {
        P2_HomePage homePage = new P2_HomePage(getDriver());
        homePage.Navigate_To_All_VideoGame();
        homePage.Select_Filter();
    }

    // ======================
    // 3) Listing + Add to Cart
    // ======================
    @Step("Collect all eligible products under configured price limit and add them to cart")
    private ListingResult collectEligibleProductsAndAddToCart() {
        P3_ProductListingPage productListingPage = new P3_ProductListingPage(getDriver());
        return productListingPage.readCardsDataAndAddToCart();
    }

    // ======================
    // 4) Basket + Address + Payment
    // ======================
    @Step("Open basket, validate content, then go to address and payment pages")
    private void proceedFromBasketToAddressAndPayment(ListingResult listingResult) throws IOException {

        P5_BasketPage basketPage = new P5_BasketPage(getDriver());

        // افتح السلة في تاب جديد
        String originalWindow = basketPage.openBasketInNewTabAndSwitch();

        // اقرأ المنتجات وتحقق من الـ subtotal
        List<String> basketProducts = basketPage.readBasketItemsAndValidateSubtotal();

        // قارن القوائم الثلاثة (فلتر / Added / Basket)
        basketPage.compareThreeLists(
                listingResult.getEligibleProducts(),
                listingResult.getAddedToCartProducts(),
                basketProducts
        );

        // من السلة → Proceed to checkout → صفحة العنوان
        P6_AddressPage addressPage = basketPage.proceedToCheckout();


        String fullName = getJsonValue("AddressData", "AddressData.FullName");
        String phone    = getJsonValue("AddressData", "AddressData.Phone");
        String street   = getJsonValue("AddressData", "AddressData.Street");
        String building = getJsonValue("AddressData", "AddressData.Building");
        String city     = getJsonValue("AddressData", "AddressData.City");
        String landmark = getJsonValue("AddressData", "AddressData.Landmark");

        // إضافة العنوان الجديد واستخدامه كعنوان افتراضي → ينتقل لصفحة الدفع
        P7_PaymentPage paymentPage = addressPage.addNewAddress(
                fullName,
                phone,
                street,
                building,
                city,
                landmark,
                true,   // isHome
                true    // setAsDefault
        );

        // تأكيد أننا على صفحة الدفع
        paymentPage.verifyOnPaymentPage();

        // رجّع للويندو الأصلي (اختياري)
        basketPage.closeBasketTabAndReturn(originalWindow);
    }
}
