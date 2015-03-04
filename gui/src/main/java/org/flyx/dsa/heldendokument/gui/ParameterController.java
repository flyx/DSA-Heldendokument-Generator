package org.flyx.dsa.heldendokument.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.controlsfx.dialog.Dialogs;
import org.flyx.dsa.heldendokument.generator.AdditionalParameter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author flyx
 */
public class ParameterController {
    @FXML private GridPane content;

    private Map<String, StringProperty> parameters;

    @FXML public void initialize() {
        parameters = new HashMap<>();

        int row = 0;
        for (AdditionalParameter parameter : App.getInstance().builder.getAdditionalParameters()) {
            Label caption = new Label(parameter.name + ":");
            GridPane.setConstraints(caption, 0, row);
            content.getChildren().add(caption);

            switch (parameter.type) {
                case PATH:
                    FileSelector fileSelector = new FileSelector();
                    SimpleStringProperty textProperty = new SimpleStringProperty();
                    parameters.put(parameter.name, textProperty);
                    fileSelector.textProperty().bindBidirectional(textProperty);
                    fileSelector.textProperty().setValue(App.getInstance().builder.getPathParameter(parameter.name));
                    GridPane.setConstraints(fileSelector, 1, row);
                    content.getChildren().add(fileSelector);
                    break;
            }

            Label description = new Label(parameter.description);
            GridPane.setConstraints(description, 2, row);
            content.getChildren().add(description);

            row++;
        }
    }

    public void cancel(ActionEvent event) {
        content.getScene().getWindow().hide();
    }

    public void ok(ActionEvent event) {
        for (Map.Entry<String, StringProperty> entry: parameters.entrySet()) {
            if (entry.getValue().getValue().length() == 0) {
                Dialogs.create()
                        .owner(content.getScene())
                        .title("Wert nicht gesetzt")
                        .masthead(null)
                        .message(entry.getKey() + " muss gesetzt sein!")
                        .showError();
                return;
            } else {
                App.getInstance().builder.setPathParameter(entry.getKey(), entry.getValue().getValue());
            }
        }
        content.getScene().getWindow().hide();
    }
}
