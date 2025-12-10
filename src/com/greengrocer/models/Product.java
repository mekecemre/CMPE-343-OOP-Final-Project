package com.greengrocer.models;

/**
 * Represents a product (vegetable or fruit) in the Greengrocer system.
 * Products have prices that double when stock falls below threshold.
 * 
 * @author Group17
 * @version 1.0
 */
public class Product {

    /** Unique identifier for the product */
    private int id;

    /** Name of the product */
    private String name;

    /** Type: VEGETABLE or FRUIT */
    private String type;

    /** Base price per kilogram */
    private double price;

    /** Current stock in kilograms */
    private double stock;

    /** Threshold below which price doubles */
    private double threshold;

    /** Product image as byte array (BLOB) */
    private byte[] image;

    /**
     * Default constructor.
     */
    public Product() {
    }

    /**
     * Constructor with basic product info.
     * 
     * @param name      Product name
     * @param type      Product type (VEGETABLE or FRUIT)
     * @param price     Base price per kg
     * @param stock     Current stock in kg
     * @param threshold Stock threshold for price doubling
     */
    public Product(String name, String type, double price, double stock, double threshold) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.stock = stock;
        this.threshold = threshold;
    }

    /**
     * Full constructor with all fields.
     * 
     * @param id        Product ID
     * @param name      Product name
     * @param type      Product type
     * @param price     Base price per kg
     * @param stock     Current stock in kg
     * @param threshold Stock threshold
     * @param image     Product image bytes
     */
    public Product(int id, String name, String type, double price,
            double stock, double threshold, byte[] image) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.stock = stock;
        this.threshold = threshold;
        this.image = image;
    }

    // ==================== GETTERS ====================

    /**
     * Gets the product ID.
     * 
     * @return The product ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the product name.
     * 
     * @return The product name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the product type.
     * 
     * @return The product type (VEGETABLE or FRUIT)
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the base price per kilogram.
     * 
     * @return The base price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Gets the current stock in kilograms.
     * 
     * @return The current stock
     */
    public double getStock() {
        return stock;
    }

    /**
     * Gets the threshold value.
     * 
     * @return The threshold in kg
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * Gets the product image bytes.
     * 
     * @return The image as byte array
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Gets the displayed price considering threshold.
     * If stock is at or below threshold, price is doubled.
     * 
     * @return The effective price to display to customer
     */
    public double getDisplayPrice() {
        if (stock <= threshold) {
            return price * 2; // Double the price when stock is low
        }
        return price;
    }

    /**
     * Checks if stock is at or below threshold (low stock).
     * 
     * @return true if low stock, false otherwise
     */
    public boolean isLowStock() {
        return stock <= threshold;
    }

    /**
     * Checks if this is a vegetable.
     * 
     * @return true if vegetable, false otherwise
     */
    public boolean isVegetable() {
        return "VEGETABLE".equals(type);
    }

    /**
     * Checks if this is a fruit.
     * 
     * @return true if fruit, false otherwise
     */
    public boolean isFruit() {
        return "FRUIT".equals(type);
    }

    // ==================== SETTERS ====================

    /**
     * Sets the product ID.
     * 
     * @param id The product ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the product name.
     * 
     * @param name The product name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the product type.
     * 
     * @param type The product type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the base price.
     * 
     * @param price The price per kg
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Sets the current stock.
     * 
     * @param stock The stock in kg
     */
    public void setStock(double stock) {
        this.stock = stock;
    }

    /**
     * Sets the threshold value.
     * 
     * @param threshold The threshold in kg
     */
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    /**
     * Sets the product image.
     * 
     * @param image The image as byte array
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", threshold=" + threshold +
                '}';
    }
}
