# 🛒 ShopEasy - Real-World Spring Boot Ecommerce Web Application

ShopEasy is a full-featured, real-world full-stack ecommerce web application built with **Java 21**, **Spring Boot 3.3.4**, **Spring Data JPA**, **H2 Database**, and a responsive frontend interface.

---

## 🌟 Key Features

1. **Product Catalog & Management**:
   - 33+ pre-seeded realistic products spanning 11 categories: *Smartphones, Laptops, Tablets, Men's Shirts, Women's Dresses, Men's Shoes, Women's Shoes, Fragrances, Skincare, Groceries, Home Decoration*.
   - Live search, category filtering, price/rating/name sorting, and random product discovery.
   - Comprehensive product detail view with live stock, rating, and descriptions.

2. **User Authentication & Profiles**:
   - Secure registration and login with SHA-256 hashed passwords.
   - Profile dashboard with order history, loyalty points, and tier status.
   - Pre-seeded **Demo Account**:
     - **Email**: `demo@shopeasy.com`
     - **Password**: `password123`

3. **Shopping Cart & Wishlist**:
   - Real-time cart calculations, quantity adjustment, subtotal, and price conversions (USD to INR at ₹80/$).
   - Wishlist toggle and bookmarking.

4. **Real-World Business Logic & Discounts**:
   - **3rd Purchase Monthly Offer**: Automatically calculates purchases in the current calendar month (`yyyy-MM`). After 2 completed orders, the 3rd purchase unlocks **20% OFF**!
   - **Coupon Discount System**: Validates coupons (`SAVE10` -> 10% off, `SAVE20` -> 20% off, `WELCOME15` -> 15% off).
   - **Loyalty Rewards Program**: Earns **1 loyalty point for every ₹100 spent**.
   - **Points Redemption**: Redeem 100 points to unlock a 10% reward discount.
   - Automatic tier progression: *Bronze*, *Silver* (200+ pts), *Gold* (500+ pts), *Platinum* (1000+ pts).

5. **Orders & Checkout**:
   - Full checkout flow: customer details, shipping address, payment method selection (COD, UPI, Card).
   - Automatically generates order records, order items, discounts applied, and totals.
   - Awards loyalty points upon order placement and updates purchase history.

6. **Customer Support**:
   - Contact form with database persistence for customer inquiries.

7. **Zero-Configuration Persistence**:
   - Built-in embedded **H2 Database** with web console at `/h2-console`.
   - Pre-loaded with demo user, active coupons, and realistic product catalog on startup.

---

## 🚀 Getting Started

### Prerequisites
- **Java 21** or later installed.
- (Maven is included via the included Maven Wrapper `mvnw` / `mvnw.cmd`).

### Running the Application

#### Option 1: Using the Maven Wrapper (Recommended)
**Windows (PowerShell or Command Prompt):**
```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

#### Option 2: Running the Pre-built Executable JAR
```bash
java -jar target/shopeasy-backend-1.0.0.jar
```

Once started, open your browser to:
👉 **`http://localhost:8081`**

---

## 🌐 Web Application Pages

- **Home**: `http://localhost:8081/index.html`
- **Products Catalog**: `http://localhost:8081/products.html`
- **Product Details**: `http://localhost:8081/product-details.html?id=1`
- **Cart**: `http://localhost:8081/cart.html`
- **Checkout**: `http://localhost:8081/checkout.html`
- **Orders History**: `http://localhost:8081/orders.html`
- **Wishlist**: `http://localhost:8081/wishlist.html`
- **Loyalty Program**: `http://localhost:8081/loyalty.html`
- **Account Dashboard**: `http://localhost:8081/account.html`
- **Login / Register**: `http://localhost:8081/login.html`
- **Contact Support**: `http://localhost:8081/contact.html`
- **H2 Database Console**: `http://localhost:8081/h2-console`
  - *JDBC URL*: `jdbc:h2:mem:shopeasydb`
  - *Username*: `sa`
  - *Password*: *(blank)*

---

## 📡 REST API Reference

### 🔐 Authentication (`/api/auth`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user (`name`, `email`, `password`) |
| `POST` | `/api/auth/login` | Authenticate user (`email`, `password`) |
| `GET` | `/api/auth/me?email=...` | Retrieve user profile & loyalty points |

### 🛍️ Products (`/api/products`)
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/products` | Retrieve all products (supports `?category=...&search=...&sort=...`) |
| `GET` | `/api/products/{id}` | Retrieve single product by ID |
| `GET` | `/api/products/categories` | Retrieve distinct category list |
| `GET` | `/api/products/random?limit=12` | Retrieve random products |

### 🛒 Cart & Wishlist (`/api/cart`, `/api/wishlist`)
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/cart?userId=...` | Get user cart items |
| `POST` | `/api/cart/add?userId=...&productId=...&quantity=...` | Add item to cart |
| `PUT` | `/api/cart/update?userId=...&productId=...&quantity=...` | Update item quantity |
| `DELETE` | `/api/cart/remove?userId=...&productId=...` | Remove item |
| `DELETE` | `/api/cart/clear?userId=...` | Clear cart |
| `GET` | `/api/wishlist?userId=...` | Get user wishlist |
| `POST` | `/api/wishlist/toggle?userId=...&productId=...` | Toggle wishlist item |

### 📦 Orders (`/api/orders`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/orders` | Place new order (applies 3rd-month discount, awards loyalty points) |
| `GET` | `/api/orders?email=...` | Retrieve orders by user email |
| `GET` | `/api/orders/{id}` | Retrieve order details by ID |
| `GET` | `/api/orders/monthly-offer?email=...` | Check monthly purchases and 3rd purchase 20% discount eligibility |

### 🎟️ Coupons (`/api/coupons`)
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/coupons/validate?code=...` | Validate coupon code (`SAVE10`, `SAVE20`, `WELCOME15`) |
| `POST` | `/api/coupons/apply` | Apply coupon via JSON body |

### ⭐ Loyalty & Rewards (`/api/loyalty`)
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/loyalty/status?email=...` | Get points, tier, and purchase stats |
| `POST` | `/api/loyalty/redeem?email=...` | Redeem 100 points for a 10% reward discount |

### ✉️ Contact (`/api/contact`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/contact` | Submit contact support inquiry |
| `GET` | `/api/contact` | Retrieve customer inquiries |

---

## 🧪 Running Automated Tests

Run the full automated test suite (13 unit & integration tests):
```bash
.\mvnw.cmd test
```
*(All 13 tests pass with 0 failures and 0 errors).*

---

## 📂 Project Structure

```
shopeasy-backend/
├── pom.xml                                  # Maven dependencies & build configuration
├── mvnw & mvnw.cmd                          # Maven wrapper executables
├── .mvn/wrapper/                            # Maven wrapper properties
├── README.md                                # Project documentation
├── src/
│   ├── main/
│   │   ├── java/com/shopeasy/
│   │   │   ├── ShopEasyApplication.java     # Spring Boot application entry point
│   │   │   ├── config/                      # CORS & Data Initializer (Seed Data)
│   │   │   ├── controller/                  # REST Controllers (Auth, Products, Orders, etc.)
│   │   │   ├── dto/                         # Request & Response Data Transfer Objects
│   │   │   ├── model/                       # JPA Entities (User, Product, Order, Coupon, etc.)
│   │   │   ├── repository/                  # Spring Data JPA Repositories
│   │   │   └── service/                     # Business Logic Services
│   │   └── resources/
│   │       ├── application.properties       # Server port & H2 database configuration
│   │       └── static/                      # Frontend HTML, CSS, JavaScript, and Assets
│   └── test/
│       └── java/com/shopeasy/               # JUnit 5 & MockMvc integration tests
└── target/
    └── shopeasy-backend-1.0.0.jar           # Executable Spring Boot JAR
```
