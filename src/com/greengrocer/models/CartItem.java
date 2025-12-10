package com.greengrocer.models;

/**
 * Represents an item in the shopping cart.
 * Cart items can be merged when same product is added multiple times.
 * 
 * @author Group17
 * @version 1.0
 */
public class CartItem {

    /** The product in this cart item */
    private Product product;

    /** Quantity in kilograms */
    private double quantity;

    /** Price at time of adding to cart (considering threshold) */
    private double priceAtTime;

    /**
     * Constructor for a cart item.
     * 
     * @param product  The product to add
     * @param quantity The quantity in kg
     */
    public CartItem(Product product, double quantity) {
        this.product = product;
        this.quantity = quantity;
        // Store the display price at the time of adding (considers threshold)
        this.priceAtTime = product.getDisplayPrice();
    }

    /**
     * Gets the product.
     * 
     * @return The product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Gets the quantity.
     * 
     * @return The quantity in kg
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Gets the price at time of adding.
     * 
     * @return The price per kg when item was added
     */
    public double getPriceAtTime() {
        return priceAtTime;
    }

    /**
     * Sets the product.
     * 
     * @param product The product
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Sets the quantity.
     * 
     * @param quantity The quantity in kg
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the price at time.
     * 
     * @param priceAtTime The price per kg
     */
    public void setPriceAtTime(double priceAtTime) {
        this.priceAtTime = priceAtTime;
    }

    /**
     * Adds more quantity to this cart item (for merging same products).
     * 
     * @param additionalQuantity The additional quantity to add in kg
     */
    public void addQuantity(double additionalQuantity) {
        this.quantity += additionalQuantity;
    }

    /**
     * Calculates the total price for this cart item.
     * 
     * @return Total price (quantity * price per kg)
     */
    public double getTotalPrice() {
        return quantity * priceAtTime;
    }

    /**
     * Gets the product name for display.
     * 
     * @return The product name
     */
    public String getProductName() {
        return product.getName();
    }

    /**
     * Gets the product ID.
     * 
     * @return The product ID
     */
    public int getProductId() {
        return product.getId();
    }

    @Override
    public String toString() {
        return String.format("%s - %.2f kg @ $%.2f/kg = $%.2f",
                product.getName(), quantity, priceAtTime, getTotalPrice());
    }
}
