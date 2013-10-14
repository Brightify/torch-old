package com.brightgestures.brightify.action.load;

import android.os.Handler;
import com.brightgestures.brightify.util.Callback;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class LoaderHelper {

    protected final Handler mHandler;

    public LoaderHelper() {
        mHandler = new Handler();
    }

    public <E> void asyncSuccessCallback(final Callback<E> callback, final E data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(data);
            }
        });
    }

    public <E> void asyncFailedCallback(final Callback<E> callback, final Exception exception) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(exception);
            }
        });
    }

    public void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }
}
