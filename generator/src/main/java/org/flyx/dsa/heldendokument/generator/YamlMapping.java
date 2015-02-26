package org.flyx.dsa.heldendokument.generator;

import java.lang.annotation.*;

/**
 * Gives the object's key in parameter.yaml
 *
 * @author flyx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface YamlMapping {
    String value();
}
