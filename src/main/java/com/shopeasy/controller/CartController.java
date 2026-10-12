package com.shopeasy.controller;

import com.shopeasy.model.CartItem;
import com.shopeasy.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        CartItem item = cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/update")
    public ResponseEntity<CartItem> updateQuantity(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        CartItem item = cartService.updateQuantity(userId, productId, quantity);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, String>> removeFromCart(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(Map.of("message", "Item removed from cart"));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("message", "Cart cleared successfully"));
    }
}
