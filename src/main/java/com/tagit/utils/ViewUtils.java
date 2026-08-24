package com.tagit.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
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
}
