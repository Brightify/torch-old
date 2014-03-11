package org.brightify.torch.util;

import android.os.AsyncTask;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AsyncRunner {

    private AsyncRunner() {
    }

    public static <RESULT> void run(final Callback<RESULT> callback, final Task<RESULT> task) {
        new CallbackAsyncTask<RESULT>(callback, task).execute();
    }

    public interface Task<RESULT> {
        RESULT doWork() throws Exception;
    }

    private static class CallbackAsyncTask<RESULT> extends AsyncTask<Void, Exception, CallbackAsyncTask.Result<RESULT>> {

        private final Task<RESULT> task;
        private final Callback<RESULT> callback;

        public CallbackAsyncTask(Callback<RESULT> callback, Task<RESULT> task) {
            this.task = task;
            this.callback = callback;
        }

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
            if(resultResult.exception != null) {
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
        }
    }
}
