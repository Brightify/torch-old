package com.brightgestures.brightify;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.brightgestures.brightify.util.Callback;

import java.util.LinkedList;


public class BrightifyService {
    private static final String TAG = BrightifyService.class.getSimpleName();

    private static BrightifyFactory sFactory;

    private static final ThreadLocal<LinkedList<Brightify>> STACK = new ThreadLocal<LinkedList<Brightify>>() {
        @Override
        protected LinkedList<Brightify> initialValue() {
            return new LinkedList<Brightify>();
        }
    };

    /**
     * Using this method the database will get opened (or created if it doesn't yet exist) in background.
     * In order to get all async callbacks delivered on UI Thread, you have to call this on UI Thread
     *
     * This overrides the {@link FactoryConfigurationImpl#IMMEDIATE_DATABASE_CREATION_PROPERTY} setting.
     * @param callback will be called when database is opened or on failure
     * @return
     */
    public static AsyncInitializer asyncInit(final Callback<Void> callback) {
        return new AsyncFactoryBuilder(new Callback<BrightifyFactory>() {
            @Override
            public void onSuccess(BrightifyFactory factory) {
                sFactory = factory;
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Could not initialize database in background!", e);
                callback.onFailure(e);
            }
        });
    }

    /**
     * Loads the BrightifyFactory with passed context. In order to get all async callbacks delivered on UI Thread,
     * you have to call this on UI Thread
     * @param context Any context you can provide, {@link android.content.Context#getApplicationContext()} will be used.
     * @return EntityRegistrar for {@link com.brightgestures.brightify.annotation.Entity} registration
     */
    public static BrightifyFactory with(Context context) {
        if(sFactory != null) {
            throw new IllegalStateException("Call BrightifyService#forceUnload if you want to reload BrightifyFactory!");
        }

        sFactory = new BrightifyFactoryImpl(context.getApplicationContext());

        return sFactory;
    }

    /**
     * Use this to force unload Brightify. Probably used in tests only.
     *
     * This will NOT delete the database. It will only unload the factory and unregister all the Entities.
     */
    public static void forceUnload() {
        sFactory.unload();

        STACK.get().clear();

        sFactory = null;
    }

    /**
     * Returns instance of Brightify which can be used to work on.
     * @return
     */
    public static Brightify bfy() {
        LinkedList<Brightify> stack = STACK.get();
        if(stack.isEmpty()) {
            stack.add(sFactory.begin());
        }

        return stack.getLast();
    }

    public static BrightifyFactory factory() {
        return sFactory;
    }

    public static boolean isLoaded() {
        return sFactory != null;
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
