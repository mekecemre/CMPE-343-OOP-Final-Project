package com.greengrocer.models;

/**
 * Represents a user in the Greengrocer system.
 * Users can be customers, carriers, or the owner.
 * 
 * @author Group17
 * @version 1.0
 */
public class User {

    /** Unique identifier for the user */
    private int id;

    /** Username for login */
    private String username;

    /** Password for login */
    private String password;

    /** Role of the user (CUSTOMER, CARRIER, or OWNER) */
    private String role;

    /** Full name of the user */
    private String fullName;

    /** Address of the user */
    private String address;

    /** Phone number */
    private String phone;

    /** Email address */
    private String email;

    /** Loyalty points earned by customer */
    private int loyaltyPoints;

    /** Number of completed orders (for loyalty discount) */
    private int completedOrders;

    /**
     * Default constructor.
     */
    public User() {
    }

    /**
     * Constructor with basic login credentials.
     * 
     * @param username The username
     * @param password The password
     * @param role     The user role
     */
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Full constructor with all fields.
     * 
     * @param id              User ID
     * @param username        Username
     * @param password        Password
     * @param role            User role
     * @param fullName        Full name
     * @param address         Address
     * @param phone           Phone number
     * @param email           Email address
     * @param loyaltyPoints   Loyalty points
     * @param completedOrders Number of completed orders
     */
    public User(int id, String username, String password, String role,
            String fullName, String address, String phone, String email,
            int loyaltyPoints, int completedOrders) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.loyaltyPoints = loyaltyPoints;
        this.completedOrders = completedOrders;
    }

    // ==================== GETTERS ====================

    /**
     * Gets the user ID.
     * 
     * @return The user ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the username.
     * 
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password.
     * 
     * @return The password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the user role.
     * 
     * @return The role (CUSTOMER, CARRIER, or OWNER)
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets the full name.
     * 
     * @return The full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Gets the address.
     * 
     * @return The address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the phone number.
     * 
     * @return The phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Gets the email address.
     * 
     * @return The email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the loyalty points.
     * 
     * @return The loyalty points
     */
    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    /**
     * Gets the number of completed orders.
     * 
     * @return The completed orders count
     */
    public int getCompletedOrders() {
        return completedOrders;
    }

    // ==================== SETTERS ====================

    /**
     * Sets the user ID.
     * 
     * @param id The user ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the username.
     * 
     * @param username The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password.
     * 
     * @param password The password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the user role.
     * 
     * @param role The role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Sets the full name.
     * 
     * @param fullName The full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Sets the address.
     * 
     * @param address The address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Sets the phone number.
     * 
     * @param phone The phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Sets the email address.
     * 
     * @param email The email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the loyalty points.
     * 
     * @param loyaltyPoints The loyalty points
     */
    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    /**
     * Sets the number of completed orders.
     * 
     * @param completedOrders The completed orders count
     */
    public void setCompletedOrders(int completedOrders) {
        this.completedOrders = completedOrders;
    }

    /**
     * Checks if the user is a customer.
     * 
     * @return true if customer, false otherwise
     */
    public boolean isCustomer() {
        return "CUSTOMER".equals(role);
    }

    /**
     * Checks if the user is a carrier.
     * 
     * @return true if carrier, false otherwise
     */
    public boolean isCarrier() {
        return "CARRIER".equals(role);
    }

    /**
     * Checks if the user is the owner.
     * 
     * @return true if owner, false otherwise
     */
    public boolean isOwner() {
        return "OWNER".equals(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
