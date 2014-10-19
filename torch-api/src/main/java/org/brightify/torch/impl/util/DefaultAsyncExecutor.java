package org.brightify.torch.impl.util;

import org.brightify.torch.util.async.AsyncExecutor;
import org.brightify.torch.util.async.AsyncResult;
import org.brightify.torch.util.async.Callback;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class DefaultAsyncExecutor implements AsyncExecutor {

    private ExecutorService executorService;

    public DefaultAsyncExecutor() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public <RESULT> Future<AsyncResult<RESULT>> submit(Callback<RESULT> callback, Callable<RESULT> callable) {
        CallableWrapper<RESULT> wrapper = new CallableWrapper<RESULT>(callback, callable);
        return executorService.submit(wrapper);
    }

    public static class CallableWrapper<RESULT> implements Callable<AsyncResult<RESULT>> {

        private final Callback<RESULT> callback;
        private final Callable<RESULT> task;

        public CallableWrapper(Callback<RESULT> callback, Callable<RESULT> task) {
            this.callback = callback;
            this.task = task;
        }

        @Override
        public AsyncResult<RESULT> call() throws Exception {
            AsyncResult<RESULT> result;
            try {
                RESULT value = task.call();
                result = new AsyncResult<RESULT>(value);
            } catch (Exception e) {
                result = new AsyncResult<RESULT>(e);
            }

            if(callback != null) {
                Exception exception = result.getException();
                if(exception != null) {
                    callback.onFailure(exception);
                } else {
                    callback.onSuccess(result.getData());
                }
            }

            return result;
        }
    }
}
