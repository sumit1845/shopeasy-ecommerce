/* ================= GLOBAL DATA & CONFIG ================= */

const API_BASE = window.location.protocol.startsWith("http")
    ? ""
    : "http://localhost:8081";

let allProducts = [];
let products = [];

let cart = JSON.parse(localStorage.getItem("shopEasyCart")) || [];
let wishlist = JSON.parse(localStorage.getItem("shopEasyWishlist")) || [];
let orders = JSON.parse(localStorage.getItem("shopEasyOrders")) || [];
let user = JSON.parse(localStorage.getItem("shopEasyUser")) || null;
let loggedIn = localStorage.getItem("shopEasyLoggedIn") === "true";
let loyaltyPoints = Number(localStorage.getItem("shopEasyPoints")) || 0;
let rewardDiscount = Number(localStorage.getItem("shopEasyRewardDiscount")) || 0;
let couponDiscount = Number(localStorage.getItem("shopEasyCouponDiscount")) || 0;

/* ================= SAVE DATA ================= */

function saveData() {
    localStorage.setItem("shopEasyCart", JSON.stringify(cart));
    localStorage.setItem("shopEasyWishlist", JSON.stringify(wishlist));
    localStorage.setItem("shopEasyOrders", JSON.stringify(orders));
    localStorage.setItem("shopEasyPoints", loyaltyPoints);
    localStorage.setItem("shopEasyRewardDiscount", rewardDiscount);
    localStorage.setItem("shopEasyCouponDiscount", couponDiscount);
}

/* ================= USER ================= */

function saveUser() {
    localStorage.setItem("shopEasyUser", JSON.stringify(user));
    localStorage.setItem("shopEasyLoggedIn", loggedIn);
}

function requireLogin() {
    if (!loggedIn || !user) {
        window.location.href = "login.html";
        return false;
    }
    return true;
}

/* ================= MONTH LOGIC ================= */

function getCurrentMonthKey() {
    const date = new Date();
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    return `${year}-${month}`;
}

function getMonthlyPurchaseCount() {
    const monthKey = getCurrentMonthKey();
    return orders.filter(order => order.monthKey === monthKey).length;
}

/*
   After 2 completed purchases:
   the THIRD purchase gets 20% OFF.
*/
function isThirdPurchaseOfferActive() {
    const count = getMonthlyPurchaseCount();
    return count === 2;
}

function getMonthlyDiscount() {
    return isThirdPurchaseOfferActive() ? 20 : 0;
}

/* ================= PRICE ================= */

function getINRPrice(price) {
    return Math.round((price || 0) * 80);
}

function getProductCardPrice(product) {
    const originalPrice = getINRPrice(product.price);
    const monthlyDiscount = getMonthlyDiscount();

    if (monthlyDiscount > 0) {
        return Math.round(originalPrice - (originalPrice * monthlyDiscount / 100));
    }
    return originalPrice;
}

/* ================= CART COUNT ================= */

function updateCartCount() {
    const elements = document.querySelectorAll("#cartCount");
    let count = 0;
    cart.forEach(item => {
        count += (item.quantity || 1);
    });
    elements.forEach(element => {
        element.textContent = count;
    });
}

/* ================= PRODUCTS ================= */

async function loadProducts() {
    const container = document.getElementById("products-container");
    if (!container) return;

    const loading = document.getElementById("loading");

    try {
        let response;
        try {
            // Fetch from Spring Boot backend REST API
            response = await fetch(`${API_BASE}/api/products`);
        } catch (backendError) {
            console.warn("Backend not reachable, fallback to external catalog:", backendError);
            response = await fetch("https://dummyjson.com/products?limit=0");
        }

        if (!response.ok) {
            response = await fetch("https://dummyjson.com/products?limit=0");
        }

        if (!response.ok) {
            throw new Error("Failed to load products");
        }

        const data = await response.json();

        // Support both Spring Boot payload { products: [...], total: n } and array / dummyjson format
        if (Array.isArray(data)) {
            allProducts = data;
        } else if (data.products && Array.isArray(data.products)) {
            allProducts = data.products;
        } else {
            allProducts = [];
        }

        products = [...allProducts];

        const productCount = document.getElementById("productCount");
        if (productCount) {
            productCount.textContent = `${allProducts.length} Products Available`;
        }

        displayProducts(products);
        displayMonthlyOffer();

    } catch (error) {
        console.error("Product loading error:", error);
        container.innerHTML = `
            <div class="loading">
                ❌ Unable to load products.
                <br><br>
                Please check your network or ensure Spring Boot backend is running.
                <br><br>
                <button id="retryProducts" class="btn">
                    🔄 Try Again
                </button>
            </div>
        `;

        const retryButton = document.getElementById("retryProducts");
        if (retryButton) {
            retryButton.addEventListener("click", loadProducts);
        }
    } finally {
        if (loading) {
            loading.style.display = "none";
        }
    }
}

/* ================= DISPLAY PRODUCTS ================= */

function displayProducts(productList) {
    const container = document.getElementById("products-container");
    if (!container) return;

    container.innerHTML = "";

    if (productList.length === 0) {
        container.innerHTML = `
            <div class="loading">
                No products found.
            </div>
        `;
        return;
    }

    productList.forEach(product => {
        const originalPrice = getINRPrice(product.price);
        const finalPrice = getProductCardPrice(product);
        const monthlyDiscount = getMonthlyDiscount();
        const isWishlist = wishlist.some(item => item.id === product.id);

        const card = document.createElement("div");
        card.className = "product-card";

        let discountHTML = "";
        if (monthlyDiscount > 0) {
            discountHTML = `
                <span class="discount-badge">
                    🎉 20% OFF - 3rd Purchase Offer
                </span>
            `;
        }

        let priceHTML = "";
        if (monthlyDiscount > 0) {
            priceHTML = `
                <div class="product-price">
                    ₹${finalPrice}
                    <span class="old-price">₹${originalPrice}</span>
                </div>
            `;
        } else {
            priceHTML = `
                <div class="product-price">
                    ₹${originalPrice}
                </div>
            `;
        }

        card.innerHTML = `
            <a href="product-details.html?id=${product.id}" class="product-image-link">
                <img src="${product.thumbnail}" alt="${product.title}" loading="lazy">
            </a>

            <div>
                ${discountHTML}
            </div>

            <h3>${product.title}</h3>

            ${priceHTML}

            <p>⭐ ${product.rating || 4.5}</p>

            <div class="product-actions">
                <button class="view-product" data-id="${product.id}">
                    View
                </button>

                <button class="add-cart" data-id="${product.id}">
                    Add
                </button>

                <button class="wishlist-btn" data-id="${product.id}" title="Wishlist">
                    ${isWishlist ? "❤️" : "♡"}
                </button>
            </div>
        `;

        container.appendChild(card);
    });
}

/* ================= MONTHLY OFFER DISPLAY ================= */

async function displayMonthlyOffer() {
    const container = document.getElementById("monthlyOffer");
    if (!container) return;

    // Check with backend if logged in
    let count = getMonthlyPurchaseCount();

    if (loggedIn && user && user.email) {
        try {
            const res = await fetch(`${API_BASE}/api/orders/monthly-offer?email=${encodeURIComponent(user.email)}`);
            if (res.ok) {
                const offerData = await res.json();
                count = offerData.monthlyPurchaseCount;
            }
        } catch (e) {
            // fallback to local count
        }
    }

    if (count === 0) {
        container.innerHTML = `
            🛍️ You have made <strong>0 purchases</strong> this month.
            Buy twice to unlock <strong>20% OFF on your 3rd purchase.</strong>
        `;
    } else if (count === 1) {
        container.innerHTML = `
            🎯 You have made <strong>1 purchase</strong> this month.
            One more purchase unlocks your <strong>20% OFF 3rd purchase offer.</strong>
        `;
    } else if (count === 2) {
        container.className = "monthly-offer third-offer";
        container.innerHTML = `
            🎉 <strong>20% OFF UNLOCKED!</strong>
            Your next purchase is your <strong>3rd purchase of this month</strong>.
            Discounted prices are shown on product cards.
        `;
    } else {
        container.innerHTML = `
            ✅ Your 3rd-purchase monthly offer has already been used.
            Your purchase count this month: <strong>${count}</strong>
        `;
    }
}

/* ================= SEARCH ================= */

function searchProducts() {
    const input = document.getElementById("searchInput");
    if (!input) return;

    const value = input.value.toLowerCase().trim();

    products = allProducts.filter(product =>
        (product.title && product.title.toLowerCase().includes(value)) ||
        (product.category && product.category.toLowerCase().includes(value)) ||
        (product.brand && product.brand.toLowerCase().includes(value))
    );

    displayProducts(products);

    const productCount = document.getElementById("productCount");
    if (productCount) {
        productCount.textContent = `${products.length} Products Found`;
    }
}

/* ================= CATEGORY ================= */

function filterCategory(category) {
    document.querySelectorAll(".category-btn").forEach(button => {
        button.classList.toggle("active", button.dataset.category === category);
    });

    if (category === "all") {
        products = [...allProducts];
    } else {
        products = allProducts.filter(product => product.category === category);
    }

    displayProducts(products);

    const productCount = document.getElementById("productCount");
    if (productCount) {
        productCount.textContent = `${products.length} Products Found`;
    }
}

/* ================= SORT ================= */

function sortProducts(value) {
    if (value === "low") {
        products.sort((a, b) => (a.price || 0) - (b.price || 0));
    }
    if (value === "high") {
        products.sort((a, b) => (b.price || 0) - (a.price || 0));
    }
    if (value === "rating") {
        products.sort((a, b) => (b.rating || 0) - (a.rating || 0));
    }
    if (value === "name") {
        products.sort((a, b) => (a.title || "").localeCompare(b.title || ""));
    }

    displayProducts(products);
}

/* ================= RANDOM ================= */

function randomProducts() {
    products = [...allProducts].sort(() => Math.random() - 0.5).slice(0, 12);
    displayProducts(products);
}

/* ================= FIND PRODUCT ================= */

function findProduct(id) {
    return allProducts.find(product => product.id === Number(id));
}

/* ================= ADD CART ================= */

function addToCart(id) {
    const product = findProduct(id);
    if (!product) return;

    const existing = cart.find(item => item.id === product.id);

    if (existing) {
        existing.quantity++;
    } else {
        cart.push({
            id: product.id,
            title: product.title,
            price: product.price,
            thumbnail: product.thumbnail,
            quantity: 1
        });
    }

    saveData();
    updateCartCount();
    showToast("Product added to cart 🛒");
}

/* ================= CART DISPLAY ================= */

function displayCart() {
    const container = document.getElementById("cart-container");
    if (!container) return;

    container.innerHTML = "";

    if (cart.length === 0) {
        container.innerHTML = `
            <div class="loading">
                🛒 Your cart is empty.
                <br><br>
                <a href="products.html" class="btn">
                    Start Shopping
                </a>
            </div>
        `;
        displayCartTotal();
        return;
    }

    cart.forEach(item => {
        const price = getINRPrice(item.price);

        const div = document.createElement("div");
        div.className = "cart-item";

        div.innerHTML = `
            <img src="${item.thumbnail}" alt="${item.title}">

            <div>
                <h3>${item.title}</h3>
                <p>₹${price}</p>

                <div class="quantity-controls">
                    <button class="decrease-cart" data-id="${item.id}">−</button>
                    <strong>${item.quantity}</strong>
                    <button class="increase-cart" data-id="${item.id}">+</button>
                </div>

                <button class="remove-btn remove-cart" data-id="${item.id}">
                    Remove
                </button>
            </div>
        `;

        container.appendChild(div);
    });

    displayCartTotal();
}

/* ================= CART TOTAL ================= */

function getCartSubtotal() {
    let total = 0;
    cart.forEach(item => {
        total += getINRPrice(item.price) * (item.quantity || 1);
    });
    return total;
}

function getCartTotal() {
    const subtotal = getCartSubtotal();
    const monthlyDiscount = getMonthlyDiscount();

    let percentage = monthlyDiscount + couponDiscount + rewardDiscount;
    if (percentage > 100) {
        percentage = 100;
    }

    return Math.round(subtotal - (subtotal * percentage / 100));
}

/* ================= CART TOTAL DISPLAY ================= */

function displayCartTotal() {
    const container = document.getElementById("cart-total");
    if (!container) return;

    const subtotal = getCartSubtotal();
    const monthly = getMonthlyDiscount();
    const total = getCartTotal();

    container.innerHTML = `
        <div class="summary-row">
            <span>Subtotal</span>
            <strong>₹${subtotal}</strong>
        </div>

        ${
            monthly > 0
            ? `
            <div class="summary-row">
                <span>3rd Purchase Offer</span>
                <strong>- ${monthly}%</strong>
            </div>
            `
            : ""
        }

        ${
            couponDiscount > 0
            ? `
            <div class="summary-row">
                <span>Coupon</span>
                <strong>- ${couponDiscount}%</strong>
            </div>
            `
            : ""
        }

        ${
            rewardDiscount > 0
            ? `
            <div class="summary-row">
                <span>Reward</span>
                <strong>- ${rewardDiscount}%</strong>
            </div>
            `
            : ""
        }

        <div class="final-total">
            Total: ₹${total}
        </div>
    `;
}

/* ================= QUANTITY ================= */

function increaseQuantity(id) {
    const item = cart.find(item => item.id === Number(id));
    if (!item) return;

    item.quantity++;
    saveData();
    displayCart();
    updateCartCount();
}

function decreaseQuantity(id) {
    const item = cart.find(item => item.id === Number(id));
    if (!item) return;

    if (item.quantity > 1) {
        item.quantity--;
    } else {
        cart = cart.filter(item => item.id !== Number(id));
    }

    saveData();
    displayCart();
    updateCartCount();
}

function removeFromCart(id) {
    cart = cart.filter(item => item.id !== Number(id));
    saveData();
    displayCart();
    updateCartCount();
}

/* ================= COUPON ================= */

async function applyCoupon() {
    const input = document.getElementById("couponInput");
    const message = document.getElementById("couponMessage");

    if (!input || !message) return;

    const code = input.value.toUpperCase().trim();
    if (!code) return;

    try {
        const response = await fetch(`${API_BASE}/api/coupons/validate?code=${encodeURIComponent(code)}`);
        const data = await response.json();

        if (response.ok && data.valid) {
            couponDiscount = data.discountPercent;
            message.textContent = data.message || `✓ ${data.discountPercent}% coupon applied.`;
            message.style.color = "#16a34a";
        } else {
            couponDiscount = 0;
            message.textContent = data.message || "Invalid coupon.";
            message.style.color = "#dc2626";
        }
    } catch (e) {
        // Local fallback
        if (code === "SAVE10") {
            couponDiscount = 10;
            message.textContent = "✓ 10% coupon applied.";
            message.style.color = "#16a34a";
        } else if (code === "SAVE20") {
            couponDiscount = 20;
            message.textContent = "✓ 20% coupon applied.";
            message.style.color = "#16a34a";
        } else {
            couponDiscount = 0;
            message.textContent = "Invalid coupon.";
            message.style.color = "#dc2626";
        }
    }

    saveData();
    displayCartTotal();
}

/* ================= WISHLIST ================= */

function toggleWishlist(id) {
    const product = findProduct(id);
    if (!product) return;

    const exists = wishlist.some(item => item.id === product.id);

    if (exists) {
        wishlist = wishlist.filter(item => item.id !== product.id);
        showToast("Removed from wishlist");
    } else {
        wishlist.push(product);
        showToast("Added to wishlist ❤️");
    }

    saveData();
    displayProducts(products);
    displayWishlist();
}

function displayWishlist() {
    const container = document.getElementById("wishlist-container");
    if (!container) return;

    container.innerHTML = "";

    if (wishlist.length === 0) {
        container.innerHTML = `
            <div class="loading">
                ❤️ Your wishlist is empty.
                <br><br>
                <a href="products.html" class="btn">
                    Explore Products
                </a>
            </div>
        `;
        return;
    }

    wishlist.forEach(product => {
        const price = getProductCardPrice(product);

        const div = document.createElement("div");
        div.className = "product-card";

        div.innerHTML = `
            <a href="product-details.html?id=${product.id}">
                <img src="${product.thumbnail}" alt="${product.title}">
            </a>

            <h3>${product.title}</h3>

            <div class="product-price">
                ₹${price}
            </div>

            <div class="product-actions">
                <button class="add-cart" data-id="${product.id}">
                    Add to Cart
                </button>

                <button class="wishlist-remove" data-id="${product.id}">
                    Remove ❤️
                </button>
            </div>
        `;

        container.appendChild(div);
    });
}

/* ================= PRODUCT DETAILS ================= */

async function displayProductDetails() {
    const container = document.getElementById("product-details");
    if (!container) return;

    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (!id) {
        container.innerHTML = `<p>Product not found.</p>`;
        return;
    }

    try {
        let response;
        try {
            response = await fetch(`${API_BASE}/api/products/${id}`);
        } catch (e) {
            response = await fetch(`https://dummyjson.com/products/${id}`);
        }

        if (!response.ok) {
            response = await fetch(`https://dummyjson.com/products/${id}`);
        }

        if (!response.ok) {
            throw new Error("Product not found");
        }

        const product = await response.json();
        const originalPrice = getINRPrice(product.price);
        const finalPrice = getProductCardPrice(product);
        const discount = getMonthlyDiscount();

        container.innerHTML = `
            <div class="product-detail-card">
                <div>
                    <img src="${product.thumbnail}" alt="${product.title}">
                </div>

                <div class="detail-info">
                    ${
                        discount > 0
                        ? `
                        <span class="discount-badge">
                            🎉 20% OFF - 3rd Purchase
                        </span>
                        `
                        : ""
                    }

                    <h1>${product.title}</h1>

                    <p>
                        ⭐ ${product.rating || 4.5} &nbsp; | &nbsp; ${product.stock || 25} items available
                    </p>

                    <div class="detail-price">
                        ₹${finalPrice}
                        ${
                            discount > 0
                            ? `<span class="old-price">₹${originalPrice}</span>`
                            : ""
                        }
                    </div>

                    <p class="detail-description">
                        ${product.description}
                    </p>

                    <p>
                        <strong>Category:</strong> ${product.category}
                    </p>

                    <p>
                        <strong>Brand:</strong> ${product.brand || "ShopEasy"}
                    </p>

                    <div class="detail-buttons">
                        <button id="detailsAddCart" class="btn">
                            🛒 Add to Cart
                        </button>

                        <button id="detailsWishlist" class="outline-btn">
                            ❤️ Wishlist
                        </button>
                    </div>
                </div>
            </div>
        `;

        const addButton = document.getElementById("detailsAddCart");
        const wishlistButton = document.getElementById("detailsWishlist");

        if (addButton) {
            addButton.addEventListener("click", function() {
                // Ensure product is in allProducts
                if (!allProducts.some(p => p.id === product.id)) {
                    allProducts.push(product);
                }
                addToCart(product.id);
            });
        }

        if (wishlistButton) {
            wishlistButton.addEventListener("click", function() {
                if (!allProducts.some(p => p.id === product.id)) {
                    allProducts.push(product);
                }
                toggleWishlist(product.id);
            });
        }

    } catch (error) {
        console.error(error);
        container.innerHTML = `<p>Unable to load product details.</p>`;
    }
}

/* ================= LOGIN ================= */

async function handleLogin(event) {
    event.preventDefault();

    const email = document.getElementById("loginEmail").value.trim().toLowerCase();
    const password = document.getElementById("loginPassword").value;
    const message = document.getElementById("authMessage");

    try {
        const response = await fetch(`${API_BASE}/api/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: email, password: password })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            user = {
                id: data.id,
                name: data.name,
                email: data.email,
                createdAt: data.createdAt
            };
            loggedIn = true;
            loyaltyPoints = data.loyaltyPoints || 0;
            saveUser();
            saveData();

            await syncBackendOrders();

            message.textContent = "Login successful! Redirecting...";
            message.style.color = "#16a34a";

            setTimeout(function() {
                window.location.href = "index.html";
            }, 800);
            return;
        } else {
            message.textContent = data.message || "Invalid email or password.";
            message.style.color = "#dc2626";
            return;
        }
    } catch (err) {
        console.warn("Backend login failed, fallback to local:", err);
        const savedUser = JSON.parse(localStorage.getItem("shopEasyUser"));
        if (savedUser && savedUser.email.toLowerCase() === email && savedUser.password === password) {
            user = savedUser;
            loggedIn = true;
            saveUser();
            message.textContent = "Login successful! Redirecting...";
            message.style.color = "#16a34a";
            setTimeout(() => { window.location.href = "index.html"; }, 800);
        } else {
            message.textContent = "Invalid email or password.";
            message.style.color = "#dc2626";
        }
    }
}

/* ================= SIGNUP ================= */

async function handleSignup(event) {
    event.preventDefault();

    const name = document.getElementById("signupName").value.trim();
    const email = document.getElementById("signupEmail").value.trim().toLowerCase();
    const password = document.getElementById("signupPassword").value;
    const message = document.getElementById("authMessage");

    if (password.length < 6) {
        message.textContent = "Password must contain at least 6 characters.";
        message.style.color = "#dc2626";
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/api/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name: name, email: email, password: password })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            user = {
                id: data.id,
                name: data.name,
                email: data.email,
                createdAt: data.createdAt
            };
            loggedIn = true;
            loyaltyPoints = 0;
            rewardDiscount = 0;
            couponDiscount = 0;
            saveUser();
            saveData();

            message.textContent = "Account created successfully! Redirecting...";
            message.style.color = "#16a34a";

            setTimeout(function() {
                window.location.href = "index.html";
            }, 800);
            return;
        } else {
            message.textContent = data.message || "Failed to create account.";
            message.style.color = "#dc2626";
            return;
        }
    } catch (err) {
        console.warn("Backend signup failed, fallback to local:", err);
        user = {
            name: name,
            email: email,
            password: password,
            createdAt: new Date().toLocaleDateString()
        };
        loggedIn = true;
        saveUser();
        loyaltyPoints = 0;
        rewardDiscount = 0;
        couponDiscount = 0;
        saveData();

        message.textContent = "Account created successfully! Redirecting...";
        message.style.color = "#16a34a";

        setTimeout(function() {
            window.location.href = "index.html";
        }, 800);
    }
}

/* ================= ACCOUNT ================= */

async function displayAccount() {
    const container = document.getElementById("accountPage");
    if (!container) return;

    if (!loggedIn || !user) {
        container.innerHTML = `
            <div class="form-container" style="text-align:center">
                <h2>👤 Welcome to ShopEasy</h2>
                <p>
                    Login or create an account to view your shopping history.
                </p>
                <br>
                <a href="login.html" class="btn">
                    Login / Sign Up
                </a>
            </div>
        `;
        return;
    }

    // Sync with backend
    await syncLoyaltyStatus();
    await syncBackendOrders();

    const monthlyPurchases = getMonthlyPurchaseCount();
    const totalPurchases = orders.length;

    let offerText = "";
    if (monthlyPurchases === 2) {
        offerText = `
            🎉 Your <strong>20% OFF</strong> third-purchase offer is unlocked!
            Check the Products page.
        `;
    } else if (monthlyPurchases >= 3) {
        offerText = `
            ✓ Your third-purchase offer has already been used this month.
        `;
    } else {
        offerText = `
            Buy ${2 - monthlyPurchases} more time(s) this month to unlock 20% OFF on your third purchase.
        `;
    }

    const initials = (user.name || "U").charAt(0).toUpperCase();

    container.innerHTML = `
        <div class="account-header">
            <div>
                <div class="profile-avatar">${initials}</div>
            </div>

            <div style="flex:1">
                <h1>Hello, ${user.name} 👋</h1>
                <p>${user.email}</p>
                <p>Member since ${user.createdAt || "2026"}</p>
            </div>

            <button id="logoutAccountBtn" class="outline-btn">
                Logout
            </button>
        </div>

        <div class="monthly-offer third-offer">
            ${offerText}
        </div>

        <div class="account-stats">
            <div class="stat-card">
                <span>Total Orders</span>
                <strong>${totalPurchases}</strong>
            </div>

            <div class="stat-card">
                <span>This Month</span>
                <strong>${monthlyPurchases}</strong>
            </div>

            <div class="stat-card">
                <span>Loyalty Points</span>
                <strong>${loyaltyPoints}</strong>
            </div>

            <div class="stat-card">
                <span>Membership</span>
                <strong>${getMembershipTier()}</strong>
            </div>
        </div>

        <h2>My ShopEasy</h2>

        <div class="account-menu">
            <a href="orders.html">
                📦 <strong>Orders</strong><br>View order history
            </a>
            <a href="wishlist.html">
                ❤️ <strong>Wishlist</strong><br>Saved products
            </a>
            <a href="cart.html">
                🛒 <strong>Cart</strong><br>Shopping cart
            </a>
            <a href="loyalty.html">
                ⭐ <strong>Loyalty</strong><br>Rewards and points
            </a>
        </div>

        <br>
        <h2>Recent Orders</h2>
        <div id="accountRecentOrders"></div>
    `;

    displayRecentAccountOrders();

    const logoutButton = document.getElementById("logoutAccountBtn");
    if (logoutButton) {
        logoutButton.addEventListener("click", logout);
    }
}

function displayRecentAccountOrders() {
    const container = document.getElementById("accountRecentOrders");
    if (!container) return;

    const recent = orders.slice(-3).reverse();

    if (recent.length === 0) {
        container.innerHTML = `
            <div class="order-card">
                No orders yet.
                <br><br>
                <a href="products.html" class="btn">
                    Start Shopping
                </a>
            </div>
        `;
        return;
    }

    recent.forEach(order => {
        const div = document.createElement("div");
        div.className = "order-card";

        div.innerHTML = `
            <div class="order-header">
                <div>
                    <strong>Order #${order.id}</strong>
                    <p>${order.date || order.dateString}</p>
                </div>
                <span class="order-status">${order.status || "Order Placed"}</span>
            </div>
            <p>Total: <strong>₹${order.total}</strong></p>
        `;

        container.appendChild(div);
    });
}

/* ================= LOGOUT ================= */

function logout() {
    loggedIn = false;
    user = null;
    localStorage.setItem("shopEasyLoggedIn", "false");
    localStorage.removeItem("shopEasyUser");
    window.location.href = "index.html";
}

/* ================= LOYALTY ================= */

function getMembershipTier() {
    if (loyaltyPoints >= 1000) return "Platinum";
    if (loyaltyPoints >= 500) return "Gold";
    if (loyaltyPoints >= 200) return "Silver";
    return "Bronze";
}

async function syncLoyaltyStatus() {
    if (!loggedIn || !user || !user.email) return;
    try {
        const res = await fetch(`${API_BASE}/api/loyalty/status?email=${encodeURIComponent(user.email)}`);
        if (res.ok) {
            const data = await res.json();
            if (data.loyaltyPoints !== undefined) {
                loyaltyPoints = data.loyaltyPoints;
                saveData();
            }
        }
    } catch (e) {
        console.warn("Could not sync loyalty with backend:", e);
    }
}

async function displayLoyalty() {
    await syncLoyaltyStatus();

    const points = document.getElementById("loyaltyPoints");
    const purchases = document.getElementById("purchaseCount");
    const monthly = document.getElementById("monthlyPurchaseCount");
    const tier = document.getElementById("membershipTier");

    if (points) points.textContent = loyaltyPoints;
    if (purchases) purchases.textContent = orders.length;
    if (monthly) monthly.textContent = getMonthlyPurchaseCount();
    if (tier) tier.textContent = getMembershipTier();

    const status = document.getElementById("monthlyRewardStatus");
    if (status) {
        const count = getMonthlyPurchaseCount();
        if (count === 2) {
            status.innerHTML = `🎉 Your 20% third-purchase discount is unlocked!`;
            status.className = "monthly-offer third-offer";
        } else if (count < 2) {
            status.innerHTML = `🛍️ Make ${2 - count} more purchase(s) this month to unlock your third-purchase offer.`;
        } else {
            status.innerHTML = `✓ Third-purchase offer used this month.`;
        }
    }
}

async function redeemPoints() {
    const message = document.getElementById("rewardMessage");
    if (!message) return;

    if (!loggedIn || !user) {
        message.textContent = "Please login first.";
        message.style.color = "#dc2626";
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/api/loyalty/redeem?email=${encodeURIComponent(user.email)}`, {
            method: "POST"
        });
        const data = await res.json();

        if (res.ok && data.success) {
            loyaltyPoints = data.remainingPoints;
            rewardDiscount = 10;
            message.textContent = "🎉 100 points redeemed. 10% reward discount applied.";
            message.style.color = "#16a34a";
            saveData();
            displayLoyalty();
            return;
        } else {
            message.textContent = data.message || "You need at least 100 points.";
            message.style.color = "#dc2626";
            return;
        }
    } catch (e) {
        if (loyaltyPoints >= 100) {
            loyaltyPoints -= 100;
            rewardDiscount = 10;
            message.textContent = "🎉 100 points redeemed. 10% reward discount applied.";
            message.style.color = "#16a34a";
            saveData();
            displayLoyalty();
        } else {
            message.textContent = "You need at least 100 points.";
            message.style.color = "#dc2626";
        }
    }
}

/* ================= ORDERS ================= */

async function syncBackendOrders() {
    if (!loggedIn || !user || !user.email) return;

    try {
        const res = await fetch(`${API_BASE}/api/orders?email=${encodeURIComponent(user.email)}`);
        if (res.ok) {
            const data = await res.json();
            if (Array.isArray(data) && data.length > 0) {
                orders = data;
                saveData();
            }
        }
    } catch (e) {
        console.warn("Could not sync orders from backend:", e);
    }
}

async function displayOrders() {
    const container = document.getElementById("orders-container");
    if (!container) return;

    if (!loggedIn || !user) {
        container.innerHTML = `
            <div class="form-container" style="text-align:center">
                <h2>Login Required</h2>
                <p>Login to view your orders.</p>
                <br>
                <a href="login.html" class="btn">Login</a>
            </div>
        `;
        return;
    }

    await syncBackendOrders();

    container.innerHTML = "";

    if (orders.length === 0) {
        container.innerHTML = `
            <div class="loading">
                📦 No orders yet.
                <br><br>
                <a href="products.html" class="btn">Start Shopping</a>
            </div>
        `;
        return;
    }

    [...orders].reverse().forEach(order => {
        const div = document.createElement("div");
        div.className = "order-card";

        let itemsHTML = "";
        const items = order.items || [];
        items.forEach(item => {
            itemsHTML += `
                <div class="order-product">
                    <img src="${item.thumbnail}" alt="${item.title}">
                    <div>
                        <strong>${item.title}</strong>
                        <p>Qty: ${item.quantity}</p>
                    </div>
                </div>
            `;
        });

        div.innerHTML = `
            <div class="order-header">
                <div>
                    <h3>Order #${order.id}</h3>
                    <p>${order.date || order.dateString}</p>
                </div>
                <span class="order-status">${order.status || "Order Placed"}</span>
            </div>

            ${itemsHTML}

            <hr>

            <p>Payment: <strong>${order.payment || order.paymentMethod}</strong></p>
            <p>Discount: <strong>${order.discountPercent || 0}%</strong></p>
            <p>Total: <strong>₹${order.total}</strong></p>
        `;

        container.appendChild(div);
    });
}

/* ================= CHECKOUT ================= */

function displayCheckout() {
    const container = document.getElementById("checkout-summary");
    if (!container) return;

    if (!loggedIn || !user) {
        container.innerHTML = `
            <div class="monthly-offer">
                Please <a href="login.html">login</a> before placing an order.
            </div>
        `;
        return;
    }

    if (cart.length === 0) {
        container.innerHTML = `
            <div class="monthly-offer">
                Your cart is empty. <a href="products.html">Shop now</a>
            </div>
        `;
        return;
    }

    const monthly = getMonthlyDiscount();

    container.innerHTML = `
        <div class="monthly-offer ${monthly > 0 ? "third-offer" : ""}">
            ${
                monthly > 0
                ? `🎉 Your third purchase gets 20% OFF!`
                : `Order Total: <strong>₹${getCartTotal()}</strong>`
            }
        </div>
    `;

    const name = document.getElementById("customerName");
    const email = document.getElementById("customerEmail");

    if (name) name.value = user.name || "";
    if (email) email.value = user.email || "";
}

/* ================= PLACE ORDER ================= */

async function placeOrder(event) {
    event.preventDefault();

    const message = document.getElementById("checkoutMessage");

    if (!loggedIn || !user) {
        message.textContent = "Please login first.";
        return;
    }

    if (cart.length === 0) {
        message.textContent = "Your cart is empty.";
        return;
    }

    const name = document.getElementById("customerName").value;
    const email = document.getElementById("customerEmail").value;
    const address = document.getElementById("address").value;
    const city = document.getElementById("city").value;
    const pincode = document.getElementById("pincode").value;
    const payment = document.getElementById("paymentMethod").value;

    const monthlyCount = getMonthlyPurchaseCount();
    const isThirdPurchase = (monthlyCount === 2);
    const monthlyDiscount = isThirdPurchase ? 20 : 0;
    const subtotal = getCartSubtotal();

    let totalDiscount = monthlyDiscount + couponDiscount + rewardDiscount;
    if (totalDiscount > 100) totalDiscount = 100;

    const total = Math.round(subtotal - (subtotal * totalDiscount / 100));

    const orderPayload = {
        id: Date.now(),
        date: new Date().toLocaleString(),
        monthKey: getCurrentMonthKey(),
        customer: name,
        email: email,
        address: address,
        city: city,
        pincode: pincode,
        payment: payment,
        items: cart.map(item => ({
            id: item.id,
            title: item.title,
            price: item.price,
            thumbnail: item.thumbnail,
            quantity: item.quantity
        })),
        subtotal: subtotal,
        discountPercent: totalDiscount,
        monthlyDiscount: monthlyDiscount,
        couponDiscount: couponDiscount,
        rewardDiscount: rewardDiscount,
        total: total,
        status: "Order Placed",
        userId: user.id || null
    };

    try {
        // Send order to Spring Boot backend REST API
        const response = await fetch(`${API_BASE}/api/orders`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(orderPayload)
        });

        if (response.ok) {
            const savedOrder = await response.json();
            orders.push(savedOrder);
            loyaltyPoints += Math.floor(savedOrder.total / 100);
        } else {
            orders.push(orderPayload);
            loyaltyPoints += Math.floor(total / 100);
        }
    } catch (e) {
        console.warn("Backend order submission failed, saved locally:", e);
        orders.push(orderPayload);
        loyaltyPoints += Math.floor(total / 100);
    }

    cart = [];
    couponDiscount = 0;
    rewardDiscount = 0;

    saveData();

    message.innerHTML = `
        🎉 <strong>Order placed successfully!</strong>
        <br>
        Order ID: #${orderPayload.id}
        <br><br>
        Redirecting to your orders...
    `;

    document.getElementById("checkoutForm").reset();
    updateCartCount();

    setTimeout(function() {
        window.location.href = "orders.html";
    }, 1500);
}

/* ================= CONTACT ================= */

async function contactFormSubmit(event) {
    event.preventDefault();

    const response = document.getElementById("contactResponse");
    const name = document.getElementById("contactName").value;
    const email = document.getElementById("contactEmail").value;
    const message = document.getElementById("contactMessage").value;

    try {
        const res = await fetch(`${API_BASE}/api/contact`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name: name, email: email, message: message })
        });
        const data = await res.json();
        response.textContent = "✓ " + (data.message || "Thank you! Your message has been received.");
        response.style.color = "#16a34a";
    } catch (e) {
        response.textContent = "✓ Thank you! Your message has been received.";
        response.style.color = "#16a34a";
    }

    document.getElementById("contactForm").reset();
}

/* ================= DARK MODE ================= */

function toggleDarkMode() {
    document.body.classList.toggle("dark-mode");
    const enabled = document.body.classList.contains("dark-mode");
    localStorage.setItem("shopEasyDarkMode", enabled);
}

function loadDarkMode() {
    const enabled = localStorage.getItem("shopEasyDarkMode");
    if (enabled === "true") {
        document.body.classList.add("dark-mode");
    }
}

/* ================= MOBILE MENU ================= */

function toggleMenu() {
    const navbar = document.getElementById("navbar");
    if (navbar) {
        navbar.classList.toggle("active");
    }
}

/* ================= TOAST ================= */

function showToast(message) {
    let toast = document.getElementById("shopEasyToast");

    if (!toast) {
        toast = document.createElement("div");
        toast.id = "shopEasyToast";
        toast.style.position = "fixed";
        toast.style.bottom = "25px";
        toast.style.right = "25px";
        toast.style.zIndex = "9999";
        toast.style.padding = "14px 20px";
        toast.style.background = "#172033";
        toast.style.color = "white";
        toast.style.borderRadius = "10px";
        toast.style.boxShadow = "0 10px 30px rgba(0,0,0,0.2)";
        toast.style.transition = "0.3s";
        document.body.appendChild(toast);
    }

    toast.textContent = message;
    toast.style.opacity = "1";

    setTimeout(function() {
        toast.style.opacity = "0";
    }, 1800);
}

/* ================= AUTH TABS ================= */

function setupAuthTabs() {
    const loginTab = document.getElementById("loginTab");
    const signupTab = document.getElementById("signupTab");
    const loginForm = document.getElementById("loginForm");
    const signupForm = document.getElementById("signupForm");

    if (!loginTab || !signupTab || !loginForm || !signupForm) {
        return;
    }

    loginTab.addEventListener("click", function() {
        loginTab.classList.add("active");
        signupTab.classList.remove("active");
        loginForm.classList.remove("hidden");
        signupForm.classList.add("hidden");
    });

    signupTab.addEventListener("click", function() {
        signupTab.classList.add("active");
        loginTab.classList.remove("active");
        signupForm.classList.remove("hidden");
        loginForm.classList.add("hidden");
    });
}

/* ================= EVENT LISTENERS ================= */

document.addEventListener("DOMContentLoaded", function() {
    loadDarkMode();
    updateCartCount();
    loadProducts();
    displayCart();
    displayWishlist();
    displayOrders();
    displayLoyalty();
    displayCheckout();
    displayProductDetails();
    displayAccount();

    /* Mobile menu */
    const menuBtn = document.getElementById("menuBtn");
    if (menuBtn) {
        menuBtn.addEventListener("click", toggleMenu);
    }

    /* Dark mode */
    const darkModeBtn = document.getElementById("darkModeBtn");
    if (darkModeBtn) {
        darkModeBtn.addEventListener("click", toggleDarkMode);
    }

    /* Search */
    const search = document.getElementById("searchInput");
    if (search) {
        search.addEventListener("input", searchProducts);
    }

    /* Sort */
    const sort = document.getElementById("sortSelect");
    if (sort) {
        sort.addEventListener("change", function() {
            sortProducts(sort.value);
        });
    }

    /* Random */
    const randomBtn = document.getElementById("randomBtn");
    if (randomBtn) {
        randomBtn.addEventListener("click", randomProducts);
    }

    /* Categories */
    document.querySelectorAll(".category-btn").forEach(button => {
        button.addEventListener("click", function() {
            filterCategory(button.dataset.category);
        });
    });

    /* Dynamic buttons */
    document.addEventListener("click", function(event) {
        const target = event.target;
        const id = target.dataset.id;

        if (target.classList.contains("add-cart")) {
            addToCart(id);
        }
        if (target.classList.contains("view-product")) {
            window.location.href = `product-details.html?id=${id}`;
        }
        if (target.classList.contains("wishlist-btn") || target.classList.contains("wishlist-remove")) {
            toggleWishlist(id);
        }
        if (target.classList.contains("increase-cart")) {
            increaseQuantity(id);
        }
        if (target.classList.contains("decrease-cart")) {
            decreaseQuantity(id);
        }
        if (target.classList.contains("remove-cart")) {
            removeFromCart(id);
        }
    });

    /* Coupon */
    const couponBtn = document.getElementById("couponBtn");
    if (couponBtn) {
        couponBtn.addEventListener("click", applyCoupon);
    }

    /* Login */
    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.addEventListener("submit", handleLogin);
    }

    /* Signup */
    const signupForm = document.getElementById("signupForm");
    if (signupForm) {
        signupForm.addEventListener("submit", handleSignup);
    }

    setupAuthTabs();

    /* Loyalty */
    const redeemBtn = document.getElementById("redeemBtn");
    if (redeemBtn) {
        redeemBtn.addEventListener("click", redeemPoints);
    }

    /* Checkout */
    const checkoutForm = document.getElementById("checkoutForm");
    if (checkoutForm) {
        checkoutForm.addEventListener("submit", placeOrder);
    }

    /* Contact */
    const contactForm = document.getElementById("contactForm");
    if (contactForm) {
        contactForm.addEventListener("submit", contactFormSubmit);
    }

    /* Service Worker */
    if ("serviceWorker" in navigator) {
        navigator.serviceWorker.register("./service-worker.js")
            .then(function() {
                console.log("ShopEasy PWA Service Worker registered.");
            })
            .catch(function(error) {
                console.log("Service Worker Error:", error);
            });
    }
});