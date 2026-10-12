package com.shopeasy.controller;

import com.shopeasy.dto.CouponRequest;
import com.shopeasy.model.Coupon;
import com.shopeasy.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> applyCoupon(@RequestBody CouponRequest request) {
        return processCoupon(request.getCode());
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateCoupon(@RequestParam String code) {
        return processCoupon(code);
    }

    private ResponseEntity<Map<String, Object>> processCoupon(String code) {
        Optional<Coupon> couponOpt = couponService.validateCoupon(code);
        if (couponOpt.isPresent()) {
            Coupon coupon = couponOpt.get();
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "code", coupon.getCode(),
                    "discountPercent", coupon.getDiscountPercent(),
                    "message", "✓ " + coupon.getDiscountPercent() + "% coupon applied."
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "discountPercent", 0,
                    "message", "Invalid coupon."
            ));
        }
    }
}
