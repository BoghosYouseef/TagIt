package com.tagit.ui;

import com.tagit.AppConfig;
import com.tagit.TagItApp;
import com.tagit.model.FileModel;
import com.tagit.model.TagModel;
import com.tagit.service.TagService;
import com.tagit.utils.ColorUtils;
import com.tagit.utils.ViewUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    private TableColumn<TagModel, TagModel> tagColumn;

    @FXML
    private TableColumn<TagModel, String> descriptionColumn;
    
    @FXML
    private TableColumn<TagModel, Instant> createdAtColumn;
    
    @FXML
    private Button createTagButton;

    @FXML
    private Label tagCountLabel;

    private double MINIMUM_TAB_PANE_WIDTH;

    @FXML
    public void initialize() {
        logger.info("TABS MANAGER INITIALIZED!");

        // #TODO: load tags from service
        // #TODO: bind tags to TableView

        addCustomHeader(tagColumn, "Tag");
        // enableTextWrapping(tagColumn);

        addCustomHeader(descriptionColumn, "Description");
        enableTextWrapping(descriptionColumn);

        addCustomHeader(createdAtColumn, "Created At");
        enableInstantFormatting(createdAtColumn);

        this.MINIMUM_TAB_PANE_WIDTH = appConfig.getMinimumTabPaneWidth();

        setColumnMinimumWidths(this.MINIMUM_TAB_PANE_WIDTH);
        tagColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        

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
                Button tagWrapper = createTagButton(tag);
                setGraphic(tagWrapper);
            }
        });
        addTagsToTableViewColumns();
    }

    @FXML
    private void onAddTag() {
        // #TODO: open dialog to create new tag
        // Stage tagCreatorStage =  new Stage();
        // tagCreatorStage.setTitle("Create a new tag!");
        Label onAddTagLabel = new Label("Create a new tag!");
        ColorPicker colorPicker = createColorPicker();
        Button createTagButton = new Button("Create Tag");
        createTagButton.setPadding(new Insets(40));
        VBox inputTextfieldBox = createOnTagAddInputText();
        VBox vbox = new VBox(10, onAddTagLabel,inputTextfieldBox, colorPicker, createTagButton);
        
        Stage tagCreatorStage = ViewUtils.createBasePopupStage(vbox, "Create a new tag!", 700, 450);

        createTagButton.setOnAction(event -> {
            
                Instant createdAt = Instant.now();
                String tagName = "";
                String tagDescription = "";
                for(Node child: inputTextfieldBox.getChildren()){
                    if (child instanceof TextField textField && "tagString".equals(textField.getId()))  {
                        tagName = textField.getText();
                    }
                    if (child instanceof TextField textField && "tagDescriptionString".equals(textField.getId()))  {
                        tagDescription = textField.getText();
                    }
                };
                String tagColor = colorPicker.getValue().toString();
                logger.info("[TagManagerTabController.onAddTag] createdAt: " + createdAt);
                tagService.saveToDataBase(tagName, tagDescription, tagColor, createdAt);
                addTagsToTableViewColumns();
                tagCreatorStage.close();
        });
        vbox.setStyle("-fx-padding: 100; -fx-alignment: center;");
        vbox.setAlignment(Pos.CENTER);
        // Scene tagCreatorScene = new Scene(vbox, 700, 450);
        // tagCreatorStage.setScene(tagCreatorScene);
        tagCreatorStage.initModality(Modality.APPLICATION_MODAL);
        tagCreatorStage.show();
    }

    @FXML
    private void onDeleteTag() {
        // #TODO: delete selected tag from database
    }

    @FXML
    private void onEditTag() {
        // #TODO: open dialog to edit selected tag
    }

    private VBox createOnTagAddInputText(){
        TextField tagNameInputTextField = new TextField();
        tagNameInputTextField.setId("tagString");
        TextField tagDescriptionOptionalInputTextField = new TextField();
        tagDescriptionOptionalInputTextField.setId("tagDescriptionString");
        tagDescriptionOptionalInputTextField.setStyle("-fx-padding: 40;");
        VBox inputTextHBox = new VBox(10,tagNameInputTextField, tagDescriptionOptionalInputTextField);
        inputTextHBox.setAlignment(Pos.CENTER);
        inputTextHBox.setMinWidth(100);
        inputTextHBox.setMinHeight(300);
        
        return inputTextHBox;
    }

    private ColorPicker createColorPicker(){
        
        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setStyle("-fx-padding: 15;");
        // colorPicker.setMinWidth(140);
        colorPicker.setMinHeight(60);
        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                Color c = colorPicker.getValue();
            }
        });
        return colorPicker;
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
        logger.info("Adding {} files to table", tags.size());
        tagCountLabel.setText(String.format("(%d tags)", tags.size()));
        tagsTable.getItems().clear();
        tagsTable.getItems().addAll(tags);
    }

    private void setColumnMinimumWidths(double minimumWidth) {
        logger.warn("Setting file-column minimum widths to: " + minimumWidth);
        for (TableColumn<?, ?> column : tagsTable.getColumns()) {
            if (column.getStyleClass().contains("file-column")) { // Check for the CSS class
                column.setMinWidth(minimumWidth); // Set your desired minimum width
            }
        }
    }

    private Button createTagButton(TagModel tag){
        Button tagLabelContainerButton = new Button(tag.getText());
        tagLabelContainerButton.getStyleClass().add("tag-button-display");
        tagLabelContainerButton.setDisable(true);
        String ChosenTagColor = ColorUtils.toCssColor(tag.getColorHashString());
        String contrastingTextColor = ColorUtils.getContrastTextColor(ChosenTagColor);
        tagLabelContainerButton.setStyle(
            "-fx-background-color: " + ChosenTagColor + ";" +
            "-fx-text-fill: " + contrastingTextColor + ";");
        return tagLabelContainerButton;
    }
}
