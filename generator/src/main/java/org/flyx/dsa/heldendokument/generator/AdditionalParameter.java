package org.flyx.dsa.heldendokument.generator;

/**
 * @author flyx
 */
public class AdditionalParameter {
    public static enum Type {
        PATH
    }
    public final Type type;
    public final String name;
    public final String description;

    public AdditionalParameter(Type type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }
}
