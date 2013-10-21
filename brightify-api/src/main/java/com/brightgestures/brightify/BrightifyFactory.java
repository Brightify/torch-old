package com.brightgestures.brightify;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import com.brightgestures.brightify.util.Callback;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface BrightifyFactory extends DatabaseEngine.OnCreateDatabaseListener {
    FactoryConfiguration getConfiguration();

    DatabaseEngine getDatabaseEngine();

    Brightify begin();

    void setHandler(Handler handler);

    <E> boolean asyncSuccessCallback(Callback<E> callback, E data);

    <E> boolean asyncFailedCallback(Callback<E> callback, Exception exception);

    boolean runOnUiThread(Runnable runnable);

    SQLiteDatabase forceOpenOrCreateDatabase();

    boolean deleteDatabase();

    public void unload();
}