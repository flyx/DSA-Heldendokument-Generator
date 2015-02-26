package org.flyx.dsa.heldendokument.gui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.lang.reflect.Field;

/**
 * @author flyx
 */
public class IntegerInputProperty extends SimpleIntegerProperty implements ChangeListener<Number> {
    Object target;
    Field field;

    public IntegerInputProperty(int initialValue) {
        super(initialValue);
        this.addListener(this);
    }

    public void forwardBind(Object target, Field field) {
        this.target = target;
        this.field = field;
        assert(int.class.equals(field.getType()));
    }

    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (target != null) {
            try {
                field.set(target, newValue.intValue());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("warbl", e);
            }
        }
    }
}
