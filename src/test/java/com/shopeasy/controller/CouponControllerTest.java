package com.shopeasy.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testValidateSave10Coupon() throws Exception {
        mockMvc.perform(get("/api/coupons/validate").param("code", "SAVE10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.discountPercent").value(10));
    }

    @Test
    void testValidateSave20Coupon() throws Exception {
        mockMvc.perform(get("/api/coupons/validate").param("code", "SAVE20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.discountPercent").value(20));
    }

    @Test
    void testValidateInvalidCoupon() throws Exception {
        mockMvc.perform(get("/api/coupons/validate").param("code", "INVALID_CODE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false));
    }
}
