package com.shopeasy.controller;

import com.shopeasy.dto.LoyaltyResponse;
import com.shopeasy.service.LoyaltyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/loyalty")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping("/status")
    public ResponseEntity<LoyaltyResponse> getLoyaltyStatus(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(loyaltyService.getLoyaltyStatus(email, userId));
    }

    @PostMapping("/redeem")
    public ResponseEntity<Map<String, Object>> redeemPoints(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long userId) {
        Map<String, Object> result = loyaltyService.redeemPoints(email, userId);
        return ResponseEntity.ok(result);
    }
}
