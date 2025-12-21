package com.greengrocer.utils;

import com.greengrocer.models.CartItem;
import com.greengrocer.models.Order;
import com.greengrocer.models.OrderItem;
import com.greengrocer.models.User;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PDF Invoice Generator using iTextPDF library.
 * Generates PDF invoices stored as BLOB in database.
 * 
 * @author Group17
 * @version 1.0
 */
public class PdfInvoiceGenerator {

    /** Date formatter for invoice */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Custom fonts */
    private static Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    private static Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.GRAY);
    private static Font TOTAL_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);

    /**
     * Generates a PDF invoice as a byte array.
     * 
     * @param order           The order
     * @param customer        The customer
     * @param items           The cart items
     * @param discountPercent Applied discount percentage
     * @return PDF as byte array for storage in database
     */
    public static byte[] generatePdfInvoice(Order order, User customer, List<CartItem> items, double discountPercent) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add header
            addHeader(document, order);

            // Add customer details
            addCustomerDetails(document, customer);

            // Add delivery details
            addDeliveryDetails(document, order);

            // Add items table
            addItemsTable(document, items);

            // Add totals
            addTotals(document, order, discountPercent);

            // Add footer
            addFooter(document);

            document.close();

        } catch (DocumentException e) {
            System.err.println("Error generating PDF invoice: " + e.getMessage());
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    /**
     * Generates a PDF invoice from an existing order (for viewing history).
     * 
     * @param order The order with items loaded
     * @return PDF as byte array
     */
    public static byte[] generatePdfFromOrder(Order order) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Header
            Paragraph title = new Paragraph("Group17 GREENGROCER", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("INVOICE", HEADER_FONT);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            document.add(Chunk.NEWLINE);

            // Invoice info
            document.add(new Paragraph("Invoice No: INV-" + String.format("%06d", order.getId()), NORMAL_FONT));
            document.add(new Paragraph("Order Date: " + order.getOrderTime().format(DATE_FORMAT), NORMAL_FONT));
            document.add(new Paragraph("Status: " + order.getStatus(), NORMAL_FONT));

            document.add(Chunk.NEWLINE);

            // Items table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 3, 1.5f, 1.5f, 1.5f });

            // Table header
            addTableHeader(table, "Product", "Qty (kg)", "Price/kg", "Total");

            // Table rows
            for (OrderItem item : order.getItems()) {
                addTableRow(table,
                        item.getProductName(),
                        String.format("%.2f", item.getQuantity()),
                        String.format("$%.2f", item.getPriceAtTime()),
                        String.format("$%.2f", item.getTotalPrice()));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Totals
            Paragraph totals = new Paragraph();
            totals.add(new Chunk(String.format("Subtotal: $%.2f\n", order.getSubtotal()), NORMAL_FONT));
            if (order.getDiscount() > 0) {
                totals.add(new Chunk(String.format("Discount: -$%.2f\n", order.getDiscount()), NORMAL_FONT));
            }
            totals.add(new Chunk(String.format("VAT (18%%): $%.2f\n", order.getVat()), NORMAL_FONT));
            totals.add(new Chunk(String.format("TOTAL: $%.2f", order.getTotalCost()), TOTAL_FONT));
            totals.setAlignment(Element.ALIGN_RIGHT);
            document.add(totals);

            // Footer
            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Thank you for shopping at Group17 GreenGrocer!", SMALL_FONT);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (DocumentException e) {
            System.err.println("Error generating PDF invoice: " + e.getMessage());
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    /**
     * Adds the header section to the document.
     */
    private static void addHeader(Document document, Order order) throws DocumentException {
        Paragraph title = new Paragraph("Group17 GREENGROCER", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph("INVOICE", HEADER_FONT);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);

        document.add(Chunk.NEWLINE);

        // Invoice info
        document.add(new Paragraph("Invoice No: INV-" + String.format("%06d", order.getId()), NORMAL_FONT));
        document.add(new Paragraph("Date: " + LocalDateTime.now().format(DATE_FORMAT), NORMAL_FONT));

        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds customer details section.
     */
    private static void addCustomerDetails(Document document, User customer) throws DocumentException {
        document.add(new Paragraph("CUSTOMER DETAILS", HEADER_FONT));
        document.add(new LineSeparator());

        String name = customer.getFullName() != null ? customer.getFullName() : customer.getUsername();
        String address = customer.getAddress() != null ? customer.getAddress() : "N/A";
        String phone = customer.getPhone() != null ? customer.getPhone() : "N/A";
        String email = customer.getEmail() != null ? customer.getEmail() : "N/A";

        document.add(new Paragraph("Name: " + name, NORMAL_FONT));
        document.add(new Paragraph("Address: " + address, NORMAL_FONT));
        document.add(new Paragraph("Phone: " + phone, NORMAL_FONT));
        document.add(new Paragraph("Email: " + email, NORMAL_FONT));

        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds delivery details section.
     */
    private static void addDeliveryDetails(Document document, Order order) throws DocumentException {
        document.add(new Paragraph("DELIVERY DETAILS", HEADER_FONT));
        document.add(new LineSeparator());

        document.add(
                new Paragraph("Requested Delivery: " + order.getRequestedDelivery().format(DATE_FORMAT), NORMAL_FONT));

        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds the items table to the document.
     */
    private static void addItemsTable(Document document, List<CartItem> items) throws DocumentException {
        document.add(new Paragraph("ORDER ITEMS", HEADER_FONT));
        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 3, 1.5f, 1.5f, 1.5f });

        // Table header
        addTableHeader(table, "Product", "Qty (kg)", "Price/kg", "Total");

        // Table rows
        for (CartItem item : items) {
            addTableRow(table,
                    item.getProductName(),
                    String.format("%.2f", item.getQuantity()),
                    String.format("$%.2f", item.getPriceAtTime()),
                    String.format("$%.2f", item.getTotalPrice()));
        }

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds a header row to the table.
     */
    private static void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    /**
     * Adds a data row to the table.
     */
    private static void addTableRow(PdfPTable table, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value, NORMAL_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    /**
     * Adds the totals section to the document.
     */
    private static void addTotals(Document document, Order order, double discountPercent) throws DocumentException {
        Paragraph totals = new Paragraph();
        totals.add(new Chunk(String.format("Subtotal: $%.2f\n", order.getSubtotal()), NORMAL_FONT));

        if (discountPercent > 0) {
            totals.add(new Chunk(String.format("Discount (%.0f%%): -$%.2f\n", discountPercent, order.getDiscount()),
                    NORMAL_FONT));
        }

        totals.add(new Chunk(String.format("VAT (18%%): $%.2f\n", order.getVat()), NORMAL_FONT));
        totals.add(new Chunk(String.format("TOTAL: $%.2f", order.getTotalCost()), TOTAL_FONT));
        totals.setAlignment(Element.ALIGN_RIGHT);
        document.add(totals);
    }

    /**
     * Adds the footer section to the document.
     */
    private static void addFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);

        Paragraph thanks = new Paragraph("Thank you for shopping at Group17 GreenGrocer!", NORMAL_FONT);
        thanks.setAlignment(Element.ALIGN_CENTER);
        document.add(thanks);

        Paragraph contact = new Paragraph("For questions or concerns, please contact us through the app.", SMALL_FONT);
        contact.setAlignment(Element.ALIGN_CENTER);
        document.add(contact);
    }
}
