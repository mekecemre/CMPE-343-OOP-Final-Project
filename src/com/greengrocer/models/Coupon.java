package com.greengrocer.models;

import java.time.LocalDate;

/**
 * Represents a discount coupon in the system.
 * Coupons are created and managed by the owner.
 * 
 * @author Group17
 * @version 1.0
 */
public class Coupon {

    /** Unique identifier for the coupon */
    private int id;

    /** Coupon code */
    private String code;

    /** Discount percentage (e.g., 10 for 10%) */
    private double discountPercent;

    /** Minimum order value required to use coupon */
    private double minOrderValue;

    /** Expiry date of the coupon */
    private LocalDate expiryDate;

    /** Whether the coupon is active */
    private boolean isActive;

    /**
     * Default constructor.
     */
    public Coupon() {
        this.isActive = true;
    }

    /**
     * Constructor with all fields.
     * 
     * @param code            Coupon code
     * @param discountPercent Discount percentage
     * @param minOrderValue   Minimum order value
     * @param expiryDate      Expiry date
     */
    public Coupon(String code, double discountPercent, double minOrderValue, LocalDate expiryDate) {
        this();
        this.code = code;
        this.discountPercent = discountPercent;
        this.minOrderValue = minOrderValue;
        this.expiryDate = expiryDate;
    }

    // ==================== GETTERS ====================

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public double getMinOrderValue() {
        return minOrderValue;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public boolean isActive() {
        return isActive;
    }

    // ==================== SETTERS ====================

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public void setMinOrderValue(double minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Checks if coupon is valid (active and not expired).
     * 
     * @return true if valid
     */
    public boolean isValid() {
        return isActive && (expiryDate == null || !expiryDate.isBefore(LocalDate.now()));
    }

    /**
     * Checks if order value meets minimum requirement.
     * 
     * @param orderValue The order value to check
     * @return true if order meets or exceeds minimum
     */
    public boolean meetsMinimum(double orderValue) {
        return orderValue >= minOrderValue;
    }

    /**
     * Calculates the discount amount for a given order value.
     * 
     * @param orderValue The order value
     * @return The discount amount
     */
    public double calculateDiscount(double orderValue) {
        if (isValid() && meetsMinimum(orderValue)) {
            return orderValue * (discountPercent / 100.0);
        }
        return 0;
    }

    @Override
    public String toString() {
        return code + " - " + discountPercent + "% off (min $" + minOrderValue + ")";
    }
}
