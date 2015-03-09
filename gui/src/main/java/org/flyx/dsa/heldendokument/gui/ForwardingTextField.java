package org.flyx.dsa.heldendokument.gui;

import javafx.scene.control.TextField;

/**
 * @author flyx
 */
public class ForwardingTextField extends TextField {
    final private TextInputProperty textInputProperty;

    public ForwardingTextField(String initialValue) {
        super(initialValue);
        textInputProperty = new TextInputProperty(initialValue);
        this.textProperty().bindBidirectional(textInputProperty);
    }

    public TextInputProperty valueProperty() {
        return textInputProperty;
    }
}
