package com.tagit.tag.controller;

import com.tagit.AppConfig;
import com.tagit.TagItApp;
import com.tagit.exception.DuplicateTagException;
import com.tagit.exception.TagPersistenceException;
import com.tagit.file.domain.FileModel;
import com.tagit.tag.domain.TagModel;
import com.tagit.tag.event.NewTagCreatedEvent;
import com.tagit.tag.service.TagService;
import com.tagit.ui.ViewUtils;
import com.tagit.utils.ColorUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;

import javafx.fxml.FXML;
import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.text.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class TagManagerTabController {

    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);

    @Autowired
    AppConfig appConfig;

    @Autowired
    TagService tagService;

    @FXML
    private TableView<TagModel> tagsTable;
        
    @FXML
    private TextField searchBar;

    @FXML
    private TableColumn<TagModel, TagModel> tagColumn;

    @FXML
    private TableColumn<TagModel, String> descriptionColumn;
    
    @FXML
    private TableColumn<TagModel, Instant> createdAtColumn;
    
    @FXML
    private TableColumn<TagModel, Void> actionsColumn;
    
    @FXML
    private Button createTagButton;

    @FXML
    private Label tagCountLabel;

    private double MINIMUM_TAB_PANE_WIDTH;

    @FXML
    public void initialize() {

        searchBar.textProperty().addListener((observable, oldText, newText) -> {
            addTextFilteredTagsToTableViewColumns(newText);
        });
        addCustomHeader(tagColumn, "Tag");
        // enableTextWrapping(tagColumn);

        addCustomHeader(descriptionColumn, "Description");
        enableTextWrapping(descriptionColumn);
        
        addCustomHeader(createdAtColumn, "Created At");
        enableInstantFormatting(createdAtColumn);
        
        addCustomHeader(actionsColumn, "Actions");
        this.MINIMUM_TAB_PANE_WIDTH = appConfig.getMinimumTabPaneWidth();

        setColumnMinimumWidths(this.MINIMUM_TAB_PANE_WIDTH);
        tagColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        tagsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        
        tagColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(TagModel tag, boolean empty) {
                super.updateItem(tag, empty);
                
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Text tagText = new Text(tag.getText());
                {
                    tagText.wrappingWidthProperty().bind(widthProperty().subtract(10));
                }
                // Button tagWrapper = createTagButton(tag);
                Label tagLabel = ViewUtils.createTagLabel(
                    tag.getText(),
                    ColorUtils.toCssColor(tag.getColorHashString())
                );
                setGraphic(tagLabel);
            }
        });
        
        descriptionColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String description, boolean empty) {
                super.updateItem(description, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }
                Label tagDescriptionTextLabel = new Label(description);
                setGraphic(tagDescriptionTextLabel);
            }
        });
        
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                    return;
                }
                TagModel tag = getTableView().getItems().get(getIndex());
                
                String deleteTagText = "Delete";
                String editTagText = "Edit";
                Button deleteTagButton = ViewUtils.createButtonWithTextAndColor(
                    deleteTagText,
                    "#e0344b"
                );
                
                deleteTagButton.setOnAction( event -> 
                    {
                        deleteTag(tag);
                    }
                );
                Button editTagButton = ViewUtils.createButtonWithTextAndColor(
                    editTagText,
                    "#e79f2b"
                );
                
                editTagButton.setOnAction( event -> 
                    {
                        editTag(tag);
                    }
                );
                
                HBox actionButtonsContainer = new HBox(
                    editTagButton, deleteTagButton
                );
                actionButtonsContainer.setAlignment(Pos.CENTER);
                actionButtonsContainer.setSpacing(5);
                setGraphic(actionButtonsContainer);
            }
        });
        addTagsToTableViewColumns();
        logger.info("TABS MANAGER INITIALIZED!");
    }
    
    @FXML
    private void onAddTag() {
        
        Label onAddTagLabel = new Label("Create a new tag!");
        
        ColorPicker colorPicker = createColorPicker();
        Button createTagButton = new Button("Create Tag");
        createTagButton.setPadding(new Insets(40));
        VBox inputTextfieldBox = createTagFormInputFields("","");
        VBox vbox = new VBox(10, onAddTagLabel, inputTextfieldBox, colorPicker, createTagButton);
        
        Stage tagCreatorStage = ViewUtils.createBasePopupStage(
            vbox,
            "Create a new tag!",
            350, 
            450);

        createTagButton.setOnAction(event -> {
            
                Instant createdAt = Instant.now();
                String tagName = getTextFieldValue(inputTextfieldBox, "tagString");
                String tagDescription = getTextFieldValue(inputTextfieldBox, "tagDescriptionString");
                logger.info("[onAddTag: description:] " + tagDescription);
                String tagColor = colorPicker.getValue().toString();
                logger.info("[TagManagerTabController.onAddTag] createdAt: " + createdAt);
                try {
                    tagService.saveToDataBase(tagName, tagDescription, tagColor, createdAt);
                    addTagsToTableViewColumns();
                    tagCreatorStage.close();
                } catch (DuplicateTagException e) {
                    logger.warn("Duplicate tag attempted: {}", e.getMessage());
                    
                    String errorMessageTitle = "Could not create tag";
                    String errorMessageBody = "The tag text and color combination already exist!";
                    ViewUtils.showError(
                        errorMessageTitle,
                        errorMessageBody);
                    

                } catch (TagPersistenceException e) {
                    logger.error("Could not save tag", e);


                }
            });

        formatAndShowFormWindow(vbox, tagCreatorStage);
    }

    private void deleteTag(TagModel tagModel) {
        String tagText = tagModel.getText();
        String tagColorHashString = tagModel.getColorHashString();
        String cssTagColorHashString = ColorUtils.toCssColor(tagColorHashString);
        tagService.deleteTag(tagText, tagColorHashString);
        String successMessageTitle = "Tag deleted!";
        String successMessageBody = "The following tag has been deleted!";
        Label tagLabel = ViewUtils.createTagLabel(tagText, cssTagColorHashString);
        addTagsToTableViewColumns();
        ViewUtils.showSuccess(
            successMessageTitle,
            successMessageBody,
            tagLabel
        );
    }

    private void editTag(TagModel tagModel) {
        
        String existingTagText = tagModel.getText();
        String existingTagDescription = tagModel.getDescription();
        String existingTagColorHashString = tagModel.getColorHashString();
        
        Label onAddTagLabel = new Label("Edit an existing tag!");

        Color existingTagColor = Color.web(tagModel.getColorHashString(),1);
        ColorPicker colorPicker = createColorPicker(existingTagColor);
        Button createTagButton = new Button("Save Tag");
        createTagButton.setPadding(new Insets(40));
        VBox inputTextfieldBox = createTagFormInputFields(tagModel.getText(),tagModel.getDescription());
        VBox vbox = new VBox(10, onAddTagLabel, inputTextfieldBox, colorPicker, createTagButton);
        

        Stage tagCreatorStage = ViewUtils.createBasePopupStage(
            vbox,
            "Edit an existing tag!",
            350, 
            450);

        createTagButton.setOnAction(event -> {
            
                Instant createdAt = Instant.now();
                String tagName = getTextFieldValue(inputTextfieldBox, "tagString");
                String tagDescription = getTextFieldValue(inputTextfieldBox, "tagDescriptionString");
                String tagColor = colorPicker.getValue().toString();
                logger.info("[TagManagerTabController.onAddTag] createdAt: " + createdAt);
                try {
                    tagService.editTag(
                        existingTagText,
                        existingTagDescription,
                        existingTagColorHashString,
                        tagName,
                        tagDescription,
                        tagColor
                    );
                    addTagsToTableViewColumns();
                    ViewUtils.showSuccess("Tag Changed!","Tag changed successfully!");
                    tagCreatorStage.close();
                } catch (DuplicateTagException e) {
                    logger.warn("Duplicate tag attempted: {}", e.getMessage());
                    
                    String errorMessageTitle = "Could not create tag";
                    String errorMessageBody = "The tag text and color combination already exist!";
                    ViewUtils.showError(
                        errorMessageTitle,
                        errorMessageBody);
                    

                } catch (TagPersistenceException e) {
                    logger.error("Could not save tag", e);


                }
            });

        formatAndShowFormWindow(vbox, tagCreatorStage);
    }

    private VBox createTagFormInputFields(
        String initialName, 
        String initialDescription
    ){
        Label tagNameInputTextFieldLabel = new Label("Tag text");
        TextField tagNameInputTextField = new TextField(initialName);
        tagNameInputTextField.setId("tagString");
        
        Label tagDescriptionOptionalInputTextFieldLabel = new Label("Tag Description (optional)");
        TextArea tagDescriptionOptionalInputTextField = new TextArea(initialDescription);
        tagDescriptionOptionalInputTextField.setWrapText(true);
        tagDescriptionOptionalInputTextField.setId("tagDescriptionString");
        // tagDescriptionOptionalInputTextField.setStyle("-fx-padding: 0;");
        
        VBox inputTextHBox = new VBox(10,
            tagNameInputTextFieldLabel,
            tagNameInputTextField,
            tagDescriptionOptionalInputTextFieldLabel,
            tagDescriptionOptionalInputTextField);

        inputTextHBox.setAlignment(Pos.CENTER);
        inputTextHBox.setMinWidth(100);
        inputTextHBox.setMinHeight(300);
        
        return inputTextHBox;
    }

    private ColorPicker createColorPicker() {
        Color defaultWhite = Color.WHITE;
        return createColorPicker(defaultWhite); 
    }


    private ColorPicker createColorPicker(Color initialColor) {
        final ColorPicker colorPicker = new ColorPicker(initialColor);
        
        // Set your strict heights programmatically
        colorPicker.setMinHeight(40);
        colorPicker.setPrefHeight(40);
        colorPicker.setMaxHeight(40);

        // Apply the initial background color right when it's created
        updateColorPickerAppearance(colorPicker, colorPicker.getValue());

        // Use a clean lambda expression to update the UI and handle your logic whenever a color is chosen
        colorPicker.setOnAction(event -> {
            Color chosenColor = colorPicker.getValue();
            
            // 1. Run the appearance updater code
            updateColorPickerAppearance(colorPicker, chosenColor);
            
            // 2. Your original code or future custom event logic goes here:
            System.out.println("User selected color: " + chosenColor); 
        });

        return colorPicker;
    }

    /**
     * Helper method to dynamically change the ColorPicker background 
     * and automatically flip the text color for contrast readability.
     */
    private void updateColorPickerAppearance(ColorPicker picker, Color color) {
        if (color == null) return;

        // Convert JavaFX Color to a standard hex string (RRGGBB)
        String hex = String.format("%02X%02X%02X", 
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
        
        // Automatically swap text color based on background brightness (Black text for light colors, White for dark)
        String textColor = ColorUtils.getContrastTextColor(hex);
        
        // Apply inline structural styles to the button background
        // Force background-insets to 0 to prevent internal focus rings from offsetting
        picker.setStyle(
            "-fx-background-color: #" + hex + ";" +
            "-fx-background-insets: 0;" + 
            "-fx-background-radius: 4px;" // Optional: adds a slight modern rounded edge
        );
        
        // Apply text color directly to the label node inside the picker
        // We wrap this in a Platform.runLater to ensure the internal skin graph has rendered on creation
        javafx.application.Platform.runLater(() -> {
            var labelNode = picker.lookup(".label");
            if (labelNode != null) {
                labelNode.setStyle("-fx-text-fill: " + textColor + ";");
            }
        });
    }

    private void addCustomHeader(TableColumn<?, ?> column, String text) {
        HBox header = new HBox();
        Label label = new Label(text);
        
        // header.setPadding(new Insets(0, 0, 0, 10));
        header.getChildren().add(label);
        header.setAlignment(javafx.geometry.Pos.CENTER);
        column.setGraphic(header); // set the custom header
    }
    
    private void enableInstantFormatting(TableColumn<TagModel, Instant> column) {
        final DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("MMMM dd, yyyy - HH:mm")
                        .withZone(ZoneId.systemDefault());
        column.setCellFactory(tableCell -> new TableCell<>() {

            @Override
            protected void updateItem(Instant item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
    }

    private <T> void enableTextWrapping(TableColumn<TagModel, T> column) {
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

    private void addTagsToTableViewColumns(){
        List<TagModel> tags = tagService.getAllTags();
        logger.info("Adding {} tags to table", tags.size());
        tagCountLabel.setText(String.format("(%d tags)", tags.size()));
        tagsTable.getItems().clear();
        tagsTable.getItems().addAll(tags);
    }

    private void addTextFilteredTagsToTableViewColumns(String text){
        logger.info("inside addTextFilteredTagsToTableViewColumns:: TEXT: " + text);
        if (text == null || text.trim().isEmpty()) {
            addTagsToTableViewColumns();
        }
        List<TagModel> matchingTags = tagService.findTagsMatching(text);
        
        tagsTable.getItems().clear();
        tagsTable.getItems().addAll(matchingTags);
    }

    // Spring automatically calls this when TagService publishes the event
    @EventListener
    public void onNewTagCreated(NewTagCreatedEvent event) {
        Platform.runLater(() -> {
            TagModel newTag = event.tag();
            logger.info("new tag added!: " + newTag.getText());
            // Defensive check: only add if it's not already visually rendered
            if (!tagsTable.getItems().contains(newTag)) {
                tagsTable.getItems().add(newTag);
            }
        });
    }



    private void setColumnMinimumWidths(double minimumWidth) {
        logger.warn("Setting file-column minimum widths to: " + minimumWidth);
        for (TableColumn<?, ?> column : tagsTable.getColumns()) {
            if (column.getStyleClass().contains("file-column")) { // Check for the CSS class
                column.setMinWidth(200); // Set your desired minimum width
            }
        }
    }

    // Helper to extract text from your VBox children loops cleanly
    private String getTextFieldValue(VBox container, String elementId) {
        for (Node child : container.getChildren()) {
            if (elementId.equals(child.getId())) {
                if (child instanceof TextInputControl textInput) {
                    return textInput.getText();
                }
            }
        }
        return "";
    }

    // Shared method handling the stage modailty and padding settings
    private void formatAndShowFormWindow(VBox vbox, Stage stage) {
        vbox.setStyle("-fx-padding: 100; -fx-alignment: center;");
        vbox.setAlignment(Pos.CENTER);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
}
