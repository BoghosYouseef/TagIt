package com.tagit.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;

public class CollectionsTabController {
    @FXML
    private TilePane collectionsTile;

    @FXML
    private Button addCollectionButton;

    @FXML
    public void initialize() {
        // #TODO: load collections from service
        // #TODO: create collection cards as nodes and add to tile pane
        // #TODO: bind collection data to tile children (name, description, file count, color)
        // TilePane will wrap children automatically as window resizes, making layout responsive
    }

    @FXML
    private void onAddCollection() {
        // #TODO: open dialog to create new collection
    }
}
