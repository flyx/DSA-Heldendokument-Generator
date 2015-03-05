package org.flyx.dsa.heldendokument.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author flyx
 */
public class ExternalCallException extends Exception {
    private final String externalCallOutput;

    public ExternalCallException(String message, String externalCallOutput) {
        super(message);
        this.externalCallOutput = externalCallOutput;
    }

    public ExternalCallException(String message, InputStream externalCallInput) {
        super(message);
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(externalCallInput));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

        } catch (IOException e) {
            builder.append("Could not extract external call output");
        }
        this.externalCallOutput = builder.toString();
    }

    public String getExternalCallOutput() {
        return externalCallOutput;
    }
}
