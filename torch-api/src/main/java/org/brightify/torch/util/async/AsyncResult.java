package org.brightify.torch.util.async;

/**
* @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
*/
public class AsyncResult<RESULT> {
    private final RESULT data;
    private final Exception exception;

    public AsyncResult(RESULT data) {
        this(data, null);
    }

    public AsyncResult(Exception exception) {
        this(null, exception);
    }

    public AsyncResult(RESULT data, Exception exception) {
        this.data = data;
        this.exception = exception;
    }

    public RESULT getData() {
        return data;
    }

    public Exception getException() {
        return exception;
    }

    public RESULT unwrap() throws AsyncExecutionException {
        if(exception != null) {
            throw new AsyncExecutionException(exception);
        }
        return data;
    }
}
