package com.greengrocer.database;

import com.greengrocer.models.Rating;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Rating operations.
 * Handles carrier ratings from customers.
 * 
 * @author Group17
 * @version 1.0
 */
public class RatingDAO {

    /** Database adapter instance */
    private DatabaseAdapter db;

    /**
     * Constructor - initializes database adapter.
     */
    public RatingDAO() {
        this.db = DatabaseAdapter.getInstance();
    }

    /**
     * Creates a new rating.
     * 
     * @param rating The rating to create
     * @return true if successful
     */
    public boolean create(Rating rating) {
        String query = "INSERT INTO Ratings (order_id, carrier_id, customer_id, rating, comment) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, rating.getOrderId());
            stmt.setInt(2, rating.getCarrierId());
            stmt.setInt(3, rating.getCustomerId());
            stmt.setInt(4, rating.getRating());
            stmt.setString(5, rating.getComment());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Create rating error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets all ratings for a carrier.
     * 
     * @param carrierId The carrier ID
     * @return List of ratings
     */
    public List<Rating> findByCarrier(int carrierId) {
        List<Rating> ratings = new ArrayList<>();
        String query = "SELECT r.*, c.username as carrier_name, cu.username as customer_name " +
                "FROM Ratings r " +
                "JOIN UserInfo c ON r.carrier_id = c.id " +
                "JOIN UserInfo cu ON r.customer_id = cu.id " +
                "WHERE r.carrier_id = ? " +
                "ORDER BY r.created_at DESC";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, carrierId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ratings.add(extractRatingFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Find ratings error: " + e.getMessage());
        }

        return ratings;
    }

    /**
     * Gets all ratings (for owner view).
     * 
     * @return List of all ratings
     */
    public List<Rating> findAll() {
        List<Rating> ratings = new ArrayList<>();
        String query = "SELECT r.*, c.username as carrier_name, cu.username as customer_name " +
                "FROM Ratings r " +
                "JOIN UserInfo c ON r.carrier_id = c.id " +
                "JOIN UserInfo cu ON r.customer_id = cu.id " +
                "ORDER BY r.created_at DESC";

        try {
            ResultSet rs = db.executeQuery(query);

            while (rs.next()) {
                ratings.add(extractRatingFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Find all ratings error: " + e.getMessage());
        }

        return ratings;
    }

    /**
     * Gets the average rating for a carrier.
     * 
     * @param carrierId The carrier ID
     * @return Average rating (1-5) or 0 if no ratings
     */
    public double getAverageRating(int carrierId) {
        String query = "SELECT AVG(rating) as avg_rating FROM Ratings WHERE carrier_id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, carrierId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            System.err.println("Get average rating error: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Checks if a customer has already rated an order.
     * 
     * @param orderId    The order ID
     * @param customerId The customer ID
     * @return true if already rated
     */
    public boolean hasRated(int orderId, int customerId) {
        String query = "SELECT COUNT(*) as count FROM Ratings WHERE order_id = ? AND customer_id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, orderId);
            stmt.setInt(2, customerId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Check rated error: " + e.getMessage());
        }

        return false;
    }

    /**
     * Gets rating count for a carrier.
     * 
     * @param carrierId The carrier ID
     * @return Number of ratings
     */
    public int getRatingCount(int carrierId) {
        String query = "SELECT COUNT(*) as count FROM Ratings WHERE carrier_id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, carrierId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Get rating count error: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Extracts a Rating object from a ResultSet.
     * 
     * @param rs The ResultSet positioned at the rating row
     * @return Rating object
     * @throws SQLException If data extraction fails
     */
    private Rating extractRatingFromResultSet(ResultSet rs) throws SQLException {
        Rating rating = new Rating();
        rating.setId(rs.getInt("id"));
        rating.setOrderId(rs.getInt("order_id"));
        rating.setCarrierId(rs.getInt("carrier_id"));
        rating.setCarrierName(rs.getString("carrier_name"));
        rating.setCustomerId(rs.getInt("customer_id"));
        rating.setCustomerName(rs.getString("customer_name"));
        rating.setRating(rs.getInt("rating"));
        rating.setComment(rs.getString("comment"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            rating.setCreatedAt(createdAt.toLocalDateTime());
        }

        return rating;
    }
}
