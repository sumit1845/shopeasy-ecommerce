package com.shopeasy.dto;

public class CouponRequest {

    private String code;

    public CouponRequest() {}

    public CouponRequest(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
