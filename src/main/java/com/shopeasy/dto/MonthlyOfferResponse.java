package com.shopeasy.dto;

public class MonthlyOfferResponse {

    private long monthlyPurchaseCount;
    private boolean thirdPurchaseOfferActive;
    private int discountPercent;
    private String monthKey;
    private String message;

    public MonthlyOfferResponse() {}

    public MonthlyOfferResponse(long monthlyPurchaseCount, boolean thirdPurchaseOfferActive, int discountPercent, String monthKey, String message) {
        this.monthlyPurchaseCount = monthlyPurchaseCount;
        this.thirdPurchaseOfferActive = thirdPurchaseOfferActive;
        this.discountPercent = discountPercent;
        this.monthKey = monthKey;
        this.message = message;
    }

    public long getMonthlyPurchaseCount() {
        return monthlyPurchaseCount;
    }

    public void setMonthlyPurchaseCount(long monthlyPurchaseCount) {
        this.monthlyPurchaseCount = monthlyPurchaseCount;
    }

    public boolean isThirdPurchaseOfferActive() {
        return thirdPurchaseOfferActive;
    }

    public void setThirdPurchaseOfferActive(boolean thirdPurchaseOfferActive) {
        this.thirdPurchaseOfferActive = thirdPurchaseOfferActive;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getMonthKey() {
        return monthKey;
    }

    public void setMonthKey(String monthKey) {
        this.monthKey = monthKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
