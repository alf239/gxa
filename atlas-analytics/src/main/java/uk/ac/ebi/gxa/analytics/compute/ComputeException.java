package uk.ac.ebi.gxa.analytics.compute;

/**
 * A {@link Throwable} that is thrown whenever a {@link ComputeTask} fails.
 *
 * @author Tony Burdett
 * @date 14-Jan-2010
 */
public class ComputeException extends Exception {
    public ComputeException() {
        super();
    }

    public ComputeException(String message) {
        super(message);
    }

    public ComputeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComputeException(Throwable cause) {
        super(cause);
    }
}
