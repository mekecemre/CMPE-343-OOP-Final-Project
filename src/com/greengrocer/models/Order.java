package com.greengrocer.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an order in the Greengrocer system.
 * Orders track customer purchases, delivery status, and invoices.
 * 
 * @author Group17
 * @version 1.0
 */
public class Order {

    /** Unique identifier for the order */
    private int id;

    /** ID of the customer who placed the order */
    private int userId;

    /** Username of the customer (for display) */
    private String customerName;

    /** Customer address (for delivery) */
    private String customerAddress;

    /** ID of the carrier assigned to deliver */
    private int carrierId;

    /** Name of the carrier (for display) */
    private String carrierName;

    /** Time when order was placed */
    private LocalDateTime orderTime;

    /** Requested delivery date and time */
    private LocalDateTime requestedDelivery;

    /** Actual delivery time */
    private LocalDateTime deliveryTime;

    /** Order status: PENDING, SELECTED, DELIVERED, CANCELLED */
    private String status;

    /** Subtotal before tax */
    private double subtotal;

    /** VAT amount (18%) */
    private double vat;

    /** Discount applied */
    private double discount;

    /** Total cost including tax and discount */
    private double totalCost;

    /** Invoice stored as text (CLOB) */
    private String invoice;

    /** Invoice stored as PDF (BLOB) */
    private byte[] invoicePdf;

    /** List of items in this order */
    private List<OrderItem> items;

    /**
     * Default constructor.
     */
    public Order() {
        this.items = new ArrayList<>();
        this.status = "PENDING";
    }

    /**
     * Constructor with basic order info.
     * 
     * @param userId            Customer ID
     * @param subtotal          Order subtotal
     * @param vat               VAT amount
     * @param totalCost         Total cost
     * @param requestedDelivery Requested delivery time
     */
    public Order(int userId, double subtotal, double vat, double totalCost,
            LocalDateTime requestedDelivery) {
        this();
        this.userId = userId;
        this.subtotal = subtotal;
        this.vat = vat;
        this.totalCost = totalCost;
        this.requestedDelivery = requestedDelivery;
        this.orderTime = LocalDateTime.now();
    }

    // ==================== GETTERS ====================

    /**
     * Gets the unique identifier for this order.
     * 
     * @return the order ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the ID of the customer who placed this order.
     * 
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Gets the name of the customer.
     * 
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Gets the delivery address for this order.
     * 
     * @return the customer address
     */
    public String getCustomerAddress() {
        return customerAddress;
    }

    /**
     * Gets the ID of the carrier assigned to deliver this order.
     * 
     * @return the carrier ID
     */
    public int getCarrierId() {
        return carrierId;
    }

    /**
     * Gets the name of the assigned carrier.
     * 
     * @return the carrier name
     */
    public String getCarrierName() {
        return carrierName;
    }

    /**
     * Gets the time when the order was placed.
     * 
     * @return the order time
     */
    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    /**
     * Gets the requested delivery date and time.
     * 
     * @return the requested delivery time
     */
    public LocalDateTime getRequestedDelivery() {
        return requestedDelivery;
    }

    /**
     * Gets the actual delivery time.
     * 
     * @return the delivery time, or null if not yet delivered
     */
    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * Gets the current status of the order.
     * 
     * @return the order status (PENDING, SELECTED, DELIVERED, or CANCELLED)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the subtotal before tax and discount.
     * 
     * @return the subtotal amount
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Gets the VAT (Value Added Tax) amount.
     * 
     * @return the VAT amount (18%)
     */
    public double getVat() {
        return vat;
    }

    /**
     * Gets the discount applied to this order.
     * 
     * @return the discount amount
     */
    public double getDiscount() {
        return discount;
    }

    /**
     * Gets the total cost including tax and discount.
     * 
     * @return the total cost
     */
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * Gets the text-based invoice content.
     * 
     * @return the invoice as a string
     */
    public String getInvoice() {
        return invoice;
    }

    /**
     * Gets the PDF invoice as a byte array.
     * 
     * @return the invoice PDF bytes
     */
    public byte[] getInvoicePdf() {
        return invoicePdf;
    }

    /**
     * Gets the list of items in this order.
     * 
     * @return the list of order items
     */
    public List<OrderItem> getItems() {
        return items;
    }

    // ==================== SETTERS ====================

    /**
     * Sets the unique identifier for this order.
     * 
     * @param id the order ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the ID of the customer who placed this order.
     * 
     * @param userId the user ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Sets the name of the customer.
     * 
     * @param customerName the customer name to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Sets the delivery address for this order.
     * 
     * @param customerAddress the customer address to set
     */
    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    /**
     * Sets the ID of the carrier assigned to deliver this order.
     * 
     * @param carrierId the carrier ID to set
     */
    public void setCarrierId(int carrierId) {
        this.carrierId = carrierId;
    }

    /**
     * Sets the name of the assigned carrier.
     * 
     * @param carrierName the carrier name to set
     */
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    /**
     * Sets the time when the order was placed.
     * 
     * @param orderTime the order time to set
     */
    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    /**
     * Sets the requested delivery date and time.
     * 
     * @param requestedDelivery the requested delivery time to set
     */
    public void setRequestedDelivery(LocalDateTime requestedDelivery) {
        this.requestedDelivery = requestedDelivery;
    }

    /**
     * Sets the actual delivery time.
     * 
     * @param deliveryTime the delivery time to set
     */
    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    /**
     * Sets the current status of the order.
     * 
     * @param status the order status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the subtotal before tax and discount.
     * 
     * @param subtotal the subtotal amount to set
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Sets the VAT (Value Added Tax) amount.
     * 
     * @param vat the VAT amount to set
     */
    public void setVat(double vat) {
        this.vat = vat;
    }

    /**
     * Sets the discount applied to this order.
     * 
     * @param discount the discount amount to set
     */
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    /**
     * Sets the total cost including tax and discount.
     * 
     * @param totalCost the total cost to set
     */
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Sets the text-based invoice content.
     * 
     * @param invoice the invoice string to set
     */
    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    /**
     * Sets the PDF invoice as a byte array.
     * 
     * @param invoicePdf the invoice PDF bytes to set
     */
    public void setInvoicePdf(byte[] invoicePdf) {
        this.invoicePdf = invoicePdf;
    }

    /**
     * Sets the list of items in this order.
     * 
     * @param items the list of order items to set
     */
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    /**
     * Adds an item to the order.
     * 
     * @param item The order item to add
     */
    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    /**
     * Checks if order is pending (available for carriers).
     * 
     * @return true if pending
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    /**
     * Checks if order is selected by a carrier.
     * 
     * @return true if selected
     */
    public boolean isSelected() {
        return "SELECTED".equals(status);
    }

    /**
     * Checks if order is delivered.
     * 
     * @return true if delivered
     */
    public boolean isDelivered() {
        return "DELIVERED".equals(status);
    }

    /**
     * Checks if order is cancelled.
     * 
     * @return true if cancelled
     */
    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    /**
     * Gets a formatted string of all items in the order.
     * 
     * @return Formatted items list
     */
    public String getItemsDisplay() {
        StringBuilder sb = new StringBuilder();
        for (OrderItem item : items) {
            sb.append(String.format("%s: %.2f kg\n", item.getProductName(), item.getQuantity()));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Order #" + id + " - " + status + " - $" + String.format("%.2f", totalCost);
    }
}
