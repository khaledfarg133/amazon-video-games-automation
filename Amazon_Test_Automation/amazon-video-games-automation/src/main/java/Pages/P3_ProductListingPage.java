package Pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import static AutomationFramework.Utilities.DataUtils.getJsonValue;
import static AutomationFramework.Utilities.Utility.*;

public class P3_ProductListingPage {

    public static int basketSize;
    private final WebDriver driver;

    public P3_ProductListingPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Simple DTO used by the test to access products that matched the price filter
     * and the products that were actually added to the cart.
     */
    public static class ListingResult {
        private final List<String> eligibleProducts;
        private final List<String> addedToCartProducts;

        public ListingResult(List<String> eligibleProducts, List<String> addedToCartProducts) {
            this.eligibleProducts = eligibleProducts;
            this.addedToCartProducts = addedToCartProducts;
        }

        public List<String> getEligibleProducts() {
            return eligibleProducts;
        }

        public List<String> getAddedToCartProducts() {
            return addedToCartProducts;
        }
    }

    // Pagination "Next" button
    private final By buttonNext = By.xpath("(//a[normalize-space()='Next'])[1]");

    // Root locator for all listing result cards
    private final By allCardsLocator = By.xpath("//div[@data-component-type='s-search-result']");

    // Cart item count in header
    private final By cartCountLocator = By.xpath("(//span[@id='nav-cart-count'])[1]");

    // =========================
    // 1) Product title (name)
    // =========================

    private By getCardNameLocator(int cardNumber) {
        String selector = String.format(
                "(//div[@data-component-type='s-search-result'])[%d]" +
                        "//div[@data-cy='title-recipe']//h2",
                cardNumber
        );
        return By.xpath(selector);
    }

    public String getCardName(int cardNumber) {
        By cardNameSelector = getCardNameLocator(cardNumber);
        String name = getText(driver, cardNameSelector);
        System.out.println("Card name: " + name);
        return name;
    }

    private By getCardRootLocator(int cardNumber) {
        String xpath = String.format(
                "(//div[@data-component-type='s-search-result'])[%d]",
                cardNumber
        );
        return By.xpath(xpath);
    }

    // Add to cart button on listing card
    private By getCardAddToCartButtonLocator(int cardNumber) {
        String xpath = String.format(
                "(//div[@data-component-type='s-search-result'])[%d]" +
                        "//div[@data-cy='add-to-cart']" +
                        "//button[@type='button' and @name='submit.addToCart']",
                cardNumber
        );
        return By.xpath(xpath);
    }

    // Product title link used to open product details page
    private By getCardTitleLinkLocator(int cardNumber) {
        String xpath = String.format(
                "(//div[@data-component-type='s-search-result'])[%d]" +
                        "//div[@data-cy='title-recipe']//a",
                cardNumber
        );
        return By.xpath(xpath);
    }

    // =========================
    // 2) Price locator with fallbacks
    // =========================

    private By getCardPriceLocator(int cardNumber) {

        String priceXpath1 = String.format(
                "(//div[@data-component-type='s-search-result'])[%d]" +
                        "//div[@data-cy='price-recipe']" +
                        "//span[contains(@class,'a-price') and not(contains(@class,'a-text-price'))][1]" +
                        "/span[@aria-hidden='true']",
                cardNumber
        );

        String priceXpath2 = String.format(
                "(//div[@data-component-type='s-search-result'])[%d]" +
                        "//div[@data-cy='secondary-offer-recipe']" +
                        "//span[contains(@class,'a-color-base') and contains(normalize-space(),'EGP')][1]",
                cardNumber
        );

        String priceXpath3 = String.format(
                "(//div[@data-component-type='s-search-result'])[%d]" +
                        "//span[" +
                        "contains(normalize-space(),'EGP') " +
                        "or contains(normalize-space(),'USD') " +
                        "or contains(normalize-space(),'$')" +
                        "][1]",
                cardNumber
        );

        By locator1 = By.xpath(priceXpath1);
        By locator2 = By.xpath(priceXpath2);
        By locator3 = By.xpath(priceXpath3);

        if (!driver.findElements(locator1).isEmpty()) {
            System.out.println("Using priceXpath1 for card " + cardNumber);
            return locator1;
        }

        if (!driver.findElements(locator2).isEmpty()) {
            System.out.println("Using priceXpath2 for card " + cardNumber);
            return locator2;
        }

        if (!driver.findElements(locator3).isEmpty()) {
            System.out.println("Using priceXpath3 for card " + cardNumber);
            return locator3;
        }

        System.out.println("No price found for card " + cardNumber + " using any locator.");
        return locator3;
    }

    // Legacy version kept as a backup if needed
    public double getCardPrice0(int cardNumber) {
        By cardPriceSelector = getCardPriceLocator(cardNumber);
        String priceText = getText(driver, cardPriceSelector);

        System.out.println("Raw price text: " + (priceText == null ? "null" : priceText.replace("\n", "\\n")));

        if (priceText == null || priceText.trim().isEmpty()) {
            return 0.0;
        }

        String[] lines = priceText.split("\\R+");
        String majorLine = lines[0].trim();
        String minorLine = (lines.length > 1) ? lines[1].trim() : "";

        System.out.println("Major line: " + majorLine + " | Minor line: " + minorLine);

        String majorDigits = majorLine.replaceAll("[^0-9]", "");
        String minorDigits = minorLine.replaceAll("[^0-9]", "");

        if (majorDigits.isEmpty()) {
            System.out.println("Major digits empty for card " + cardNumber);
            return 0.0;
        }

        if (minorDigits.isEmpty()) {
            minorDigits = "00";
        }

        if (minorDigits.length() == 1) {
            minorDigits = minorDigits + "0";
        }

        String combined = majorDigits + "." + minorDigits.substring(0, 2);

        try {
            double value = Double.parseDouble(combined);
            System.out.println("Parsed price for card " + cardNumber + " = " + value +
                    " (major=" + majorDigits + ", minor=" + minorDigits + ")");
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Failed to parse price for card " + cardNumber + " | combined: " + combined);
            return 0.0;
        }
    }

    /**
     * Reads price from listing card. Supports both one-line and two-line price formats.
     */
    @Step("Read price for listing card #{0}")
    public double getCardPrice(int cardNumber) {
        By cardPriceSelector = getCardPriceLocator(cardNumber);
        String priceText = getText(driver, cardPriceSelector);

        System.out.println("Raw price text: " + (priceText == null ? "null" : priceText.replace("\n", "\\n")));

        if (priceText == null || priceText.trim().isEmpty()) {
            return 0.0;
        }

        String[] lines = priceText.split("\\R+");

        // Single-line price (e.g. "EGP 13,899.00")
        if (lines.length == 1) {
            String line = lines[0].trim();
            System.out.println("Single-line price: " + line);

            String cleaned = line.replaceAll("[^0-9.,]", "");
            if (cleaned.isEmpty()) {
                System.out.println("Cleaned single-line price is empty for card " + cardNumber);
                return 0.0;
            }

            cleaned = cleaned.replace(",", "");

            try {
                double value = Double.parseDouble(cleaned);
                System.out.println("Parsed single-line price for card " + cardNumber + " = " + value +
                        " | cleaned=" + cleaned);
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Failed to parse single-line price for card " + cardNumber + " | cleaned: " + cleaned);
                return 0.0;
            }
        }

        // Two-line price (major + minor)
        String majorLine = lines[0].trim();
        String minorLine = (lines.length > 1) ? lines[1].trim() : "";

        System.out.println("Major line: " + majorLine + " | Minor line: " + minorLine);

        String majorDigits = majorLine.replaceAll("[^0-9]", "");
        String minorDigits = minorLine.replaceAll("[^0-9]", "");

        if (majorDigits.isEmpty()) {
            System.out.println("Major digits empty for card " + cardNumber);
            return 0.0;
        }

        if (minorDigits.isEmpty()) {
            minorDigits = "00";
        }

        if (minorDigits.length() == 1) {
            minorDigits = minorDigits + "0";
        }

        String combined = majorDigits + "." + minorDigits.substring(0, 2);

        try {
            double value = Double.parseDouble(combined);
            System.out.println("Parsed multi-line price for card " + cardNumber + " = " + value +
                    " (major=" + majorDigits + ", minor=" + minorDigits + ")");
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Failed to parse multi-line price for card " + cardNumber + " | combined: " + combined);
            return 0.0;
        }
    }

    // =========================
    // 3) Cart count helpers
    // =========================

    private int getCartCountSafe() {
        try {
            WebElement el = driver.findElement(cartCountLocator);
            String txt = el.getText();
            if (txt == null) return 0;

            txt = txt.trim();
            if (txt.isEmpty()) {
                return 0;
            }

            int value = Integer.parseInt(txt);
            System.out.println("Current cart count: " + value);
            return value;
        } catch (Exception e) {
            System.out.println("Cannot read cart count: " + e.getMessage());
            return 0;
        }
    }

    private boolean waitForCartCountIncrease(int oldCount, int timeoutSeconds, String debugContext) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

            return wait.until(d -> {
                int current = getCartCountSafe();
                System.out.println(debugContext + " | waiting for cart count to increase: old="
                        + oldCount + " | current=" + current);
                return current > oldCount;
            });
        } catch (TimeoutException e) {
            System.out.println(debugContext + " | timeout while waiting for cart count increase from " + oldCount);
            return false;
        }
    }

    private boolean clickAddToCartAndConfirm(By addToCartLocator, String debugContext) {

        int before = getCartCountSafe();
        int maxAttempts = 3;
        int timeoutSeconds = 5;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            System.out.println(debugContext + " | add to cart attempt #" + attempt
                    + " | cartCountBefore=" + before);

            clickElement(driver, addToCartLocator);

            boolean increased = waitForCartCountIncrease(before, timeoutSeconds, debugContext);

            if (increased) {
                int after = getCartCountSafe();
                System.out.println(debugContext + " | cart count increased: " + before + " -> " + after);
                return true;
            } else {
                System.out.println(debugContext + " | cart count did not increase after attempt #" + attempt);
            }
        }

        System.out.println(debugContext + " | failed to confirm add to cart after "
                + maxAttempts + " attempts.");
        return false;
    }

    // =========================
    // 4) Add to cart (listing first, then details)
    // =========================

    @Step("Add listing card #{0} to cart (from listing or product details page)")
    public boolean addCardToCart_HandleCardOrDetails(int cardNumber) {
        System.out.println("Trying to add card " + cardNumber + " to cart");

        // Try add to cart button on listing card
        By atcOnCard = getCardAddToCartButtonLocator(cardNumber);

        List<WebElement> atcButtons = driver.findElements(atcOnCard);

        if (!atcButtons.isEmpty()) {
            System.out.println("Add to cart button found on listing for card " + cardNumber);
            return clickAddToCartAndConfirm(atcOnCard, "LISTING card " + cardNumber);
        }

        // Fallback: open product details in a new tab and try from there
        System.out.println("No add to cart button on listing for card " + cardNumber +
                ". Opening product in new tab.");

        String originalWindow = driver.getWindowHandle();
        Set<String> oldWindows = new HashSet<>(driver.getWindowHandles());

        By titleLink = getCardTitleLinkLocator(cardNumber);
        WebElement linkElement = driver.findElement(titleLink);
        String href = linkElement.getAttribute("href");

        ((JavascriptExecutor) driver).executeScript("window.open(arguments[0], '_blank');", href);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> d.getWindowHandles().size() > oldWindows.size());

        Set<String> newWindows = new HashSet<>(driver.getWindowHandles());
        newWindows.removeAll(oldWindows);

        if (newWindows.isEmpty()) {
            System.out.println("Failed to detect new tab for card " + cardNumber);
            return false;
        }

        String newTabHandle = newWindows.iterator().next();

        driver.switchTo().window(newTabHandle);
        System.out.println("Switched to product details tab for card " + cardNumber);

        P4_ProductDetailsPage detailsPage = new P4_ProductDetailsPage(driver);
        boolean added = detailsPage.clickAddToCartOnDetails();

        try {
            driver.close();
            System.out.println("Closed product details tab for card " + cardNumber);
        } catch (Exception e) {
            System.out.println("Could not close details tab for card " + cardNumber + " | " + e.getMessage());
        }

        try {
            driver.switchTo().window(originalWindow);
            System.out.println("Switched back to listing tab.");
        } catch (Exception e) {
            System.out.println("Could not switch back to original window after card " + cardNumber + " | " + e.getMessage());
        }

        return added;
    }

    // =========================
    // 5) Reading cards, filtering by price, adding to cart
    // =========================

    public static int getListSize(List<?> list, String listName) {
        int size = (list == null) ? 0 : list.size();
        System.out.println(listName + " size = " + size);
        return size;
    }

    public void readCardsDataAndAddToCart0() {
        String maxNoOfPage = getJsonValue("LoginData", "NumberOfPage_DoYouWantToTest.Number");
        int Max_NO_OF_Page = Integer.parseInt(maxNoOfPage);

        String value = getJsonValue("LoginData", "NumberOfPage_DoYouWantToTest.priceLimit");
        double priceLimit = Double.parseDouble(value);

        List<String> eligibleProducts = new ArrayList<>();
        List<String> addedToCartProducts = new ArrayList<>();

        for (int i = 1; i <= Max_NO_OF_Page; i++) {

            int cardsCount = driver.findElements(allCardsLocator).size();
            System.out.println("Page " + i + " | found " + cardsCount + " cards");

            for (int j = 1; j <= 16; j++) {
                System.out.println("Card index on page " + i + ": " + j);

                try {
                    String name = getCardName(j);
                    double price = getCardPrice(j);
                    System.out.printf("Raw -> Page %d | Card %d | name=%s | price=%.2f%n", i, j, name, price);

                    if (price == 0.0) {
                        System.out.println("No valid price for page " + i + " | card " + j);
                        continue;
                    }

                    if (price > 0 && price <= priceLimit) {
                        String key = buildProductKey(name, price);

                        String debugInfo = String.format(
                                "Page %d | Card %02d => %s",
                                i, j, key
                        );
                        System.out.println(debugInfo);

                        eligibleProducts.add(key);

                        boolean added = addCardToCart_HandleCardOrDetails(j);

                        if (added) {
                            addedToCartProducts.add(key);
                            System.out.println("Added to cart: " + debugInfo);
                        } else {
                            System.out.println("Matched filter (<= " + priceLimit + ") but not added to cart: " + debugInfo);
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Error reading card on page " + i + " | card " + j + " | " + e.getMessage());
                }
            }

            if (i < Max_NO_OF_Page) {

                if (driver.findElements(buttonNext).isEmpty()) {
                    System.out.println("No Next button found. Stopping pagination.");
                    break;
                }

                try {
                    scrollToElement(driver, buttonNext);
                    clickElement(driver, buttonNext);
                } catch (Exception e) {
                    System.out.println("Cannot click Next or no more pages: " + e.getMessage());
                    break;
                }
            }
        }

        System.out.println("\n======= PRODUCTS UNDER " + priceLimit + " (FILTER RESULT) =======");
        for (String item : eligibleProducts) {
            System.out.println(item);
        }

        System.out.println("\n======= PRODUCTS ACTUALLY ADDED TO CART =======");
        for (String item : addedToCartProducts) {
            System.out.println(item);
        }

        basketSize = getListSize(addedToCartProducts, "addedToCartProducts");
        System.out.println("Basket_Size: found " + basketSize + " active cards.");

        P5_BasketPage basketPage = new P5_BasketPage(driver);
        String originalWindow = basketPage.openBasketInNewTabAndSwitch();

        List<String> basketProducts = basketPage.readBasketItemsAndValidateSubtotal();

        basketPage.closeBasketTabAndReturn(originalWindow);

        basketPage.compareThreeLists(eligibleProducts, addedToCartProducts, basketProducts);

        System.out.println("====================================");
    }

    @Step("Scan listing pages, filter products by price limit, and add matching products to cart")
    public ListingResult readCardsDataAndAddToCart() {
        String maxNoOfPage = getJsonValue("LoginData", "NumberOfPage_DoYouWantToTest.Number");
        int Max_NO_OF_Page = Integer.parseInt(maxNoOfPage);

        String value = getJsonValue("LoginData", "NumberOfPage_DoYouWantToTest.priceLimit");
        double priceLimit = Double.parseDouble(value);

        List<String> eligibleProducts = new ArrayList<>();
        List<String> addedToCartProducts = new ArrayList<>();

        for (int i = 1; i <= Max_NO_OF_Page; i++) {

            int cardsCount = driver.findElements(allCardsLocator).size();
            System.out.println("Page " + i + " | found " + cardsCount + " cards");

            for (int j = 1; j <= 16; j++) {
                System.out.println("Card index on page " + i + ": " + j);

                try {
                    String name = getCardName(j);
                    double price = getCardPrice(j);
                    System.out.printf("Raw -> Page %d | Card %d | name=%s | price=%.2f%n", i, j, name, price);

                    if (price == 0.0) {
                        System.out.println("No valid price for page " + i + " | card " + j);
                        continue;
                    }

                    if (price > 0 && price <= priceLimit) {
                        String key = buildProductKey(name, price);

                        String debugInfo = String.format(
                                "Page %d | Card %02d => %s",
                                i, j, key
                        );
                        System.out.println(debugInfo);

                        eligibleProducts.add(key);

                        boolean added = addCardToCart_HandleCardOrDetails(j);

                        if (added) {
                            addedToCartProducts.add(key);
                            System.out.println("Added to cart: " + debugInfo);
                        } else {
                            System.out.println("Matched filter (<= " + priceLimit + ") but not added to cart: " + debugInfo);
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Error reading card on page " + i + " | card " + j + " | " + e.getMessage());
                }
            }

            if (i < Max_NO_OF_Page) {

                if (driver.findElements(buttonNext).isEmpty()) {
                    System.out.println("No Next button found. Stopping pagination.");
                    break;
                }

                try {
                    scrollToElement(driver, buttonNext);
                    clickElement(driver, buttonNext);
                } catch (Exception e) {
                    System.out.println("Cannot click Next or no more pages: " + e.getMessage());
                    break;
                }
            }
        }

        System.out.println("\n======= PRODUCTS UNDER " + priceLimit + " (FILTER RESULT) =======");
        for (String item : eligibleProducts) {
            System.out.println(item);
        }

        System.out.println("\n======= PRODUCTS ACTUALLY ADDED TO CART =======");
        for (String item : addedToCartProducts) {
            System.out.println(item);
        }

        basketSize = getListSize(addedToCartProducts, "addedToCartProducts");
        System.out.println("Basket_Size: found " + basketSize + " active cards.");

        return new ListingResult(eligibleProducts, addedToCartProducts);
    }
}
