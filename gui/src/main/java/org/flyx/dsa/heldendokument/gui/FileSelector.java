package org.flyx.dsa.heldendokument.gui;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * @author flyx
 */
public class FileSelector extends HBox {
    private TextField field;
    private Button button;

    public FileSelector() {
        field = new TextField();
        field.setEditable(false);
        field.setStyle("-fx-background-radius: 5 0 0 5;");
        field.setMinWidth(200);
        setHgrow(field, Priority.ALWAYS);

        button = new Button("Auswählen…");
        button.setStyle("-fx-background-radius: 0 5 5 0;");
        button.setOnAction((ActionEvent event) -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Datei auswählen");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TrueType Fonts", "*.ttf"));
            File file = chooser.showOpenDialog(this.getScene().getWindow());
            if (file != null) {
                field.textProperty().setValue(file.getAbsolutePath());
            }
        });

        getChildren().addAll(field, button);
    }

    public StringProperty textProperty() {
        return field.textProperty();
    }
}
