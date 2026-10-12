package com.shopeasy.service;

import com.shopeasy.dto.MonthlyOfferResponse;
import com.shopeasy.dto.OrderItemDto;
import com.shopeasy.dto.OrderRequest;
import com.shopeasy.model.Order;
import com.shopeasy.model.OrderItem;
import com.shopeasy.model.User;
import com.shopeasy.repository.CartItemRepository;
import com.shopeasy.repository.OrderRepository;
import com.shopeasy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public String getCurrentMonthKey() {
        return YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    public long getMonthlyPurchaseCount(String email, Long userId) {
        String monthKey = getCurrentMonthKey();
        if (email != null && !email.trim().isEmpty()) {
            return orderRepository.countByCustomerEmailIgnoreCaseAndMonthKey(email.trim(), monthKey);
        } else if (userId != null) {
            return orderRepository.countByUserIdAndMonthKey(userId, monthKey);
        }
        return 0;
    }

    public MonthlyOfferResponse getMonthlyOffer(String email, Long userId) {
        String monthKey = getCurrentMonthKey();
        long count = getMonthlyPurchaseCount(email, userId);
        boolean isThirdPurchase = (count == 2);
        int discount = isThirdPurchase ? 20 : 0;

        String message;
        if (count == 0) {
            message = "You have made 0 purchases this month. Buy twice to unlock 20% OFF on your 3rd purchase.";
        } else if (count == 1) {
            message = "You have made 1 purchase this month. One more purchase unlocks your 20% OFF 3rd purchase offer.";
        } else if (count == 2) {
            message = "20% OFF UNLOCKED! Your next purchase is your 3rd purchase of this month.";
        } else {
            message = "Your 3rd-purchase monthly offer has already been used this month.";
        }

        return new MonthlyOfferResponse(count, isThirdPurchase, discount, monthKey, message);
    }

    @Transactional
    public Order placeOrder(OrderRequest request) {
        String monthKey = getCurrentMonthKey();
        long monthlyCount = getMonthlyPurchaseCount(request.getCustomerEmail(), request.getUserId());
        boolean isThirdPurchase = (monthlyCount == 2);
        int monthlyDiscount = isThirdPurchase ? 20 : 0;

        int couponDiscount = request.getCouponDiscount() != null ? request.getCouponDiscount() : 0;
        int rewardDiscount = request.getRewardDiscount() != null ? request.getRewardDiscount() : 0;

        int totalDiscountPercent = Math.min(100, monthlyDiscount + couponDiscount + rewardDiscount);

        // Calculate subtotal if not provided or to verify
        int calculatedSubtotal = 0;
        if (request.getItems() != null) {
            for (OrderItemDto itemDto : request.getItems()) {
                int itemPrice = (int) Math.round((itemDto.getPrice() != null ? itemDto.getPrice() : 0.0) * 80);
                int qty = itemDto.getQuantity() != null ? itemDto.getQuantity() : 1;
                calculatedSubtotal += itemPrice * qty;
            }
        }

        int subtotal = (request.getSubtotal() != null && request.getSubtotal() > 0)
                ? request.getSubtotal()
                : calculatedSubtotal;

        int total = (int) Math.round(subtotal - (subtotal * totalDiscountPercent / 100.0));

        Order order = new Order();
        order.setId(request.getId() != null ? request.getId() : System.currentTimeMillis());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setAddress(request.getAddress());
        order.setCity(request.getCity());
        order.setPincode(request.getPincode());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setMonthKey(monthKey);
        order.setSubtotal(subtotal);
        order.setDiscountPercent(totalDiscountPercent);
        order.setMonthlyDiscount(monthlyDiscount);
        order.setCouponDiscount(couponDiscount);
        order.setRewardDiscount(rewardDiscount);
        order.setTotal(total);
        order.setStatus("Order Placed");
        order.setUserId(request.getUserId());
        order.setCreatedAt(LocalDateTime.now());
        order.setDateString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a")));

        if (request.getItems() != null) {
            for (OrderItemDto itemDto : request.getItems()) {
                OrderItem item = new OrderItem(
                        itemDto.getId(),
                        itemDto.getTitle(),
                        itemDto.getPrice(),
                        itemDto.getThumbnail(),
                        itemDto.getQuantity() != null ? itemDto.getQuantity() : 1
                );
                order.addItem(item);
            }
        }

        Order saved = orderRepository.save(order);

        // Award loyalty points: 1 point for every 100 spent
        int pointsEarned = total / 100;
        if (pointsEarned > 0) {
            Optional<User> userOpt = Optional.empty();
            if (request.getUserId() != null) {
                userOpt = userRepository.findById(request.getUserId());
            } else if (request.getCustomerEmail() != null) {
                userOpt = userRepository.findByEmailIgnoreCase(request.getCustomerEmail().trim());
            }

            userOpt.ifPresent(u -> {
                u.setLoyaltyPoints(u.getLoyaltyPoints() + pointsEarned);
                userRepository.save(u);
            });
        }

        // Clear cart if user exists
        if (request.getUserId() != null) {
            cartItemRepository.deleteByUserId(request.getUserId());
        }

        return saved;
    }

    public List<Order> getOrdersByUser(String email, Long userId) {
        if (email != null && !email.trim().isEmpty()) {
            return orderRepository.findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(email.trim());
        } else if (userId != null) {
            return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
}
