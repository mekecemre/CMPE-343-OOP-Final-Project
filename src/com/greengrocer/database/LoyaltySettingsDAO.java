package com.greengrocer.database;

import com.greengrocer.models.LoyaltySettings;
import java.sql.*;

/**
 * Data Access Object for LoyaltySettings operations.
 * Handles loyalty discount settings managed by owner.
 * 
 * @author Group17
 * @version 1.0
 */
public class LoyaltySettingsDAO {

    /** Database adapter instance */
    private DatabaseAdapter db;

    /**
     * Constructor - initializes database adapter.
     */
    public LoyaltySettingsDAO() {
        this.db = DatabaseAdapter.getInstance();
    }

    /**
     * Gets the current loyalty settings.
     * 
     * @return LoyaltySettings or default if not found
     */
    public LoyaltySettings getSettings() {
        String query = "SELECT * FROM LoyaltySettings LIMIT 1";

        try {
            ResultSet rs = db.executeQuery(query);

            if (rs.next()) {
                LoyaltySettings settings = new LoyaltySettings();
                settings.setId(rs.getInt("id"));
                settings.setMinOrdersForDiscount(rs.getInt("min_orders_for_discount"));
                settings.setDiscountPercent(rs.getDouble("discount_percent"));
                return settings;
            }
        } catch (SQLException e) {
            System.err.println("Get loyalty settings error: " + e.getMessage());
        }

        // Return default settings if none found
        return new LoyaltySettings();
    }

    /**
     * Updates the loyalty settings.
     * 
     * @param settings The settings to save
     * @return true if successful
     */
    public boolean update(LoyaltySettings settings) {
        String query = "UPDATE LoyaltySettings SET min_orders_for_discount = ?, discount_percent = ? WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, settings.getMinOrdersForDiscount());
            stmt.setDouble(2, settings.getDiscountPercent());
            stmt.setInt(3, settings.getId());

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                // No existing settings, insert new
                return insert(settings);
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Update loyalty settings error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts new loyalty settings.
     * 
     * @param settings The settings to insert
     * @return true if successful
     */
    private boolean insert(LoyaltySettings settings) {
        String query = "INSERT INTO LoyaltySettings (min_orders_for_discount, discount_percent) VALUES (?, ?)";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, settings.getMinOrdersForDiscount());
            stmt.setDouble(2, settings.getDiscountPercent());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Insert loyalty settings error: " + e.getMessage());
            return false;
        }
    }
}
