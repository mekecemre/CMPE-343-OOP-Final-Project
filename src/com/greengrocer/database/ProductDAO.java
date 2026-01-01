package com.greengrocer.database;

import com.greengrocer.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product operations.
 * Handles all database operations related to products.
 * 
 * @author Group17
 * @version 1.0
 */
public class ProductDAO {

    /** Database adapter instance */
    private DatabaseAdapter db;

    /**
     * Constructor - initializes database adapter.
     */
    public ProductDAO() {
        this.db = DatabaseAdapter.getInstance();
    }

    /**
     * Gets all products sorted by name.
     * 
     * @return List of all products
     */
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM ProductInfo ORDER BY name ASC";

        try {
            ResultSet rs = db.executeQuery(query);

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Find all products error: " + e.getMessage());
        }

        return products;
    }

    /**
     * Gets all products of a specific type (VEGETABLE or FRUIT), sorted by name.
     * Only returns products with stock > 0.
     * 
     * @param type The product type
     * @return List of products of the specified type
     */
    public List<Product> findByType(String type) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM ProductInfo WHERE type = ? AND stock > 0 ORDER BY name ASC";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, type);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Find products by type error: " + e.getMessage());
        }

        return products;
    }

    /**
     * Gets all vegetables with stock > 0, sorted by name.
     * 
     * @return List of vegetables
     */
    public List<Product> getVegetables() {
        return findByType("VEGETABLE");
    }

    /**
     * Gets all fruits with stock > 0, sorted by name.
     * 
     * @return List of fruits
     */
    public List<Product> getFruits() {
        return findByType("FRUIT");
    }

    /**
     * Searches products by name keyword.
     * 
     * @param keyword The search keyword
     * @return List of matching products
     */
    public List<Product> searchByName(String keyword) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM ProductInfo WHERE name LIKE ? AND stock > 0 ORDER BY name ASC";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Search products error: " + e.getMessage());
        }

        return products;
    }

    /**
     * Finds a product by ID.
     * 
     * @param id The product ID
     * @return Product or null if not found
     */
    public Product findById(int id) {
        String query = "SELECT * FROM ProductInfo WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Find product error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Checks if a product with the given name already exists.
     * 
     * @param name The product name to check
     * @return true if a product with this name exists
     */
    public boolean existsByName(String name) {
        String query = "SELECT COUNT(*) FROM ProductInfo WHERE LOWER(name) = LOWER(?)";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, name.trim());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Check product exists error: " + e.getMessage());
        }

        return false;
    }

    /**
     * Checks if a product with the given name exists, excluding a specific ID.
     * Used when updating a product to allow keeping the same name.
     * 
     * @param name The product name to check
     * @param excludeId The product ID to exclude from the check
     * @return true if another product with this name exists
     */
    public boolean existsByNameExcluding(String name, int excludeId) {
        String query = "SELECT COUNT(*) FROM ProductInfo WHERE LOWER(name) = LOWER(?) AND id != ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, name.trim());
            stmt.setInt(2, excludeId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Check product exists error: " + e.getMessage());
        }

        return false;
    }

    /**
     * Adds a new product.
     * 
     * @param product The product to add
     * @return true if successful
     */
    public boolean add(Product product) {
        String query = "INSERT INTO ProductInfo (name, type, price, stock, threshold, image) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getType());
            stmt.setDouble(3, product.getPrice());
            stmt.setDouble(4, product.getStock());
            stmt.setDouble(5, product.getThreshold());
            stmt.setBytes(6, product.getImage());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Add product error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing product.
     * 
     * @param product The product with updated information
     * @return true if successful
     */
    public boolean update(Product product) {
        String query = "UPDATE ProductInfo SET name = ?, type = ?, price = ?, stock = ?, threshold = ? WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getType());
            stmt.setDouble(3, product.getPrice());
            stmt.setDouble(4, product.getStock());
            stmt.setDouble(5, product.getThreshold());
            stmt.setInt(6, product.getId());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Update product error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates product with image.
     * 
     * @param product The product with updated information including image
     * @return true if successful
     */
    public boolean updateWithImage(Product product) {
        String query = "UPDATE ProductInfo SET name = ?, type = ?, price = ?, stock = ?, threshold = ?, image = ? WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getType());
            stmt.setDouble(3, product.getPrice());
            stmt.setDouble(4, product.getStock());
            stmt.setDouble(5, product.getThreshold());
            stmt.setBytes(6, product.getImage());
            stmt.setInt(7, product.getId());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Update product with image error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a product.
     * 
     * @param id The product ID to delete
     * @return true if successful
     */
    public boolean delete(int id) {
        String query = "DELETE FROM ProductInfo WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Delete product error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the stock of a product.
     * 
     * @param productId The product ID
     * @param quantity  The quantity to subtract from stock
     * @return true if successful
     */
    public boolean updateStock(int productId, double quantity) {
        String query = "UPDATE ProductInfo SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setDouble(1, quantity);
            stmt.setInt(2, productId);
            stmt.setDouble(3, quantity);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Update stock error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if there is enough stock for the requested quantity.
     * 
     * @param productId The product ID
     * @param quantity  The requested quantity
     * @return true if enough stock available
     */
    public boolean hasEnoughStock(int productId, double quantity) {
        String query = "SELECT stock FROM ProductInfo WHERE id = ?";

        try {
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, productId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double stock = rs.getDouble("stock");
                return stock >= quantity;
            }
        } catch (SQLException e) {
            System.err.println("Check stock error: " + e.getMessage());
        }

        return false;
    }

    /**
     * Gets all products including those with zero stock (for owner).
     * 
     * @return List of all products
     */
    public List<Product> findAllIncludingOutOfStock() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM ProductInfo ORDER BY type, name ASC";

        try {
            ResultSet rs = db.executeQuery(query);

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Find all products error: " + e.getMessage());
        }

        return products;
    }

    /**
     * Extracts a Product object from a ResultSet.
     * 
     * @param rs The ResultSet positioned at the product row
     * @return Product object
     * @throws SQLException If data extraction fails
     */
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setType(rs.getString("type"));
        product.setPrice(rs.getDouble("price"));
        product.setStock(rs.getDouble("stock"));
        product.setThreshold(rs.getDouble("threshold"));
        product.setImage(rs.getBytes("image"));
        return product;
    }
}
