package org.brightify.torch;

import android.util.Log;
import org.brightify.torch.util.Callback;
import org.brightify.torch.util.Validate;

import java.util.LinkedList;


public class TorchService {
    private static final String TAG = TorchService.class.getSimpleName();

    private static TorchFactory factoryInstance;
    private static AsyncFactoryBuilder activeFactoryBuilder;

    private static final ThreadLocal<LinkedList<Torch>> STACK = new ThreadLocal<LinkedList<Torch>>() {
        @Override
        protected LinkedList<Torch> initialValue() {
            return new LinkedList<Torch>();
        }
    };

    /**
     * Using this method the database will get opened (or created if it doesn't yet exist) in background.
     * In order to get all async callbacks delivered on UI Thread, you have to call this on UI Thread.
     *
     * @param callback will be called when database is opened or on failure, on UI thread
     * @return Asynchronous initializer.
     */
    public static AsyncInitializer asyncInit(final Callback<Void> callback) {
        activeFactoryBuilder = new AsyncFactoryBuilder(new Callback<TorchFactory>() {
            @Override
            public void onSuccess(TorchFactory factory) {
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
     * Loads the TorchFactory with passed context. In order to get all async callbacks delivered on UI Thread,
     * you have to call this on UI Thread
     *
     * @param engine A database engine to be used in the factory.
     * @return EntityRegistrar for {@link org.brightify.torch.annotation.Entity} registration
     */
    public static TorchFactory with(DatabaseEngine engine) {
        Validate.isNull(factoryInstance, "Call TorchService#forceUnload if you want to reload TorchFactory!");

        factoryInstance = new TorchFactoryImpl(engine);

        return factoryInstance;
    }

    /**
     * Use this to force unload Torch. Probably used in tests only.
     * <p/>
     * This will NOT delete the database. It will only unload the factory and unregister all the Entities.
     */
    public static void forceUnload() {
        factoryInstance.unload();

        STACK.get().clear();

        factoryInstance = null;
    }

    /**
     * This is the main method that will initialize the Torch.
     * <p/>
     * We recommend to static import this method, so that you can only call torch() to begin.
     *
     * @return
     */
    public static Torch torch() {
        if(!isLoaded()) {
            throw new IllegalStateException("Factory is not loaded!");
        }

        LinkedList<Torch> stack = STACK.get();
        if (stack.isEmpty()) {
            stack.add(factoryInstance.begin());
        }

        return stack.getLast();
    }

    public static TorchFactory factory() {
        return factoryInstance;
    }

    public static boolean isLoaded() {
        return factoryInstance != null;
    }

    private static void push(Torch db) {
        STACK.get().add(db);
    }

    private static void pop() {
        STACK.get().removeLast();
    }

    private static void reset() {
        STACK.get().clear();
    }


}
