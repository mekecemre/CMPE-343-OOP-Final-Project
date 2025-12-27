package com.greengrocer.controllers;

import com.greengrocer.database.*;
import com.greengrocer.models.*;
import com.greengrocer.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the Customer interface.
 * Handles product browsing, cart management, and order history.
 * 
 * @author Group17
 * @version 1.0
 */
public class CustomerController {

    @FXML
    private TextField searchField;
    @FXML
    private Button cartButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label loyaltyLabel;
    @FXML
    private TitledPane vegetablesPane;
    @FXML
    private TitledPane fruitsPane;
    @FXML
    private FlowPane vegetablesContainer;
    @FXML
    private FlowPane fruitsContainer;

    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private MessageDAO messageDAO;
    private UserDAO userDAO;
    private LoyaltySettingsDAO loyaltySettingsDAO;
    @SuppressWarnings("unused")
    private CouponDAO couponDAO;
    private RatingDAO ratingDAO;
    private CartManager cartManager;
    private User currentUser;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // Initialize DAOs
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        messageDAO = new MessageDAO();
        userDAO = new UserDAO();
        loyaltySettingsDAO = new LoyaltySettingsDAO();
        couponDAO = new CouponDAO();
        ratingDAO = new RatingDAO();
        cartManager = CartManager.getInstance();

        // Get current user
        currentUser = SessionManager.getInstance().getCurrentUser();

        // Update UI
        usernameLabel.setText("Welcome, " + currentUser.getUsername());
        updateCartButton();
        updateLoyaltyStatus();

        // Load products
        loadProducts();

        // Add search field listener for real-time search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                loadProducts();
            }
        });

        statusLabel.setText("Browse our fresh products!");
    }

    /**
     * Loads all products into the display containers.
     */
    private void loadProducts() {
        // Load vegetables
        List<Product> vegetables = productDAO.getVegetables();
        displayProducts(vegetablesContainer, vegetables);
        vegetablesPane.setText("[V] Vegetables (" + vegetables.size() + ")");

        // Load fruits
        List<Product> fruits = productDAO.getFruits();
        displayProducts(fruitsContainer, fruits);
        fruitsPane.setText("[F] Fruits (" + fruits.size() + ")");
    }

    /**
     * Displays products in a container.
     * 
     * @param container The FlowPane container
     * @param products  The list of products to display
     */
    private void displayProducts(FlowPane container, List<Product> products) {
        container.getChildren().clear();

        for (Product product : products) {
            VBox productCard = createProductCard(product);
            container.getChildren().add(productCard);
        }
    }

    /**
     * Creates a product card UI element.
     * 
     * @param product The product to display
     * @return VBox containing the product card
     */
    private VBox createProductCard(Product product) {
        VBox card = new VBox(8);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(160);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));

        // Product image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);

        // Load image from database, file, or use placeholder
        if (product.getImage() != null) {
            try {
                Image image = new Image(new ByteArrayInputStream(product.getImage()));
                imageView.setImage(image);
            } catch (Exception e) {
                loadImageFromFile(imageView, product.getName());
            }
        } else {
            loadImageFromFile(imageView, product.getName());
        }

        // Product name
        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);

        // Price with threshold indication
        double displayPrice = product.getDisplayPrice();
        Label priceLabel = new Label(String.format("$%.2f/kg", displayPrice));
        priceLabel.getStyleClass().add("product-price");

        // Show threshold warning if applicable
        if (product.isLowStock()) {
            priceLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            Label thresholdLabel = new Label("Low Stock - Price Doubled!");
            thresholdLabel.getStyleClass().add("threshold-warning");
            thresholdLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 9px;");
            card.getChildren().add(thresholdLabel);
        }

        // Stock info
        Label stockLabel = new Label(String.format("Stock: %.1f kg", product.getStock()));
        stockLabel.getStyleClass().add("product-stock");
        stockLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");

        // Quantity input and add button
        HBox addBox = new HBox(5);
        addBox.setAlignment(Pos.CENTER);

        TextField quantityField = new TextField();
        quantityField.setPromptText("Qty");
        quantityField.setPrefWidth(50);
        quantityField.getStyleClass().add("quantity-field");

        Button addButton = new Button("Add");
        addButton.getStyleClass().add("add-button");
        addButton.setOnAction(e -> handleAddToCart(product, quantityField));

        addBox.getChildren().addAll(quantityField, addButton);

        card.getChildren().addAll(imageView, nameLabel, priceLabel, stockLabel, addBox);

        return card;
    }

    /**
     * Loads an image from the images folder or uses placeholder.
     * 
     * @param imageView   The ImageView to set the image on
     * @param productName The name of the product (used to find the image file)
     */
    private void loadImageFromFile(ImageView imageView, String productName) {
        String baseName = productName.toLowerCase().replace(" ", "_");
        String[] extensions = { ".png", ".jpg", ".jpeg" };

        for (String ext : extensions) {
            try {
                // Try to load product-specific image
                Image image = new Image(getClass().getResourceAsStream("/com/greengrocer/images/" + baseName + ext));
                if (image != null && !image.isError()) {
                    imageView.setImage(image);
                    return;
                }
            } catch (Exception e) {
                // Try next extension
            }
        }
        // No image found, use placeholder
        loadPlaceholder(imageView);
    }

    /**
     * Loads the placeholder image.
     * 
     * @param imageView The ImageView to set the placeholder on
     */
    private void loadPlaceholder(ImageView imageView) {
        try {
            Image placeholder = new Image(getClass().getResourceAsStream("/com/greengrocer/images/placeholder.png"));
            if (placeholder != null && !placeholder.isError()) {
                imageView.setImage(placeholder);
            } else {
                imageView.setStyle("-fx-background-color: #e0e0e0;");
            }
        } catch (Exception e) {
            imageView.setStyle("-fx-background-color: #e0e0e0;");
        }
    }

    /**
     * Handles adding a product to the cart.
     * 
     * @param product       The product to add
     * @param quantityField The text field containing the quantity
     */
    private void handleAddToCart(Product product, TextField quantityField) {
        String quantityText = quantityField.getText().trim();

        // Validate quantity input
        if (!ValidationUtils.isValidPositiveDouble(quantityText)) {
            AlertUtils.showValidationError("Please enter a valid positive quantity (e.g., 0.5, 1, 2.25)");
            return;
        }

        double quantity = ValidationUtils.parseDouble(quantityText);

        // Check if quantity is positive
        if (quantity <= 0) {
            AlertUtils.showValidationError("Quantity must be greater than 0.");
            return;
        }

        // Check stock availability
        // Need to consider what's already in cart for this product
        double alreadyInCart = 0;
        for (CartItem item : cartManager.getItems()) {
            if (item.getProductId() == product.getId()) {
                alreadyInCart = item.getQuantity();
                break;
            }
        }

        if (quantity + alreadyInCart > product.getStock()) {
            AlertUtils.showWarning("Insufficient Stock",
                    String.format("Only %.2f kg available (%.2f kg already in cart).",
                            product.getStock(), alreadyInCart));
            return;
        }

        // Add to cart (CartManager handles merging)
        cartManager.addItem(product, quantity);

        // Update UI
        updateCartButton();
        quantityField.clear();
        statusLabel.setText(String.format("Added %.2f kg of %s to cart!", quantity, product.getName()));

        AlertUtils.showInfo("Added to Cart",
                String.format("%.2f kg of %s added to your cart.", quantity, product.getName()));
    }

    /**
     * Updates the cart button text with item count.
     */
    private void updateCartButton() {
        int count = cartManager.getItemCount();
        cartButton.setText("Cart (" + count + ")");
    }

    /**
     * Updates the loyalty status display.
     */
    private void updateLoyaltyStatus() {
        LoyaltySettings settings = loyaltySettingsDAO.getSettings();
        int completedOrders = currentUser.getCompletedOrders();

        if (settings.isEligible(completedOrders)) {
            loyaltyLabel.setText(String.format("* Loyalty Member - %.0f%% discount!", settings.getDiscountPercent()));
            loyaltyLabel.setStyle("-fx-text-fill: #27ae60;");
        } else {
            int ordersNeeded = settings.getMinOrdersForDiscount() - completedOrders;
            loyaltyLabel.setText(String.format("Complete %d more orders for %.0f%% loyalty discount!",
                    ordersNeeded, settings.getDiscountPercent()));
        }
    }

    /**
     * Handles the search button click.
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadProducts();
            return;
        }

        List<Product> results = productDAO.searchByName(keyword);

        // Split into vegetables and fruits
        vegetablesContainer.getChildren().clear();
        fruitsContainer.getChildren().clear();

        int vegCount = 0, fruitCount = 0;

        for (Product product : results) {
            VBox card = createProductCard(product);
            if (product.isVegetable()) {
                vegetablesContainer.getChildren().add(card);
                vegCount++;
            } else {
                fruitsContainer.getChildren().add(card);
                fruitCount++;
            }
        }

        vegetablesPane.setText("[V] Vegetables (" + vegCount + ")");
        fruitsPane.setText("[F] Fruits (" + fruitCount + ")");

        statusLabel.setText("Found " + results.size() + " products for '" + keyword + "'");
    }

    /**
     * Handles clearing the search.
     */
    @FXML
    private void handleClearSearch(ActionEvent event) {
        searchField.clear();
        loadProducts();
        statusLabel.setText("Browse our fresh products!");
    }

    /**
     * Opens the shopping cart window.
     */
    @FXML
    private void handleOpenCart(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/greengrocer/views/ShoppingCart.fxml"));
            Parent root = loader.load();

            ShoppingCartController cartController = loader.getController();
            cartController.setParentController(this);

            Stage cartStage = new Stage();
            cartStage.setTitle("Shopping Cart - Group17 GreenGrocer");
            Scene scene = new Scene(root, 700, 600);
            scene.getStylesheets()
                    .add(getClass().getResource("/com/greengrocer/styles/application.css").toExternalForm());
            cartStage.setScene(scene);
            cartStage.initModality(Modality.APPLICATION_MODAL);
            cartStage.showAndWait();

            // Refresh after cart closes
            updateCartButton();
            loadProducts(); // Refresh stock display

            // Refresh user data for loyalty status
            currentUser = userDAO.findById(currentUser.getId());
            SessionManager.getInstance().setCurrentUser(currentUser);
            updateLoyaltyStatus();

        } catch (Exception e) {
            System.err.println("ERROR opening cart: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Error", "Could not open shopping cart: " + e.getMessage());
        }
    }

    /**
     * Opens the order history view.
     */
    @FXML
    private void handleViewOrders(ActionEvent event) {
        List<Order> orders = orderDAO.findByUser(currentUser.getId());

        // Create a dialog to show orders
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("My Orders");
        dialog.setHeaderText("Your Order History");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(600);
        content.setPrefHeight(400);

        if (orders.isEmpty()) {
            content.getChildren().add(new Label("No orders found."));
        } else {
            ListView<Order> orderList = new ListView<>();
            orderList.getItems().addAll(orders);

            orderList.setCellFactory(lv -> new ListCell<Order>() {
                @Override
                protected void updateItem(Order order, boolean empty) {
                    super.updateItem(order, empty);
                    if (empty || order == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        VBox cell = new VBox(5);
                        cell.setPadding(new Insets(5));

                        Label idLabel = new Label("Order #" + order.getId() + " - " + order.getStatus());
                        idLabel.setStyle("-fx-font-weight: bold;");

                        Label dateLabel = new Label("Ordered: " + order.getOrderTime().toString());
                        Label totalLabel = new Label(String.format("Total: $%.2f", order.getTotalCost()));

                        HBox actions = new HBox(10);

                        // View details button
                        Button viewBtn = new Button("View Invoice");
                        viewBtn.setOnAction(e -> showInvoice(order));
                        actions.getChildren().add(viewBtn);

                        // Cancel button (only for pending orders within 24 hour time limit)
                        if (order.isPending()) {
                            if (orderDAO.canCancelOrder(order.getId())) {
                                int hoursRemaining = orderDAO.getHoursRemainingToCancel(order.getId());
                                Button cancelBtn = new Button("Cancel (" + hoursRemaining + "h left)");
                                cancelBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                                cancelBtn.setOnAction(e -> {
                                    if (AlertUtils.showConfirmation("Cancel Order",
                                            "Are you sure you want to cancel this order?\n" +
                                                    "You have " + hoursRemaining + " hour(s) remaining to cancel.")) {
                                        if (orderDAO.cancelOrder(order.getId())) {
                                            AlertUtils.showSuccess("Order cancelled successfully.");
                                            orderList.getItems().remove(order);
                                        } else {
                                            AlertUtils.showError("Cannot Cancel",
                                                    "Order cannot be cancelled. The 24-hour cancellation window has expired.");
                                        }
                                    }
                                });
                                actions.getChildren().add(cancelBtn);
                            } else {
                                Label expiredLabel = new Label("Cancellation expired");
                                expiredLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 10px;");
                                actions.getChildren().add(expiredLabel);
                            }
                        }

                        // Rate button (only for delivered orders)
                        if (order.isDelivered() && order.getCarrierId() > 0) {
                            if (!ratingDAO.hasRated(order.getId(), currentUser.getId())) {
                                Button rateBtn = new Button("Rate Carrier");
                                rateBtn.setOnAction(e -> rateCarrier(order));
                                actions.getChildren().add(rateBtn);
                            }
                        }

                        cell.getChildren().addAll(idLabel, dateLabel, totalLabel, actions);
                        setGraphic(cell);
                    }
                }
            });

            content.getChildren().add(orderList);
            VBox.setVgrow(orderList, Priority.ALWAYS);
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    /**
     * Shows the invoice for an order with option to save as PDF.
     */
    private void showInvoice(Order order) {
        String invoice = order.getInvoice();
        if (invoice == null || invoice.isEmpty()) {
            invoice = InvoiceGenerator.generateFromOrder(order);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invoice #" + order.getId());
        alert.setHeaderText("Order Invoice - Click 'Save PDF' to download");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        TextArea textArea = new TextArea(invoice);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(500, 350);
        textArea.setStyle("-fx-font-family: 'Courier New', monospace;");

        Button savePdfBtn = new Button("Save PDF Invoice");
        savePdfBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        final Order orderFinal = order;
        savePdfBtn.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Save Invoice as PDF");
            fileChooser.setInitialFileName("Invoice_" + orderFinal.getId() + ".pdf");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            java.io.File file = fileChooser.showSaveDialog(alert.getDialogPane().getScene().getWindow());
            if (file != null) {
                try {
                    byte[] pdfData = orderFinal.getInvoicePdf();
                    if (pdfData == null || pdfData.length == 0) {
                        // Generate PDF if not stored
                        pdfData = PdfInvoiceGenerator.generatePdfFromOrder(orderFinal);
                    }
                    java.nio.file.Files.write(file.toPath(), pdfData);
                    AlertUtils.showSuccess("PDF saved successfully to:\n" + file.getAbsolutePath());
                } catch (Exception ex) {
                    AlertUtils.showError("Save Failed", "Could not save PDF: " + ex.getMessage());
                }
            }
        });

        content.getChildren().addAll(textArea, savePdfBtn);

        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    /**
     * Opens the carrier rating dialog.
     */
    private void rateCarrier(Order order) {
        Dialog<Rating> dialog = new Dialog<>();
        dialog.setTitle("Rate Carrier");
        dialog.setHeaderText("Rate your delivery experience for Order #" + order.getId());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Rating selection
        Label ratingLabel = new Label("Rating (1-5 stars):");
        ComboBox<Integer> ratingCombo = new ComboBox<>();
        ratingCombo.getItems().addAll(1, 2, 3, 4, 5);
        ratingCombo.setValue(5);

        Label commentLabel = new Label("Comment (optional):");
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Share your experience...");
        commentArea.setPrefRowCount(3);

        content.getChildren().addAll(ratingLabel, ratingCombo, commentLabel, commentArea);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new Rating(order.getId(), order.getCarrierId(),
                        currentUser.getId(), ratingCombo.getValue(), commentArea.getText());
            }
            return null;
        });

        Optional<Rating> result = dialog.showAndWait();
        result.ifPresent(rating -> {
            if (ratingDAO.create(rating)) {
                AlertUtils.showSuccess("Thank you for your feedback!");
            }
        });
    }

    /**
     * Opens the profile edit dialog.
     */
    @FXML
    private void handleEditProfile(ActionEvent event) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your profile information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField fullNameField = new TextField(currentUser.getFullName());
        fullNameField.setPromptText("Full Name");

        TextArea addressField = new TextArea(currentUser.getAddress());
        addressField.setPromptText("Address");
        addressField.setPrefRowCount(2);

        TextField phoneField = new TextField(currentUser.getPhone());
        phoneField.setPromptText("Phone");

        TextField emailField = new TextField(currentUser.getEmail());
        emailField.setPromptText("Email");

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Address:"), 0, 1);
        grid.add(addressField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                currentUser.setFullName(fullNameField.getText().trim());
                currentUser.setAddress(addressField.getText().trim());
                currentUser.setPhone(phoneField.getText().trim());
                currentUser.setEmail(emailField.getText().trim());
                return currentUser;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            if (userDAO.update(user)) {
                AlertUtils.showSuccess("Profile updated successfully!");
            } else {
                AlertUtils.showError("Error", "Could not update profile.");
            }
        });
    }

    /**
     * Opens the messages dialog.
     */
    @FXML
    private void handleMessages(ActionEvent event) {
        // Get owner for messaging
        User owner = userDAO.getOwner();
        if (owner == null) {
            AlertUtils.showError("Error", "Could not find store owner.");
            return;
        }

        // Get existing messages
        List<Message> messages = messageDAO.findBySender(currentUser.getId());

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Messages");
        dialog.setHeaderText("Contact Store Owner");

        TabPane tabPane = new TabPane();
        tabPane.setPrefSize(500, 400);

        // Send Message Tab
        Tab sendTab = new Tab("Send Message");
        sendTab.setClosable(false);

        VBox sendContent = new VBox(10);
        sendContent.setPadding(new Insets(15));

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");

        TextArea messageField = new TextArea();
        messageField.setPromptText("Your message...");
        messageField.setPrefRowCount(5);

        Button sendBtn = new Button("Send Message");
        sendBtn.setOnAction(e -> {
            if (subjectField.getText().trim().isEmpty() || messageField.getText().trim().isEmpty()) {
                AlertUtils.showValidationError("Please enter both subject and message.");
                return;
            }

            Message msg = new Message(currentUser.getId(), owner.getId(),
                    subjectField.getText().trim(), messageField.getText().trim());

            if (messageDAO.send(msg)) {
                AlertUtils.showSuccess("Message sent successfully!");
                subjectField.clear();
                messageField.clear();
            }
        });

        sendContent.getChildren().addAll(
                new Label("To: Store Owner"),
                new Label("Subject:"), subjectField,
                new Label("Message:"), messageField,
                sendBtn);
        sendTab.setContent(sendContent);

        // View Messages Tab
        Tab viewTab = new Tab("My Messages");
        viewTab.setClosable(false);

        VBox viewContent = new VBox(10);
        viewContent.setPadding(new Insets(15));

        ListView<Message> messageList = new ListView<>();
        messageList.getItems().addAll(messages);
        messageList.setCellFactory(lv -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                } else {
                    String text = msg.getSubject() + "\n" +
                            "Sent: " + msg.getSentAt().toString();
                    if (msg.hasReply()) {
                        text += "\n> Reply: " + msg.getReply();
                    }
                    setText(text);
                }
            }
        });

        viewContent.getChildren().add(messageList);
        VBox.setVgrow(messageList, Priority.ALWAYS);
        viewTab.setContent(viewContent);

        tabPane.getTabs().addAll(sendTab, viewTab);

        dialog.getDialogPane().setContent(tabPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    /**
     * Handles logout.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        if (!cartManager.isEmpty()) {
            if (!AlertUtils.showConfirmation("Logout",
                    "You have items in your cart. Are you sure you want to logout?")) {
                return;
            }
        }

        // Clear cart and session
        cartManager.clear();
        SessionManager.getInstance().logout();

        // Navigate to login
        Stage stage = (Stage) usernameLabel.getScene().getWindow();
        SceneNavigator.loadScene(stage, "Login.fxml", "Group17 GreenGrocer - Login");
    }

    /**
     * Refreshes the product display (called from cart controller).
     */
    public void refreshProducts() {
        loadProducts();
        updateCartButton();
    }
}
