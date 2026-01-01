package com.greengrocer.utils;

import com.greengrocer.models.CartItem;
import com.greengrocer.models.Order;
import com.greengrocer.models.OrderItem;
import com.greengrocer.models.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Invoice Generator for creating text-based invoices.
 * Generates invoice content stored as CLOB in database.
 * 
 * @author Group17
 * @version 1.0
 */
public class InvoiceGenerator {

    /** Date formatter for invoice */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private InvoiceGenerator() {
    }

    /**
     * Generates an invoice for an order.
     * 
     * @param order           The order
     * @param customer        The customer
     * @param items           The cart items
     * @param discountPercent Applied discount percentage
     * @return Invoice as formatted string
     */
    public static String generateInvoice(Order order, User customer, List<CartItem> items, double discountPercent) {
        StringBuilder invoice = new StringBuilder();

        // Header
        invoice.append("===========================================================\n");
        invoice.append("                    Group17 GREENGROCER                    \n");
        invoice.append("                         INVOICE                           \n");
        invoice.append("===========================================================\n\n");

        // Order Info
        invoice.append("Invoice No: INV-").append(String.format("%06d", order.getId())).append("\n");
        invoice.append("Date: ").append(LocalDateTime.now().format(DATE_FORMAT)).append("\n");
        invoice.append("\n");

        // Customer Info
        invoice.append("CUSTOMER DETAILS:\n");
        invoice.append("-----------------------------------------------------------\n");
        invoice.append("Name: ")
                .append(customer.getFullName() != null ? customer.getFullName() : customer.getUsername()).append("\n");
        invoice.append("Address: ").append(customer.getAddress() != null ? customer.getAddress() : "N/A").append("\n");
        invoice.append("Phone: ").append(customer.getPhone() != null ? customer.getPhone() : "N/A").append("\n");
        invoice.append("Email: ").append(customer.getEmail() != null ? customer.getEmail() : "N/A").append("\n");
        invoice.append("\n");

        // Delivery Info
        invoice.append("DELIVERY DETAILS:\n");
        invoice.append("-----------------------------------------------------------\n");
        invoice.append("Requested Delivery: ").append(order.getRequestedDelivery().format(DATE_FORMAT)).append("\n");
        invoice.append("\n");

        // Items
        invoice.append("ORDER ITEMS:\n");
        invoice.append("-----------------------------------------------------------\n");
        invoice.append(String.format("%-20s %10s %12s %12s\n", "Product", "Qty (kg)", "Price/kg", "Total"));
        invoice.append("-----------------------------------------------------------\n");

        for (CartItem item : items) {
            invoice.append(String.format("%-20s %10.2f %12.2f %12.2f\n",
                    item.getProductName(),
                    item.getQuantity(),
                    item.getPriceAtTime(),
                    item.getTotalPrice()));
        }

        invoice.append("-----------------------------------------------------------\n");

        // Totals
        invoice.append(String.format("%44s %12.2f\n", "Subtotal:", order.getSubtotal()));

        if (discountPercent > 0) {
            invoice.append(
                    String.format("%44s %12.2f\n", "Discount (" + discountPercent + "%):", -order.getDiscount()));
        }

        invoice.append(String.format("%44s %12.2f\n", "VAT (18%):", order.getVat()));
        invoice.append("===========================================================\n");
        invoice.append(String.format("%44s %12.2f\n", "TOTAL:", order.getTotalCost()));
        invoice.append("===========================================================\n");

        // Footer
        invoice.append("\n");
        invoice.append("Thank you for shopping at Group17 GreenGrocer!\n");
        invoice.append("For questions or concerns, please contact us through the app.\n");
        invoice.append("\n");
        invoice.append("===========================================================\n");

        return invoice.toString();
    }

    /**
     * Generates a simple invoice from order items (for viewing history).
     * 
     * @param order The order with items loaded
     * @return Invoice as formatted string
     */
    public static String generateFromOrder(Order order) {
        StringBuilder invoice = new StringBuilder();

        // Header
        invoice.append("===========================================================\n");
        invoice.append("                    Group17 GREENGROCER                    \n");
        invoice.append("                         INVOICE                           \n");
        invoice.append("===========================================================\n\n");

        // Order Info
        invoice.append("Invoice No: INV-").append(String.format("%06d", order.getId())).append("\n");
        invoice.append("Order Date: ").append(order.getOrderTime().format(DATE_FORMAT)).append("\n");
        invoice.append("Status: ").append(order.getStatus()).append("\n");
        invoice.append("\n");

        // Items
        invoice.append("ORDER ITEMS:\n");
        invoice.append("-----------------------------------------------------------\n");
        invoice.append(String.format("%-20s %10s %12s %12s\n", "Product", "Qty (kg)", "Price/kg", "Total"));
        invoice.append("-----------------------------------------------------------\n");

        for (OrderItem item : order.getItems()) {
            invoice.append(String.format("%-20s %10.2f %12.2f %12.2f\n",
                    item.getProductName(),
                    item.getQuantity(),
                    item.getPriceAtTime(),
                    item.getTotalPrice()));
        }

        invoice.append("-----------------------------------------------------------\n");

        // Totals
        invoice.append(String.format("%44s %12.2f\n", "Subtotal:", order.getSubtotal()));

        if (order.getDiscount() > 0) {
            invoice.append(String.format("%44s %12.2f\n", "Discount:", -order.getDiscount()));
        }

        invoice.append(String.format("%44s %12.2f\n", "VAT (18%):", order.getVat()));
        invoice.append("===========================================================\n");
        invoice.append(String.format("%44s %12.2f\n", "TOTAL:", order.getTotalCost()));
        invoice.append("===========================================================\n");

        return invoice.toString();
    }
}
