package com.tagit;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.springframework.context.ApplicationContext;

public class TagItGUI extends Application {
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AppConfig config = applicationContext.getBean(AppConfig.class);

        primaryStage.setTitle(config.getTitle());
        
        BorderPane root = new BorderPane();
        Label label = new Label("Hello from JavaFX!");
        root.setCenter(label);

        TabPane tabPane = createTabPane();
        root.setTop(tabPane);
        Scene scene = new Scene(root, config.getWindow().getWidth(), config.getWindow().getHeight());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Prevent closing tabs
        
        // Create tabs
        Tab tab1 = new Tab("Tab 1", new Label("Content for Tab 1"));
        Tab tab2 = new Tab("Tab 2", new Label("Content for Tab 2"));
        Tab tab3 = new Tab("Tab 3", new Label("Content for Tab 3"));
        
        tabPane.getTabs().addAll(tab1, tab2, tab3);
        return tabPane;
    }
}
