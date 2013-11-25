package com.brightgestures.brightify;

import android.content.Context;
import android.util.Log;
import com.brightgestures.brightify.util.Callback;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;


public class BrightifyService {
    private static final String TAG = BrightifyService.class.getSimpleName();

    private static BrightifyFactory factoryInstance;
    private static AsyncFactoryBuilder activeFactoryBuilder;

    private static final ThreadLocal<LinkedList<Brightify>> STACK = new ThreadLocal<LinkedList<Brightify>>() {
        @Override
        protected LinkedList<Brightify> initialValue() {
            return new LinkedList<>();
        }
    };

    /**
     * Using this method the database will get opened (or created if it doesn't yet exist) in background.
     * In order to get all async callbacks delivered on UI Thread, you have to call this on UI Thread
     * <p/>
     * This overrides the {@link com.brightgestures.brightify.FactoryConfigurationImpl#isImmediateDatabaseCreation()}
     * setting, making it true
     *
     * @param callback will be called when database is opened or on failure, on UI thread
     * @return Asynchronous initializer.
     */
    public static AsyncInitializer asyncInit(final Callback<Void> callback) {
        activeFactoryBuilder = new AsyncFactoryBuilder(new Callback<BrightifyFactory>() {
            @Override
            public void onSuccess(BrightifyFactory factory) {
                factoryInstance = factory;
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Could not initialize database in background!", e);
                callback.onFailure(e);
            }
        });

        return activeFactoryBuilder;
    }

    /**
     * Loads the BrightifyFactory with passed context. In order to get all async callbacks delivered on UI Thread,
     * you have to call this on UI Thread
     *
     * @param context Any context you can provide, {@link android.content.Context#getApplicationContext()} will be used.
     * @return EntityRegistrar for {@link com.brightgestures.brightify.annotation.Entity} registration
     */
    public static BrightifyFactory with(Context context) {
        if (factoryInstance != null) {
            throw new IllegalStateException("Call BrightifyService#forceUnload if you want to reload " +
                    "BrightifyFactory!");
        }

        factoryInstance = new BrightifyFactoryImpl(context.getApplicationContext());

        return factoryInstance;
    }

    /**
     * Use this to force unload Brightify. Probably used in tests only.
     * <p/>
     * This will NOT delete the database. It will only unload the factory and unregister all the Entities.
     */
    public static void forceUnload() {
        factoryInstance.unload();

        STACK.get().clear();

        factoryInstance = null;
    }

    /**
     * This is the main method that will initialize the Brightify.
     * <p/>
     * We recommend to static import this method, so that you can only call bfy() to begin.
     *
     * @return
     */
    public static Brightify bfy() {
        LinkedList<Brightify> stack = STACK.get();
        if (stack.isEmpty()) {
            stack.add(factoryInstance.begin());
        }

        return stack.getLast();
    }

    public static BrightifyFactory factory() {
        return factoryInstance;
    }

    public static boolean isLoaded() {
        return factoryInstance != null;
    }

    private static void push(Brightify db) {
        STACK.get().add(db);
    }

    private static void pop() {
        STACK.get().removeLast();
    }

    private static void reset() {
        STACK.get().clear();
    }


}
