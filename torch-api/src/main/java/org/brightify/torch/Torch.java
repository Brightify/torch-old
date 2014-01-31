package org.brightify.torch;

import android.database.sqlite.SQLiteDatabase;
import org.brightify.torch.action.delete.Deleter;
import org.brightify.torch.action.load.Loader;
import org.brightify.torch.action.save.Saver;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface Torch {

    /**
     * @return A factory that created this instance of Torch
     */
    TorchFactory getFactory();

    /**
     * Shorthand for {@link DatabaseEngine#getDatabase()} source {@link TorchFactory#getDatabaseEngine()}.
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
