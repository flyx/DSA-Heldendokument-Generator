package org.flyx.dsa.heldendokument.gui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.flyx.dsa.heldendokument.generator.DocumentConfiguration;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author flyx
 */
public class IntegerInputProperty extends SimpleIntegerProperty implements ChangeListener<Number> {
    Object target;
    Field field;

    Map<DocumentConfiguration.Talentbogen.Gruppen, Integer> mapTarget;
    DocumentConfiguration.Talentbogen.Gruppen targetKey;

    public IntegerInputProperty(int initialValue) {
        super(initialValue);
        this.addListener(this);
    }

    public void forwardBind(Object target, Field field) {
        this.target = target;
        this.field = field;
        assert(int.class.equals(field.getType()));
    }

    public void forwardBind(Map<DocumentConfiguration.Talentbogen.Gruppen, Integer> mapTarget,
                       DocumentConfiguration.Talentbogen.Gruppen key) {
        this.mapTarget = mapTarget;
        this.targetKey = key;
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
        if (mapTarget != null) {
            mapTarget.put(targetKey, newValue.intValue());
        }
    }
}
