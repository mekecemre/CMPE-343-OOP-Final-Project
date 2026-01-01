package com.greengrocer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.greengrocer.database.DatabaseAdapter;

/**
 * Main entry point for the Greengrocer Application.
 * This JavaFX application provides a complete shopping system for a local
 * greengrocer
 * with support for customers, carriers, and owner roles.
 * 
 * @author Group17
 * @version 1.0
 */
public class Main extends Application {

    /** The initial width of the application window */
    public static final int WINDOW_WIDTH = 960;

    /** The initial height of the application window */
    public static final int WINDOW_HEIGHT = 540;

    /**
     * Default constructor for the Main application class.
     */
    public Main() {
    }

    /**
     * The main entry point for the JavaFX application.
     * Initializes the database connection and displays the login screen.
     * 
     * @param primaryStage The primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database connection
            DatabaseAdapter.getInstance().getConnection();
            System.out.println("Database connected successfully!");

            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/greengrocer/views/Login.fxml"));
            Parent root = loader.load();

            // Create the scene
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

            // Add CSS stylesheet
            scene.getStylesheets()
                    .add(getClass().getResource("/com/greengrocer/styles/application.css").toExternalForm());

            // Configure the stage
            primaryStage.setTitle("Group17 GreenGrocer - Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(450);

            // Center on screen
            primaryStage.centerOnScreen();

            // Show the stage
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the application is stopped.
     * Closes the database connection.
     */
    @Override
    public void stop() {
        DatabaseAdapter.getInstance().closeConnection();
        System.out.println("Application closed. Database connection closed.");
    }

    /**
     * Main method - launches the JavaFX application.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
