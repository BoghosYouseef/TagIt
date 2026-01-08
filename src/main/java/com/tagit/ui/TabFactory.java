package com.tagit.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;


public class TabFactory {
    private static ApplicationContext applicationContext;

    // static method to set the ApplicationContext
    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    private TabFactory() {}

    

    public static TabPane createTabPane() {

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-font-size: 12;");

        List<Tab> tabs = new ArrayList<>();
        tabs.add(loadTab("/ui/files-tab.fxml", "Files", "📁"));
        tabs.add(loadTab("/ui/statistics-tab.fxml", "Statistics", "📊"));
        tabs.add(loadTab("/ui/tags-tab.fxml", "Manage Tags", "🏷️"));

        tabPane.getTabs().addAll(tabs);
        return tabPane;
    }

    private static Tab loadTab(String fxmlPath, String title, String icon) {
        try {
            FXMLLoader loader = new FXMLLoader(TabFactory.class.getResource(fxmlPath));
            loader.setControllerFactory(applicationContext::getBean);
            Region content = loader.load();


            // Create tab with icon and title
            HBox tabHeader = new HBox(4);
            tabHeader.setAlignment(Pos.CENTER);
            Label iconLabel = new Label(icon);
            iconLabel.setStyle("-fx-font-size: 14;");
            Label titleLabel = new Label(title);
            tabHeader.getChildren().addAll(iconLabel, titleLabel); //, separator);
            
            Tab tab = new Tab(title, content);
            tab.setGraphic(tabHeader);
            tab.setStyle("-fx-padding: 8 16;");
            
            // #TODO: wire controller with DI if needed (controller available via loader.getController())
            return tab;
        } catch (IOException e) {
            Tab tab = new Tab(title);
            tab.setContent(new Region());
            // #TODO: log error
            return tab;
        }
    }
}
