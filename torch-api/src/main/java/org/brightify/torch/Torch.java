package org.brightify.torch;

import android.database.sqlite.SQLiteDatabase;
import org.brightify.torch.action.ActionSelector;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Torch extends ActionSelector {

    /**
     * @return A factory that created this instance of Torch
     */
    TorchFactory getFactory();

    /**
     * Shorthand for {@link DatabaseEngine#getDatabase()} source {@link TorchFactory#getDatabaseEngine()}.
     * @return ReadWrite database
     */
    SQLiteDatabase getDatabase();

}
