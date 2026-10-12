package com.shopeasy.service;

import com.shopeasy.dto.LoyaltyResponse;
import com.shopeasy.model.User;
import com.shopeasy.repository.OrderRepository;
import com.shopeasy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class LoyaltyService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public LoyaltyService(UserRepository userRepository, OrderRepository orderRepository, OrderService orderService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    public LoyaltyResponse getLoyaltyStatus(String email, Long userId) {
        User user = findUser(email, userId);
        int points = (user != null) ? user.getLoyaltyPoints() : 0;
        String tier = computeMembershipTier(points);

        long totalOrders = 0;
        long monthlyOrders = 0;
        if (email != null && !email.trim().isEmpty()) {
            totalOrders = orderRepository.findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(email.trim()).size();
            monthlyOrders = orderService.getMonthlyPurchaseCount(email, userId);
        } else if (userId != null) {
            totalOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId).size();
            monthlyOrders = orderService.getMonthlyPurchaseCount(null, userId);
        }

        return new LoyaltyResponse(points, tier, totalOrders, monthlyOrders, points >= 100);
    }

    @Transactional
    public Map<String, Object> redeemPoints(String email, Long userId) {
        User user = findUser(email, userId);
        if (user == null) {
            return Map.of("success", false, "message", "User not found.");
        }

        if (user.getLoyaltyPoints() >= 100) {
            user.setLoyaltyPoints(user.getLoyaltyPoints() - 100);
            userRepository.save(user);
            return Map.of(
                    "success", true,
                    "message", "100 points redeemed. 10% reward discount applied.",
                    "remainingPoints", user.getLoyaltyPoints(),
                    "rewardDiscount", 10
            );
        } else {
            return Map.of(
                    "success", false,
                    "message", "You need at least 100 points.",
                    "currentPoints", user.getLoyaltyPoints()
            );
        }
    }

    public String computeMembershipTier(int points) {
        if (points >= 1000) return "Platinum";
        if (points >= 500) return "Gold";
        if (points >= 200) return "Silver";
        return "Bronze";
    }

    private User findUser(String email, Long userId) {
        if (userId != null) {
            Optional<User> u = userRepository.findById(userId);
            if (u.isPresent()) return u.get();
        }
        if (email != null && !email.trim().isEmpty()) {
            return userRepository.findByEmailIgnoreCase(email.trim()).orElse(null);
        }
        return null;
    }
}
