package com.greengrocer.controllers;

import com.greengrocer.database.*;
import com.greengrocer.models.*;
import com.greengrocer.utils.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Controller for the Carrier interface.
 * Handles order selection and delivery completion.
 *
 * @author Group17
 * @version 1.0
 */
public class CarrierController {

    @FXML
    private Label usernameLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label ratingLabel;

    @FXML
    private ListView<Order> availableOrdersList;

    @FXML
    private ListView<Order> currentOrdersList;

    @FXML
    private ListView<Order> completedOrdersList;

    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private RatingDAO ratingDAO;
    private MessageDAO messageDAO;
    private User currentUser;

    /**
     * Default constructor for CarrierController.
     * Called by JavaFX when loading the FXML file.
     */
    public CarrierController() {}

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        orderDAO = new OrderDAO();
        ratingDAO = new RatingDAO();
        userDAO = new UserDAO();
        messageDAO = new MessageDAO();
        currentUser = SessionManager.getInstance().getCurrentUser();

        usernameLabel.setText("Carrier: " + currentUser.getUsername());

        // Enable multiple selection for available orders
        availableOrdersList
            .getSelectionModel()
            .setSelectionMode(SelectionMode.MULTIPLE);

        setupListViews();
        loadOrders();
        updateRating();
    }

    /**
     * Sets up the list view cell factories.
     */
    private void setupListViews() {
        // Available orders list
        availableOrdersList.setCellFactory(lv -> createOrderCell());

        // Current orders list
        currentOrdersList.setCellFactory(lv -> createOrderCell());

        // Completed orders list
        completedOrdersList.setCellFactory(lv -> createOrderCell());
    }

    /**
     * Creates a cell factory for order display.
     */
    private ListCell<Order> createOrderCell() {
        return new ListCell<Order>() {
            @Override
            protected void updateItem(Order order, boolean empty) {
                super.updateItem(order, empty);
                if (empty || order == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    StringBuilder text = new StringBuilder();
                    text.append("Order #").append(order.getId()).append("\n");
                    text
                        .append("Customer: ")
                        .append(order.getCustomerName())
                        .append("\n");
                    text
                        .append("Address: ")
                        .append(
                            order.getCustomerAddress() != null
                                ? order.getCustomerAddress()
                                : "N/A"
                        )
                        .append("\n");
                    text
                        .append("Delivery: ")
                        .append(order.getRequestedDelivery().toString())
                        .append("\n");
                    text.append(
                        String.format(
                            "Total: $%.2f (incl. VAT)\n",
                            order.getTotalCost()
                        )
                    );
                    text.append("Items:\n");
                    for (OrderItem item : order.getItems()) {
                        text
                            .append("  - ")
                            .append(item.getProductName())
                            .append(": ")
                            .append(
                                String.format("%.2f kg", item.getQuantity())
                            )
                            .append("\n");
                    }
                    setText(text.toString());
                }
            }
        };
    }

    /**
     * Loads all orders into the appropriate lists.
     */
    private void loadOrders() {
        // Available (Pending) orders
        List<Order> available = orderDAO.findPending();
        availableOrdersList.getItems().clear();
        availableOrdersList.getItems().addAll(available);

        // Current (Selected by this carrier) orders
        List<Order> current = orderDAO.findByCarrierSelected(
            currentUser.getId()
        );
        currentOrdersList.getItems().clear();
        currentOrdersList.getItems().addAll(current);

        // Completed orders
        List<Order> completed = orderDAO.findByCarrierCompleted(
            currentUser.getId()
        );
        completedOrdersList.getItems().clear();
        completedOrdersList.getItems().addAll(completed);

        statusLabel.setText(
            String.format(
                "Available: %d | Current: %d | Completed: %d",
                available.size(),
                current.size(),
                completed.size()
            )
        );
    }

    /**
     * Updates the carrier's rating display.
     */
    private void updateRating() {
        double avgRating = ratingDAO.getAverageRating(currentUser.getId());
        int ratingCount = ratingDAO.getRatingCount(currentUser.getId());

        if (ratingCount > 0) {
            ratingLabel.setText(
                String.format(
                    "Your Rating: %.1f/5 (%d reviews)",
                    avgRating,
                    ratingCount
                )
            );
        } else {
            ratingLabel.setText("Your Rating: No reviews yet");
        }
    }

    /**
     * Handles selecting orders for delivery.
     */
    @FXML
    private void handleSelectOrders(ActionEvent event) {
        List<Order> selected = availableOrdersList
            .getSelectionModel()
            .getSelectedItems();

        if (selected.isEmpty()) {
            AlertUtils.showWarning(
                "No Selection",
                "Please select at least one order to deliver."
            );
            return;
        }

        int successCount = 0;
        int failCount = 0;

        for (Order order : selected) {
            boolean success = orderDAO.selectOrder(
                order.getId(),
                currentUser.getId()
            );
            if (success) {
                successCount++;
            } else {
                failCount++;
            }
        }

        if (failCount > 0) {
            AlertUtils.showWarning(
                "Some Orders Unavailable",
                failCount +
                    " order(s) were already selected by another carrier."
            );
        }

        if (successCount > 0) {
            AlertUtils.showSuccess(
                successCount + " order(s) selected for delivery!"
            );
        }

        loadOrders();
    }

    /**
     * Handles completing a delivery.
     */
    @FXML
    private void handleCompleteDelivery(ActionEvent event) {
        Order selected = currentOrdersList
            .getSelectionModel()
            .getSelectedItem();

        if (selected == null) {
            AlertUtils.showWarning(
                "No Selection",
                "Please select an order to complete."
            );
            return;
        }

        // Create dialog for delivery date and time input
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Complete Delivery");
        dialog.setHeaderText(
            "Enter delivery date and time for Order #" +
                selected.getId() +
                "\nPayment collected: $" +
                String.format("%.2f", selected.getTotalCost())
        );

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        DatePicker datePicker = new DatePicker(LocalDate.now());

        // Time input fields
        Spinner<Integer> hourSpinner = new Spinner<>(
            0,
            23,
            LocalDateTime.now().getHour()
        );
        hourSpinner.setEditable(true);
        hourSpinner.setPrefWidth(70);

        Spinner<Integer> minuteSpinner = new Spinner<>(
            0,
            59,
            LocalDateTime.now().getMinute()
        );
        minuteSpinner.setEditable(true);
        minuteSpinner.setPrefWidth(70);

        grid.add(new Label("Delivery Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Delivery Time:"), 0, 1);
        grid.add(new Label("Hour:"), 1, 1);
        grid.add(hourSpinner, 2, 1);
        grid.add(new Label("Minute:"), 3, 1);
        grid.add(minuteSpinner, 4, 1);

        dialog.getDialogPane().setContent(grid);
        dialog
            .getDialogPane()
            .getButtonTypes()
            .addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                LocalDate date = datePicker.getValue();
                int hour = hourSpinner.getValue();
                int minute = minuteSpinner.getValue();

                if (date == null) {
                    AlertUtils.showValidationError(
                        "Please select a delivery date."
                    );
                    return null;
                }

                LocalDateTime deliveryTime = LocalDateTime.of(
                    date,
                    LocalTime.of(hour, minute)
                );

                // Validate that delivery time is not in the future
                if (deliveryTime.isAfter(LocalDateTime.now())) {
                    AlertUtils.showValidationError(
                        "Delivery time cannot be in the future."
                    );
                    return null;
                }

                // Validate that delivery time is not before order time
                if (deliveryTime.isBefore(selected.getOrderTime())) {
                    AlertUtils.showValidationError(
                        "Delivery time cannot be before order time."
                    );
                    return null;
                }

                return deliveryTime;
            }
            return null;
        });

        Optional<LocalDateTime> result = dialog.showAndWait();
        result.ifPresent(deliveryTime -> {
            // Complete the order with the specified delivery time
            boolean success = orderDAO.completeOrder(
                selected.getId(),
                deliveryTime
            );

            if (success) {
                // Update customer's completed orders count
                userDAO.incrementCompletedOrders(selected.getUserId());

                // Send notification to customer
                String notificationSubject =
                    "Order #" + selected.getId() + " Delivered";
                String notificationContent = String.format(
                    "Your order #%d has been delivered successfully at %s.\n\n" +
                        "Total: $%.2f\n\n" +
                        "Thank you for shopping with us!\n\n" +
                        "You can rate your delivery experience from your orders page.",
                    selected.getId(),
                    deliveryTime.format(
                        java.time.format.DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd HH:mm"
                        )
                    ),
                    selected.getTotalCost()
                );

                Message notification = new Message(
                    currentUser.getId(),
                    selected.getUserId(),
                    notificationSubject,
                    notificationContent
                );
                messageDAO.send(notification);

                AlertUtils.showSuccess(
                    "Delivery completed successfully at " +
                        deliveryTime.format(
                            java.time.format.DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd HH:mm"
                            )
                        ) +
                        "\n\nCustomer has been notified."
                );
                loadOrders();
                updateRating();
            } else {
                AlertUtils.showError(
                    "Error",
                    "Could not complete delivery. Please try again."
                );
            }
        });
    }

    /**
     * Handles refresh button click.
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadOrders();
        updateRating();
        statusLabel.setText("Orders refreshed.");
    }

    /**
     * Handles logout.
     */
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
