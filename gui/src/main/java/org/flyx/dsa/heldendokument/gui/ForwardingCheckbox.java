package org.flyx.dsa.heldendokument.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;

import java.lang.reflect.Field;

/**
 * @author flyx
 */
public class ForwardingCheckbox extends CheckBox implements ChangeListener<Boolean> {
    private Object target;
    private Field field;

    public ForwardingCheckbox(String caption) {
        super(caption);
        this.selectedProperty().addListener(this);
    }

    public void forwardBind(Object target, Field field) {
        this.target = target;
        this.field = field;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (target != null) {
            try {
                field.set(target, newValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("warbl", e);
            }
        }
    }
}
