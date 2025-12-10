package com.greengrocer.utils;

import com.greengrocer.models.CartItem;
import com.greengrocer.models.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * Shopping Cart Manager for the current shopping session.
 * Handles cart operations including merging same products.
 * Uses Singleton pattern.
 * 
 * @author Group17
 * @version 1.0
 */
public class CartManager {

    /** Singleton instance */
    private static CartManager instance;

    /** List of items in the cart */
    private List<CartItem> items;

    /** Minimum cart value required to checkout */
    public static final double MINIMUM_CART_VALUE = 10.0;

    /** VAT percentage */
    public static final double VAT_RATE = 0.18; // 18% VAT

    /**
     * Private constructor for singleton.
     */
    private CartManager() {
        this.items = new ArrayList<>();
    }

    /**
     * Gets the singleton instance.
     * 
     * @return CartManager instance
     */
    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    /**
     * Adds a product to the cart.
     * If the product already exists, quantities are merged.
     * 
     * @param product  The product to add
     * @param quantity The quantity in kg
     */
    public void addItem(Product product, double quantity) {
        // Check if product already in cart - merge if so
        for (CartItem item : items) {
            if (item.getProductId() == product.getId()) {
                // Merge quantities
                item.addQuantity(quantity);
                return;
            }
        }

        // New product - add to cart
        items.add(new CartItem(product, quantity));
    }

    /**
     * Removes an item from the cart.
     * 
     * @param productId The product ID to remove
     */
    public void removeItem(int productId) {
        items.removeIf(item -> item.getProductId() == productId);
    }

    /**
     * Updates the quantity of an item in the cart.
     * 
     * @param productId   The product ID
     * @param newQuantity The new quantity
     */
    public void updateQuantity(int productId, double newQuantity) {
        for (CartItem item : items) {
            if (item.getProductId() == productId) {
                item.setQuantity(newQuantity);
                return;
            }
        }
    }

    /**
     * Gets all items in the cart.
     * 
     * @return List of cart items
     */
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * Clears all items from the cart.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Calculates the subtotal (before tax).
     * 
     * @return Subtotal amount
     */
    public double getSubtotal() {
        double subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getTotalPrice();
        }
        return subtotal;
    }

    /**
     * Calculates the VAT amount.
     * 
     * @return VAT amount
     */
    public double getVat() {
        return getSubtotal() * VAT_RATE;
    }

    /**
     * Calculates the total including VAT.
     * 
     * @return Total amount
     */
    public double getTotal() {
        return getSubtotal() + getVat();
    }

    /**
     * Calculates total after applying discount.
     * 
     * @param discountPercent The discount percentage to apply
     * @return Total after discount
     */
    public double getTotalWithDiscount(double discountPercent) {
        double subtotal = getSubtotal();
        double discount = subtotal * (discountPercent / 100.0);
        double afterDiscount = subtotal - discount;
        double vat = afterDiscount * VAT_RATE;
        return afterDiscount + vat;
    }

    /**
     * Calculates the discount amount.
     * 
     * @param discountPercent The discount percentage
     * @return Discount amount
     */
    public double getDiscountAmount(double discountPercent) {
        return getSubtotal() * (discountPercent / 100.0);
    }

    /**
     * Checks if cart is empty.
     * 
     * @return true if empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Gets the number of items in cart.
     * 
     * @return Item count
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Checks if cart meets minimum value requirement.
     * 
     * @return true if meets minimum
     */
    public boolean meetsMinimum() {
        return getSubtotal() >= MINIMUM_CART_VALUE;
    }

    /**
     * Gets the minimum cart value.
     * 
     * @return Minimum value
     */
    public double getMinimumCartValue() {
        return MINIMUM_CART_VALUE;
    }
}
