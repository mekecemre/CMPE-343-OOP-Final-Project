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

    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getQuantity() {
        return quantity;
    }

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

    public void setId(int id) {
        this.id = id;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setPriceAtTime(double priceAtTime) {
        this.priceAtTime = priceAtTime;
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f kg @ $%.2f = $%.2f",
                productName, quantity, priceAtTime, getTotalPrice());
    }
}
