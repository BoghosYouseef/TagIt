package com.tagit.ui;

import com.tagit.ui.enums.NotificationType;
import com.tagit.utils.ColorUtils;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ViewUtils {

    public static Stage createBasePopupStage(Parent stageContent, String title, double width, double height) {
        Stage stage = new Stage();
        Scene scene = new Scene(stageContent, width, height);
        

        
        scene.getStylesheets().add(ViewUtils.class.getResource("/ui/fileorganizer.css").toExternalForm());
        
        stage.setTitle(title);
        stage.setScene(scene);
        
        return stage; 
    }

    public static Label createTagLabel(
        String tagText,
        String tagColorHashString){

        Label tagLabel = new Label(tagText);
        String fontColor = ColorUtils.getContrastTextColor(tagColorHashString);
        
        tagLabel.setStyle(
        """
        -fx-background-color: %s;
        -fx-text-fill: %s;
        -fx-padding: 10;
        -fx-background-radius: 12;
        """.formatted(tagColorHashString, fontColor)
        );

        return tagLabel;
    }

    /**
     * Displays a standard success notification.
     *
     * @param title   the text displayed in the operating system window title bar
     * @param message the main descriptive text displayed inside the alert content area
     */
    public static void showSuccess(String title, String message) {
        showNotification(NotificationType.SUCCESS, title, null, message, null);
    }

    /**
     * Displays a success notification containing a custom graphic or node.
     *
     * @param title         the text displayed in the operating system window title bar
     * @param message       the main descriptive text displayed inside the alert content area
     * @param customGraphic a JavaFX Node representing the icon or visual element to display
     */
    public static void showSuccess(String title, String message, Node customGraphic) {
        showNotification(NotificationType.SUCCESS, title, null, message, customGraphic);
    }

    /**
     * Displays a standard warning notification alert.
     *
     * @param title   the text displayed in the operating system window title bar
     * @param message the main descriptive text displayed inside the alert content area
     */
    public static void showWarning(String title, String message) {
        showNotification(NotificationType.WARNING, title, null, message, null);
    }

    /**
     * Displays a warning notification alert containing a custom graphic or node.
     *
     * @param title         the text displayed in the operating system window title bar
     * @param message       the main descriptive text displayed inside the alert content area
     * @param customGraphic a JavaFX Node representing the icon or visual element to display
     */
    public static void showWarning(String title, String message, Node customGraphic) {
        showNotification(NotificationType.WARNING, title, null, message, customGraphic);
    }

    /**
     * Displays a standard error notification alert.
     *
     * @param title   the text displayed in the operating system window title bar
     * @param message the main descriptive text displayed inside the alert content area
     */
    public static void showError(String title, String message) {
        showNotification(NotificationType.ERROR, title, null, message, null);
    }

    /**
     * Displays an error notification alert containing a custom graphic or node.
     *
     * @param title         the text displayed in the operating system window title bar
     * @param message       the main descriptive text displayed inside the alert content area
     * @param customGraphic a JavaFX Node representing the icon or visual element to display
     */
    public static void showError(String title, String message, Node customGraphic) {
        showNotification(NotificationType.ERROR, title, null, message, customGraphic);
    }

    /**
     * Displays a standard informational notification alert.
     *
     * @param title   the text displayed in the operating system window title bar
     * @param message the main descriptive text displayed inside the alert content area
     */
    public static void showInfo(String title, String message) {
        showNotification(NotificationType.INFO, title, null, message, null);
    }

    /**
     * Displays an informational notification alert containing a custom graphic or node.
     *
     * @param title         the text displayed in the operating system window title bar
     * @param message       the main descriptive text displayed inside the alert content area
     * @param customGraphic a JavaFX Node representing the icon or visual element to display
     */
    public static void showInfo(String title, String message, Node customGraphic) {
        showNotification(NotificationType.INFO, title, null, message, customGraphic);
    }

    /**
     * Displays a highly detailed notification supporting all layout configurations.
     *
     * @param title         the text displayed in the operating system window title bar
     * @param header        the bold text displayed in the top section inside the dialog box (can be null)
     * @param message       the main descriptive text displayed inside the alert content area
     * @param type          the custom NotificationType enum indicating the category of alert
     * @param customGraphic a JavaFX Node representing the icon or visual element to display (can be null)
     */
    public static void showDetailedNotification(String title, String header, String message, NotificationType type, Node customGraphic) {
        showNotification(type, title, header, message, customGraphic);
    }

    private static void showNotification(NotificationType type, String title, String header, String message, Node customGraphic) {
        Alert.AlertType alertType = mapNotificationType(type);
        Alert alert = new Alert(alertType);
        
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        if (customGraphic != null) {
            alert.setGraphic(customGraphic);
        }

        if (alertType == Alert.AlertType.NONE) {
            alert.getButtonTypes().add(ButtonType.OK);
        }

        alert.showAndWait();
    }

    private static Alert.AlertType mapNotificationType(NotificationType type) {
        return switch (type) {
            case INFO -> Alert.AlertType.INFORMATION;
            case WARNING -> Alert.AlertType.WARNING;
            case ERROR -> Alert.AlertType.ERROR;
            case SUCCESS -> Alert.AlertType.NONE;
        };
    }
}
