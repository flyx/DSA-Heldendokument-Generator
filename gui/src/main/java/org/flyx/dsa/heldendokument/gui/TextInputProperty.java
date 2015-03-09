package org.flyx.dsa.heldendokument.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.lang.reflect.Field;

/**
 * @author flyx
 */
public class TextInputProperty extends SimpleStringProperty implements ChangeListener<String> {
    Object target;
    Field field;

    public TextInputProperty(String initialValue) {
        super(initialValue);
        this.addListener(this);
    }

    public void forwardBind(Object target, Field field) {
        this.target = target;
        this.field = field;
        assert(String.class.equals(field.getType()));
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (target != null) {
            try {
                field.set(target, newValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("warbl", e);
            }
        }
    }
}
