package com.greengrocer.models;

import java.time.LocalDateTime;

/**
 * Represents a carrier rating from a customer.
 * Customers can rate carriers after delivery is completed.
 * 
 * @author Group17
 * @version 1.0
 */
public class Rating {

    /** Unique identifier for the rating */
    private int id;

    /** ID of the order being rated */
    private int orderId;

    /** ID of the carrier being rated */
    private int carrierId;

    /** Name of the carrier (for display) */
    private String carrierName;

    /** ID of the customer giving the rating */
    private int customerId;

    /** Name of the customer (for display) */
    private String customerName;

    /** Rating value (1-5 stars) */
    private int rating;

    /** Optional comment from customer */
    private String comment;

    /** Time when rating was created */
    private LocalDateTime createdAt;

    /**
     * Default constructor.
     */
    public Rating() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructor with required fields.
     * 
     * @param orderId    Order ID
     * @param carrierId  Carrier ID
     * @param customerId Customer ID
     * @param rating     Rating value (1-5)
     * @param comment    Optional comment
     */
    public Rating(int orderId, int carrierId, int customerId, int rating, String comment) {
        this();
        this.orderId = orderId;
        this.carrierId = carrierId;
        this.customerId = customerId;
        setRating(rating); // Use setter for validation
        this.comment = comment;
    }

    // ==================== GETTERS ====================

    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getCarrierId() {
        return carrierId;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ==================== SETTERS ====================

    public void setId(int id) {
        this.id = id;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setCarrierId(int carrierId) {
        this.carrierId = carrierId;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Sets the rating value with validation.
     * 
     * @param rating Rating value (must be 1-5)
     */
    public void setRating(int rating) {
        if (rating < 1) {
            this.rating = 1;
        } else if (rating > 5) {
            this.rating = 5;
        } else {
            this.rating = rating;
        }
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns a star representation of the rating.
     * 
     * @return String of stars (e.g., "★★★★☆" for 4 stars)
     */
    public String getStarsDisplay() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    @Override
    public String toString() {
        return getStarsDisplay() + " - " + (comment != null ? comment : "No comment");
    }
}
