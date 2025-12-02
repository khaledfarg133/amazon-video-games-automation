package TestCase;

import AutomationFramework.listeners.TestNGListeners;
import Base.BaseTest;
import Pages.P1_LoginPage;
import Pages.P2_HomePage;
import Pages.P3_ProductListingPage;
import Pages.P5_BasketPage;
import Pages.P3_ProductListingPage.ListingResult;

import io.qameta.allure.Step;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static AutomationFramework.Utilities.DataUtils.getJsonValue;

//@Listeners(TestNGListeners.class)
public class TC1_VideoGamesPriceFilterAndBasketValidation extends BaseTest {

    @Test

    public void userCanFilterVideoGames_AddEligibleProductsToCart_AndValidateBasket() throws IOException {
        loginWithValidCredentials();
        navigateToVideoGamesAndApplyFilter();

        ListingResult listingResult = collectEligibleProductsAndAddToCart();

        validateBasketContentAndSubtotal(listingResult);
    }

    // ======================
    // 1) Login Step
    // ======================
    @Step("Login with valid Amazon credentials")
    private void loginWithValidCredentials() throws IOException {
        P1_LoginPage loginPage = new P1_LoginPage(getDriver());
        String phone = getJsonValue("LoginData", "LoginData.Phone");
        String password = getJsonValue("LoginData", "LoginData.Password");

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
    // 4) Basket Validation
    // ======================
    @Step("Validate basket content and subtotal against filtered listing result")
    private void validateBasketContentAndSubtotal(ListingResult listingResult) {
        P5_BasketPage basketPage = new P5_BasketPage(getDriver());

        String originalWindow = basketPage.openBasketInNewTabAndSwitch();

        List<String> basketProducts = basketPage.readBasketItemsAndValidateSubtotal();

        basketPage.closeBasketTabAndReturn(originalWindow);

        basketPage.compareThreeLists(
                listingResult.getEligibleProducts(),
                listingResult.getAddedToCartProducts(),
                basketProducts
        );
    }
}
