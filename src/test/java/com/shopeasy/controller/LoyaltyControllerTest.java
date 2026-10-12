package com.shopeasy.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoyaltyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testLoyaltyStatusForDemoUser() throws Exception {
        // Demo user is pre-seeded with 150 points
        mockMvc.perform(get("/api/loyalty/status").param("email", "demo@shopeasy.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loyaltyPoints").isNumber())
                .andExpect(jsonPath("$.membershipTier").isNotEmpty());
    }

    @Test
    void testRedeemPoints() throws Exception {
        mockMvc.perform(post("/api/loyalty/redeem").param("email", "demo@shopeasy.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.rewardDiscount").value(10));
    }
}
