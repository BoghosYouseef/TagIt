package com.tagit;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.tagit.ui.TabFactory;

public class TagItGUI extends Application {
    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
        TabFactory.setApplicationContext(context);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AppConfig config = applicationContext.getBean(AppConfig.class);

        primaryStage.setTitle(config.getTitle());
        primaryStage.setMinWidth(config.getMinimumWindowWidth());
        BorderPane root = new BorderPane();
        TabPane tabPane = createTabPane();
        // place tab pane in center so it expands with the window
        root.setCenter(tabPane);
        logger.warn("Setting minimum window width to: " + config.getMinimumWindowWidth());
        HBox.setHgrow(tabPane, Priority.ALWAYS);
        tabPane.prefHeightProperty().bind(root.heightProperty());
        tabPane.prefWidthProperty().bind(root.widthProperty());
        


        Scene scene = new Scene(root, config.getWindow().getWidth(), config.getWindow().getHeight());
        scene.getRoot().setStyle("-fx-background-color: -bg-color;");

        // Load UI stylesheet (theme can be provided via DI later)
        scene.getStylesheets().add(getClass().getResource("/ui/fileorganizer.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TabPane createTabPane() {
        // Use modular tab factory so each tab is an independent FXML + controller
        return TabFactory.createTabPane();
    }
}
