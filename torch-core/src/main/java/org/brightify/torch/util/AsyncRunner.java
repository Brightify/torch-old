package org.brightify.torch.util;

import android.os.AsyncTask;
import org.brightify.torch.Settings;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AsyncRunner {

    private AsyncRunner() {
    }

    public static <RESULT> CallbackAsyncTask<RESULT> submit(final Callback<RESULT> callback, final Task<RESULT> task) {
        CallbackAsyncTask<RESULT> asyncTask = new CallbackAsyncTask<RESULT>(callback, task);
        asyncTask.executeOnExecutor(Settings.getAsyncExecutor());
        return asyncTask;
    }

    public static <RESULT> RESULT run(final Task<RESULT> task) throws ExecutionException, InterruptedException {
        return submit(null, task).get().unwrap();
    }

    public static <RESULT> RESULT run(final Task<RESULT> task, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return submit(null, task).get(timeout, unit).unwrap();
    }

    public static <RESULT> RESULT runNoThrow(final Task<RESULT> task) {
        try {
            return run(task);
        } catch (Exception e) {
            return null;
        }
    }

    public static <RESULT> RESULT runNoThrow(final Task<RESULT> task, long timeout, TimeUnit unit) {
        try {
            return run(task, timeout, unit);
        } catch (Exception e) {
            return null;
        }
    }

    public interface Task<RESULT> {
        RESULT doWork() throws Exception;
    }

    public static class CallbackAsyncTask<RESULT> extends AsyncTask<Void, Exception, CallbackAsyncTask.Result<RESULT>> {

        private final Task<RESULT> task;
        private final Callback<RESULT> callback;

        /**
         * Null callback can mean two things: a) you don't care about the result b) you will fetch the result
         * synchronously via {@link AsyncTask#get()}.
         *
         * @param callback Callback to be executed after the task is complete or null.
         * @param task     Task to be executed, never null.
         *
         * @throws IllegalArgumentException If task is null.
         */
        public CallbackAsyncTask(final Callback<RESULT> callback, final Task<RESULT> task)
                throws IllegalArgumentException {
            if (task == null) {
                throw new IllegalArgumentException("Task to run cannot be null!");
            }

            this.task = task;
            this.callback = callback;
        }

        /**
         * @return Instance of {@link Result} with either data, or exception. Never returns null.
         */
        @Override
        protected Result<RESULT> doInBackground(Void... params) {
            Result<RESULT> result;
            try {
                RESULT resultData = task.doWork();
                result = new Result<RESULT>(resultData);
            } catch (Exception e) {
                result = new Result<RESULT>(e);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Result<RESULT> resultResult) {
            if (callback == null) {
                return;
            }
            if (resultResult.exception != null) {
                callback.onFailure(resultResult.exception);
            } else {
                callback.onSuccess(resultResult.data);
            }
        }

        static class Result<RESULT> {
            private final RESULT data;
            private final Exception exception;

            public Result(RESULT data) {
                this.data = data;
                this.exception = null;
            }

            public Result(Exception exception) {
                this.exception = exception;
                this.data = null;
            }

            public RESULT unwrap() throws RuntimeException {
                if (exception != null) {
                    throw new RuntimeException("Task ended with an exception.", exception);
                }

                return data;
            }
        }
    }
}
