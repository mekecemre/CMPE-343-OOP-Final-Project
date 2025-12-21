package com.greengrocer.controllers;

import com.greengrocer.database.*;
import com.greengrocer.models.*;
import com.greengrocer.utils.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller for the Shopping Cart window.
 * Handles cart management, checkout, and order creation.
 * 
 * @author Group17
 * @version 1.0
 */
public class ShoppingCartController {

    @FXML
    private TableView<CartItem> cartTable;
    @FXML
    private TableColumn<CartItem, String> productColumn;
    @FXML
    private TableColumn<CartItem, Double> quantityColumn;
    @FXML
    private TableColumn<CartItem, Double> priceColumn;
    @FXML
    private TableColumn<CartItem, Double> totalColumn;
    @FXML
    private TableColumn<CartItem, Void> actionColumn;

    @FXML
    private Label subtotalLabel;
    @FXML
    private Label vatLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private HBox discountRow;
    @FXML
    private Label discountLabel;
    @FXML
    private Label discountValueLabel;
    @FXML
    private Label minimumWarning;
    @FXML
    private ComboBox<Coupon> couponCombo;
    @FXML
    private DatePicker deliveryDatePicker;
    @FXML
    private ComboBox<String> deliveryTimeCombo;
    @FXML
    private Button checkoutButton;
    @FXML
    private HBox couponSection;

    private CartManager cartManager;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private CouponDAO couponDAO;
    private LoyaltySettingsDAO loyaltySettingsDAO;
    private User currentUser;
    /** Reference to parent controller - kept for potential future use */
    @SuppressWarnings("unused")
    private CustomerController parentController;

    private double appliedDiscountPercent = 0;
    private Coupon appliedCoupon = null;
    private LoyaltySettings loyaltySettings;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        cartManager = CartManager.getInstance();
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        userDAO = new UserDAO();
        couponDAO = new CouponDAO();
        loyaltySettingsDAO = new LoyaltySettingsDAO();
        currentUser = SessionManager.getInstance().getCurrentUser();
        loyaltySettings = loyaltySettingsDAO.getSettings();

        setupTableColumns();
        setupDeliveryOptions();
        loadCoupons();
        refreshCart();

        // Check and apply loyalty discount automatically
        checkLoyaltyDiscount();
    }

    /**
     * Sets up the table columns.
     */
    private void setupTableColumns() {
        productColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));

        quantityColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getQuantity()).asObject());

        priceColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPriceAtTime()).asObject());
        priceColumn.setCellFactory(col -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });

        totalColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalPrice()).asObject());
        totalColumn.setCellFactory(col -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", total));
                }
            }
        });

        // Action column with remove button
        actionColumn.setCellFactory(col -> new TableCell<CartItem, Void>() {
            private final Button removeBtn = new Button("Remove");

            {
                removeBtn.getStyleClass().add("danger-button");
                removeBtn.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    cartManager.removeItem(item.getProductId());
                    refreshCart();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeBtn);
            }
        });
    }

    /**
     * Sets up delivery date and time options.
     */
    private void setupDeliveryOptions() {
        // Date picker - only allow next 2 days
        LocalDate today = LocalDate.now();
        deliveryDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Only allow dates within next 48 hours
                LocalDate maxDate = today.plusDays(2);
                setDisable(empty || date.isBefore(today) || date.isAfter(maxDate));
            }
        });
        deliveryDatePicker.setValue(today);

        // Time slots
        ObservableList<String> timeSlots = FXCollections.observableArrayList();
        for (int hour = 9; hour <= 20; hour++) {
            timeSlots.add(String.format("%02d:00", hour));
            timeSlots.add(String.format("%02d:30", hour));
        }
        deliveryTimeCombo.setItems(timeSlots);
        deliveryTimeCombo.setValue("10:00");
    }

    /**
     * Loads available coupons for the user.
     */
    private void loadCoupons() {
        List<Coupon> coupons = couponDAO.findUserCoupons(currentUser.getId());
        couponCombo.getItems().clear();
        couponCombo.getItems().addAll(coupons);

        if (coupons.isEmpty()) {
            couponSection.setVisible(false);
        }
    }

    /**
     * Checks if user is eligible for loyalty discount.
     */
    private void checkLoyaltyDiscount() {
        if (loyaltySettings.isEligible(currentUser.getCompletedOrders())) {
            appliedDiscountPercent = loyaltySettings.getDiscountPercent();
            discountLabel.setText(String.format("Loyalty Discount (%.0f%%):", appliedDiscountPercent));
            discountRow.setVisible(true);
            updateTotals();
        }
    }

    /**
     * Refreshes the cart display.
     */
    private void refreshCart() {
        List<CartItem> items = cartManager.getItems();
        cartTable.setItems(FXCollections.observableArrayList(items));
        updateTotals();
        checkMinimum();
    }

    /**
     * Updates the totals display.
     */
    private void updateTotals() {
        double subtotal = cartManager.getSubtotal();
        double discount = subtotal * (appliedDiscountPercent / 100.0);
        double afterDiscount = subtotal - discount;
        double vat = afterDiscount * CartManager.VAT_RATE;
        double total = afterDiscount + vat;

        subtotalLabel.setText(String.format("$%.2f", subtotal));

        if (appliedDiscountPercent > 0) {
            discountValueLabel.setText(String.format("-$%.2f", discount));
            discountRow.setVisible(true);
        }

        vatLabel.setText(String.format("$%.2f", vat));
        totalLabel.setText(String.format("$%.2f", total));
    }

    /**
     * Checks minimum cart value.
     */
    private void checkMinimum() {
        if (!cartManager.meetsMinimum()) {
            double needed = CartManager.MINIMUM_CART_VALUE - cartManager.getSubtotal();
            minimumWarning.setText(String.format("Minimum order is $%.2f. Add $%.2f more to checkout.",
                    CartManager.MINIMUM_CART_VALUE, needed));
            minimumWarning.setVisible(true);
            checkoutButton.setDisable(true);
        } else {
            minimumWarning.setVisible(false);
            checkoutButton.setDisable(cartManager.isEmpty());
        }
    }

    /**
     * Handles applying a coupon.
     */
    @FXML
    private void handleApplyCoupon(ActionEvent event) {
        Coupon selected = couponCombo.getValue();
        if (selected == null) {
            AlertUtils.showWarning("No Coupon", "Please select a coupon to apply.");
            return;
        }

        if (!selected.meetsMinimum(cartManager.getSubtotal())) {
            AlertUtils.showWarning("Cannot Apply",
                    String.format("Minimum order value of $%.2f required for this coupon.",
                            selected.getMinOrderValue()));
            return;
        }

        // Add coupon discount to existing loyalty discount
        appliedCoupon = selected;
        appliedDiscountPercent += selected.getDiscountPercent();
        discountLabel.setText(String.format("Discount (%.0f%%):", appliedDiscountPercent));
        discountRow.setVisible(true);

        couponCombo.setDisable(true);
        updateTotals();

        AlertUtils.showSuccess("Coupon applied: " + selected.getDiscountPercent() + "% off!");
    }

    /**
     * Handles clearing the cart.
     */
    @FXML
    private void handleClearCart(ActionEvent event) {
        if (AlertUtils.showConfirmation("Clear Cart", "Remove all items from cart?")) {
            cartManager.clear();
            refreshCart();
        }
    }

    /**
     * Handles continue shopping (close cart window).
     */
    @FXML
    private void handleContinueShopping(ActionEvent event) {
        Stage stage = (Stage) cartTable.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the checkout process.
     */
    @FXML
    private void handleCheckout(ActionEvent event) {
        if (cartManager.isEmpty()) {
            AlertUtils.showWarning("Empty Cart", "Please add items to your cart before checkout.");
            return;
        }

        if (!cartManager.meetsMinimum()) {
            AlertUtils.showWarning("Minimum Not Met",
                    String.format("Minimum order value is $%.2f", CartManager.MINIMUM_CART_VALUE));
            return;
        }

        // Validate delivery selection
        LocalDate deliveryDate = deliveryDatePicker.getValue();
        String deliveryTime = deliveryTimeCombo.getValue();

        if (deliveryDate == null || deliveryTime == null) {
            AlertUtils.showValidationError("Please select a delivery date and time.");
            return;
        }

        // Parse delivery datetime
        String[] timeParts = deliveryTime.split(":");
        LocalTime time = LocalTime.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
        LocalDateTime requestedDelivery = LocalDateTime.of(deliveryDate, time);

        // Validate within 48 hours
        if (requestedDelivery.isAfter(LocalDateTime.now().plusHours(48))) {
            AlertUtils.showValidationError("Delivery must be within 48 hours from now.");
            return;
        }

        // Verify stock one more time
        for (CartItem item : cartManager.getItems()) {
            if (!productDAO.hasEnoughStock(item.getProductId(), item.getQuantity())) {
                AlertUtils.showError("Stock Issue",
                        "Sorry, " + item.getProductName() + " no longer has sufficient stock.");
                return;
            }
        }

        // Show order summary
        if (!showOrderSummary(requestedDelivery)) {
            return;
        }

        // Create order
        double subtotal = cartManager.getSubtotal();
        double discount = cartManager.getDiscountAmount(appliedDiscountPercent);
        double afterDiscount = subtotal - discount;
        double vat = afterDiscount * CartManager.VAT_RATE;
        double total = afterDiscount + vat;

        Order order = new Order();
        order.setUserId(currentUser.getId());
        order.setRequestedDelivery(requestedDelivery);
        order.setStatus("PENDING");
        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setVat(vat);
        order.setTotalCost(total);

        // Add items to order
        for (CartItem cartItem : cartManager.getItems()) {
            OrderItem orderItem = new OrderItem(0, cartItem.getProductId(),
                    cartItem.getProductName(), cartItem.getQuantity(), cartItem.getPriceAtTime());
            order.addItem(orderItem);
        }

        // Generate text invoice
        String invoice = InvoiceGenerator.generateInvoice(order, currentUser,
                cartManager.getItems(), appliedDiscountPercent);
        order.setInvoice(invoice);

        // Generate PDF invoice
        byte[] pdfInvoice = PdfInvoiceGenerator.generatePdfInvoice(order, currentUser,
                cartManager.getItems(), appliedDiscountPercent);
        order.setInvoicePdf(pdfInvoice);

        // Save order
        int orderId = orderDAO.create(order);

        if (orderId > 0) {
            // Update stock
            for (CartItem item : cartManager.getItems()) {
                productDAO.updateStock(item.getProductId(), item.getQuantity());
            }

            // Mark coupon as used if applied
            if (appliedCoupon != null) {
                couponDAO.markCouponUsed(currentUser.getId(), appliedCoupon.getId());
            }

            // Increment user's completed orders for loyalty discount eligibility
            userDAO.incrementCompletedOrders(currentUser.getId());

            // Show invoice
            showInvoiceDialog(invoice, orderId);

            // Clear cart
            cartManager.clear();

            // Close cart window
            Stage stage = (Stage) cartTable.getScene().getWindow();
            stage.close();
        } else {
            AlertUtils.showError("Order Failed", "Could not create order. Please try again.");
        }
    }

    /**
     * Shows the order summary confirmation dialog.
     */
    private boolean showOrderSummary(LocalDateTime deliveryTime) {
        StringBuilder summary = new StringBuilder();
        summary.append("ORDER SUMMARY\n\n");

        for (CartItem item : cartManager.getItems()) {
            summary.append(String.format("%s: %.2f kg @ $%.2f = $%.2f\n",
                    item.getProductName(), item.getQuantity(),
                    item.getPriceAtTime(), item.getTotalPrice()));
        }

        summary.append("\n─────────────────────────\n");
        summary.append(String.format("Subtotal: $%.2f\n", cartManager.getSubtotal()));

        if (appliedDiscountPercent > 0) {
            summary.append(String.format("Discount (%.0f%%): -$%.2f\n",
                    appliedDiscountPercent, cartManager.getDiscountAmount(appliedDiscountPercent)));
        }

        double afterDiscount = cartManager.getSubtotal() - cartManager.getDiscountAmount(appliedDiscountPercent);
        summary.append(String.format("VAT (18%%): $%.2f\n", afterDiscount * CartManager.VAT_RATE));
        summary.append(String.format("TOTAL: $%.2f\n", afterDiscount + afterDiscount * CartManager.VAT_RATE));
        summary.append("\nDelivery: ").append(deliveryTime.toString());

        return AlertUtils.showConfirmation("Confirm Order", summary.toString());
    }

    /**
     * Shows the invoice after successful order.
     */
    private void showInvoiceDialog(String invoice, int orderId) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Placed Successfully!");
        alert.setHeaderText("Order #" + orderId + " has been placed.");

        TextArea textArea = new TextArea(invoice);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(500, 400);
        textArea.setStyle("-fx-font-family: 'Courier New', monospace;");

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    /**
     * Sets the parent controller reference.
     */
    public void setParentController(CustomerController controller) {
        this.parentController = controller;
    }
}
