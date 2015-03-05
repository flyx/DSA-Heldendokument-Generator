package org.flyx.dsa.heldendokument.generator;

import java.io.File;
import java.util.List;

/**
 * An IBuilder object creates a PDF file that can later be filled with values.
 * This interface carries no semantics about the creation process.
 *
 * @author flyx
 */
public interface IBuilder {
    /**
     * Register a callback to send status reports to.
     * @param callback Object that wants to receive status reports
     */
    void setCallback(IBuilderCallback callback);

    /**
     *
     * @return a list of parameters that are needed by the IBuilder implementation.
     */
    List<AdditionalParameter> getAdditionalParameters();

    /**
     * Set a parameter defined in the list returned by {@link #getAdditionalParameters}.
     * @param name Parameter name as returned by {@link #getAdditionalParameters}
     * @param value Value of the parameter. Must be a valid path.
     */
    void setPathParameter(String name, String value);

    /**
     * Check if a paramater has a valid value
     * @param name Parameter name as returned by {@link #getAdditionalParameters}
     * @return true iff the parameter has a valid value
     */
    boolean isParameterValid(String name);

    default boolean areAllParametersValid() {
        for (AdditionalParameter parameter : getAdditionalParameters()) {
            if (!isParameterValid(parameter.name)) return false;
        }
        return true;
    }

    /**
     * @param name Parameter name as returned by {@link #getAdditionalParameters}
     * @return The current value of the parameter
     */
    String getPathParameter(String name);

    /**
     * Checks if the environment has the necessary tools installed for running the builder.
     * @param messages A list for appending any error messages
     * @return true iff the environment can run the builder
     */
    boolean isEnvironmentValid(List<String> messages);

    /**
     * Prepare the build environment for building the PDF.
     * Before calling this method, all additional parameters must have a valid value.
     */
    void prepare();

    /**
     * Builds the PDF document.
     * @param configuration Desired configuration of the PDF document
     */
    File build(DocumentConfiguration configuration) throws ExternalCallException;
}
