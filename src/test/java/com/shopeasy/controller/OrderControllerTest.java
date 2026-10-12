package com.shopeasy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopeasy.dto.OrderItemDto;
import com.shopeasy.dto.OrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testOrderPlacementAndThirdPurchaseDiscount() throws Exception {
        String customerEmail = "shopper_" + System.currentTimeMillis() + "@shopeasy.com";

        // Initial monthly offer: 0 purchases
        mockMvc.perform(get("/api/orders/monthly-offer").param("email", customerEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyPurchaseCount").value(0))
                .andExpect(jsonPath("$.thirdPurchaseOfferActive").value(false))
                .andExpect(jsonPath("$.discountPercent").value(0));

        // Create 1st order
        OrderRequest order1 = createSampleOrder(customerEmail, "Order 1");
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Order Placed"));

        // Check monthly offer: 1 purchase
        mockMvc.perform(get("/api/orders/monthly-offer").param("email", customerEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyPurchaseCount").value(1))
                .andExpect(jsonPath("$.thirdPurchaseOfferActive").value(false));

        // Create 2nd order
        OrderRequest order2 = createSampleOrder(customerEmail, "Order 2");
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order2)))
                .andExpect(status().isOk());

        // Check monthly offer: 2 purchases! 3rd purchase offer should now be active with 20% discount!
        mockMvc.perform(get("/api/orders/monthly-offer").param("email", customerEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyPurchaseCount").value(2))
                .andExpect(jsonPath("$.thirdPurchaseOfferActive").value(true))
                .andExpect(jsonPath("$.discountPercent").value(20));

        // Create 3rd order -> should receive 20% monthly discount
        OrderRequest order3 = createSampleOrder(customerEmail, "Order 3");
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyDiscount").value(20));

        // Verify order history for this user
        mockMvc.perform(get("/api/orders").param("email", customerEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }

    private OrderRequest createSampleOrder(String email, String label) {
        OrderRequest req = new OrderRequest();
        req.setCustomerName("Shopper Bob");
        req.setCustomerEmail(email);
        req.setAddress("42 Market Street");
        req.setCity("Mumbai");
        req.setPincode("400001");
        req.setPaymentMethod("UPI");
        req.setItems(List.of(
                new OrderItemDto(1L, "iPhone 15 Pro", 999.0, "https://example.com/thumb.png", 1)
        ));
        req.setSubtotal(79920);
        req.setTotal(79920);
        return req;
    }
}
