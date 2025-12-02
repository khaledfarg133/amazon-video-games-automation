package Pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

import static AutomationFramework.Utilities.Utility.*;
import static Pages.P3_ProductListingPage.basketSize;

public class P5_BasketPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public P5_BasketPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // =========================
    // Basket locators
    // =========================

    // Active cart items (cards with name + price + quantity)
    private final By activeItemsLocator = By.xpath(
            "//div[@id='sc-active-cart']" +
                    "//div[contains(@class,'sc-list-item-content-inner') and contains(@class,'a-grid-top')]"
    );

    // Subtotal amount displayed in UI
    private final By subtotalLocator = By.xpath(
            "//span[@id='sc-subtotal-amount-buybox']/span | " +
                    "//div[@id='sc-buy-box']//*[contains(@id,'sc-subtotal-amount')]/span"
    );

    // "Go to basket" button from overlay after Add to cart
    private final By btnGoToBasket = By.xpath(
            "//a[normalize-space()='Go to basket' and contains(@href, '/cart')]"
    );

    // Cart icon in header (fallback)
    private final By btnNavCart = By.id("nav-cart");

    // Simple model for basket item
    public static class BasketItem {
        private final String name;
        private final double unitPrice;
        private final int quantity;
        private final double lineTotal;

        public BasketItem(String name, double unitPrice, int quantity, double lineTotal) {
            this.name = name;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.lineTotal = lineTotal;
        }

        public String getName() {
            return name;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getLineTotal() {
            return lineTotal;
        }

        @Override
        public String toString() {
            return name + " | " + unitPrice;
        }
    }

    // =========================
    // 1) Open / close basket tab
    // =========================

    /**
     * Opens basket page in a new tab.
     * Uses "Go to basket" if available; otherwise header cart icon.
     *
     * @return original window handle to be used for switching back.
     */
    @Step("Open basket page in a new browser tab")
    public String openBasketInNewTabAndSwitch() {
        System.out.println("Opening basket page in new tab...");

        String originalWindow = driver.getWindowHandle();
        Set<String> oldWindows = new HashSet<>(driver.getWindowHandles());

        By locatorToUse;
        if (!driver.findElements(btnGoToBasket).isEmpty()) {
            locatorToUse = btnGoToBasket;
            System.out.println("Using 'Go to basket' button.");
        } else {
            locatorToUse = btnNavCart;
            System.out.println("'Go to basket' not found. Using header cart icon.");
        }

        WebElement element = driver.findElement(locatorToUse);
        String href = element.getAttribute("href");

        if (href == null || href.trim().isEmpty()) {
            throw new RuntimeException("Basket href is empty. Cannot open basket in new tab.");
        }

        ((JavascriptExecutor) driver).executeScript("window.open(arguments[0], '_blank');", href);

        wait.until(d -> d.getWindowHandles().size() > oldWindows.size());

        Set<String> allWindows = new HashSet<>(driver.getWindowHandles());
        allWindows.removeAll(oldWindows);

        if (allWindows.isEmpty()) {
            throw new RuntimeException("Failed to detect new basket tab.");
        }

        String newTabHandle = allWindows.iterator().next();
        driver.switchTo().window(newTabHandle);
        System.out.println("Switched to basket tab.");

        return originalWindow;
    }

    /**
     * Closes basket tab and switches back to original window.
     */
    @Step("Close basket tab and switch back to original window")
    public void closeBasketTabAndReturn(String originalWindowHandle) {
        try {
            driver.close();
            System.out.println("Closed basket tab.");
        } catch (Exception e) {
            System.out.println("Failed to close basket tab: " + e.getMessage());
        }

        try {
            driver.switchTo().window(originalWindowHandle);
            System.out.println("Switched back to original window.");
        } catch (Exception e) {
            System.out.println("Failed to switch back to original window: " + e.getMessage());
        }
    }

    // =========================
    // 2) Card-level helpers (by index)
    // =========================

    private WebElement getBasketCardByIndex(int index) {
        String xpath = String.format(
                "(//div[@id='sc-active-cart']" +
                        "//div[contains(@class,'sc-list-item-content-inner') and contains(@class,'a-grid-top')])[%d]",
                index
        );
        return driver.findElement(By.xpath(xpath));
    }

    private String getBasketCardName(int index) {
        WebElement card = getBasketCardByIndex(index);
        String selector = String.format("(//span[@class='a-truncate-cut'])[%d]", index);
        By nameLocator = By.xpath(selector);
        String name = getText(driver, nameLocator);

        return name;
    }

    /**
     * Parses a price string that may contain pounds and cents on separate lines.
     * Example:
     *  - "EGP19,595\n00" -> 19595.00
     *  - "EGP19,595.50"  -> 19595.50
     */
    private double parsePriceWithCents(String rawText, String debugContext) {
        if (rawText == null || rawText.trim().isEmpty()) {
            System.out.println(debugContext + " | raw price text is null/empty");
            return 0.0;
        }

        String normalized = rawText.replace("\r", "");
        System.out.println(debugContext + " | raw price text = " + normalized.replace("\n", "\\n"));

        // Multi-line price: first line = major, second line = minor
        if (normalized.contains("\n")) {
            String[] lines = normalized.split("\\R+");
            String majorLine = lines[0].trim();
            String minorLine = (lines.length > 1) ? lines[1].trim() : "";

            System.out.println(debugContext + " | majorLine='" + majorLine + "', minorLine='" + minorLine + "'");

            String majorDigits = majorLine.replaceAll("[^0-9]", "");
            String minorDigits = minorLine.replaceAll("[^0-9]", "");

            if (majorDigits.isEmpty()) {
                System.out.println(debugContext + " | major digits empty");
                return 0.0;
            }

            if (minorDigits.isEmpty()) {
                minorDigits = "00";
            } else if (minorDigits.length() == 1) {
                minorDigits = minorDigits + "0";
            }

            String combined = majorDigits + "." + minorDigits.substring(0, 2);

            try {
                double value = Double.parseDouble(combined);
                System.out.println(debugContext + " | parsed multi-line price = " + value +
                        " (major=" + majorDigits + ", minor=" + minorDigits + ")");
                return value;
            } catch (NumberFormatException e) {
                System.out.println(debugContext + " | failed to parse combined price: " + combined);
                return 0.0;
            }
        } else {
            // Single-line price
            String cleaned = normalized.replaceAll("[^0-9.,]", "");
            if (cleaned.isEmpty()) {
                System.out.println(debugContext + " | cleaned price is empty for text: " + normalized);
                return 0.0;
            }

            cleaned = cleaned.replace(",", "");

            try {
                double value = Double.parseDouble(cleaned);
                System.out.println(debugContext + " | parsed single-line price = " + value +
                        " | cleaned: " + cleaned);
                return value;
            } catch (NumberFormatException e) {
                System.out.println(debugContext + " | failed to parse single-line price: " + cleaned);
                return 0.0;
            }
        }
    }

    // Legacy version kept as backup
    private double getBasketCardUnitPrice0(int index) {
        WebElement card = getBasketCardByIndex(index);

        String selector = String.format(
                "//div[@id='sc-active-cart']" +
                        "//div[contains(@class,'sc-list-item') and @data-itemtype='active' and @data-item-index='%d']" +
                        "//div[contains(@class,'sc-apex-cart-price')]" +
                        "//span[contains(@class,'a-price') and contains(@class,'apex-price-to-pay-value')]",
                index
        );

        By priceLocator = By.xpath(selector);
        String raw = getText(driver, priceLocator);

        if (raw == null) {
            return 0.0;
        }

        String normalized = raw.replace("\r", "");
        String firstLine = normalized.split("\\R")[0].trim();

        String cleaned = firstLine.replaceAll("[^0-9.,]", "");
        if (cleaned.isEmpty()) {
            return 0.0;
        }

        cleaned = cleaned.replace(",", "");

        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            System.out.println("Failed to parse basket unit price from text: " +
                    raw + " | firstLine: " + firstLine + " | cleaned: " + cleaned);
            return 0.0;
        }
    }

    // Unit price for basket card by index
    private double getBasketCardUnitPrice(int index) {
        WebElement card = getBasketCardByIndex(index);

        String selector = String.format(
                "//div[@id='sc-active-cart']" +
                        "//div[contains(@class,'sc-list-item') and @data-itemtype='active' and @data-item-index='%d']" +
                        "//div[contains(@class,'sc-apex-cart-price')]" +
                        "//span[contains(@class,'a-price') and contains(@class,'apex-price-to-pay-value')]",
                index
        );

        By priceLocator = By.xpath(selector);
        String raw = getText(driver, priceLocator);

        return parsePriceWithCents(raw, "Basket card #" + index + " unitPrice");
    }

    private int getBasketCardQuantity(int index) {
        WebElement card = getBasketCardByIndex(index);

        String q = tryGetText(card,
                By.cssSelector("fieldset[name='sc-quantity'] span[data-a-selector='value']"));

        if (q.isBlank()) {
            q = tryGetText(card, By.cssSelector("span.a-dropdown-prompt"));
        }

        if (q.isBlank()) {
            return 1;
        }

        try {
            return Integer.parseInt(q.trim());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private String tryGetText(WebElement scope, By locator) {
        try {
            WebElement el = scope.findElement(locator);
            String txt = el.getText();
            return txt == null ? "" : txt.trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    // =========================
    // 3) Reading all basket items
    // =========================

    @Step("Read all active basket items (name, unit price, quantity)")
    public List<BasketItem> readActiveItems() {
        List<BasketItem> result = new ArrayList<>();

        int cardsCount = driver.findElements(activeItemsLocator).size();

        System.out.println("Basket: found " + cardsCount + " active cards.");
        System.out.println("Basket_Size value: " + basketSize);

        for (int i = 1; i <= basketSize; i++) {
            try {
                String name = getBasketCardName(i);
                double unitPrice = getBasketCardUnitPrice(i);
                int qty = getBasketCardQuantity(i);
                double lineTotal = unitPrice * qty;

                System.out.printf(
                        "Basket card #%d => %s | unitPrice=%.2f | qty=%d | lineTotal=%.2f%n",
                        i, name, unitPrice, qty, lineTotal
                );

                result.add(new BasketItem(name, unitPrice, qty, lineTotal));

            } catch (Exception e) {
                System.out.println("Failed to read basket card #" + i + " : " + e.getMessage());
            }
        }

        return result;
    }

    // =========================
    // 4) Subtotal (UI vs calculated)
    // =========================

    @Step("Read subtotal value displayed in basket UI")
    public double getBasketSubtotalFromUI() {
        String subtotalText = getText(driver, subtotalLocator);
        System.out.println("Subtotal raw text: " + subtotalText);

        if (subtotalText == null) {
            return 0.0;
        }

        String cleaned = subtotalText.replaceAll("[^0-9.,]", "");
        if (cleaned.isEmpty()) {
            return 0.0;
        }
        cleaned = cleaned.replace(",", "");

        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            System.out.println("Failed to parse subtotal from text: " + subtotalText + " | cleaned: " + cleaned);
            return 0.0;
        }
    }

    @Step("Calculate subtotal from basket items (unit price * quantity)")
    public double calculateSubtotalFromItems(List<BasketItem> items) {
        double sum = 0.0;
        for (BasketItem item : items) {
            sum += item.getLineTotal();
        }
        return sum;
    }

    /**
     * Reads items as BasketItem list, compares calculated subtotal
     * with UI subtotal, and returns normalized product keys.
     */
    @Step("Read basket items, validate subtotal against UI, and return normalized product keys")
    public List<String> readBasketItemsAndValidateSubtotal() {
        System.out.println("Reading items from basket page...");

        List<BasketItem> items = readActiveItems();
        double computedTotal = calculateSubtotalFromItems(items);
        double displayedSubtotal = getBasketSubtotalFromUI();

        System.out.println("\n======= BASKET SUBTOTAL VALIDATION =======");
        System.out.println("Calculated subtotal from items = " + computedTotal);
        System.out.println("Displayed subtotal on UI      = " + displayedSubtotal);

        if (Math.abs(computedTotal - displayedSubtotal) < 0.01) {
            System.out.println("Subtotal in basket matches the sum of item prices.");
        } else {
            System.out.println("Subtotal mismatch between items sum and UI subtotal.");
        }
        System.out.println("==========================================\n");

        List<String> basketProducts = new ArrayList<>();
        for (BasketItem item : items) {
            String key = buildProductKey(item.getName(), item.getUnitPrice());
            basketProducts.add(key);
        }

        return basketProducts;
    }

    // =========================
    // 5) Comparing the three lists
    // =========================

    @Step("Compare filtered products, added-to-cart products, and basket products by normalized name")
    public void compareThreeLists(List<String> filterList, List<String> addedToCartList, List<String> basketList) {

        final int PREFIX_LEN = 70;

        System.out.println("\n======= 3-LISTS COMPARISON (FILTER vs ADDED vs BASKET) =======");

        java.util.function.Function<String, String> toKey = s -> {
            if (s == null) return "";
            String nameOnly = s.split("\\|")[0];
            nameOnly = nameOnly
                    .toLowerCase(Locale.ROOT)
                    .trim()
                    .replaceAll("\\s+", " ");
            if (nameOnly.length() > PREFIX_LEN) {
                nameOnly = nameOnly.substring(0, PREFIX_LEN);
            }
            return nameOnly;
        };

        Set<String> filterSet = new LinkedHashSet<>();
        for (String item : filterList) {
            filterSet.add(toKey.apply(item));
        }

        Set<String> addedSet = new LinkedHashSet<>();
        for (String item : addedToCartList) {
            addedSet.add(toKey.apply(item));
        }

        Set<String> basketSet = new LinkedHashSet<>();
        for (String item : basketList) {
            basketSet.add(toKey.apply(item));
        }

        boolean sameFilterAdded = filterSet.equals(addedSet);
        boolean sameFilterBasket = filterSet.equals(basketSet);

        if (sameFilterAdded && sameFilterBasket) {
            System.out.println("All three lists are identical (by product name).");
        } else {
            System.out.println("Mismatch between the three lists (by product name).\n");

            // FILTER vs ADDED
            for (String item : filterList) {
                String key = toKey.apply(item);
                if (!addedSet.contains(key)) {
                    System.out.println("In FILTER but not in ADDED (name): " + item);
                }
            }
            for (String item : addedToCartList) {
                String key = toKey.apply(item);
                if (!filterSet.contains(key)) {
                    System.out.println("In ADDED but not in FILTER (name): " + item);
                }
            }

            // FILTER vs BASKET
            for (String item : filterList) {
                String key = toKey.apply(item);
                if (!basketSet.contains(key)) {
                    System.out.println("In FILTER but not in BASKET (name): " + item);
                }
            }
            for (String item : basketList) {
                String key = toKey.apply(item);
                if (!filterSet.contains(key)) {
                    System.out.println("In BASKET but not in FILTER (name): " + item);
                }
            }

            // ADDED vs BASKET
            for (String item : addedToCartList) {
                String key = toKey.apply(item);
                if (!basketSet.contains(key)) {
                    System.out.println("In ADDED but not in BASKET (name): " + item);
                }
            }
            for (String item : basketList) {
                String key = toKey.apply(item);
                if (!addedSet.contains(key)) {
                    System.out.println("In BASKET but not in ADDED (name): " + item);
                }
            }
        }

        System.out.println("==============================================================\n");
    }
}
