package org.brightify.torch.util.async;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AsyncExecutionException extends RuntimeException {
    public AsyncExecutionException(Throwable cause) {
        super("Asynchronous task ended with an exception.", cause);
    }
}
