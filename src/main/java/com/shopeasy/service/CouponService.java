package com.shopeasy.service;

import com.shopeasy.model.Coupon;
import com.shopeasy.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Optional<Coupon> validateCoupon(String code) {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }
        return couponRepository.findByCodeIgnoreCaseAndActiveTrue(code.trim());
    }

    public Coupon save(Coupon coupon) {
        return couponRepository.save(coupon);
    }
}
