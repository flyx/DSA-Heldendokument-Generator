package org.flyx.dsa.heldendokument.generator;

import java.io.InputStream;

/**
 * Grants access to some of the resources that may be needed
 * by GUI / Heldendokument plugin
 *
 * @author flyx
 */
public class Resources {
    public static InputStream getParameterYAML() {
        return Resources.class.getResourceAsStream("/DSA-Heldendokument/data/parameter.yaml");
    }
}
