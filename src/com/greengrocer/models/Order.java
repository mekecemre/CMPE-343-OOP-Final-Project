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

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public int getCarrierId() {
        return carrierId;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public LocalDateTime getRequestedDelivery() {
        return requestedDelivery;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public String getStatus() {
        return status;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getVat() {
        return vat;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public String getInvoice() {
        return invoice;
    }

    public byte[] getInvoicePdf() {
        return invoicePdf;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    // ==================== SETTERS ====================

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setCarrierId(int carrierId) {
        this.carrierId = carrierId;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public void setRequestedDelivery(LocalDateTime requestedDelivery) {
        this.requestedDelivery = requestedDelivery;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public void setInvoicePdf(byte[] invoicePdf) {
        this.invoicePdf = invoicePdf;
    }

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
