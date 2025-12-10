package com.greengrocer.controllers;

import com.greengrocer.database.*;
import com.greengrocer.models.*;
import com.greengrocer.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.util.List;

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
    private RatingDAO ratingDAO;
    private UserDAO userDAO;
    private User currentUser;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        orderDAO = new OrderDAO();
        ratingDAO = new RatingDAO();
        userDAO = new UserDAO();
        currentUser = SessionManager.getInstance().getCurrentUser();

        usernameLabel.setText("Carrier: " + currentUser.getUsername());

        // Enable multiple selection for available orders
        availableOrdersList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
                    text.append("Customer: ").append(order.getCustomerName()).append("\n");
                    text.append("Address: ")
                            .append(order.getCustomerAddress() != null ? order.getCustomerAddress() : "N/A")
                            .append("\n");
                    text.append("Delivery: ").append(order.getRequestedDelivery().toString()).append("\n");
                    text.append(String.format("Total: $%.2f (incl. VAT)\n", order.getTotalCost()));
                    text.append("Items:\n");
                    for (OrderItem item : order.getItems()) {
                        text.append("  - ").append(item.getProductName())
                                .append(": ").append(String.format("%.2f kg", item.getQuantity())).append("\n");
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
        List<Order> current = orderDAO.findByCarrierSelected(currentUser.getId());
        currentOrdersList.getItems().clear();
        currentOrdersList.getItems().addAll(current);

        // Completed orders
        List<Order> completed = orderDAO.findByCarrierCompleted(currentUser.getId());
        completedOrdersList.getItems().clear();
        completedOrdersList.getItems().addAll(completed);

        statusLabel.setText(String.format("Available: %d | Current: %d | Completed: %d",
                available.size(), current.size(), completed.size()));
    }

    /**
     * Updates the carrier's rating display.
     */
    private void updateRating() {
        double avgRating = ratingDAO.getAverageRating(currentUser.getId());
        int ratingCount = ratingDAO.getRatingCount(currentUser.getId());

        if (ratingCount > 0) {
            ratingLabel.setText(String.format("Your Rating: %.1f/5 (%d reviews)", avgRating, ratingCount));
        } else {
            ratingLabel.setText("Your Rating: No reviews yet");
        }
    }

    /**
     * Handles selecting orders for delivery.
     */
    @FXML
    private void handleSelectOrders(ActionEvent event) {
        List<Order> selected = availableOrdersList.getSelectionModel().getSelectedItems();

        if (selected.isEmpty()) {
            AlertUtils.showWarning("No Selection", "Please select at least one order to deliver.");
            return;
        }

        int successCount = 0;
        int failCount = 0;

        for (Order order : selected) {
            boolean success = orderDAO.selectOrder(order.getId(), currentUser.getId());
            if (success) {
                successCount++;
            } else {
                failCount++;
            }
        }

        if (failCount > 0) {
            AlertUtils.showWarning("Some Orders Unavailable",
                    failCount + " order(s) were already selected by another carrier.");
        }

        if (successCount > 0) {
            AlertUtils.showSuccess(successCount + " order(s) selected for delivery!");
        }

        loadOrders();
    }

    /**
     * Handles completing a delivery.
     */
    @FXML
    private void handleCompleteDelivery(ActionEvent event) {
        Order selected = currentOrdersList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AlertUtils.showWarning("No Selection", "Please select an order to complete.");
            return;
        }

        // Confirm completion
        if (!AlertUtils.showConfirmation("Complete Delivery",
                "Have you delivered Order #" + selected.getId() + " and collected payment of $" +
                        String.format("%.2f", selected.getTotalCost()) + "?")) {
            return;
        }

        // Complete the order
        boolean success = orderDAO.completeOrder(selected.getId(), LocalDateTime.now());

        if (success) {
            // Update customer's completed orders count
            userDAO.incrementCompletedOrders(selected.getUserId());

            AlertUtils.showSuccess("Delivery completed successfully!");
            loadOrders();
            updateRating();
        } else {
            AlertUtils.showError("Error", "Could not complete delivery. Please try again.");
        }
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
        SceneNavigator.loadScene(stage, "Login.fxml", "Group17 GreenGrocer - Login");
    }
}
