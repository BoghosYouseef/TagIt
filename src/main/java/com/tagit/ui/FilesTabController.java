package com.tagit.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import com.tagit.AppConfig;
import com.tagit.TagItApp;
import com.tagit.service.FileService;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class FilesTabController {
    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);

    @FXML
    private TableView<?> filesTable;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TableColumn<?, ?> typeColumn;

    @FXML
    private TableColumn<?, ?> sizeColumn;

    @FXML   
    private TableColumn<?, ?> modifiedColumn;

    @FXML
    private TableColumn<?, ?> tagsColumn;

    @FXML
    private TableColumn<?, ?> locationColumn;

    @FXML
    private BorderPane FilesRootPane;  

    @FXML
    private Button importButton;

    @Autowired
    private ApplicationContext applicationContext;

    private double MINIMUM_TAB_PANE_WIDTH;



    @FXML
    public void initialize() {
        logger.debug("Now in FilesTabController!!@#!");

        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext is null!");
        } else {
            logger.warn("applicationContext: {}", applicationContext);
        }

        AppConfig config = applicationContext.getBean(AppConfig.class);

        addCustomHeader(nameColumn, "Name");
        addCustomHeader(typeColumn, "Type");
        addCustomHeader(sizeColumn, "Size");
        addCustomHeader(modifiedColumn, "Modified");
        addCustomHeader(tagsColumn, "Tags");
        addCustomHeader(locationColumn, "Location");

        this.MINIMUM_TAB_PANE_WIDTH = config.getMinimumTabPaneWidth();

        setColumnMinimumWidths(this.MINIMUM_TAB_PANE_WIDTH);
        filesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        filesTable.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });

        filesTable.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {

                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                List<File> droppedFiles = event.getDragboard().getFiles();
                logger.info("Files dropped: " + droppedFiles.toString());
                importAndHandleFiles(droppedFiles);
                event.consume();
            }
        });
    }
    
    @FXML
    private void onImport() {
        showImportWindow();
    }

    private void showImportWindow() {
        Stage importFilesStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        
        List<File> userChoice = fileChooser.showOpenMultipleDialog(importFilesStage);
        
        if (userChoice == null || userChoice.isEmpty()) {
            return; // User cancelled or didn't select any files
        }
        
        // Initiate the import process with selected files
        importAndHandleFiles(userChoice);
    }

    private void importAndHandleFiles(List<File> filesToImport) {
        Stage importFilesStage = new Stage();
        importFilesStage.setTitle("Importing...");
        importFilesStage.initModality(Modality.APPLICATION_MODAL);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        Label label = new Label("Importing files...");

        VBox vbox = new VBox(10, label, progressIndicator);
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene importFilesScene = new Scene(vbox, 250, 100);
        importFilesStage.setScene(importFilesScene);
        importFilesStage.show();

        // Make the upload task
        Task<Void> importFilesTask = new Task<Void>() {
            @Override
            protected Void call() {
                for (int i = 0; i <= 100; i++) {
                    try {
                        Thread.sleep(10); // Simulate upload delay
                        updateProgress(i, 100);
                    } catch (InterruptedException e) {
                        // Handle interruption
                    }
                }
                return null;
            }
        };

        // Bind the progress indicator to the task
        progressIndicator.progressProperty().bind(importFilesTask.progressProperty());

        // Start the upload process in a new thread
        new Thread(importFilesTask).start();

        importFilesTask.setOnSucceeded(e -> {
            importFilesStage.close(); // Close upload window
            // handleUploadedFiles(filesToUpload); // Call your method with uploaded files
            FileService fileService = applicationContext.getBean(FileService.class);
            fileService.fileService(filesToImport);
        });
    }

    // Method to set minimum width for columns with a specific style class
    private void setColumnMinimumWidths(double minimumWidth) {
        logger.warn("Setting file-column minimum widths to: " + minimumWidth);
        for (TableColumn<?, ?> column : filesTable.getColumns()) {
            if (column.getStyleClass().contains("file-column")) { // Check for the CSS class
                column.setMinWidth(minimumWidth); // Set your desired minimum width
            }
        }
    }

    private void addCustomHeader(TableColumn<?, ?> column, String text) {
        HBox header = new HBox();
        Label label = new Label(text);
        
        // header.setPadding(new Insets(0, 0, 0, 10));
        header.getChildren().add(label);
        header.setAlignment(javafx.geometry.Pos.CENTER);
        column.setGraphic(header); // set the custom header
    }
}
