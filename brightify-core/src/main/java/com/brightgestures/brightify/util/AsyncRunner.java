package com.brightgestures.brightify.util;

import android.os.AsyncTask;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public abstract class AsyncRunner {

    public static <RESULT> void run(final Task<RESULT> task, final Callback<RESULT> callback) {
        new CallbackAsyncTask<RESULT>(task, callback).execute();
    }

    public interface Task<RESULT> {
        RESULT doWork() throws Exception;
    }

    private static class CallbackAsyncTask<RESULT> extends AsyncTask<Void, Exception, AsyncRunner.Result<RESULT>> {

        private final Task<RESULT> mTask;
        private final Callback<RESULT> mCallback;

        public CallbackAsyncTask(Task<RESULT> task, Callback<RESULT> callback) {
            mTask = task;
            mCallback = callback;
        }

        @Override
        protected Result<RESULT> doInBackground(Void... params) {
            Result<RESULT> result;
            try {
                RESULT resultData = mTask.doWork();
                result = new Result<RESULT>(resultData);
            } catch (Exception e) {
                result = new Result<RESULT>(e);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Result<RESULT> resultResult) {
            if(resultResult.mException != null) {
                mCallback.onFailure(resultResult.mException);
            } else {
                mCallback.onSuccess(resultResult.mData);
            }
        }
    }

    private static class Result<RESULT> {
        final RESULT mData;
        final Exception mException;

        public Result(RESULT data) {
            mData = data;
            mException = null;
        }

        public Result(Exception exception) {
            mException = exception;
            mData = null;
        }
    }
}
