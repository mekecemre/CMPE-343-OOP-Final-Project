package com.greengrocer.controllers;

import com.greengrocer.database.*;
import com.greengrocer.models.*;
import com.greengrocer.utils.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    private TableColumn<CartItem, Void> imageColumn;

    @FXML
    private TableColumn<CartItem, String> productColumn;

    @FXML
    private TableColumn<CartItem, Double> quantityColumn;

    @FXML
    private TableColumn<CartItem, Double> priceColumn;

    @FXML
    private TableColumn<CartItem, Double> totalColumn;

    @FXML
    private TableColumn<CartItem, Void> editColumn;

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
    private VBox couponSection;

    @FXML
    private Button removeCouponBtn;

    @FXML
    private VBox loyaltySection;

    @FXML
    private ComboBox<String> loyaltyCombo;

    @FXML
    private Label loyaltyCalcLabel;

    private CartManager cartManager;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private CouponDAO couponDAO;
    private LoyaltySettingsDAO loyaltySettingsDAO;
    private MessageDAO messageDAO;
    private User currentUser;

    /** Reference to parent controller - kept for potential future use */
    @SuppressWarnings("unused")
    private CustomerController parentController;

    private double appliedDiscountPercent = 0;
    private double loyaltyDiscountPercent = 0;
    private Coupon appliedCoupon = null;
    private LoyaltySettings loyaltySettings;

    /**
     * Tracks if user is eligible for loyalty discount - kept for potential future
     * use
     */
    @SuppressWarnings("unused")
    private boolean loyaltyEligible = false;

    /**
     * Default constructor for ShoppingCartController.
     * Called by JavaFX when loading the FXML file.
     */
    public ShoppingCartController() {
    }

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        cartManager = CartManager.getInstance();
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        couponDAO = new CouponDAO();
        loyaltySettingsDAO = new LoyaltySettingsDAO();
        messageDAO = new MessageDAO();
        userDAO = new UserDAO();
        currentUser = SessionManager.getInstance().getCurrentUser();
        loyaltySettings = loyaltySettingsDAO.getSettings();

        // Set fixed row height for better image display
        cartTable.setFixedCellSize(50);

        // Make columns fill the table width (no empty space on right)
        cartTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

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
        // Image column
        imageColumn.setCellFactory(col -> new TableCell<CartItem, Void>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CartItem cartItem = getTableView()
                            .getItems()
                            .get(getIndex());
                    loadProductImage(imageView, cartItem.getProductName());
                    setGraphic(imageView);
                }
            }
        });

        productColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));

        quantityColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getQuantity()).asObject());

        priceColumn.setCellValueFactory(data -> new SimpleDoubleProperty(
                data.getValue().getPriceAtTime()).asObject());
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

        // Edit column with +/- buttons only (no editable text field)
        editColumn.setCellFactory(col -> new TableCell<CartItem, Void>() {
            private final Button minusBtn = new Button("-");
            private final Button plusBtn = new Button("+");
            private final Label qtyLabel = new Label();
            private final HBox container = new HBox(5);

            {
                // Style buttons
                minusBtn.setStyle(
                        "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px; " +
                                "-fx-min-width: 28px; -fx-max-width: 28px; -fx-padding: 3 6 3 6;");
                plusBtn.setStyle(
                        "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 10px; " +
                                "-fx-min-width: 28px; -fx-max-width: 28px; -fx-padding: 3 6 3 6;");
                qtyLabel.setStyle("-fx-font-size: 11px; -fx-min-width: 40px; -fx-alignment: center;");
                qtyLabel.setAlignment(javafx.geometry.Pos.CENTER);

                container.setAlignment(javafx.geometry.Pos.CENTER);
                container.getChildren().addAll(minusBtn, qtyLabel, plusBtn);

                // Minus button action - decrease quantity by 0.5
                minusBtn.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    double newQty = item.getQuantity() - 0.5;
                    if (newQty >= 0.5) {
                        updateItemQuantity(item, newQty);
                    }
                });

                // Plus button action - increase quantity by 0.5
                plusBtn.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    Product product = productDAO.findById(item.getProductId());
                    double newQty = item.getQuantity() + 0.5;
                    if (product != null && newQty <= product.getStock()) {
                        updateItemQuantity(item, newQty);
                    } else {
                        AlertUtils.showWarning("Stock Limit",
                                "Cannot add more - only " + (product != null ? product.getStock() : 0)
                                        + " kg available.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CartItem cartItem = getTableView().getItems().get(getIndex());
                    qtyLabel.setText(String.format("%.1f", cartItem.getQuantity()));
                    setGraphic(container);
                }
            }
        });

        // Action column with compact remove button
        actionColumn.setCellFactory(col -> new TableCell<CartItem, Void>() {
            private final Button removeBtn = new Button("✕");

            {
                removeBtn.setStyle(
                        "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 6 2 6;");
                removeBtn.setOnAction(e -> {
                    CartItem item = getTableView()
                            .getItems()
                            .get(getIndex());
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
     * Loads product image from database or resources.
     */
    private void loadProductImage(ImageView imageView, String productName) {
        // First, try to get the product from the database to retrieve the image
        for (CartItem item : cartManager.getItems()) {
            if (item.getProductName().equals(productName)) {
                Product product = item.getProduct();
                byte[] imageBytes = product.getImage();

                if (imageBytes != null && imageBytes.length > 0) {
                    try {
                        Image image = new Image(new java.io.ByteArrayInputStream(imageBytes));
                        if (image != null && !image.isError()) {
                            imageView.setImage(image);
                            return;
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading image from database: " + e.getMessage());
                    }
                }
                break;
            }
        }

        // Fallback: try to load from resources
        String baseName = productName.toLowerCase().replace(" ", "_");
        String[] extensions = { ".png", ".jpg", ".jpeg" };

        for (String ext : extensions) {
            try {
                Image image = new Image(
                        getClass().getResourceAsStream(
                                "/com/greengrocer/images/" + baseName + ext));
                if (image != null && !image.isError()) {
                    imageView.setImage(image);
                    return;
                }
            } catch (Exception e) {
                // Try next extension
            }
        }
        // Set placeholder style if no image found
        imageView.setImage(null);
    }

    /**
     * Updates the quantity of a cart item and refreshes the display.
     * 
     * @param item        The cart item to update
     * @param newQuantity The new quantity
     */
    private void updateItemQuantity(CartItem item, double newQuantity) {
        cartManager.updateQuantity(item.getProductId(), newQuantity);
        refreshCart();
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
                setDisable(
                        empty || date.isBefore(today) || date.isAfter(maxDate));
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
        System.out.println(
                "DEBUG: Loading coupons for user ID: " +
                        currentUser.getId() +
                        ", found " +
                        coupons.size() +
                        " coupons");
        for (Coupon c : coupons) {
            System.out.println(
                    "DEBUG: Coupon found: " +
                            c.getCode() +
                            " - " +
                            c.getDiscountPercent() +
                            "%");
        }
        couponCombo.getItems().clear();
        couponCombo.getItems().addAll(coupons);

        if (coupons.isEmpty()) {
            couponSection.setVisible(false);
            couponSection.setManaged(false);
        } else {
            couponSection.setVisible(true);
            couponSection.setManaged(true);
        }
    }

    /**
     * Checks if user is eligible for loyalty discount and populates dropdown.
     */
    private void checkLoyaltyDiscount() {
        if (loyaltySettings == null || currentUser == null) {
            return;
        }

        // Always show loyalty section
        loyaltySection.setVisible(true);
        loyaltySection.setManaged(true);
        loyaltyCombo.getItems().clear();

        int completedOrders = currentUser.getCompletedOrders();
        int requiredOrders = loyaltySettings.getMinOrdersForDiscount();

        if (loyaltySettings.isEligible(completedOrders)) {
            loyaltyEligible = true;
            // User is eligible - show discount options
            loyaltyCombo.getItems().add("No discount");
            loyaltyCombo
                    .getItems()
                    .add(
                            String.format(
                                    "Loyalty Discount: %.0f%% off",
                                    loyaltySettings.getDiscountPercent()));
            loyaltyCombo.setValue("No discount");
            loyaltyCalcLabel.setText(
                    String.format(
                            "🎉 You have %d completed orders - discount unlocked!",
                            completedOrders));
            loyaltyCalcLabel.setVisible(true);
            loyaltyCalcLabel.setManaged(true);
        } else {
            // User not eligible yet - show progress
            loyaltyEligible = false;
            int remaining = requiredOrders - completedOrders;
            loyaltyCombo
                    .getItems()
                    .add(
                            String.format(
                                    "Complete %d more order(s) to unlock %.0f%% off",
                                    remaining,
                                    loyaltySettings.getDiscountPercent()));
            loyaltyCombo.setValue(loyaltyCombo.getItems().get(0));
            loyaltyCombo.setDisable(true);
            loyaltyCalcLabel.setText(
                    String.format(
                            "Progress: %d/%d orders completed",
                            completedOrders,
                            requiredOrders));
            loyaltyCalcLabel.setVisible(true);
            loyaltyCalcLabel.setManaged(true);
        }
    }

    /**
     * Handles loyalty discount selection from dropdown.
     */
    @FXML
    private void handleApplyLoyalty(ActionEvent event) {
        String selected = loyaltyCombo.getValue();
        if (selected == null || selected.equals("No discount")) {
            // Remove loyalty discount
            loyaltyDiscountPercent = 0;
            loyaltyCalcLabel.setVisible(false);
            loyaltyCalcLabel.setManaged(false);
        } else {
            // Apply loyalty discount
            loyaltyDiscountPercent = loyaltySettings.getDiscountPercent();
            double subtotal = cartManager.getSubtotal();
            double discountAmount = subtotal * (loyaltyDiscountPercent / 100.0);
            double newPrice = subtotal - discountAmount;

            // Show calculation
            loyaltyCalcLabel.setText(
                    String.format(
                            "Subtotal: $%.2f - %.0f%% = $%.2f (Save $%.2f)",
                            subtotal,
                            loyaltyDiscountPercent,
                            newPrice,
                            discountAmount));
            loyaltyCalcLabel.setVisible(true);
            loyaltyCalcLabel.setManaged(true);
        }
        // Recalculate total discount
        appliedDiscountPercent = loyaltyDiscountPercent +
                (appliedCoupon != null ? appliedCoupon.getDiscountPercent() : 0);
        if (appliedDiscountPercent > 0) {
            discountLabel.setText(
                    String.format(
                            "Total Discount (%.0f%%):",
                            appliedDiscountPercent));
            discountRow.setVisible(true);
            discountRow.setManaged(true);
        } else {
            discountRow.setVisible(false);
            discountRow.setManaged(false);
        }
        updateTotals();
    }

    /**
     * Refreshes the cart display.
     */
    private void refreshCart() {
        List<CartItem> items = cartManager.getItems();
        cartTable.setItems(FXCollections.observableArrayList(items));
        cartTable.refresh(); // Force visual refresh of the table
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
            discountRow.setManaged(true);
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
            minimumWarning.setText(
                    String.format(
                            "Minimum order: $%.2f. Add $%.2f more.",
                            CartManager.MINIMUM_CART_VALUE,
                            needed));
            minimumWarning.setVisible(true);
            minimumWarning.setManaged(true);
            checkoutButton.setDisable(true);
        } else {
            minimumWarning.setVisible(false);
            minimumWarning.setManaged(false);
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
            AlertUtils.showWarning(
                    "No Coupon",
                    "Please select a coupon to apply.");
            return;
        }

        if (!selected.isValid()) {
            AlertUtils.showWarning(
                    "Cannot Apply",
                    "This coupon is no longer valid. It may have expired or reached its usage limit.");
            return;
        }

        if (!selected.meetsMinimum(cartManager.getSubtotal())) {
            AlertUtils.showWarning(
                    "Cannot Apply",
                    String.format(
                            "Minimum order value of $%.2f required for this coupon.",
                            selected.getMinOrderValue()));
            return;
        }

        // Add coupon discount to existing loyalty discount
        appliedCoupon = selected;
        appliedDiscountPercent = loyaltyDiscountPercent + selected.getDiscountPercent();
        discountLabel.setText(
                String.format("Discount (%.0f%%):", appliedDiscountPercent));
        discountRow.setVisible(true);

        couponCombo.setDisable(true);
        removeCouponBtn.setVisible(true);
        removeCouponBtn.setManaged(true);
        updateTotals();

        AlertUtils.showSuccess(
                "Coupon applied: " + selected.getDiscountPercent() + "% off!");
    }

    /**
     * Handles removing an applied coupon.
     */
    @FXML
    private void handleRemoveCoupon(ActionEvent event) {
        // Remove coupon discount
        appliedCoupon = null;
        appliedDiscountPercent = loyaltyDiscountPercent; // Keep only loyalty discount if any

        // Update UI
        couponCombo.setDisable(false);
        couponCombo.setValue(null);
        removeCouponBtn.setVisible(false);
        removeCouponBtn.setManaged(false);

        // Update discount display
        if (appliedDiscountPercent > 0) {
            discountLabel.setText(
                    String.format("Discount (%.0f%%):", appliedDiscountPercent));
            discountRow.setVisible(true);
            discountRow.setManaged(true);
        } else {
            discountRow.setVisible(false);
            discountRow.setManaged(false);
        }

        updateTotals();
        AlertUtils.showInfo("Coupon Removed", "The coupon has been removed.");
    }

    /**
     * Handles clearing the cart.
     */
    @FXML
    private void handleClearCart(ActionEvent event) {
        if (AlertUtils.showConfirmation(
                "Clear Cart",
                "Remove all items from cart?")) {
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
            AlertUtils.showWarning(
                    "Empty Cart",
                    "Please add items to your cart before checkout.");
            return;
        }

        if (!cartManager.meetsMinimum()) {
            AlertUtils.showWarning(
                    "Minimum Not Met",
                    String.format(
                            "Minimum order value is $%.2f",
                            CartManager.MINIMUM_CART_VALUE));
            return;
        }

        // Validate delivery selection
        LocalDate deliveryDate = deliveryDatePicker.getValue();
        String deliveryTime = deliveryTimeCombo.getValue();

        if (deliveryDate == null || deliveryTime == null) {
            AlertUtils.showValidationError(
                    "Please select a delivery date and time.");
            return;
        }

        // Parse delivery datetime
        String[] timeParts = deliveryTime.split(":");
        LocalTime time = LocalTime.of(
                Integer.parseInt(timeParts[0]),
                Integer.parseInt(timeParts[1]));
        LocalDateTime requestedDelivery = LocalDateTime.of(deliveryDate, time);

        // Validate within 48 hours
        if (requestedDelivery.isAfter(LocalDateTime.now().plusHours(48))) {
            AlertUtils.showValidationError(
                    "Delivery must be within 48 hours from now.");
            return;
        }

        // Verify stock one more time
        for (CartItem item : cartManager.getItems()) {
            if (!productDAO.hasEnoughStock(
                    item.getProductId(),
                    item.getQuantity())) {
                AlertUtils.showError(
                        "Stock Issue",
                        "Sorry, " +
                                item.getProductName() +
                                " no longer has sufficient stock.");
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
            OrderItem orderItem = new OrderItem(
                    0,
                    cartItem.getProductId(),
                    cartItem.getProductName(),
                    cartItem.getQuantity(),
                    cartItem.getPriceAtTime());
            order.addItem(orderItem);
        }

        // Generate text invoice
        String invoice = InvoiceGenerator.generateInvoice(
                order,
                currentUser,
                cartManager.getItems(),
                appliedDiscountPercent);
        order.setInvoice(invoice);

        // Generate PDF invoice
        byte[] pdfInvoice = PdfInvoiceGenerator.generatePdfInvoice(
                order,
                currentUser,
                cartManager.getItems(),
                appliedDiscountPercent);
        order.setInvoicePdf(pdfInvoice);

        // Save order
        int orderId = orderDAO.create(order);

        if (orderId > 0) {
            // Update stock and check for notifications
            for (CartItem item : cartManager.getItems()) {
                // Get product state before stock update
                Product productBefore = productDAO.findById(
                        item.getProductId());
                boolean wasAboveThreshold = productBefore != null &&
                        productBefore.getStock() > productBefore.getThreshold();

                // Update stock
                productDAO.updateStock(item.getProductId(), item.getQuantity());

                // Get product state after stock update
                Product productAfter = productDAO.findById(item.getProductId());
                if (productAfter != null) {
                    // Check if stock ran out
                    if (productAfter.getStock() <= 0) {
                        notifyOwnerStockOut(productAfter);
                    }
                    // Check if stock just crossed below threshold (price doubled)
                    else if (wasAboveThreshold && productAfter.isLowStock()) {
                        notifyOwnerThresholdDoubled(productAfter);
                    }
                }
            }

            // Mark coupon as used if applied
            if (appliedCoupon != null) {
                couponDAO.markCouponUsed(
                        currentUser.getId(),
                        appliedCoupon.getId());
            }

            // Reset completed orders count if loyalty discount was used
            // User must earn the discount again by completing more orders
            if (loyaltyDiscountPercent > 0) {
                userDAO.resetCompletedOrders(currentUser.getId());
            }

            // NOTE: Completed orders count is incremented by CarrierController
            // when the order is marked as DELIVERED, not here when placed.

            // Show invoice
            showInvoiceDialog(invoice, orderId);

            // Clear cart
            cartManager.clear();

            // Close cart window
            Stage stage = (Stage) cartTable.getScene().getWindow();
            stage.close();
        } else {
            AlertUtils.showError(
                    "Order Failed",
                    "Could not create order. Please try again.");
        }
    }

    /**
     * Notifies the owner when a product stock runs out.
     *
     * @param product The product that ran out of stock
     */
    private void notifyOwnerStockOut(Product product) {
        // Find the owner user
        User owner = userDAO.getOwner();
        if (owner == null) {
            return;
        }

        String subject = "⚠️ Stock Alert: " + product.getName() + " is OUT OF STOCK";
        String content = String.format(
                "STOCK ALERT\n\n" +
                        "Product: %s\n" +
                        "Type: %s\n" +
                        "Current Stock: %.2f kg\n" +
                        "Status: OUT OF STOCK\n\n" +
                        "Action Required: Please restock this product as soon as possible.\n\n" +
                        "This notification was automatically generated after a customer order.",
                product.getName(),
                product.getType(),
                product.getStock());

        Message notification = new Message(
                currentUser.getId(),
                owner.getId(),
                subject,
                content);
        messageDAO.send(notification);
    }

    /**
     * Notifies the owner when a product's price is doubled due to low stock.
     *
     * @param product The product that fell below threshold
     */
    private void notifyOwnerThresholdDoubled(Product product) {
        // Find the owner user
        User owner = userDAO.getOwner();
        if (owner == null) {
            return;
        }

        String subject = "⚠️ Price Alert: " + product.getName() + " - PRICE DOUBLED";
        String content = String.format(
                "THRESHOLD ALERT\n\n" +
                        "Product: %s\n" +
                        "Type: %s\n" +
                        "Current Stock: %.2f kg\n" +
                        "Threshold: %.2f kg\n" +
                        "Status: LOW STOCK - PRICE DOUBLED\n\n" +
                        "Original Price: $%.2f/kg\n" +
                        "Current Price: $%.2f/kg (2x)\n\n" +
                        "The stock has fallen below the threshold. The price has been automatically doubled for customers.\n\n"
                        +
                        "Action Required: Consider restocking this product soon.\n\n" +
                        "This notification was automatically generated after a customer order.",
                product.getName(),
                product.getType(),
                product.getStock(),
                product.getThreshold(),
                product.getPrice(),
                product.getDisplayPrice());

        Message notification = new Message(
                currentUser.getId(),
                owner.getId(),
                subject,
                content);
        messageDAO.send(notification);
    }

    /**
     * Shows the invoice in a dialog with option to save as PDF.
     * Shows the order summary confirmation dialog.
     */
    private boolean showOrderSummary(LocalDateTime deliveryTime) {
        StringBuilder summary = new StringBuilder();
        summary.append("ORDER SUMMARY\n\n");

        for (CartItem item : cartManager.getItems()) {
            summary.append(
                    String.format(
                            "%s: %.2f kg @ $%.2f = $%.2f\n",
                            item.getProductName(),
                            item.getQuantity(),
                            item.getPriceAtTime(),
                            item.getTotalPrice()));
        }

        summary.append("\n─────────────────────────\n");
        summary.append(
                String.format("Subtotal: $%.2f\n", cartManager.getSubtotal()));

        if (appliedDiscountPercent > 0) {
            summary.append(
                    String.format(
                            "Discount (%.0f%%): -$%.2f\n",
                            appliedDiscountPercent,
                            cartManager.getDiscountAmount(appliedDiscountPercent)));
        }

        double afterDiscount = cartManager.getSubtotal() -
                cartManager.getDiscountAmount(appliedDiscountPercent);
        summary.append(
                String.format(
                        "VAT (18%%): $%.2f\n",
                        afterDiscount * CartManager.VAT_RATE));
        summary.append(
                String.format(
                        "TOTAL: $%.2f\n",
                        afterDiscount + afterDiscount * CartManager.VAT_RATE));
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
     *
     * @param controller The parent CustomerController
     */
    public void setParentController(CustomerController controller) {
        this.parentController = controller;
    }
}
