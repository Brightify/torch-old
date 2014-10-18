package org.brightify.torch.util.async;

import org.brightify.torch.impl.util.DefaultAsyncExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AsyncRunner {
    private static final Logger LOGGER = Logger.getLogger(AsyncRunner.class.getName());
    private static AsyncExecutor executor;

    private AsyncRunner() {
        throw new UnsupportedOperationException("AsyncRunner is only a static class and cannot be instantiated.");
    }

    public static <RESULT> Future<AsyncResult<RESULT>> submit(Callback<RESULT> callback, Callable<RESULT> task) {
        if (executor == null) {
            executor = new DefaultAsyncExecutor();
            LOGGER.log(Level.INFO, "AsyncExecutor was not set, default executor {0} will be used.",
                       executor.getClass().getSimpleName());
        }

        return executor.submit(callback, task);
    }

    public static <RESULT> RESULT run(Callable<RESULT> task) throws ExecutionException, InterruptedException {
        return submit(null, task).get().unwrap();
    }

    public static <RESULT> RESULT run(Callable<RESULT> task, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return submit(null, task).get(timeout, unit).unwrap();
    }

    public static void setExecutor(AsyncExecutor executor) {
        AsyncRunner.executor = executor;
    }

}
