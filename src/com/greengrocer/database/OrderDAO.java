package com.greengrocer.database;

import com.greengrocer.models.Order;
import com.greengrocer.models.OrderItem;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Order operations.
 * Handles all database operations related to orders.
 * 
 * @author Group17
 * @version 1.0
 */
public class OrderDAO {

    /** Database adapter instance */
    private DatabaseAdapter db;

    /**
     * Constructor - initializes database adapter.
     */
    public OrderDAO() {
        this.db = DatabaseAdapter.getInstance();
    }

    /**
     * Creates a new order.
     * 
     * @param order The order to create
     * @return The created order ID, or -1 if failed
     */
    public int create(Order order) {
        String query = "INSERT INTO OrderInfo (user_id, requested_delivery, status, subtotal, vat, discount, total_cost, invoice) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = db.prepareStatementWithKeys(query);
            stmt.setInt(1, order.getUserId());
            stmt.setTimestamp(2, Timestamp.valueOf(order.getRequestedDelivery()));
            stmt.setString(3, order.getStatus());
            stmt.setDouble(4, order.getSubtotal());
            stmt.setDouble(5, order.getVat());
            stmt.setDouble(6, order.getDiscount());
            stmt.setDouble(7, order.getTotalCost());
            stmt.setString(8, order.getInvoice());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);

                    // Insert order items
                    for (OrderItem item : order.getItems()) {
                        addOrderItem(orderId, item);
                    }

                    return orderId;
                }
            }
        } catch (SQLException e) {
            System.err.println("Create order error: " + e.getMessage());
        }

        return -1;
    }

    /**
     * Adds an order item to the database.
     * 
     * @param orderId The order ID
     * @param item    The order item
     * @return true if successful
     */
    public boolean addOrderItem(int orderId, OrderItem item) {
        String query = "INSERT INTO OrderItems (order_id, product_id, product_name, quantity, price_at_time) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, orderId);
            stmt.setInt(2, item.getProductId());
            stmt.setString(3, item.getProductName());
            stmt.setDouble(4, item.getQuantity());
            stmt.setDouble(5, item.getPriceAtTime());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Add order item error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets all orders for a specific customer.
     * 
     * @param userId The customer ID
     * @return List of orders
     */
    public List<Order> findByUser(int userId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.*, u.username as customer_name, u.address as customer_address, " +
                "c.username as carrier_name " +
                "FROM OrderInfo o " +
                "LEFT JOIN UserInfo u ON o.user_id = u.id " +
                "LEFT JOIN UserInfo c ON o.carrier_id = c.id " +
                "WHERE o.user_id = ? " +
                "ORDER BY o.order_time DESC";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Find orders by user error: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Gets all pending orders (available for carriers).
     * 
     * @return List of pending orders
     */
    public List<Order> findPending() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.*, u.username as customer_name, u.address as customer_address, " +
                "c.username as carrier_name " +
                "FROM OrderInfo o " +
                "LEFT JOIN UserInfo u ON o.user_id = u.id " +
                "LEFT JOIN UserInfo c ON o.carrier_id = c.id " +
                "WHERE o.status = 'PENDING' " +
                "ORDER BY o.order_time ASC";

        try {
            ResultSet rs = db.executeQuery(query);

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Find pending orders error: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Gets all orders selected by a specific carrier.
     * 
     * @param carrierId The carrier ID
     * @return List of selected orders
     */
    public List<Order> findByCarrierSelected(int carrierId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.*, u.username as customer_name, u.address as customer_address, " +
                "c.username as carrier_name " +
                "FROM OrderInfo o " +
                "LEFT JOIN UserInfo u ON o.user_id = u.id " +
                "LEFT JOIN UserInfo c ON o.carrier_id = c.id " +
                "WHERE o.carrier_id = ? AND o.status = 'SELECTED' " +
                "ORDER BY o.requested_delivery ASC";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, carrierId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Find carrier selected orders error: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Gets all orders completed by a specific carrier.
     * 
     * @param carrierId The carrier ID
     * @return List of completed orders
     */
    public List<Order> findByCarrierCompleted(int carrierId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.*, u.username as customer_name, u.address as customer_address, " +
                "c.username as carrier_name " +
                "FROM OrderInfo o " +
                "LEFT JOIN UserInfo u ON o.user_id = u.id " +
                "LEFT JOIN UserInfo c ON o.carrier_id = c.id " +
                "WHERE o.carrier_id = ? AND o.status = 'DELIVERED' " +
                "ORDER BY o.delivery_time DESC";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, carrierId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Find carrier completed orders error: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Gets all orders (for owner view).
     * 
     * @return List of all orders
     */
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.*, u.username as customer_name, u.address as customer_address, " +
                "c.username as carrier_name " +
                "FROM OrderInfo o " +
                "LEFT JOIN UserInfo u ON o.user_id = u.id " +
                "LEFT JOIN UserInfo c ON o.carrier_id = c.id " +
                "ORDER BY o.order_time DESC";

        try {
            ResultSet rs = db.executeQuery(query);

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Find all orders error: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Selects an order for a carrier (changes status to SELECTED).
     * Uses transaction to handle concurrency.
     * 
     * @param orderId   The order ID
     * @param carrierId The carrier ID
     * @return true if successful, false if already selected
     */
    public boolean selectOrder(int orderId, int carrierId) {
        // First check if order is still pending
        String checkQuery = "SELECT status FROM OrderInfo WHERE id = ? FOR UPDATE";
        String updateQuery = "UPDATE OrderInfo SET carrier_id = ?, status = 'SELECTED' WHERE id = ? AND status = 'PENDING'";

        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);

            try {
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setInt(1, orderId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && "PENDING".equals(rs.getString("status"))) {
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, carrierId);
                    updateStmt.setInt(2, orderId);

                    int rows = updateStmt.executeUpdate();
                    conn.commit();
                    return rows > 0;
                } else {
                    conn.rollback();
                    return false; // Order already selected by another carrier
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Select order error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Completes an order (changes status to DELIVERED).
     * 
     * @param orderId      The order ID
     * @param deliveryTime The actual delivery time
     * @return true if successful
     */
    public boolean completeOrder(int orderId, LocalDateTime deliveryTime) {
        String query = "UPDATE OrderInfo SET status = 'DELIVERED', delivery_time = ? WHERE id = ? AND status = 'SELECTED'";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setTimestamp(1, Timestamp.valueOf(deliveryTime));
            stmt.setInt(2, orderId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Complete order error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cancels an order.
     * 
     * @param orderId The order ID
     * @return true if successful
     */
    public boolean cancelOrder(int orderId) {
        String query = "UPDATE OrderInfo SET status = 'CANCELLED' WHERE id = ? AND status = 'PENDING'";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, orderId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Cancel order error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets all items for an order.
     * 
     * @param orderId The order ID
     * @return List of order items
     */
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String query = "SELECT * FROM OrderItems WHERE order_id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, orderId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getInt("id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getDouble("quantity"));
                item.setPriceAtTime(rs.getDouble("price_at_time"));
                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Get order items error: " + e.getMessage());
        }

        return items;
    }

    /**
     * Gets an order by ID.
     * 
     * @param orderId The order ID
     * @return Order or null if not found
     */
    public Order findById(int orderId) {
        String query = "SELECT o.*, u.username as customer_name, u.address as customer_address, " +
                "c.username as carrier_name " +
                "FROM OrderInfo o " +
                "LEFT JOIN UserInfo u ON o.user_id = u.id " +
                "LEFT JOIN UserInfo c ON o.carrier_id = c.id " +
                "WHERE o.id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, orderId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setItems(getOrderItems(order.getId()));
                return order;
            }
        } catch (SQLException e) {
            System.err.println("Find order error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Gets orders by status.
     * 
     * @param status The order status
     * @return List of orders with the specified status
     */
    public List<Order> findByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.*, u.username as customer_name, u.address as customer_address, " +
                "c.username as carrier_name " +
                "FROM OrderInfo o " +
                "LEFT JOIN UserInfo u ON o.user_id = u.id " +
                "LEFT JOIN UserInfo c ON o.carrier_id = c.id " +
                "WHERE o.status = ? " +
                "ORDER BY o.order_time DESC";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, status);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Find orders by status error: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Gets total sales amount.
     * 
     * @return Total sales
     */
    public double getTotalSales() {
        String query = "SELECT SUM(total_cost) as total FROM OrderInfo WHERE status = 'DELIVERED'";

        try {
            ResultSet rs = db.executeQuery(query);

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Get total sales error: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Gets sales by product for reports.
     * 
     * @return ResultSet with product sales data
     */
    public ResultSet getSalesByProduct() {
        String query = "SELECT oi.product_name, SUM(oi.quantity) as total_quantity, SUM(oi.quantity * oi.price_at_time) as total_sales "
                +
                "FROM OrderItems oi " +
                "JOIN OrderInfo o ON oi.order_id = o.id " +
                "WHERE o.status = 'DELIVERED' " +
                "GROUP BY oi.product_name " +
                "ORDER BY total_sales DESC";

        try {
            return db.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("Get sales by product error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extracts an Order object from a ResultSet.
     * 
     * @param rs The ResultSet positioned at the order row
     * @return Order object
     * @throws SQLException If data extraction fails
     */
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setCustomerName(rs.getString("customer_name"));
        order.setCustomerAddress(rs.getString("customer_address"));
        order.setCarrierId(rs.getInt("carrier_id"));
        order.setCarrierName(rs.getString("carrier_name"));

        Timestamp orderTime = rs.getTimestamp("order_time");
        if (orderTime != null) {
            order.setOrderTime(orderTime.toLocalDateTime());
        }

        Timestamp requestedDelivery = rs.getTimestamp("requested_delivery");
        if (requestedDelivery != null) {
            order.setRequestedDelivery(requestedDelivery.toLocalDateTime());
        }

        Timestamp deliveryTime = rs.getTimestamp("delivery_time");
        if (deliveryTime != null) {
            order.setDeliveryTime(deliveryTime.toLocalDateTime());
        }

        order.setStatus(rs.getString("status"));
        order.setSubtotal(rs.getDouble("subtotal"));
        order.setVat(rs.getDouble("vat"));
        order.setDiscount(rs.getDouble("discount"));
        order.setTotalCost(rs.getDouble("total_cost"));
        order.setInvoice(rs.getString("invoice"));

        return order;
    }
}
