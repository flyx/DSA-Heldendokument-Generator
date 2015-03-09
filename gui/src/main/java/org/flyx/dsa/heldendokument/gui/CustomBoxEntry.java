package org.flyx.dsa.heldendokument.gui;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.flyx.dsa.heldendokument.generator.DocumentConfiguration;

import java.util.List;

/**
 * @author flyx
 */
public class CustomBoxEntry extends HBox {
    private final List<DocumentConfiguration.Talentbogen.Sonstiges> list;
    private final DocumentConfiguration.Talentbogen.Sonstiges item;
    private final Pane container;

    public CustomBoxEntry(List<DocumentConfiguration.Talentbogen.Sonstiges> list,
                          DocumentConfiguration.Talentbogen.Sonstiges item,
                          Pane container) {
        this.list = list;
        this.item = item;
        this.container = container;

        setSpacing(5);

        try {
            ForwardingTextField nameField = new ForwardingTextField(item.titel);
            nameField.valueProperty().forwardBind(item, item.getClass().getField("titel"));
            nameField.setPrefWidth(200);

            ForwardingIntField linesField = new ForwardingIntField(1, 99, item.zeilen);
            linesField.valueProperty().forwardBind(item, item.getClass().getField("zeilen"));
            linesField.setPrefWidth(50);

            Button deleteButton = new Button("-");
            deleteButton.setOnAction((value) -> {
                container.getChildren().remove(this);
                list.remove(item);
            });

            this.getChildren().addAll(nameField, linesField, deleteButton);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("This should never happen.", e);
        }
    }
}
