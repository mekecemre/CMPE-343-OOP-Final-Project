package com.greengrocer.models;

/**
 * Represents loyalty settings for the system.
 * Managed by the owner to define loyalty discount conditions.
 * 
 * @author Group17
 * @version 1.0
 */
public class LoyaltySettings {

    /** Unique identifier */
    private int id;

    /** Minimum number of completed orders to qualify for loyalty discount */
    private int minOrdersForDiscount;

    /** Discount percentage for loyal customers */
    private double discountPercent;

    /**
     * Default constructor with default values.
     */
    public LoyaltySettings() {
        this.minOrdersForDiscount = 5;
        this.discountPercent = 10.0;
    }

    /**
     * Constructor with custom values.
     * 
     * @param minOrdersForDiscount Minimum orders required
     * @param discountPercent      Discount percentage
     */
    public LoyaltySettings(int minOrdersForDiscount, double discountPercent) {
        this.minOrdersForDiscount = minOrdersForDiscount;
        this.discountPercent = discountPercent;
    }

    // ==================== GETTERS ====================

    public int getId() {
        return id;
    }

    public int getMinOrdersForDiscount() {
        return minOrdersForDiscount;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    // ==================== SETTERS ====================

    public void setId(int id) {
        this.id = id;
    }

    public void setMinOrdersForDiscount(int minOrdersForDiscount) {
        this.minOrdersForDiscount = minOrdersForDiscount;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    /**
     * Checks if a user qualifies for loyalty discount.
     * 
     * @param completedOrders Number of orders the user has completed
     * @return true if eligible for discount
     */
    public boolean isEligible(int completedOrders) {
        return completedOrders >= minOrdersForDiscount;
    }

    /**
     * Calculates the loyalty discount for a given order value.
     * 
     * @param orderValue      The order value
     * @param completedOrders User's completed orders count
     * @return The discount amount (0 if not eligible)
     */
    public double calculateDiscount(double orderValue, int completedOrders) {
        if (isEligible(completedOrders)) {
            return orderValue * (discountPercent / 100.0);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Loyalty: " + discountPercent + "% off after " + minOrdersForDiscount + " orders";
    }
}
