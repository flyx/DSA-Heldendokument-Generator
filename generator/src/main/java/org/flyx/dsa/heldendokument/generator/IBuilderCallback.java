package org.flyx.dsa.heldendokument.generator;

/**
 * This is a callback interface for {@link IBuilder}. Because an action of
 * IBuilder may take longer, it will report the steps it undertakes back to
 * the IBuilderCallback, if one is set.
 *
 * @author flyx
 */
public interface IBuilderCallback {
    void nowDoing(String shortDescription, String description);
    void nowAt(int percent);
}
