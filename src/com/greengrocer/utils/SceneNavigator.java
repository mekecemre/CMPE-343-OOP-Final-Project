package com.greengrocer.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class for scene navigation.
 * Provides methods to switch between different views.
 * 
 * @author Group17
 * @version 1.0
 */
public class SceneNavigator {

    /** Default window width */
    public static final int WINDOW_WIDTH = 960;

    /** Default window height */
    public static final int WINDOW_HEIGHT = 540;

    /**
     * Loads a new scene in the given stage.
     * 
     * @param stage    The stage to load the scene in
     * @param fxmlPath The path to the FXML file (relative to views folder)
     * @param title    The window title
     */
    public static void loadScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource("/com/greengrocer/views/" + fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.getStylesheets()
                    .add(SceneNavigator.class.getResource("/com/greengrocer/styles/application.css").toExternalForm());

            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(450);
            stage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("Error loading scene: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Navigation Error", "Could not load the requested page.");
        }
    }

    /**
     * Opens a new window (stage) with the specified FXML.
     * 
     * @param fxmlPath The path to the FXML file
     * @param title    The window title
     * @return The new stage, or null if failed
     */
    public static Stage openNewWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource("/com/greengrocer/views/" + fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets()
                    .add(SceneNavigator.class.getResource("/com/greengrocer/styles/application.css").toExternalForm());

            Stage newStage = new Stage();
            newStage.setTitle(title);
            newStage.setScene(scene);
            newStage.centerOnScreen();

            return newStage;

        } catch (Exception e) {
            System.err.println("Error opening new window: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Navigation Error", "Could not open the requested window.");
            return null;
        }
    }

    /**
     * Gets the FXMLLoader for a view (useful when controller access is needed).
     * 
     * @param fxmlPath The path to the FXML file
     * @return FXMLLoader instance
     */
    public static FXMLLoader getLoader(String fxmlPath) {
        return new FXMLLoader(SceneNavigator.class.getResource("/com/greengrocer/views/" + fxmlPath));
    }
}
