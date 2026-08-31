package com.tagit.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import com.tagit.AppConfig;
import com.tagit.TagItApp;
import com.tagit.service.FileService;
import com.tagit.service.FileTagRelationshipService;
import com.tagit.service.TagService;
import com.tagit.utils.ColorUtils;
import com.tagit.utils.ViewUtils;

import jakarta.annotation.PostConstruct;

import com.tagit.model.FileModel;
import com.tagit.model.FileTag;
import com.tagit.model.TagModel;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.collections.ObservableList;

import java.io.File;
import java.sql.DriverManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class FilesTabController {
    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);

    @FXML
    private TableView<FileModel> filesTable;
    
    @FXML
    private VBox searchBarVBox;
    
    @FXML
    private TextField searchBar;
    
    @FXML
    private FlowPane TagFilter;

    @FXML
    private Label fileCountLabel;

    @FXML
    private TableColumn<FileModel, String> nameColumn;

    @FXML
    private TableColumn<FileModel, String> typeColumn;

    @FXML
    private TableColumn<FileModel, String> sizeColumn;

    @FXML   
    private TableColumn<FileModel, Instant> lastModifiedAtColumn;

    @FXML
    private TableColumn<FileModel, Set<TagModel>> tagsColumn;

    @FXML
    private TableColumn<FileModel, String> locationColumn;

    @FXML
    private TableColumn<FileModel, Void> actionsColumn;

    @FXML
    private BorderPane FilesRootPane;  

    @FXML
    private Button importButton;


    // @Autowired
    // private ApplicationContext applicationContext;
    @Autowired
    AppConfig appConfig;

    @Autowired
    private FileService fileService;

    @Autowired
    private TagService tagService;

    @Autowired
    private FileTagRelationshipService fileTagRelationshipService;



    private double MINIMUM_TAB_PANE_WIDTH;

    private Popup suggestionsPopup;
    private ListView<TagModel> suggestionsList;

    private Stage primaryStage;

    @FXML
    public void initialize() {

        suggestionsPopup = new Popup();
        suggestionsList = new ListView<>();

        setupSearchBarSuggestionPrediction();

        addCustomHeader(nameColumn, "Name");
        enableTextWrapping(nameColumn);

        addCustomHeader(typeColumn, "Type");
        addCustomHeader(sizeColumn, "Size");

        addCustomHeader(lastModifiedAtColumn, "LastModifiedAt");
        // enableTextWrapping(lastModifiedAtColumn);
        enableInstantFormatting(lastModifiedAtColumn);

        addCustomHeader(tagsColumn, "Tags");

        addCustomHeader(locationColumn, "Location");
        enableTextWrapping(locationColumn);

        // the following allow values to populate columns from the model instant with same property
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        tagsColumn.setCellValueFactory(new PropertyValueFactory<>("tags"));
        lastModifiedAtColumn.setCellValueFactory(new PropertyValueFactory<>("lastModifiedAt"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("absolutePath"));

        this.MINIMUM_TAB_PANE_WIDTH = appConfig.getMinimumTabPaneWidth();
        setColumnMinimumWidths(this.MINIMUM_TAB_PANE_WIDTH);
        filesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);


        filesTable.setOnDragOver(
            (event) -> {
                if (event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        );

        filesTable.setOnDragDropped(
            (event) -> {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                List<File> droppedFiles = event.getDragboard().getFiles();
                logger.info("Files dropped: " + droppedFiles.toString());
                importAndHandleFiles(droppedFiles);
                event.consume();
            }
        );

        actionsColumn.getStyleClass().add("file-column");
        actionsColumn.setCellFactory(column -> new TableCell<>() {

        private final MenuButton menuButton = createActionMenuButtonForFileRow(
            () ->  getTableView().getItems().get(getIndex())
        );

            {
                menuButton.setOnShowing(event -> {
                    FileModel file = getTableView().getItems().get(getIndex());
                    logger.info("you have selected: " + file.getName());
                    // Do something with file
                    
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : menuButton);
            }
        });

        tagsColumn.setCellFactory(column -> new TableCell<>() {

            private final FlowPane tagsPane = new FlowPane(5, 5);

            @Override
            protected void updateItem(Set<TagModel> tags, boolean empty) {
                super.updateItem(tags, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                FileModel file = getTableView()
                        .getItems()
                        .get(getIndex());

                tagsPane.getChildren().clear();

                for (TagModel tag : file.getTags()) {
                    Label tagLabel = createClickToFilterByTagLabel(tag);
                    tagsPane.getChildren().add(tagLabel);
                }

                setGraphic(tagsPane);
            }
    });

        addFilesToTableViewColumns();
    }
    
    @FXML
    private void onImport() {
        showImportWindow();
    }

    private void showImportWindow() {
        Stage importFilesStage = new Stage();
        this.primaryStage = importFilesStage;
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
                int totalNumberOfFiles = filesToImport.size();
                for (int i = 0; i <= totalNumberOfFiles; i++) {
                    try {
                        Thread.sleep(10); // Simulate upload delay
                        updateProgress(i+1, totalNumberOfFiles);
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
            fileService.extractInfoAndSaveToDataBase(filesToImport);
            addFilesToTableViewColumns();
        });

        
    }

    private void addFilesToTableViewColumns(){
        logger.info("TagFilter.getChildren().size()#!@#!@:" + TagFilter.getChildren().size());
        if(TagFilter.getChildren().size() == 0){
            addAllFilesToTableViewColumns();
        }
        else{
            Set<Long> tagIds = getFilterTagIds();
            addTagFilteredFilesToTableViewColumns(tagIds);
        }
        // List<FileModel> files = fileService.retrieveAllFilesAsObjects();
        // logger.info("Adding {} files to table", files.size());
        // fileCountLabel.setText(String.format("(%d files)", files.size()));
        // filesTable.getItems().clear();
        // filesTable.getItems().addAll(files);
    }

    private void addAllFilesToTableViewColumns(){
        List<FileModel> files = fileService.retrieveAllFilesAsObjects();
        logger.info("Adding {} files to table", files.size());
        fileCountLabel.setText(String.format("(%d files)", files.size()));
        filesTable.getItems().clear();
        filesTable.getItems().addAll(files);
    }

    private void addTagFilteredFilesToTableViewColumns(Set<Long> tagIds){
        List<FileModel> files = fileService.retrieveTagFilteredFilesAsObjects(tagIds);
        logger.info("Adding {} files to table", files.size());
        fileCountLabel.setText(String.format("(%d files)", files.size()));
        filesTable.getItems().clear();
        filesTable.getItems().addAll(files);
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

    private <T> void enableTextWrapping(TableColumn<FileModel, T> column) {
        column.setCellFactory(tableCell -> new TableCell<>() {

            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(widthProperty().subtract(10));
                setGraphic(text);
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    text.setText(null);
                    setGraphic(null);
                } else {
                    text.setText(item.toString());
                    text.setFill(Color.WHITE);
                    setGraphic(text);
                }
            }
        });
    }
    
    private void enableInstantFormatting(TableColumn<FileModel, Instant> column) {
        final DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("MMMM dd, yyyy - HH:mm")
                        .withZone(ZoneId.systemDefault());
        column.setCellFactory(tableCell -> new TableCell<>() {
            
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(widthProperty().subtract(10));
                setGraphic(text);
            }
            @Override
            protected void updateItem(Instant item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    text.setText(null);
                } else {
                    text.setText(formatter.format(item));
                    text.setFill(Color.WHITE);

                }
            }
        });
    }

    private MenuButton createActionMenuButtonForFileRow( Supplier<FileModel> fileSupplier){
        MenuButton menuButton = new MenuButton("");
        MenuItem addTagMenuItem = new MenuItem("add tag");
        addTagMenuItem.setOnAction(event -> {
            FileModel fileModel = fileSupplier.get();
            showTagSelectionMenu(fileModel);
        });
        menuButton.getItems().add(addTagMenuItem);
        return menuButton;
    };

    private void showTagSelectionMenu(FileModel fileModel){
        Label label = new Label("Select the tags to assign to this file");
        FlowPane tagsPane = new FlowPane();
        tagsPane.setVgap(10);
        tagsPane.setHgap(10);
        tagsPane.setPadding(new Insets(5, 5, 5, 5));
        HBox hboxShowingTags = new HBox(label);
        VBox containerVBox = new VBox(hboxShowingTags, tagsPane);
        
        List<TagModel> allTags = tagService.getAllTags();
        tagsPane.getChildren().addAll(
        allTags.stream()
           .map(tagModel -> createClickToAddTagLabels(fileModel, tagModel)) // 1. Convert String to a JavaFX Label node
           .toList()                            // 2. Collect into a list for addAll()
        );

        // Scene scene = new Scene(containerVBox, 400, 400);
        Stage stage = ViewUtils.createBasePopupStage(containerVBox, null, 400   , 400);
        // stage.setScene(scene);
        stage.show();
        
    };

    private Label createTagLabel(TagModel tag) {
        String cssBackgroundColorString = ColorUtils.toCssColor(tag.getColorHashString());
        String tagText = tag.getText();

        Label tagLabel = ViewUtils.createTagLabel(
            tagText,
            cssBackgroundColorString);
        return tagLabel;
    }

    private Label createClickToAddTagLabels(FileModel fileModel, TagModel tag){

        Label tagLabel = createTagLabel(tag);
        tagLabel.setOnMouseClicked(
            event -> {
                if(!fileModel.getTags().contains(tag)){
                    fileTagRelationshipService.addTagToFile(fileModel, tag);
                }
                else{
                    String warningTitle = "Duplicate Tag!";
                    String warningHeader = "File already contains the tag "+tag.getText();
                    String warningBody = "You cannot add the same tag twice to the same file!";
                    ViewUtils.showWarningMessage(
                        warningTitle,
                        warningHeader,
                        warningBody
                    );
                }
            addFilesToTableViewColumns();
            }
        );
        return tagLabel;
    }



    private Label createClickToFilterByTagLabel(TagModel tag){
        
        Label tagLabel = createTagLabel(tag);
        tagLabel.setId(tag.getId().toString());
        
        tagLabel.setOnMouseClicked(
            event -> {
                if(TagFilter.lookup("#"+tag.getId().toString()) == null){
                       TagFilter.getChildren().add(createClickToRemoveTagFromFilter(tag));
                    }
                    addFilesToTableViewColumns();
            }
        );
        return tagLabel;
    }

    private Label createClickToRemoveTagFromFilter(TagModel tag){

        Label tagLabel = createTagLabel(tag);
        tagLabel.setId(tag.getId().toString());
        
        logger.info("##defining tag background color##: "+"-fx-background-color: "+tag.getColorHashString().replace("0x", "#"));
        tagLabel.setOnMouseClicked(
            event -> {
                    TagFilter.getChildren().remove(tagLabel);
                    addFilesToTableViewColumns();
            }
        );
        return tagLabel;
    }

    private Set<Long> getFilterTagIds(){
        Set<Long> tagIds = new HashSet<Long>();
        TagFilter.getChildren()
                 .stream()
                 .forEach(tagButtonNode -> {
                    Long tagId = Long.parseLong(tagButtonNode.getId());
                    tagIds.add(tagId);
                 });
        return tagIds;
    };

    private void setupSearchBarSuggestionPrediction(){

        suggestionsList.setMaxHeight(200);
        suggestionsList.prefWidthProperty().bind(searchBar.widthProperty());
        suggestionsList.getStyleClass().add("suggestion-list");

        suggestionsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(TagModel tag, boolean empty) {
                super.updateItem(tag, empty);

                if (empty || tag == null) {
                    setText(null);
                    setGraphic(null);
                } else {

                    Label tagLabel = createClickToFilterByTagLabel(tag);
                    tagLabel.getStyleClass().add("suggestion-tag-item");
                    setGraphic(tagLabel);
                    // setText(tag.getText());
                }
            }
        });

        suggestionsList.setOnMouseClicked(event -> {
            TagModel selectedTag =
                    suggestionsList.getSelectionModel().getSelectedItem();

            if (selectedTag != null) {
                selectSuggestion(selectedTag);
            }
            logger.info("selected tag: " + selectedTag.getText());
        });

        suggestionsPopup.getContent().add(suggestionsList);
        suggestionsPopup.setAutoHide(true);

        searchBar.textProperty().addListener((observable, oldText, newText) -> {
            logger.info("inside listener:: TEXT:  " + newText);
            updateSuggestions(newText);
        });

        
    }

    private void selectSuggestion(TagModel selectedTag) {
        // TODO
        // implement textual search for file names
        searchBar.setText("");
        // searchBar.positionCaret(searchBar.getText().length());

        if(TagFilter.lookup("#"+selectedTag.getId().toString()) == null){
                       TagFilter.getChildren().add(createClickToRemoveTagFromFilter(selectedTag));
                    }
                    addFilesToTableViewColumns();
        suggestionsPopup.hide();

        // Use the complete object if needed:
        // selectedTag.getColorHashString();
        // selectedTag.getDescription();
    }

    // private void updateSuggestions(String text) {

    //     List <TagModel> matchingTags = tagService.findTagsMatching(text);
    //     logger.info("inside updateSuggestions:: TEXT:  ");
    //     matchingTags.stream()
    //                 .forEach(
    //                     tag -> logger.info(tag.getText())
    //                 );

    //     suggestionsList.getItems().setAll(matchingTags);
    //     // suggestionsPopup.show(searchBarVBox);
    //     // if (matchingTags.isEmpty()) {
    //     //     suggestionsPopup.hide();
    //     //     return;
    //     // }
    // }
    private void updateSuggestions(String text) {
    // 1. If text is completely blank, clean up and hide right away
        if (text == null || text.trim().isEmpty()) {
            suggestionsPopup.hide();
            return;
        }

        List<TagModel> matchingTags = tagService.findTagsMatching(text);
        logger.info("inside updateSuggestions:: TEXT: " + text);

        suggestionsList.getItems().setAll(matchingTags);

        // 2. Hide popup if no matching tags are found
        if (matchingTags.isEmpty()) {
            suggestionsPopup.hide();
            return;
        }

        // 3. Display or reposition the popup if it has items
        Window ownerWindow = searchBar.getScene().getWindow();
        
        // Calculate the absolute X and Y screen position right below the searchBar node
        Point2D absoluteCoordinates = searchBar.localToScreen(0, searchBar.getHeight());

        if (ownerWindow != null && absoluteCoordinates != null) {
            // This displays the floating popup at the exact screen location without moving layout elements
            suggestionsPopup.show(ownerWindow, absoluteCoordinates.getX(), absoluteCoordinates.getY());
        }
    }
}
