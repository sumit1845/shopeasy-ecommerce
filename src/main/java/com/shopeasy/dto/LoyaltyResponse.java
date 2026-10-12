package com.shopeasy.dto;

public class LoyaltyResponse {

    private int loyaltyPoints;
    private String membershipTier;
    private long totalPurchases;
    private long monthlyPurchases;
    private boolean rewardAvailable;

    public LoyaltyResponse() {}

    public LoyaltyResponse(int loyaltyPoints, String membershipTier, long totalPurchases, long monthlyPurchases, boolean rewardAvailable) {
        this.loyaltyPoints = loyaltyPoints;
        this.membershipTier = membershipTier;
        this.totalPurchases = totalPurchases;
        this.monthlyPurchases = monthlyPurchases;
        this.rewardAvailable = rewardAvailable;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }

    public long getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(long totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public long getMonthlyPurchases() {
        return monthlyPurchases;
    }

    public void setMonthlyPurchases(long monthlyPurchases) {
        this.monthlyPurchases = monthlyPurchases;
    }

    public boolean isRewardAvailable() {
        return rewardAvailable;
    }

    public void setRewardAvailable(boolean rewardAvailable) {
        this.rewardAvailable = rewardAvailable;
    }
}
