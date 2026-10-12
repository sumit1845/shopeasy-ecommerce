# 🛒 ShopEasy – Real-World Spring Boot E-Commerce Web Application

ShopEasy is a full-stack e-commerce web application built using **Java 21, Spring Boot 3.3.4, Spring Data JPA, H2 Database, HTML, CSS, and JavaScript**. It provides a shopping experience with product discovery, customer accounts, cart and wishlist management, checkout, order history, coupons, and loyalty rewards.

## 🌟 Key Features

### 1. Product Catalog and Management
- 33+ pre-seeded products across 11 categories.
- Live product search and category filtering.
- Sort products by price, rating, or name.
- Discover random products.
- View product descriptions, ratings, and stock information.

### 2. User Authentication and Profiles
- User registration and login.
- Password hashing.
- Account dashboard with order history, loyalty points, and membership tier.
- Pre-seeded demo account:
  - **Email:** `demo@shopeasy.com`
  - **Password:** `password123`

> The demo credentials are for local development only. Do not use demo accounts or sample passwords in a production environment.

### 3. Shopping Cart and Wishlist
- Add and remove products from the cart.
- Update item quantities and calculate subtotals.
- Convert displayed prices from USD to INR using the configured rate of ₹80 per USD.
- Add or remove products from the wishlist.

### 4. Business Logic, Coupons, and Discounts
- **Third-purchase monthly offer:** After two completed orders in the current calendar month, the third eligible purchase can receive a 20% discount.
- **Coupon system:**
  - `SAVE10` — 10% discount
  - `SAVE20` — 20% discount
  - `WELCOME15` — 15% discount
- **Loyalty rewards:** Earn 1 loyalty point for every ₹100 spent.
- **Points redemption:** Redeem 100 points for a 10% reward discount.
- **Membership tiers:**
  - Bronze — default tier
  - Silver — 200+ points
  - Gold — 500+ points
  - Platinum — 1,000+ points

Actual discount eligibility and reward rules are determined by the backend implementation.

### 5. Orders and Checkout
- Collect customer and shipping details.
- Support payment method selection for Cash on Delivery (COD), UPI, and cards.
- Create orders and order items.
- Calculate applicable discounts and order totals.
- Update purchase history and loyalty points.

Payment method selection does not necessarily mean that a real payment gateway is integrated.

### 6. Customer Support
- Submit customer inquiries through a contact form.
- Persist contact messages in the database.
- Retrieve submitted inquiries through the contact API.

### 7. H2 Database
- Embedded H2 database for local development.
- Preloaded demo user, coupons, and product catalog.
- H2 web console available at `/h2-console`, if enabled in the application configuration.

## 🛠️ Technologies Used

| Component | Technology |
|---|---|
| Programming language | Java 21 |
| Backend framework | Spring Boot 3.3.4 |
| Persistence | Spring Data JPA |
| Database | H2 |
| Frontend | HTML5, CSS3, JavaScript |
| Build tool | Maven |
| Testing | JUnit 5, Spring Boot Test, MockMvc |
| Version control | Git and GitHub |

## 🚀 Getting Started

### Prerequisites
- Java 21 or a compatible JDK.
- Git.
- Maven is optional because the repository includes the Maven Wrapper.

### 1. Clone the repository

```bash
git clone https://github.com/sumit1845/shopeasy-ecommerce.git
cd shopeasy-ecommerce
```

### 2. Run the application

**Windows PowerShell:**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux or macOS:**

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

### 3. Open the application

If the server is configured to run on port `8081`, visit:

**http://localhost:8081/**

The application uses the configuration in `src/main/resources/application.properties`.

### 4. Run automated tests

**Windows:**

```powershell
.\mvnw.cmd test
```

**Linux or macOS:**

```bash
./mvnw test
```

### 5. Build the executable JAR

```bash
./mvnw clean package
```

On Windows, use `.\mvnw.cmd clean package`.

Run the generated JAR using its actual filename under `target/`, for example:

```bash
java -jar target/shopeasy-backend-1.0.0.jar
```

The JAR filename depends on the Maven configuration in `pom.xml`.

## 🌐 Web Application Pages

When the application is running on port 8081:

| Page | URL |
|---|---|
| Home | [http://localhost:8081/index.html](http://localhost:8081/index.html) |
| Products | [http://localhost:8081/products.html](http://localhost:8081/products.html) |
| Product details | [http://localhost:8081/product-details.html?id=1](http://localhost:8081/product-details.html?id=1) |
| Login / Register | [http://localhost:8081/login.html](http://localhost:8081/login.html) |
| Cart | [http://localhost:8081/cart.html](http://localhost:8081/cart.html) |
| Checkout | [http://localhost:8081/checkout.html](http://localhost:8081/checkout.html) |
| Orders | [http://localhost:8081/orders.html](http://localhost:8081/orders.html) |
| Wishlist | [http://localhost:8081/wishlist.html](http://localhost:8081/wishlist.html) |
| Loyalty | [http://localhost:8081/loyalty.html](http://localhost:8081/loyalty.html) |
| Account | [http://localhost:8081/account.html](http://localhost:8081/account.html) |
| Contact | [http://localhost:8081/contact.html](http://localhost:8081/contact.html) |
| H2 Console | [http://localhost:8081/h2-console](http://localhost:8081/h2-console) |

### H2 Console Configuration

Use these values only if they match your local application configuration:

- **JDBC URL:** `jdbc:h2:mem:shopeasydb`
- **Username:** `sa`
- **Password:** blank

An in-memory H2 database is generally reset when the application restarts.

## 📡 REST API Reference

The following endpoints document the intended API surface. Confirm request parameters and response formats against the current controller implementation.

### Authentication — `/api/auth`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a user |
| POST | `/api/auth/login` | Log in a user |
| GET | `/api/auth/me?email=...` | Retrieve a user profile |

### Products — `/api/products`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/products` | Retrieve products; supports search, category, and sorting filters |
| GET | `/api/products/{id}` | Retrieve a product by ID |
| GET | `/api/products/categories` | Retrieve available categories |
| GET | `/api/products/random?limit=12` | Retrieve random products |

### Cart and Wishlist

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/cart?userId=...` | Retrieve cart items |
| POST | `/api/cart/add?userId=...&productId=...&quantity=...` | Add a cart item |
| PUT | `/api/cart/update?userId=...&productId=...&quantity=...` | Update item quantity |
| DELETE | `/api/cart/remove?userId=...&productId=...` | Remove an item |
| DELETE | `/api/cart/clear?userId=...` | Clear the cart |
| GET | `/api/wishlist?userId=...` | Retrieve wishlist items |
| POST | `/api/wishlist/toggle?userId=...&productId=...` | Toggle a wishlist item |

### Orders — `/api/orders`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Place an order |
| GET | `/api/orders?email=...` | Retrieve orders by email |
| GET | `/api/orders/{id}` | Retrieve order details |
| GET | `/api/orders/monthly-offer?email=...` | Check monthly-offer eligibility |

### Coupons — `/api/coupons`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/coupons/validate?code=...` | Validate a coupon |
| POST | `/api/coupons/apply` | Apply a coupon |

### Loyalty — `/api/loyalty`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/loyalty/status?email=...` | Retrieve loyalty points, tier, and purchase statistics |
| POST | `/api/loyalty/redeem?email=...` | Redeem loyalty points |

### Contact — `/api/contact`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/contact` | Submit a customer inquiry |
| GET | `/api/contact` | Retrieve customer inquiries |

## 📸 Screenshots

Screenshots help visitors understand the user interface before running the project.

Create a `screenshots/` directory and add images captured from your running application.

Suggested images:

- Homepage and navigation
- Product catalog and filters
- Product details
- Login and registration
- Cart and checkout
- Order history
- Loyalty dashboard

For example, after adding `screenshots/homepage.png`, embed it using:

```markdown
![ShopEasy Homepage](screenshots/homepage.png)
```

## 📂 Project Structure

```text
shopeasy-backend/
├── .mvn/wrapper/
├── src/
│   ├── main/
│   │   ├── java/com/shopeasy/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── ShopEasyApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   └── test/
│       └── java/com/shopeasy/
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## 🔮 Future Enhancements

- Integrate a real payment gateway.
- Add administrator authentication and inventory management.
- Improve product filtering and pagination.
- Add order confirmation emails and notifications.
- Deploy the application to a cloud platform.
- Strengthen authentication, authorization, and password storage.

## 👨‍💻 Author

**Sumit Pachlore**

GitHub: [@sumit1845](https://github.com/sumit1845)

## 📄 License

This project is intended for learning and portfolio demonstration. Add a `LICENSE` file if you wish to specify the terms under which others may use, modify, or distribute the code.
