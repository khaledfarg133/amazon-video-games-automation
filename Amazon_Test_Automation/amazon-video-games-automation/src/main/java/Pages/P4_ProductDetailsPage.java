package Pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static AutomationFramework.Utilities.Utility.*;

public class P4_ProductDetailsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public P4_ProductDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Main "Add to Cart" button on details page
    private final By mainAddToCartButton = By.id("add-to-cart-button");

    // "See All Buying Options" button
    private final By seeAllBuyingOptionsButton =
            By.xpath("//span[normalize-space()='See All Buying Options']/ancestor::span[contains(@class,'a-button')][1]");

    // First "Add to Cart" button in buying options
    private final By firstBuyingOptionAddToCartButton =
            By.xpath("(//input[@name='submit.addToCart' or @name='submit.add-to-cart' or @value='Add to Basket'])[1]");

    // "Added to cart" header shown after successful add
    private final By addedToCartHeader =
            By.xpath("(//h1[normalize-space()='Added to cart'])[1]");

    /**
     * Tries to add product to cart from details page:
     * 1) Main Add to Cart button if available.
     * 2) If not, uses "See All Buying Options" and then first add-to-cart.
     * Success is confirmed only if "Added to cart" header appears.
     */
    @Step("Add product to cart from product details page")
    public boolean clickAddToCartOnDetails() {
        System.out.println("Trying to add product to cart from product details page");

        try {
            // Try main Add to Cart
            if (!driver.findElements(mainAddToCartButton).isEmpty()) {
                System.out.println("Main Add to Cart button found on details page.");
                scrollToElement(driver, mainAddToCartButton);
                clickElement(driver, mainAddToCartButton);

                if (waitForAddedToCartHeader()) {
                    System.out.println("'Added to cart' confirmation appeared (main button).");
                    return true;
                } else {
                    System.out.println("No 'Added to cart' confirmation after clicking main Add to Cart.");
                    return false;
                }
            }

            System.out.println("No direct Add to Cart button on details page.");

            // Fallback: use "See All Buying Options"
            if (!driver.findElements(seeAllBuyingOptionsButton).isEmpty()) {
                System.out.println("Trying 'See All Buying Options' flow.");
                scrollToElement(driver, seeAllBuyingOptionsButton);
                clickElement(driver, seeAllBuyingOptionsButton);

                wait.until(ExpectedConditions.presenceOfElementLocated(firstBuyingOptionAddToCartButton));
                clickElement(driver, firstBuyingOptionAddToCartButton);

                if (waitForAddedToCartHeader()) {
                    System.out.println("'Added to cart' confirmation appeared (buying options flow).");
                    return true;
                } else {
                    System.out.println("No 'Added to cart' confirmation after buying options Add to Cart.");
                    return false;
                }
            }

            System.out.println("Neither Add to Cart nor 'See All Buying Options' is available on details page.");
            return false;

        } catch (Exception e) {
            System.out.println("Exception while trying to add to cart from details page: " + e.getMessage());
            return false;
        }
    }

    /**
     * Waits until the "Added to cart" header appears.
     * Returns true on success, false on timeout.
     */
    @Step("Wait for 'Added to cart' confirmation header to appear")
    private boolean waitForAddedToCartHeader() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(addedToCartHeader));
            return true;
        } catch (TimeoutException e) {
            System.out.println("'Added to cart' header did not appear within timeout.");
            return false;
        }
    }
}
