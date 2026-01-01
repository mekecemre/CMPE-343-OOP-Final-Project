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

    /**
     * Gets the unique identifier for this loyalty settings record.
     * 
     * @return the settings ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the minimum number of orders required for loyalty discount.
     * 
     * @return the minimum orders required
     */
    public int getMinOrdersForDiscount() {
        return minOrdersForDiscount;
    }

    /**
     * Gets the discount percentage for loyal customers.
     * 
     * @return the discount percentage
     */
    public double getDiscountPercent() {
        return discountPercent;
    }

    // ==================== SETTERS ====================

    /**
     * Sets the unique identifier for this loyalty settings record.
     * 
     * @param id the settings ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the minimum number of orders required for loyalty discount.
     * 
     * @param minOrdersForDiscount the minimum orders to set
     */
    public void setMinOrdersForDiscount(int minOrdersForDiscount) {
        this.minOrdersForDiscount = minOrdersForDiscount;
    }

    /**
     * Sets the discount percentage for loyal customers.
     * 
     * @param discountPercent the discount percentage to set
     */
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
