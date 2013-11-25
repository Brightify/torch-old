package com.brightgestures.brightify;

import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.action.delete.Deleter;
import com.brightgestures.brightify.action.load.Loader;
import com.brightgestures.brightify.action.save.Saver;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface Brightify {

    /**
     * @return A factory that created this instance of Brightify
     */
    BrightifyFactory getFactory();

    /**
     * Shorthand for {@link com.brightgestures.brightify.DatabaseEngine#getDatabase()} source {@link com.brightgestures.brightify.BrightifyFactory#getDatabaseEngine()}.
     * @return ReadWrite database
     */
    SQLiteDatabase getDatabase();

    /**
     * Initialize deletion.
     * @return
     */
    Deleter delete();

    Loader load();

    Saver save();
}
