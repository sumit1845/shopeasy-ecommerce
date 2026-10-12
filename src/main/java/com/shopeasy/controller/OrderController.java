package com.shopeasy.controller;

import com.shopeasy.dto.MonthlyOfferResponse;
import com.shopeasy.dto.OrderRequest;
import com.shopeasy.model.Order;
import com.shopeasy.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody OrderRequest request) {
        Order order = orderService.placeOrder(request);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long userId) {
        List<Order> orders = orderService.getOrdersByUser(email, userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/monthly-offer")
    public ResponseEntity<MonthlyOfferResponse> getMonthlyOffer(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long userId) {
        MonthlyOfferResponse offer = orderService.getMonthlyOffer(email, userId);
        return ResponseEntity.ok(offer);
    }
}
