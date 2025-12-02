# Amazon Price Filter & Checkout Automation -- Selenium / TestNG (Maven)

## 📌 Overview

This project is a fully automated UI testing framework for **Amazon**,
designed to cover:

-   Product listing scanning\
-   Price filtering\
-   Add-to-Cart logic\
-   Basket validation\
-   Address creation\
-   Payment page navigation

It uses **Java 21**, **Selenium WebDriver**, **TestNG**, and **Maven**.

### 🔄 Full Automated Workflow

1.  Log in using valid Amazon credentials.\
2.  Navigate to the "All Video Games" department.\
3.  Read all product cards (titles + prices) across multiple pages.\
4.  Filter products under a configurable price limit.\
5.  Add eligible products to cart:
    -   Try from listing
    -   If unavailable, open product details page\
6.  Open the basket in a new tab.\
7.  Read all basket items (name, price, quantity).\
8.  Recalculate subtotal and validate against the UI subtotal.\
9.  Compare:
    -   Filtered list\
    -   Added-to-cart list\
    -   Basket contents\
10. Proceed to checkout → Add new address → Navigate to payment page.

------------------------------------------------------------------------

## 🧰 Tech Stack

  Component            Version
  -------------------- ----------------------------
  Java                 21
  Maven                3.8+
  Selenium WebDriver   4.29.0
  TestNG               7.10.2
  Allure               2.24.0
  Logging              SLF4J + Log4j2
  JSON Handling        Gson / Jackson / JSON Path
  Excel                Apache POI
  Screenshots          Shutterbug
  Video Recording      Monte Screen Recorder
  AOP                  AspectJ

All dependencies are handled automatically by Maven through `pom.xml`.

------------------------------------------------------------------------

## 📁 Project Structure

``` text
.
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java
    │   │   ├── AutomationFramework
    │   │   │   └── Utilities
    │   │   │       ├── DataUtils.java
    │   │   │       └── Utility.java
    │   │   └── Pages
    │   │       ├── P1_LoginPage.java
    │   │       ├── P2_HomePage.java
    │   │       ├── P3_ProductListingPage.java
    │   │       ├── P4_ProductDetailsPage.java
    │   │       ├── P5_BasketPage.java
    │   │       ├── P6_AddressPage.java
    │   │       └── P7_PaymentPage.java
    │   └── resources
    │       ├── LoginData.json
    │       ├── AddressData.json
    │       └── *.properties
    └── test
        └── java
            └── TestCase
                └── TC1_VideoGamesPriceFilterAndBasketValidation.java
```

------------------------------------------------------------------------

## 🧩 Key Page Objects

### **P1_LoginPage**

Handles Amazon login: - Reads phone/password from JSON\
- Fills login form\
- Submits & asserts success

### **P2_HomePage**

-   Opens Amazon homepage\
-   Navigates to "All Video Games"\
-   Applies test filters

### **P3_ProductListingPage**

-   Reads product names & prices\
-   Applies price filtering\
-   Adds eligible products to cart (listing → details if needed)\
-   Returns:
    -   `eligibleProducts`
    -   `addedToCartProducts`

### **P4_ProductDetailsPage**

-   Adds products when listing button is unavailable\
-   Supports:
    -   Add to Cart\
    -   See All Buying Options\
-   Verifies "Added to cart"

### **P5_BasketPage**

-   Opens basket in new tab\
-   Reads basket items:
    -   Name\
    -   Unit price\
    -   Quantity\
    -   Line total\
-   Recalculates subtotal\
-   Compares:
    -   Filter list\
    -   Added-to-cart list\
    -   Basket contents\
-   Proceeds to checkout → Address Page

### **P6_AddressPage**

Manages full address entry process: - Opens "Add new address"\
- Inputs: full name, phone, street, building, city, landmark\
- Selects address type (Home / Office)\
- Marks as default\
- Saves address & navigates to Payment Page

### **P7_PaymentPage**

-   Verifies user reached **"Select a payment method"**\
-   Final checkpoint of checkout flow

------------------------------------------------------------------------

## ⚙️ Configuration

All test data is read dynamically from:

    src/main/resources/LoginData.json
    src/main/resources/AddressData.json

### JSON Example

``` json
{
  "LoginData": {
    "Phone": "1158228860",
    "Password": "ahmedfarg1"
  },

  "NumberOfPage_DoYouWantToTest": {
    "NumberOf Page": "5",
    "priceLimit": "15000.0"
  }
}
```

------------------------------------------------------------------------

## ▶️ Running the Project

Clone repo:

``` bash
git clone <REPOSITORY_URL>
cd <project-folder>
```

Run tests:

``` bash
mvn clean test
```

------------------------------------------------------------------------

## 📊 Allure Report (Optional)

``` bash
mvn allure:report
mvn allure:serve
```

------------------------------------------------------------------------

## ✔️ Reproducibility

The project runs on any machine that has:

-   Java\
-   Maven\
-   Chrome\
-   Internet
