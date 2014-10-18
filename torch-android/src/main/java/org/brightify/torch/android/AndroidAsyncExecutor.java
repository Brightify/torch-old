package org.brightify.torch.android;

import android.os.AsyncTask;
import org.brightify.torch.Settings;
import org.brightify.torch.util.async.AsyncExecutor;
import org.brightify.torch.util.async.AsyncResult;
import org.brightify.torch.util.async.Callback;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AndroidAsyncExecutor implements AsyncExecutor {
    @Override
    public <RESULT> Future<AsyncResult<RESULT>> submit(Callback<RESULT> callback, Callable<RESULT> task) {
        CallbackAsyncTask<RESULT> asyncTask = new CallbackAsyncTask<RESULT>(callback, task);
        asyncTask.executeOnExecutor(Settings.getAsyncExecutor());
        return asyncTask;
    }

    public static class CallbackAsyncTask<RESULT> extends AsyncTask<Void, Exception, AsyncResult<RESULT>>
            implements Future<AsyncResult<RESULT>> {

        private final Callback<RESULT> callback;
        private final Callable<RESULT> task;
        private final AtomicBoolean done = new AtomicBoolean(false);

        /**
         * Null callback can mean two things: a) you don't care about the result b) you will fetch the result
         * synchronously via {@link AsyncTask#get()}.
         *
         * @param callback Callback to be executed after the task is complete or null.
         * @param task     Task to be executed, never null.
         *
         * @throws IllegalArgumentException If task is null.
         */
        public CallbackAsyncTask(final Callback<RESULT> callback, final Callable<RESULT> task)
                throws IllegalArgumentException {
            if (task == null) {
                throw new IllegalArgumentException("Task to run cannot be null!");
            }

            this.callback = callback;
            this.task = task;
        }

        /**
         * @return Instance of {@link AsyncResult} with either data, or throwable. Never returns null.
         */
        @Override
        protected AsyncResult<RESULT> doInBackground(Void... params) {
            AsyncResult<RESULT> result;
            try {
                RESULT resultData = task.call();
                result = new AsyncResult<RESULT>(resultData);
            } catch (Exception e) {
                result = new AsyncResult<RESULT>(e);
            }

            return result;
        }

        @Override
        protected void onPostExecute(AsyncResult<RESULT> resultResult) {
            done.set(true);
            if (callback == null) {
                return;
            }
            Exception exception = resultResult.getException();
            if (exception != null) {
                callback.onFailure(exception);
            } else {
                callback.onSuccess(resultResult.getData());
            }
        }

        @Override
        public boolean isDone() {
            return done.get();
        }
    }

}
