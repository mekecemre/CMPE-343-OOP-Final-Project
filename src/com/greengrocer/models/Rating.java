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

    /**
     * Gets the unique identifier for this rating.
     * 
     * @return the rating ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the ID of the order being rated.
     * 
     * @return the order ID
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Gets the ID of the carrier being rated.
     * 
     * @return the carrier ID
     */
    public int getCarrierId() {
        return carrierId;
    }

    /**
     * Gets the name of the carrier being rated.
     * 
     * @return the carrier name
     */
    public String getCarrierName() {
        return carrierName;
    }

    /**
     * Gets the ID of the customer who gave the rating.
     * 
     * @return the customer ID
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Gets the name of the customer who gave the rating.
     * 
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Gets the rating value (1-5 stars).
     * 
     * @return the rating value
     */
    public int getRating() {
        return rating;
    }

    /**
     * Gets the optional comment from the customer.
     * 
     * @return the comment, or null if none
     */
    public String getComment() {
        return comment;
    }

    /**
     * Gets the time when the rating was created.
     * 
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ==================== SETTERS ====================

    /**
     * Sets the unique identifier for this rating.
     * 
     * @param id the rating ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the ID of the order being rated.
     * 
     * @param orderId the order ID to set
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    /**
     * Sets the ID of the carrier being rated.
     * 
     * @param carrierId the carrier ID to set
     */
    public void setCarrierId(int carrierId) {
        this.carrierId = carrierId;
    }

    /**
     * Sets the name of the carrier being rated.
     * 
     * @param carrierName the carrier name to set
     */
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    /**
     * Sets the ID of the customer who gave the rating.
     * 
     * @param customerId the customer ID to set
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Sets the name of the customer who gave the rating.
     * 
     * @param customerName the customer name to set
     */
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

    /**
     * Sets the optional comment from the customer.
     * 
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Sets the time when the rating was created.
     * 
     * @param createdAt the creation timestamp to set
     */
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
