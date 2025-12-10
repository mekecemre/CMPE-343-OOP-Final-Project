package com.greengrocer.utils;

import com.greengrocer.models.User;

/**
 * Session Manager for tracking the currently logged-in user.
 * Uses Singleton pattern.
 * 
 * @author Group17
 * @version 1.0
 */
public class SessionManager {

    /** Singleton instance */
    private static SessionManager instance;

    /** Currently logged-in user */
    private User currentUser;

    /**
     * Private constructor for singleton.
     */
    private SessionManager() {
    }

    /**
     * Gets the singleton instance.
     * 
     * @return SessionManager instance
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Gets the current logged-in user.
     * 
     * @return Current user or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user (call after successful login).
     * 
     * @param user The authenticated user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Clears the current user (call on logout).
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Checks if current user is a customer.
     * 
     * @return true if customer
     */
    public boolean isCustomer() {
        return currentUser != null && currentUser.isCustomer();
    }

    /**
     * Checks if current user is a carrier.
     * 
     * @return true if carrier
     */
    public boolean isCarrier() {
        return currentUser != null && currentUser.isCarrier();
    }

    /**
     * Checks if current user is the owner.
     * 
     * @return true if owner
     */
    public boolean isOwner() {
        return currentUser != null && currentUser.isOwner();
    }
}
