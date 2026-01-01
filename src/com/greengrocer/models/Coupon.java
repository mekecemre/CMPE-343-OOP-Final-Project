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

    /**
     * Gets the unique identifier for this coupon.
     * 
     * @return the coupon ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the coupon code.
     * 
     * @return the coupon code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the discount percentage.
     * 
     * @return the discount percentage (e.g., 10 for 10%)
     */
    public double getDiscountPercent() {
        return discountPercent;
    }

    /**
     * Gets the minimum order value required to use this coupon.
     * 
     * @return the minimum order value
     */
    public double getMinOrderValue() {
        return minOrderValue;
    }

    /**
     * Gets the expiry date of the coupon.
     * 
     * @return the expiry date, or null if no expiry
     */
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /**
     * Checks if the coupon is currently active.
     * 
     * @return true if the coupon is active
     */
    public boolean isActive() {
        return isActive;
    }

    // ==================== SETTERS ====================

    /**
     * Sets the unique identifier for this coupon.
     * 
     * @param id the coupon ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the coupon code.
     * 
     * @param code the coupon code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Sets the discount percentage.
     * 
     * @param discountPercent the discount percentage to set
     */
    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    /**
     * Sets the minimum order value required to use this coupon.
     * 
     * @param minOrderValue the minimum order value to set
     */
    public void setMinOrderValue(double minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    /**
     * Sets the expiry date of the coupon.
     * 
     * @param expiryDate the expiry date to set
     */
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Sets whether the coupon is active.
     * 
     * @param active true to activate the coupon, false to deactivate
     */
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
