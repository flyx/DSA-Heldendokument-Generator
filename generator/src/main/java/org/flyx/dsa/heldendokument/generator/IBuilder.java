package java.org.flyx.dsa.heldendokument.generator;

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
}
