package com.tagit.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class TagManagerTabController {
    @FXML
    private ListView<String> tagsList;

    @FXML
    private Button addTagButton;

    @FXML
    public void initialize() {
        // #TODO: load tags from service
        // #TODO: bind tags to list view
    }

    @FXML
    private void onAddTag() {
        // #TODO: open dialog to create new tag
    }

    @FXML
    private void onDeleteTag() {
        // #TODO: delete selected tag from database
    }

    @FXML
    private void onEditTag() {
        // #TODO: open dialog to edit selected tag
    }
}
