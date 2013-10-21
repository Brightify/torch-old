package com.brightgestures.brightify;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import com.brightgestures.brightify.util.Callback;

import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class AsyncFactoryBuilder implements AsyncInitializer, AsyncEntityRegistrar, AsyncSubmit {

    private Callback<BrightifyFactory> mCallback;
    private Context mContext;
    private Handler mHandler;

    AsyncFactoryBuilder(Callback<BrightifyFactory> callback) {
        mCallback = callback;
        mHandler = new Handler();
    }

    @Override
    public AsyncFactoryBuilder with(Context context) {
        mContext = context.getApplicationContext();
        return this;
    }

    @Override
    public <T extends AsyncEntityRegistrar & AsyncSubmit, ENTITY> T register(Class<ENTITY> entityClass) {
        EntitiesCompatibility.register(entityClass);
        return (T) this;
    }

    @Override
    public void submit() {
        Executors.newSingleThreadExecutor().execute(
            new Runnable() {
                @Override
                public void run() {
                    BrightifyFactory factory = new BrightifyFactoryImpl(mContext);
                    factory.setHandler(mHandler);
                    try {
                        factory.forceOpenOrCreateDatabase();

                        if(!factory.asyncSuccessCallback(mCallback, factory)) {
                            mCallback.onSuccess(factory);
                        }
                    } catch (SQLiteException e) {
                        if(!factory.asyncFailedCallback(mCallback, e)) {
                            mCallback.onFailure(e);
                        }
                    }
                }
            });


    }
}
