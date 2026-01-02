package com.greengrocer.controllers;

import com.greengrocer.database.*;
import com.greengrocer.models.*;
import com.greengrocer.utils.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller for the Owner interface.
 * Handles all administrative functions including products, carriers, orders,
 * messages, coupons, ratings, and reports.
 *
 * @author Group17
 * @version 1.0
 */
public class OwnerController {

    // Products Tab
    @FXML
    private TableView<Product> productsTable;

    @FXML
    private TableColumn<Product, Integer> prodIdColumn;

    @FXML
    private TableColumn<Product, String> prodNameColumn;

    @FXML
    private TableColumn<Product, String> prodTypeColumn;

    @FXML
    private TableColumn<Product, Double> prodPriceColumn;

    @FXML
    private TableColumn<Product, Double> prodStockColumn;

    @FXML
    private TableColumn<Product, Double> prodThresholdColumn;

    @FXML
    private TableColumn<Product, String> prodStatusColumn;

    @FXML
    private TableColumn<Product, Void> prodImageColumn;

    // Carriers Tab
    @FXML
    private TableView<User> carriersTable;

    @FXML
    private TableColumn<User, Integer> carrIdColumn;

    @FXML
    private TableColumn<User, String> carrUsernameColumn;

    @FXML
    private TableColumn<User, String> carrNameColumn;

    @FXML
    private TableColumn<User, String> carrPhoneColumn;

    @FXML
    private TableColumn<User, String> carrEmailColumn;

    @FXML
    private TableColumn<User, String> carrRatingColumn;

    // Orders Tab
    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Integer> ordIdColumn;

    @FXML
    private TableColumn<Order, String> ordCustomerColumn;

    @FXML
    private TableColumn<Order, String> ordDateColumn;

    @FXML
    private TableColumn<Order, String> ordDeliveryColumn;

    @FXML
    private TableColumn<Order, String> ordStatusColumn;

    @FXML
    private TableColumn<Order, String> ordCarrierColumn;

    @FXML
    private TableColumn<Order, Double> ordTotalColumn;

    @FXML
    private ComboBox<String> orderFilterCombo;

    @FXML
    private Label totalSalesLabel;

    // Messages Tab
    @FXML
    private ListView<Message> messagesList;

    @FXML
    private TextArea messageContentArea;

    @FXML
    private TextArea replyArea;

    // Coupons Tab
    @FXML
    private TableView<Coupon> couponsTable;

    @FXML
    private TableColumn<Coupon, Integer> coupIdColumn;

    @FXML
    private TableColumn<Coupon, String> coupCodeColumn;

    @FXML
    private TableColumn<Coupon, Double> coupDiscountColumn;

    @FXML
    private TableColumn<Coupon, Double> coupMinColumn;

    @FXML
    private TableColumn<Coupon, String> coupExpiryColumn;

    @FXML
    private TableColumn<Coupon, String> coupActiveColumn;

    @FXML
    private TableColumn<Coupon, String> coupStatusColumn;

    @FXML
    private TextField loyaltyOrdersField;

    @FXML
    private TextField loyaltyDiscountField;

    // Ratings Tab
    @FXML
    private TableView<Rating> ratingsTable;

    @FXML
    private TableColumn<Rating, String> rateCarrierColumn;

    @FXML
    private TableColumn<Rating, String> rateCustomerColumn;

    @FXML
    private TableColumn<Rating, Integer> rateOrderColumn;

    @FXML
    private TableColumn<Rating, String> rateStarsColumn;

    @FXML
    private TableColumn<Rating, String> rateCommentColumn;

    @FXML
    private TableColumn<Rating, String> rateDateColumn;

    // Reports Tab
    @FXML
    private BarChart<String, Number> productSalesChart;

    @FXML
    private PieChart productDistributionChart;

    // General
    @FXML
    private Label usernameLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private TabPane mainTabPane;

    // DAOs
    private ProductDAO productDAO;
    private UserDAO userDAO;
    private OrderDAO orderDAO;
    private MessageDAO messageDAO;
    private CouponDAO couponDAO;
    private RatingDAO ratingDAO;
    private LoyaltySettingsDAO loyaltySettingsDAO;

    private User currentUser;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm"
    );

    /**
     * Default constructor for OwnerController.
     * Called by JavaFX when loading the FXML file.
     */
    public OwnerController() {}

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        // Initialize DAOs
        productDAO = new ProductDAO();
        userDAO = new UserDAO();
        orderDAO = new OrderDAO();
        messageDAO = new MessageDAO();
        couponDAO = new CouponDAO();
        ratingDAO = new RatingDAO();
        loyaltySettingsDAO = new LoyaltySettingsDAO();

        currentUser = SessionManager.getInstance().getCurrentUser();
        usernameLabel.setText("Owner: " + currentUser.getUsername());

        // Setup all tables
        setupProductsTable();
        setupCarriersTable();
        setupOrdersTable();
        setupMessagesTab();
        setupCouponsTable();
        setupRatingsTable();

        // Load initial data
        loadProducts();
        loadCarriers();
        loadOrders();
        loadMessages();
        loadCoupons();
        loadLoyaltySettings();
        loadRatings();
        loadCharts();
    }

    // ======================== PRODUCTS TAB ========================

    private void setupProductsTable() {
        prodIdColumn.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId()).asObject()
        );
        prodNameColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getName())
        );
        prodTypeColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getType())
        );
        prodPriceColumn.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getPrice()).asObject()
        );
        prodStockColumn.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getStock()).asObject()
        );
        prodThresholdColumn.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getThreshold()).asObject()
        );
        prodStatusColumn.setCellValueFactory(data -> {
            Product p = data.getValue();
            String status = p.getStock() <= 0
                ? "Out of Stock"
                : (p.isLowStock() ? "Low Stock (2x Price)" : "Normal");
            return new SimpleStringProperty(status);
        });

        // Image column with thumbnail
        prodImageColumn.setCellFactory(col ->
            new TableCell<Product, Void>() {
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
                        Product product = getTableView()
                            .getItems()
                            .get(getIndex());
                        Image image = loadProductImage(product);
                        if (image != null) {
                            imageView.setImage(image);
                            setGraphic(imageView);
                        } else {
                            setGraphic(new Label("No img"));
                        }
                    }
                }
            }
        );

        // Set row height for images
        productsTable.setFixedCellSize(45);
    }

    /**
     * Loads product image from database or resources.
     */
    private Image loadProductImage(Product product) {
        // First try database image
        if (product.getImage() != null && product.getImage().length > 0) {
            return new Image(new ByteArrayInputStream(product.getImage()));
        }

        // Fall back to resource image
        String baseName = product.getName().toLowerCase().replace(" ", "_");
        String[] extensions = { ".png", ".jpg", ".jpeg" };

        for (String ext : extensions) {
            try {
                Image image = new Image(
                    getClass().getResourceAsStream(
                        "/com/greengrocer/images/" + baseName + ext
                    )
                );
                if (image != null && !image.isError()) {
                    return image;
                }
            } catch (Exception e) {
                // Try next extension
            }
        }
        return null;
    }

    private void loadProducts() {
        List<Product> products = productDAO.findAllIncludingOutOfStock();
        productsTable.setItems(FXCollections.observableArrayList(products));
        statusLabel.setText("Loaded " + products.size() + " products");
    }

    @FXML
    private void handleAddProduct(ActionEvent event) {
        Dialog<Product> dialog = createProductDialog(null);
        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(product -> {
            // Check for duplicate name
            if (productDAO.existsByName(product.getName())) {
                AlertUtils.showError(
                    "Duplicate Name",
                    "A product with the name '" +
                        product.getName() +
                        "' already exists."
                );
                return;
            }
            if (productDAO.add(product)) {
                AlertUtils.showSuccess("Product added successfully!");
                loadProducts();
            } else {
                AlertUtils.showError("Error", "Could not add product.");
            }
        });
    }

    @FXML
    private void handleEditProduct(ActionEvent event) {
        Product selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning(
                "No Selection",
                "Please select a product to edit."
            );
            return;
        }

        Dialog<Product> dialog = createProductDialog(selected);
        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(product -> {
            // Check for duplicate name (excluding current product)
            if (
                productDAO.existsByNameExcluding(
                    product.getName(),
                    product.getId()
                )
            ) {
                AlertUtils.showError(
                    "Duplicate Name",
                    "Another product with the name '" +
                        product.getName() +
                        "' already exists."
                );
                return;
            }
            // Use updateWithImage if image is set
            boolean success = product.getImage() != null
                ? productDAO.updateWithImage(product)
                : productDAO.update(product);
            if (success) {
                AlertUtils.showSuccess("Product updated successfully!");
                loadProducts();
            } else {
                AlertUtils.showError("Error", "Could not update product.");
            }
        });
    }

    @FXML
    private void handleDeleteProduct(ActionEvent event) {
        Product selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning(
                "No Selection",
                "Please select a product to delete."
            );
            return;
        }

        if (
            AlertUtils.showConfirmation(
                "Delete Product",
                "Are you sure you want to delete " + selected.getName() + "?"
            )
        ) {
            if (productDAO.delete(selected.getId())) {
                AlertUtils.showSuccess("Product deleted successfully!");
                loadProducts();
            } else {
                AlertUtils.showError(
                    "Error",
                    "Could not delete product. It may be in use."
                );
            }
        }
    }

    @FXML
    private void handleRefreshProducts(ActionEvent event) {
        loadProducts();
    }

    private Dialog<Product> createProductDialog(Product existing) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Product" : "Edit Product");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(
            existing != null ? existing.getName() : ""
        );
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("VEGETABLE", "FRUIT");
        typeCombo.setValue(existing != null ? existing.getType() : "VEGETABLE");
        TextField priceField = new TextField(
            existing != null ? String.valueOf(existing.getPrice()) : ""
        );
        TextField stockField = new TextField(
            existing != null ? String.valueOf(existing.getStock()) : ""
        );
        TextField thresholdField = new TextField(
            existing != null ? String.valueOf(existing.getThreshold()) : "5.0"
        );

        // Image upload section
        ImageView previewImage = new ImageView();
        previewImage.setFitWidth(60);
        previewImage.setFitHeight(60);
        previewImage.setPreserveRatio(true);

        // Array to hold selected image bytes
        final byte[][] selectedImageBytes = {
            existing != null ? existing.getImage() : null,
        };

        // Load existing image preview
        if (existing != null) {
            Image img = loadProductImage(existing);
            if (img != null) {
                previewImage.setImage(img);
            }
        }

        Button uploadBtn = new Button("Choose Image...");
        Label imageLabel = new Label(
            selectedImageBytes[0] != null ? "Image set" : "No image"
        );

        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Product Image");
            fileChooser
                .getExtensionFilters()
                .addAll(
                    new FileChooser.ExtensionFilter(
                        "Images",
                        "*.png",
                        "*.jpg",
                        "*.jpeg",
                        "*.gif"
                    )
                );
            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                try {
                    selectedImageBytes[0] = Files.readAllBytes(file.toPath());
                    Image img = new Image(
                        new ByteArrayInputStream(selectedImageBytes[0])
                    );
                    previewImage.setImage(img);
                    imageLabel.setText(file.getName());
                } catch (Exception ex) {
                    AlertUtils.showError(
                        "Error",
                        "Could not load image: " + ex.getMessage()
                    );
                }
            }
        });

        HBox imageBox = new HBox(10, previewImage, uploadBtn, imageLabel);
        imageBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        grid.add(new Label("Price/kg:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Stock (kg):"), 0, 3);
        grid.add(stockField, 1, 3);
        grid.add(new Label("Threshold (kg):"), 0, 4);
        grid.add(thresholdField, 1, 4);
        grid.add(new Label("Image:"), 0, 5);
        grid.add(imageBox, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog
            .getDialogPane()
            .getButtonTypes()
            .addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                // Validate inputs
                if (nameField.getText().trim().isEmpty()) {
                    AlertUtils.showValidationError("Name is required.");
                    return null;
                }

                double price = ValidationUtils.parseDouble(
                    priceField.getText()
                );
                double stock = ValidationUtils.parseDouble(
                    stockField.getText()
                );
                double threshold = ValidationUtils.parseDouble(
                    thresholdField.getText()
                );

                if (price <= 0) {
                    AlertUtils.showValidationError(
                        "Price must be a positive number."
                    );
                    return null;
                }
                if (stock <= 0) {
                    AlertUtils.showValidationError(
                        "Stock must be a positive number."
                    );
                    return null;
                }
                if (threshold <= 0) {
                    AlertUtils.showValidationError(
                        "Threshold must be a positive number."
                    );
                    return null;
                }

                Product product = existing != null ? existing : new Product();
                product.setName(nameField.getText().trim());
                product.setType(typeCombo.getValue());
                product.setPrice(price);
                product.setStock(stock);
                product.setThreshold(threshold);
                product.setImage(selectedImageBytes[0]);
                return product;
            }
            return null;
        });

        return dialog;
    }

    // ======================== CARRIERS TAB ========================

    private void setupCarriersTable() {
        carrIdColumn.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId()).asObject()
        );
        carrUsernameColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getUsername())
        );
        carrNameColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getFullName())
        );
        carrPhoneColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getPhone())
        );
        carrEmailColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getEmail())
        );
        carrRatingColumn.setCellValueFactory(data -> {
            double rating = ratingDAO.getAverageRating(data.getValue().getId());
            int count = ratingDAO.getRatingCount(data.getValue().getId());
            String text = count > 0
                ? String.format("%.1f/5 (%d)", rating, count)
                : "No ratings";
            return new SimpleStringProperty(text);
        });
    }

    private void loadCarriers() {
        List<User> carriers = userDAO.getAllCarriers();
        carriersTable.setItems(FXCollections.observableArrayList(carriers));
    }

    @FXML
    private void handleEmployCarrier(ActionEvent event) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Employ New Carrier");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField fullNameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Full Name:"), 0, 2);
        grid.add(fullNameField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog
            .getDialogPane()
            .getButtonTypes()
            .addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                if (
                    usernameField.getText().trim().isEmpty() ||
                    passwordField.getText().isEmpty()
                ) {
                    AlertUtils.showValidationError(
                        "Username and password are required."
                    );
                    return null;
                }

                User carrier = new User();
                carrier.setUsername(usernameField.getText().trim());
                carrier.setPassword(passwordField.getText());
                carrier.setFullName(fullNameField.getText().trim());
                carrier.setPhone(phoneField.getText().trim());
                carrier.setEmail(emailField.getText().trim());
                return carrier;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(carrier -> {
            if (userDAO.addCarrier(carrier)) {
                AlertUtils.showSuccess("Carrier employed successfully!");
                loadCarriers();
            } else {
                AlertUtils.showError(
                    "Error",
                    "Could not add carrier. Username may exist."
                );
            }
        });
    }

    @FXML
    private void handleFireCarrier(ActionEvent event) {
        User selected = carriersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning(
                "No Selection",
                "Please select a carrier to fire."
            );
            return;
        }

        if (
            AlertUtils.showConfirmation(
                "Fire Carrier",
                "Are you sure you want to fire " + selected.getUsername() + "?"
            )
        ) {
            if (userDAO.deleteCarrier(selected.getId())) {
                AlertUtils.showSuccess("Carrier removed successfully!");
                loadCarriers();
            } else {
                AlertUtils.showError("Error", "Could not remove carrier.");
            }
        }
    }

    @FXML
    private void handleRefreshCarriers(ActionEvent event) {
        loadCarriers();
    }

    // ======================== ORDERS TAB ========================

    private void setupOrdersTable() {
        ordIdColumn.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId()).asObject()
        );
        ordCustomerColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCustomerName())
        );
        ordDateColumn.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getOrderTime() != null
                    ? data.getValue().getOrderTime().format(dateFormatter)
                    : ""
            )
        );
        ordDeliveryColumn.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getRequestedDelivery() != null
                    ? data
                          .getValue()
                          .getRequestedDelivery()
                          .format(dateFormatter)
                    : ""
            )
        );
        ordStatusColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getStatus())
        );
        ordCarrierColumn.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getCarrierName() != null
                    ? data.getValue().getCarrierName()
                    : "-"
            )
        );
        ordTotalColumn.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getTotalCost()).asObject()
        );

        // Order filter combo
        orderFilterCombo
            .getItems()
            .addAll("ALL", "PENDING", "SELECTED", "DELIVERED", "CANCELLED");
        orderFilterCombo.setValue("ALL");
    }

    private void loadOrders() {
        String filter = orderFilterCombo.getValue();
        List<Order> orders;

        if ("ALL".equals(filter)) {
            orders = orderDAO.findAll();
        } else {
            orders = orderDAO.findByStatus(filter);
        }

        ordersTable.setItems(FXCollections.observableArrayList(orders));

        // Update total sales
        double totalSales = orderDAO.getTotalSales();
        totalSalesLabel.setText(
            String.format("Total Sales: $%.2f", totalSales)
        );
    }

    @FXML
    private void handleFilterOrders(ActionEvent event) {
        loadOrders();
    }

    @FXML
    private void handleRefreshOrders(ActionEvent event) {
        loadOrders();
    }

    // ======================== MESSAGES TAB ========================

    private void setupMessagesTab() {
        messagesList.setCellFactory(lv ->
            new ListCell<Message>() {
                @Override
                protected void updateItem(Message msg, boolean empty) {
                    super.updateItem(msg, empty);
                    if (empty || msg == null) {
                        setText(null);
                    } else {
                        String text =
                            (msg.isRead() ? "" : "🔴 ") +
                            "From: " +
                            msg.getSenderName() +
                            "\n" +
                            "Subject: " +
                            msg.getSubject() +
                            "\n" +
                            msg.getSentAt().format(dateFormatter);
                        if (msg.hasReply()) {
                            text += " ✓ Replied";
                        }
                        setText(text);
                    }
                }
            }
        );

        messagesList
            .getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, old, selected) -> {
                if (selected != null) {
                    messageContentArea.setText(
                        "From: " +
                            selected.getSenderName() +
                            "\n" +
                            "Subject: " +
                            selected.getSubject() +
                            "\n" +
                            "Date: " +
                            selected.getSentAt().format(dateFormatter) +
                            "\n\n" +
                            selected.getContent()
                    );

                    if (selected.hasReply()) {
                        replyArea.setText(selected.getReply());
                    } else {
                        replyArea.clear();
                    }

                    // Mark as read
                    if (!selected.isRead()) {
                        messageDAO.markAsRead(selected.getId());
                        loadMessages();
                    }
                }
            });
    }

    private void loadMessages() {
        List<Message> messages = messageDAO.findByReceiver(currentUser.getId());
        messagesList.setItems(FXCollections.observableArrayList(messages));
    }

    @FXML
    private void handleSendReply(ActionEvent event) {
        Message selected = messagesList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning(
                "No Selection",
                "Please select a message to reply to."
            );
            return;
        }

        String reply = replyArea.getText().trim();
        if (reply.isEmpty()) {
            AlertUtils.showValidationError("Please enter a reply.");
            return;
        }

        if (messageDAO.reply(selected.getId(), reply)) {
            AlertUtils.showSuccess("Reply sent successfully!");
            loadMessages();
        } else {
            AlertUtils.showError("Error", "Could not send reply.");
        }
    }

    // ======================== COUPONS TAB ========================

    private void setupCouponsTable() {
        coupIdColumn.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId()).asObject()
        );
        coupCodeColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCode())
        );
        coupDiscountColumn.setCellValueFactory(data ->
            new SimpleDoubleProperty(
                data.getValue().getDiscountPercent()
            ).asObject()
        );
        coupMinColumn.setCellValueFactory(data ->
            new SimpleDoubleProperty(
                data.getValue().getMinOrderValue()
            ).asObject()
        );
        coupExpiryColumn.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getExpiryDate() != null
                    ? data.getValue().getExpiryDate().toString()
                    : "No expiry"
            )
        );
        coupActiveColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().isActive() ? "Yes" : "No")
        );

        // Status column shows overall coupon status
        coupStatusColumn.setCellValueFactory(data -> {
            Coupon coupon = data.getValue();
            String status;
            if (!coupon.isActive()) {
                status = "Inactive";
            } else if (
                coupon.getExpiryDate() != null &&
                coupon.getExpiryDate().isBefore(java.time.LocalDate.now())
            ) {
                status = "Expired";
            } else {
                status = "Active";
            }
            return new SimpleStringProperty(status);
        });
    }

    private void loadCoupons() {
        List<Coupon> coupons = couponDAO.findAll();
        couponsTable.setItems(FXCollections.observableArrayList(coupons));
    }

    private void loadLoyaltySettings() {
        LoyaltySettings settings = loyaltySettingsDAO.getSettings();
        loyaltyOrdersField.setText(
            String.valueOf(settings.getMinOrdersForDiscount())
        );
        loyaltyDiscountField.setText(
            String.valueOf(settings.getDiscountPercent())
        );
    }

    @FXML
    private void handleCreateCoupon(ActionEvent event) {
        Dialog<Coupon> dialog = new Dialog<>();
        dialog.setTitle("Create Coupon");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField codeField = new TextField();
        TextField discountField = new TextField();
        TextField minOrderField = new TextField("0");
        DatePicker expiryPicker = new DatePicker();

        grid.add(new Label("Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Discount %:"), 0, 1);
        grid.add(discountField, 1, 1);
        grid.add(new Label("Min Order Value:"), 0, 2);
        grid.add(minOrderField, 1, 2);
        grid.add(new Label("Expiry Date:"), 0, 3);
        grid.add(expiryPicker, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog
            .getDialogPane()
            .getButtonTypes()
            .addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String code = codeField.getText().trim().toUpperCase();
                if (code.isEmpty()) {
                    AlertUtils.showValidationError("Code is required.");
                    return null;
                }

                double discount = ValidationUtils.parseDouble(
                    discountField.getText()
                );
                if (discount <= 0 || discount > 100) {
                    AlertUtils.showValidationError(
                        "Discount must be between 1 and 100."
                    );
                    return null;
                }

                double minOrder = ValidationUtils.parseDouble(
                    minOrderField.getText()
                );

                Coupon coupon = new Coupon();
                coupon.setCode(code);
                coupon.setDiscountPercent(discount);
                coupon.setMinOrderValue(minOrder);
                coupon.setExpiryDate(expiryPicker.getValue());
                coupon.setActive(true);
                return coupon;
            }
            return null;
        });

        Optional<Coupon> result = dialog.showAndWait();
        result.ifPresent(coupon -> {
            if (couponDAO.create(coupon)) {
                AlertUtils.showSuccess("Coupon created successfully!");
                loadCoupons();
            } else {
                AlertUtils.showError(
                    "Error",
                    "Could not create coupon. Code may exist."
                );
            }
        });
    }

    @FXML
    private void handleDeactivateCoupon(ActionEvent event) {
        Coupon selected = couponsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning(
                "No Selection",
                "Please select a coupon to deactivate."
            );
            return;
        }

        if (couponDAO.deactivate(selected.getId())) {
            AlertUtils.showSuccess("Coupon deactivated!");
            loadCoupons();
        }
    }

    @FXML
    private void handleAssignCoupon(ActionEvent event) {
        Coupon selected = couponsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning(
                "No Selection",
                "Please select a coupon to assign."
            );
            return;
        }

        List<User> customers = userDAO.getAllCustomers();
        ChoiceDialog<User> dialog = new ChoiceDialog<>(
            customers.isEmpty() ? null : customers.get(0),
            customers
        );
        dialog.setTitle("Assign Coupon");
        dialog.setHeaderText("Select a customer to assign the coupon");

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(customer -> {
            if (couponDAO.assignToUser(customer.getId(), selected.getId())) {
                AlertUtils.showSuccess(
                    "Coupon assigned to " + customer.getUsername() + "!"
                );
            } else {
                AlertUtils.showError("Error", "Could not assign coupon.");
            }
        });
    }

    @FXML
    private void handleRefreshCoupons(ActionEvent event) {
        loadCoupons();
    }

    @FXML
    private void handleSaveLoyalty(ActionEvent event) {
        int orders = ValidationUtils.parseInt(loyaltyOrdersField.getText());
        double discount = ValidationUtils.parseDouble(
            loyaltyDiscountField.getText()
        );

        if (orders <= 0) {
            AlertUtils.showValidationError("Orders required must be positive.");
            return;
        }
        if (discount <= 0 || discount > 100) {
            AlertUtils.showValidationError(
                "Discount must be between 1 and 100."
            );
            return;
        }

        LoyaltySettings settings = loyaltySettingsDAO.getSettings();
        settings.setMinOrdersForDiscount(orders);
        settings.setDiscountPercent(discount);

        if (loyaltySettingsDAO.update(settings)) {
            AlertUtils.showSuccess("Loyalty settings saved!");
        } else {
            AlertUtils.showError("Error", "Could not save settings.");
        }
    }

    // ======================== RATINGS TAB ========================

    private void setupRatingsTable() {
        rateCarrierColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCarrierName())
        );
        rateCustomerColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCustomerName())
        );
        rateOrderColumn.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getOrderId()).asObject()
        );
        rateStarsColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getStarsDisplay())
        );
        rateCommentColumn.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getComment())
        );
        rateDateColumn.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getCreatedAt() != null
                    ? data.getValue().getCreatedAt().format(dateFormatter)
                    : ""
            )
        );
    }

    private void loadRatings() {
        List<Rating> ratings = ratingDAO.findAll();
        ratingsTable.setItems(FXCollections.observableArrayList(ratings));
    }

    // ======================== REPORTS TAB ========================

    private void loadCharts() {
        // Sales by Product Bar Chart
        productSalesChart.getData().clear();
        XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();
        salesSeries.setName("Sales");

        try {
            ResultSet rs = orderDAO.getSalesByProduct();
            int count = 0;
            while (rs != null && rs.next() && count < 10) {
                String product = rs.getString("product_name");
                double sales = rs.getDouble("total_sales");
                salesSeries.getData().add(new XYChart.Data<>(product, sales));
                count++;
            }
        } catch (Exception e) {
            System.err.println("Error loading sales chart: " + e.getMessage());
        }

        productSalesChart.getData().add(salesSeries);

        // Product Distribution Pie Chart
        productDistributionChart.getData().clear();

        List<Product> products = productDAO.findAllIncludingOutOfStock();
        int vegCount = 0,
            fruitCount = 0;
        for (Product p : products) {
            if (p.isVegetable()) vegCount++;
            else fruitCount++;
        }

        productDistributionChart
            .getData()
            .addAll(
                new PieChart.Data("Vegetables (" + vegCount + ")", vegCount),
                new PieChart.Data("Fruits (" + fruitCount + ")", fruitCount)
            );
    }

    @FXML
    private void handleRefreshCharts(ActionEvent event) {
        loadCharts();
        AlertUtils.showInfo("Refreshed", "Charts updated with latest data.");
    }

    // ======================== GENERAL ========================

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.getInstance().logout();
        Stage stage = (Stage) usernameLabel.getScene().getWindow();
        SceneNavigator.loadScene(
            stage,
            "Login.fxml",
            "Group17 GreenGrocer - Login"
        );
    }
}
