package com.greengrocer.models;

/**
 * Represents an individual item within an order.
 * Stores product details at the time of purchase.
 * 
 * @author Group17
 * @version 1.0
 */
public class OrderItem {

    /** Unique identifier for the order item */
    private int id;

    /** ID of the order this item belongs to */
    private int orderId;

    /** ID of the product */
    private int productId;

    /** Product name (stored for historical reference) */
    private String productName;

    /** Quantity in kilograms */
    private double quantity;

    /** Price per kg at time of purchase */
    private double priceAtTime;

    /**
     * Default constructor.
     */
    public OrderItem() {
    }

    /**
     * Constructor with all required fields.
     * 
     * @param orderId     Order ID
     * @param productId   Product ID
     * @param productName Product name
     * @param quantity    Quantity in kg
     * @param priceAtTime Price per kg at purchase time
     */
    public OrderItem(int orderId, int productId, String productName,
            double quantity, double priceAtTime) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
    }

    /**
     * Creates an OrderItem from a CartItem.
     * 
     * @param orderId  The order ID
     * @param cartItem The cart item to convert
     * @return A new OrderItem
     */
    public static OrderItem fromCartItem(int orderId, CartItem cartItem) {
        return new OrderItem(
                orderId,
                cartItem.getProductId(),
                cartItem.getProductName(),
                cartItem.getQuantity(),
                cartItem.getPriceAtTime());
    }

    // ==================== GETTERS ====================

    /**
     * Gets the unique identifier for this order item.
     * 
     * @return the order item ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the ID of the order this item belongs to.
     * 
     * @return the order ID
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Gets the ID of the product.
     * 
     * @return the product ID
     */
    public int getProductId() {
        return productId;
    }

    /**
     * Gets the name of the product.
     * 
     * @return the product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Gets the quantity of the product in kilograms.
     * 
     * @return the quantity in kg
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Gets the price per kilogram at the time of purchase.
     * 
     * @return the price per kg at purchase time
     */
    public double getPriceAtTime() {
        return priceAtTime;
    }

    /**
     * Calculates the total price for this item.
     * 
     * @return Total price (quantity * price per kg)
     */
    public double getTotalPrice() {
        return quantity * priceAtTime;
    }

    // ==================== SETTERS ====================

    /**
     * Sets the unique identifier for this order item.
     * 
     * @param id the order item ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the ID of the order this item belongs to.
     * 
     * @param orderId the order ID to set
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    /**
     * Sets the ID of the product.
     * 
     * @param productId the product ID to set
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     * Sets the name of the product.
     * 
     * @param productName the product name to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Sets the quantity of the product in kilograms.
     * 
     * @param quantity the quantity in kg to set
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the price per kilogram at the time of purchase.
     * 
     * @param priceAtTime the price per kg to set
     */
    public void setPriceAtTime(double priceAtTime) {
        this.priceAtTime = priceAtTime;
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f kg @ $%.2f = $%.2f",
                productName, quantity, priceAtTime, getTotalPrice());
    }
}
