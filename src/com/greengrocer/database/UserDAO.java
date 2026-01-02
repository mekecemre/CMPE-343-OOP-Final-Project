package com.greengrocer.database;

import com.greengrocer.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations.
 * Handles all database operations related to users.
 * 
 * @author Group17
 * @version 1.0
 */
public class UserDAO {

    /** Database adapter instance */
    private DatabaseAdapter db;

    /**
     * Constructor - initializes database adapter.
     */
    public UserDAO() {
        this.db = DatabaseAdapter.getInstance();
    }

    /**
     * Authenticates a user by username and password.
     * 
     * @param username The username
     * @param password The password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticate(String username, String password) {
        String query = "SELECT * FROM UserInfo WHERE username = ? AND password = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Registers a new customer.
     * 
     * @param user The user to register
     * @return true if registration successful, false otherwise
     */
    public boolean register(User user) {
        String query = "INSERT INTO UserInfo (username, password, role, full_name, address, phone, email) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, "CUSTOMER"); // New users are always customers
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getPhone());
            stmt.setString(7, user.getEmail());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates user profile information.
     * 
     * @param user The user with updated information
     * @return true if update successful, false otherwise
     */
    public boolean update(User user) {
        String query = "UPDATE UserInfo SET full_name = ?, address = ?, phone = ?, email = ? WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getAddress());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getEmail());
            stmt.setInt(5, user.getId());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Update user error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Finds a user by ID.
     * 
     * @param id The user ID
     * @return User object or null if not found
     */
    public User findById(int id) {
        String query = "SELECT * FROM UserInfo WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Find user error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Finds a user by username.
     * 
     * @param username The username
     * @return User object or null if not found
     */
    public User findByUsername(String username) {
        String query = "SELECT * FROM UserInfo WHERE username = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Find user error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Gets all carriers (for owner management).
     * 
     * @return List of carrier users
     */
    public List<User> getAllCarriers() {
        List<User> carriers = new ArrayList<>();
        String query = "SELECT * FROM UserInfo WHERE role = 'CARRIER'";

        try {
            ResultSet rs = db.executeQuery(query);

            while (rs.next()) {
                carriers.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get carriers error: " + e.getMessage());
        }

        return carriers;
    }

    /**
     * Gets all customers.
     * 
     * @return List of customer users
     */
    public List<User> getAllCustomers() {
        List<User> customers = new ArrayList<>();
        String query = "SELECT * FROM UserInfo WHERE role = 'CUSTOMER'";

        try {
            ResultSet rs = db.executeQuery(query);

            while (rs.next()) {
                customers.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get customers error: " + e.getMessage());
        }

        return customers;
    }

    /**
     * Adds a new carrier (employed by owner).
     * 
     * @param user The carrier user to add
     * @return true if successful
     */
    public boolean addCarrier(User user) {
        String query = "INSERT INTO UserInfo (username, password, role, full_name, address, phone, email) " +
                "VALUES (?, ?, 'CARRIER', ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getAddress());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getEmail());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Add carrier error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a carrier (fired by owner).
     * 
     * @param carrierId The carrier ID to delete
     * @return true if successful
     */
    public boolean deleteCarrier(int carrierId) {
        String query = "DELETE FROM UserInfo WHERE id = ? AND role = 'CARRIER'";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, carrierId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Delete carrier error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the completed orders count for a user.
     * 
     * @param userId The user ID
     * @return true if successful
     */
    public boolean incrementCompletedOrders(int userId) {
        String query = "UPDATE UserInfo SET completed_orders = completed_orders + 1 WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, userId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Update completed orders error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Resets the completed orders count for a user (after using loyalty discount).
     * 
     * @param userId The user ID
     * @return true if successful
     */
    public boolean resetCompletedOrders(int userId) {
        String query = "UPDATE UserInfo SET completed_orders = 0 WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, userId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Reset completed orders error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the owner user.
     * 
     * @return Owner user or null
     */
    public User getOwner() {
        String query = "SELECT * FROM UserInfo WHERE role = 'OWNER' LIMIT 1";

        try {
            ResultSet rs = db.executeQuery(query);

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Get owner error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Extracts a User object from a ResultSet.
     * 
     * @param rs The ResultSet positioned at the user row
     * @return User object
     * @throws SQLException If data extraction fails
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setFullName(rs.getString("full_name"));
        user.setAddress(rs.getString("address"));
        user.setPhone(rs.getString("phone"));
        user.setEmail(rs.getString("email"));
        user.setLoyaltyPoints(rs.getInt("loyalty_points"));
        user.setCompletedOrders(rs.getInt("completed_orders"));
        return user;
    }
}
