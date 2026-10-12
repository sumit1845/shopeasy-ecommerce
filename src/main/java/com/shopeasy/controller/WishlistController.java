package com.shopeasy.controller;

import com.shopeasy.model.WishlistItem;
import com.shopeasy.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<List<WishlistItem>> getWishlist(@RequestParam Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleWishlist(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        boolean added = wishlistService.toggleWishlist(userId, productId);
        return ResponseEntity.ok(Map.of(
                "added", added,
                "message", added ? "Added to wishlist" : "Removed from wishlist"
        ));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, String>> removeFromWishlist(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.ok(Map.of("message", "Removed from wishlist"));
    }
}
