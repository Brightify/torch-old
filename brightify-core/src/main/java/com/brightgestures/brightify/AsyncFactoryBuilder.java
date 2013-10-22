package com.brightgestures.brightify;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import com.brightgestures.brightify.util.AsyncRunner;
import com.brightgestures.brightify.util.Callback;

import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class AsyncFactoryBuilder implements AsyncInitializer, AsyncEntityRegistrar, AsyncSubmit,

        AsyncEntityRegistrarSubmit {

    private Callback<BrightifyFactory> mCallback;
    private Context mContext;

    AsyncFactoryBuilder(Callback<BrightifyFactory> callback) {
        mCallback = callback;
    }

    @Override
    public AsyncEntityRegistrarSubmit with(Context context) {
        mContext = context.getApplicationContext();
        return this;
    }

    @Override
    public <ENTITY> AsyncEntityRegistrarSubmit register(Class<ENTITY> entityClass) {
        Entities.findAndRegisterMetadata(entityClass);
        return this;
    }

    @Override
    public <ENTITY> AsyncEntityRegistrarSubmit register(EntityMetadata<ENTITY> metadata) {
        Entities.registerMetadata(metadata);
        return this;
    }

    @Override
    public void submit() {
        AsyncRunner.run(new AsyncRunner.Task<BrightifyFactory>() {
            @Override
            public BrightifyFactory doWork() throws Exception {
                BrightifyFactory factory = new BrightifyFactoryImpl(mContext);

                factory.forceOpenOrCreateDatabase();

                return factory;
            }
        }, mCallback);
    }
}
