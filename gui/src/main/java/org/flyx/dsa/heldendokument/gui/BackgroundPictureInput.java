package org.flyx.dsa.heldendokument.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import org.flyx.dsa.heldendokument.generator.DocumentConfiguration;

/**
 * @author flyx
 */
public class BackgroundPictureInput extends HBox implements ChangeListener<String> {
    private DocumentConfiguration.Hintergrund.Hintergrundbild target;

    private ComboBox<String> box;

    public BackgroundPictureInput() {
        super();

        box = new ComboBox<String>();
        box.getItems().addAll("Original", "Keines", "Alterativ", "Benutzerdefiniert");
        box.getSelectionModel().selectFirst();
        box.valueProperty().addListener(this);
        this.getChildren().add(box);
    }

    public void forwardBind(DocumentConfiguration.Hintergrund.Hintergrundbild target) {
        this.target = target;
        switch (target.type) {
            case ORIGINAL: box.setValue("Original"); break;
            case NONE: box.setValue("Keines"); break;
            case ALTERNATIVE: box.setValue("Alternativ"); break;
            case CUSTOM: box.setValue("Benutzerdefiniert"); break;
        }
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (target != null) {
            if ("Original".equals(newValue)) {
                target.type = DocumentConfiguration.Hintergrund.Hintergrundbild.Type.ORIGINAL;
            } else if ("Keines".equals(newValue)) {
                target.type = DocumentConfiguration.Hintergrund.Hintergrundbild.Type.NONE;
            } else if ("Alternativ".equals(newValue)) {
                target.type = DocumentConfiguration.Hintergrund.Hintergrundbild.Type.ALTERNATIVE;
            } else if ("Benutzerdefiniert".equals(newValue)) {
                target.type = DocumentConfiguration.Hintergrund.Hintergrundbild.Type.CUSTOM;
            }
        }
    }
}
