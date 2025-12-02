# Amazon Price Filter & Basket Validation – Selenium / TestNG (Maven)

## 📌 Overview

This project is an automated UI testing framework designed to validate product listing, price filtering, cart operations, and basket subtotal accuracy on **Amazon**.  
It is built using **Java 21**, **Selenium WebDriver**, **TestNG**, and **Maven**.

### Main workflow automated by the test:

1. Log in to Amazon with valid credentials.
2. Navigate to a predefined Amazon category.
3. Scan product listing pages and extract product titles + prices.
4. Filter products based on a configurable price limit.
5. Add all eligible products to the cart:
   - First attempt from the listing card.
   - If unavailable, open product details in a new tab.
6. Open the Basket page.
7. Read all basket items (name, price, quantity).
8. Calculate subtotal from items and compare it with the UI subtotal.
9. Compare:
   - Filtered items  
   - Items added to cart  
   - Items appearing in basket  

Ensures consistency between UI pages and cart behavior.

---

## 🧰 Tech Stack

| Component | Version |
|----------|---------|
| Java | 21 |
| Maven | 3.8+ |
| Selenium WebDriver | 4.29.0 |
| TestNG | 7.10.2 |
| Allure Reporting | 2.24.0 |
| Logging | SLF4J + Log4j2 |
| JSON handling | Gson / Jackson / org.json / JSON Path |
| Excel | Apache POI |
| Screenshots | Selenium Shutterbug |
| Video recording | Monte Screen Recorder |
| AOP | AspectJ |

All dependencies are automatically downloaded by Maven from `pom.xml`.

---

## 📁 Project Structure
.
├── pom.xml
├── README.md
└── src
├── main
│ ├── java
│ │ ├── AutomationFramework/Utilities
│ │ └── Pages
│ └── resources
│ ├── LoginData.json
│ └── *.properties
└── test
└── java
└── TestCase
└── TC1_VideoGamesPriceFilterAndBasketValidation.java


---

## 🧩 Key Page Objects

### **P1_LoginPage**
Handles Amazon login flow:
- Reading login data from JSON (phone/email + password)
- Entering credentials
- Submitting login form
- Asserting login success

### **P2_HomePage**
Responsible for:
- Navigating to Amazon homepage
- Interacting with main menu
- Navigating to the target test category (example: Video Games)

### **P3_ProductListingPage**
- Reads product names and prices
- Applies price filtering
- Attempts Add to Cart from listing
- Opens product details if needed
- Returns:
  - `eligibleProducts`
  - `addedToCartProducts`

### **P4_ProductDetailsPage**
- Handles Add to Cart from product details page
- Supports “See All Buying Options”
- Confirms success through “Added to cart” page

### **P5_BasketPage**
- Opens basket in a new tab
- Reads basket items into structured objects
- Calculates subtotal
- Compares with UI subtotal
- Compares lists (filtered vs added vs basket)

---

## ⚙️ Configuration

The framework reads all test data from a JSON file located in:



### JSON Example Used by the Framework

```json
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



