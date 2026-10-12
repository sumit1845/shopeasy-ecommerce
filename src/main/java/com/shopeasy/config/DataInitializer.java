package com.shopeasy.config;

import com.shopeasy.model.Coupon;
import com.shopeasy.model.Product;
import com.shopeasy.model.User;
import com.shopeasy.repository.CouponRepository;
import com.shopeasy.repository.ProductRepository;
import com.shopeasy.repository.UserRepository;
import com.shopeasy.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    public DataInitializer(ProductRepository productRepository,
                           CouponRepository couponRepository,
                           UserRepository userRepository) {
        this.productRepository = productRepository;
        this.couponRepository = couponRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        initCoupons();
        initDemoUser();
        initProducts();
    }

    private void initCoupons() {
        if (couponRepository.count() == 0) {
            couponRepository.save(new Coupon("SAVE10", 10, "10% discount on entire cart"));
            couponRepository.save(new Coupon("SAVE20", 20, "20% special discount on entire cart"));
            couponRepository.save(new Coupon("WELCOME15", 15, "15% welcome discount for new shoppers"));
        }
    }

    private void initDemoUser() {
        if (!userRepository.existsByEmailIgnoreCase("demo@shopeasy.com")) {
            User demo = new User(
                    "Demo User",
                    "demo@shopeasy.com",
                    AuthService.hashPassword("password123")
            );
            demo.setLoyaltyPoints(150); // Has enough points to test reward redemption immediately
            userRepository.save(demo);
        }
    }

    private void initProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        List<Product> list = new ArrayList<>();

        // Smartphones
        list.add(new Product(1L, "iPhone 15 Pro",
                "Titanium design, A17 Pro chip, customizable Action button, and a versatile camera system.",
                999.0, 4.8, 45, "Apple", "smartphones",
                "https://cdn.dummyjson.com/products/images/smartphones/iPhone%2013%20Pro/thumbnail.png"));
        list.add(new Product(2L, "Samsung Galaxy S24 Ultra",
                "Galaxy AI is here. Epic titanium build, 200MP camera, built-in S Pen, Snapdragon 8 Gen 3.",
                1199.0, 4.9, 30, "Samsung", "smartphones",
                "https://cdn.dummyjson.com/products/images/smartphones/Samsung%20Galaxy%20S10/thumbnail.png"));
        list.add(new Product(3L, "Google Pixel 8 Pro",
                "The pro phone engineered by Google. Advanced camera, Google Tensor G3, and next-gen AI.",
                899.0, 4.7, 50, "Google", "smartphones",
                "https://cdn.dummyjson.com/products/images/smartphones/Realme%20C35/thumbnail.png"));
        list.add(new Product(4L, "OnePlus 12 5G",
                "Snapdragon 8 Gen 3, 4th Gen Hasselblad Camera for Mobile, 5400 mAh battery with 100W charging.",
                799.0, 4.6, 60, "OnePlus", "smartphones",
                "https://cdn.dummyjson.com/products/images/smartphones/Oppo%20A57/thumbnail.png"));

        // Laptops
        list.add(new Product(5L, "MacBook Pro 16-inch M3",
                "Liquid Retina XDR display, M3 Max chip, up to 22 hours battery life, advanced connectivity.",
                2499.0, 4.9, 25, "Apple", "laptops",
                "https://cdn.dummyjson.com/products/images/laptops/Apple%20MacBook%20Pro%2014%20Inch%20Space%20Grey/thumbnail.png"));
        list.add(new Product(6L, "Dell XPS 15 InfinityEdge",
                "Stunning OLED 3.5K display, 13th Gen Intel Core i9, NVIDIA GeForce RTX 4070 graphics.",
                1899.0, 4.7, 35, "Dell", "laptops",
                "https://cdn.dummyjson.com/products/images/laptops/Dell%20XPS%2013%209315/thumbnail.png"));
        list.add(new Product(7L, "Lenovo ThinkPad X1 Carbon",
                "Ultralight carbon-fiber chassis, legendary keyboard, Intel vPro platform, all-day battery life.",
                1499.0, 4.6, 40, "Lenovo", "laptops",
                "https://cdn.dummyjson.com/products/images/laptops/Lenovo%20Yoga%20920/thumbnail.png"));
        list.add(new Product(8L, "ASUS ROG Zephyrus G14",
                "Compact gaming powerhouse with AMD Ryzen 9, NVIDIA RTX 4060, ROG Nebula Display.",
                1599.0, 4.8, 20, "ASUS", "laptops",
                "https://cdn.dummyjson.com/products/images/laptops/Asus%20Zenbook%20Pro%20Dual%20Screen%20Laptop/thumbnail.png"));

        // Tablets
        list.add(new Product(9L, "iPad Air M2",
                "Redesigned with M2 chip, stunning Liquid Retina display, landscape front camera, ultra-fast Wi-Fi 6E.",
                599.0, 4.8, 55, "Apple", "tablets",
                "https://cdn.dummyjson.com/products/images/tablets/iPad%20Mini%202021%20Starlight/thumbnail.png"));
        list.add(new Product(10L, "Samsung Galaxy Tab S9",
                "Dynamic AMOLED 2X display, IP68 water and dust resistance, included S Pen, Snapdragon 8 Gen 2.",
                799.0, 4.7, 32, "Samsung", "tablets",
                "https://cdn.dummyjson.com/products/images/tablets/Samsung%20Galaxy%20Tab%20S8%20Plus%20Grey/thumbnail.png"));

        // Men's Shirts
        list.add(new Product(11L, "Classic Oxford Cotton Shirt",
                "100% premium long-staple cotton, tailored fit, breathable weave, button-down collar.",
                39.0, 4.5, 120, "Ralph Lauren", "mens-shirts",
                "https://cdn.dummyjson.com/products/images/mens-shirts/Blue%20&%20Black%20Check%20Shirt/thumbnail.png"));
        list.add(new Product(12L, "Slim-Fit Linen Casual Shirt",
                "Lightweight pure linen, garment-dyed finish, relaxed collar, perfect for summer outings.",
                45.0, 4.4, 90, "Zara", "mens-shirts",
                "https://cdn.dummyjson.com/products/images/mens-shirts/Gigabyte%20Aorus%20Men%20Tshirt/thumbnail.png"));
        list.add(new Product(13L, "Formal French Cuff Dress Shirt",
                "Crisp pinpoint weave, spread collar with stays, includes brushed metal cufflinks.",
                55.0, 4.6, 75, "Tommy Hilfiger", "mens-shirts",
                "https://cdn.dummyjson.com/products/images/mens-shirts/Man%20Short%20Sleeve%20Shirt/thumbnail.png"));

        // Women's Dresses
        list.add(new Product(14L, "Floral Silk Wrap Maxi Dress",
                "Flowy silhouette, pure mulberry silk, vibrant hand-printed botanical patterns, tie waist.",
                89.0, 4.8, 48, "Zara", "womens-dresses",
                "https://cdn.dummyjson.com/products/images/womens-dresses/Corset%20Style%20Belt%20Casual%20Dress/thumbnail.png"));
        list.add(new Product(15L, "Elegant Evening Velvet Gown",
                "Plush stretch velvet, flattering sweetheart neckline, thigh-high slit, concealed zipper.",
                129.0, 4.9, 35, "H&M", "womens-dresses",
                "https://cdn.dummyjson.com/products/images/womens-dresses/Dress%20with%20Bow%20Detail/thumbnail.png"));
        list.add(new Product(16L, "Casual Pleated Midi Dress",
                "Breathable blend, accordion pleating, scoop neckline, versatile day-to-evening style.",
                59.0, 4.5, 60, "Mango", "womens-dresses",
                "https://cdn.dummyjson.com/products/images/womens-dresses/Black%20Women%27s%20Gown/thumbnail.png"));

        // Men's Shoes
        list.add(new Product(17L, "Nike Air Zoom Pegasus 40",
                "Responsive cushioning, engineered mesh upper, durable waffle outsole for everyday running.",
                130.0, 4.8, 80, "Nike", "mens-shoes",
                "https://cdn.dummyjson.com/products/images/mens-shoes/Nike%20Air%20Jordan%201%20Retro%20High%20OG/thumbnail.png"));
        list.add(new Product(18L, "Classic Leather Chelsea Boots",
                "Handcrafted full-grain calfskin leather, Goodyear welted sole, elastic side gussets.",
                160.0, 4.7, 45, "Clarks", "mens-shoes",
                "https://cdn.dummyjson.com/products/images/mens-shoes/Nike%20Baseball%20Cleats/thumbnail.png"));
        list.add(new Product(19L, "Adidas Ultraboost Light",
                "Boost midsole foam, Continental rubber traction, Primeknit+ textile upper for maximum bounce.",
                180.0, 4.7, 70, "Adidas", "mens-shoes",
                "https://cdn.dummyjson.com/products/images/mens-shoes/Puma%20Future%20Rider%20Trainers/thumbnail.png"));

        // Women's Shoes
        list.add(new Product(20L, "Classic Stiletto Pumps",
                "Glossy patent leather, pointed toe, 90mm slim heel, cushioned memory-foam footbed.",
                95.0, 4.6, 50, "Aldo", "womens-shoes",
                "https://cdn.dummyjson.com/products/images/womens-shoes/Black%20Heels/thumbnail.png"));
        list.add(new Product(21L, "Chic Strappy Block Heel Sandals",
                "Soft leather straps, supportive 60mm block heel, adjustable ankle buckle closure.",
                75.0, 4.5, 65, "Nine West", "womens-shoes",
                "https://cdn.dummyjson.com/products/images/womens-shoes/Pink%20Heels/thumbnail.png"));

        // Fragrances
        list.add(new Product(22L, "Bleu de Chanel Eau de Parfum",
                "An aromatic-woody fragrance with amber and musky notes, clean, deep and intensely sensual.",
                145.0, 4.9, 40, "Chanel", "fragrances",
                "https://cdn.dummyjson.com/products/images/fragrances/Chanel%20Coco%20Noir%20Eau%20De/thumbnail.png"));
        list.add(new Product(23L, "Dior Sauvage Parfum",
                "A highly concentrated interpretation with an extreme freshness tinted with warm oriental tones.",
                155.0, 4.8, 42, "Dior", "fragrances",
                "https://cdn.dummyjson.com/products/images/fragrances/Dior%20J%27adore/thumbnail.png"));
        list.add(new Product(24L, "Yves Saint Laurent Black Opium",
                "Addictive gourmand floral with notes of black coffee, white flowers and sensual vanilla.",
                135.0, 4.9, 38, "YSL", "fragrances",
                "https://cdn.dummyjson.com/products/images/fragrances/Gucci%20Bloom%20Eau%20de%20Puff/thumbnail.png"));

        // Skincare
        list.add(new Product(25L, "Hyaluronic Acid Hydrating Serum",
                "Multi-molecular weight hyaluronic acid combined with vitamin B5 for deep multi-depth hydration.",
                22.0, 4.7, 110, "The Ordinary", "skincare",
                "https://cdn.dummyjson.com/products/images/skin-care/Attitude%20Super%20Leaves%20Hand%20Soap/thumbnail.png"));
        list.add(new Product(26L, "Advanced Night Repair Complex",
                "Patented Chronolux Power Signal Technology repairs skin texture and dramatically reduces fine lines.",
                115.0, 4.8, 55, "Estee Lauder", "skincare",
                "https://cdn.dummyjson.com/products/images/skin-care/Olay%20Ultra%20Moisture%20Shea%20Butter%20Body%20Wash/thumbnail.png"));
        list.add(new Product(27L, "Daily UV Broad Spectrum Sunscreen SPF 50",
                "Non-greasy, invisible finish mineral sunscreen infused with soothing niacinamide and antioxidants.",
                28.0, 4.6, 95, "La Roche-Posay", "skincare",
                "https://cdn.dummyjson.com/products/images/skin-care/Vaseline%20Men%20Extra%20Strength%20Lotion/thumbnail.png"));

        // Groceries
        list.add(new Product(28L, "Organic Raw Forest Honey",
                "100% pure, unfiltered wildflower honey harvested responsibly from protected natural reserves.",
                18.0, 4.9, 150, "Nature's Basket", "groceries",
                "https://cdn.dummyjson.com/products/images/groceries/Honey%20Jar/thumbnail.png"));
        list.add(new Product(29L, "Single Origin Arabica Coffee Beans",
                "Medium roast Colombian Supremo with notes of caramel, roasted hazelnut and citrus blossom.",
                24.0, 4.8, 130, "Blue Tokai", "groceries",
                "https://cdn.dummyjson.com/products/images/groceries/Instant%20Coffee/thumbnail.png"));
        list.add(new Product(30L, "Extra Virgin Olive Oil Cold Pressed",
                "Estate-grown Koroneiki olives, cold extracted within 4 hours of harvest, rich peppery finish.",
                29.0, 4.7, 90, "Olivares", "groceries",
                "https://cdn.dummyjson.com/products/images/groceries/Cooking%20Oil/thumbnail.png"));

        // Home Decoration
        list.add(new Product(31L, "Handcrafted Ceramic Vase Set",
                "Modern minimalist trio of ceramic vases in matte beige, terracotta and slate gray.",
                48.0, 4.7, 65, "Artisan Home", "home-decoration",
                "https://cdn.dummyjson.com/products/images/home-decoration/Plant%20Pot/thumbnail.png"));
        list.add(new Product(32L, "Aromatic Amber & Sandalwood Candle",
                "Natural soy wax, dual cotton wicks, 60-hour clean burn time with soothing sandalwood notes.",
                26.0, 4.8, 85, "Aura Living", "home-decoration",
                "https://cdn.dummyjson.com/products/images/home-decoration/House%20Slippers%20Soffshood/thumbnail.png"));
        list.add(new Product(33L, "Bohemian Macrame Wall Hanging",
                "Hand-woven 100% natural cotton cord on driftwood branch, adds warmth and texture to any space.",
                35.0, 4.6, 70, "Boho Chic", "home-decoration",
                "https://cdn.dummyjson.com/products/images/home-decoration/Family%20Photo%20Frame/thumbnail.png"));

        productRepository.saveAll(list);
    }
}
