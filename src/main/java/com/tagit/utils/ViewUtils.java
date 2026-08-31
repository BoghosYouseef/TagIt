package com.tagit.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

    public static void showWarningMessage(String title, String headerText, String messageBody){
        
        Alert alert = new Alert(Alert.AlertType.WARNING);
        
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(
                messageBody
        );

        String stylesheet = ViewUtils.class
            .getResource("/ui/fileorganizer.css")
            .toExternalForm();

        alert.getDialogPane().getStylesheets().add(stylesheet);

        alert.showAndWait();
    }
}
