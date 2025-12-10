package com.greengrocer.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Adapter class for managing MySQL database connections.
 * Uses Singleton pattern to ensure only one connection instance exists.
 * 
 * This class handles all database connectivity for the Greengrocer application.
 * 
 * @author Group17
 * @version 1.0
 */
public class DatabaseAdapter {
    
    /** Database connection URL */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/greengrocer";
    
    /** Database username */
    private static final String DB_USER = "myuser";
    
    /** Database password */
    private static final String DB_PASSWORD = "1234";
    
    /** Singleton instance */
    private static DatabaseAdapter instance;
    
    /** The active database connection */
    private Connection connection;
    
    /**
     * Private constructor to prevent direct instantiation.
     * Use getInstance() to get the singleton instance.
     */
    private DatabaseAdapter() {
        // Private constructor for singleton
    }
    
    /**
     * Gets the singleton instance of DatabaseAdapter.
     * 
     * @return The single DatabaseAdapter instance
     */
    public static synchronized DatabaseAdapter getInstance() {
        if (instance == null) {
            instance = new DatabaseAdapter();
        }
        return instance;
    }
    
    /**
     * Gets the database connection, creating it if necessary.
     * 
     * @return The active database connection
     * @throws SQLException If connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Establish connection
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connection established.");
                
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found: " + e.getMessage());
            }
        }
        return connection;
    }
    
    /**
     * Closes the database connection if it is open.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    /**
     * Executes a SELECT query and returns the ResultSet.
     * 
     * @param query The SQL SELECT query to execute
     * @return ResultSet containing the query results
     * @throws SQLException If query execution fails
     */
    public ResultSet executeQuery(String query) throws SQLException {
        Statement statement = getConnection().createStatement();
        return statement.executeQuery(query);
    }
    
    /**
     * Executes an INSERT, UPDATE, or DELETE query.
     * 
     * @param query The SQL query to execute
     * @return Number of rows affected
     * @throws SQLException If query execution fails
     */
    public int executeUpdate(String query) throws SQLException {
        Statement statement = getConnection().createStatement();
        return statement.executeUpdate(query);
    }
    
    /**
     * Prepares a SQL statement for execution with parameters.
     * 
     * @param query The SQL query with placeholders
     * @return PreparedStatement ready for parameter binding
     * @throws SQLException If statement preparation fails
     */
    public PreparedStatement prepareStatement(String query) throws SQLException {
        return getConnection().prepareStatement(query);
    }
    
    /**
     * Prepares a SQL statement that returns generated keys.
     * 
     * @param query The SQL query with placeholders
     * @return PreparedStatement ready for parameter binding
     * @throws SQLException If statement preparation fails
     */
    public PreparedStatement prepareStatementWithKeys(String query) throws SQLException {
        return getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    }
}
