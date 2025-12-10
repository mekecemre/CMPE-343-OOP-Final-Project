package com.greengrocer.database;

import com.greengrocer.models.Coupon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Coupon operations.
 * Handles coupon management by owner.
 * 
 * @author Group17
 * @version 1.0
 */
public class CouponDAO {

    /** Database adapter instance */
    private DatabaseAdapter db;

    /**
     * Constructor - initializes database adapter.
     */
    public CouponDAO() {
        this.db = DatabaseAdapter.getInstance();
    }

    /**
     * Gets all coupons.
     * 
     * @return List of all coupons
     */
    public List<Coupon> findAll() {
        List<Coupon> coupons = new ArrayList<>();
        String query = "SELECT * FROM Coupons ORDER BY created_at DESC";

        try {
            ResultSet rs = db.executeQuery(query);

            while (rs.next()) {
                coupons.add(extractCouponFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Find all coupons error: " + e.getMessage());
        }

        return coupons;
    }

    /**
     * Gets all active coupons.
     * 
     * @return List of active coupons
     */
    public List<Coupon> findActive() {
        List<Coupon> coupons = new ArrayList<>();
        String query = "SELECT * FROM Coupons WHERE is_active = TRUE AND (expiry_date IS NULL OR expiry_date >= CURDATE()) ORDER BY discount_percent DESC";

        try {
            ResultSet rs = db.executeQuery(query);

            while (rs.next()) {
                coupons.add(extractCouponFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Find active coupons error: " + e.getMessage());
        }

        return coupons;
    }

    /**
     * Finds a coupon by code.
     * 
     * @param code The coupon code
     * @return Coupon or null if not found
     */
    public Coupon findByCode(String code) {
        String query = "SELECT * FROM Coupons WHERE code = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, code);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractCouponFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Find coupon error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Creates a new coupon.
     * 
     * @param coupon The coupon to create
     * @return true if successful
     */
    public boolean create(Coupon coupon) {
        String query = "INSERT INTO Coupons (code, discount_percent, min_order_value, expiry_date, is_active) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, coupon.getCode());
            stmt.setDouble(2, coupon.getDiscountPercent());
            stmt.setDouble(3, coupon.getMinOrderValue());
            if (coupon.getExpiryDate() != null) {
                stmt.setDate(4, Date.valueOf(coupon.getExpiryDate()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setBoolean(5, coupon.isActive());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Create coupon error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates a coupon.
     * 
     * @param coupon The coupon to update
     * @return true if successful
     */
    public boolean update(Coupon coupon) {
        String query = "UPDATE Coupons SET code = ?, discount_percent = ?, min_order_value = ?, expiry_date = ?, is_active = ? WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, coupon.getCode());
            stmt.setDouble(2, coupon.getDiscountPercent());
            stmt.setDouble(3, coupon.getMinOrderValue());
            if (coupon.getExpiryDate() != null) {
                stmt.setDate(4, Date.valueOf(coupon.getExpiryDate()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setBoolean(5, coupon.isActive());
            stmt.setInt(6, coupon.getId());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Update coupon error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a coupon.
     * 
     * @param couponId The coupon ID
     * @return true if successful
     */
    public boolean delete(int couponId) {
        String query = "DELETE FROM Coupons WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, couponId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Delete coupon error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deactivates a coupon.
     * 
     * @param couponId The coupon ID
     * @return true if successful
     */
    public boolean deactivate(int couponId) {
        String query = "UPDATE Coupons SET is_active = FALSE WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, couponId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Deactivate coupon error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Assigns a coupon to a user.
     * 
     * @param userId   The user ID
     * @param couponId The coupon ID
     * @return true if successful
     */
    public boolean assignToUser(int userId, int couponId) {
        String query = "INSERT INTO UserCoupons (user_id, coupon_id) VALUES (?, ?)";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, couponId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Assign coupon error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets coupons available for a user.
     * 
     * @param userId The user ID
     * @return List of available coupons for the user
     */
    public List<Coupon> findUserCoupons(int userId) {
        List<Coupon> coupons = new ArrayList<>();
        String query = "SELECT c.* FROM Coupons c " +
                "JOIN UserCoupons uc ON c.id = uc.coupon_id " +
                "WHERE uc.user_id = ? AND uc.is_used = FALSE " +
                "AND c.is_active = TRUE AND (c.expiry_date IS NULL OR c.expiry_date >= CURDATE())";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                coupons.add(extractCouponFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Find user coupons error: " + e.getMessage());
        }

        return coupons;
    }

    /**
     * Marks a user's coupon as used.
     * 
     * @param userId   The user ID
     * @param couponId The coupon ID
     * @return true if successful
     */
    public boolean markCouponUsed(int userId, int couponId) {
        String query = "UPDATE UserCoupons SET is_used = TRUE WHERE user_id = ? AND coupon_id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, couponId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Mark coupon used error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extracts a Coupon object from a ResultSet.
     * 
     * @param rs The ResultSet positioned at the coupon row
     * @return Coupon object
     * @throws SQLException If data extraction fails
     */
    private Coupon extractCouponFromResultSet(ResultSet rs) throws SQLException {
        Coupon coupon = new Coupon();
        coupon.setId(rs.getInt("id"));
        coupon.setCode(rs.getString("code"));
        coupon.setDiscountPercent(rs.getDouble("discount_percent"));
        coupon.setMinOrderValue(rs.getDouble("min_order_value"));

        Date expiryDate = rs.getDate("expiry_date");
        if (expiryDate != null) {
            coupon.setExpiryDate(expiryDate.toLocalDate());
        }

        coupon.setActive(rs.getBoolean("is_active"));

        return coupon;
    }
}
