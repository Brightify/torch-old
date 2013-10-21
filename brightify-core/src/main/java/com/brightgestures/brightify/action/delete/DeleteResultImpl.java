package com.brightgestures.brightify.action.delete;

import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.Entities;
import com.brightgestures.brightify.EntityMetadata;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.util.Callback;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class DeleteResultImpl<ENTITY> implements DeleteResult<ENTITY> {

    protected final Brightify mBrightify;
    protected final Collection<Key<ENTITY>> mEntityKeys;

    public DeleteResultImpl(Brightify brightify, Collection<Key<ENTITY>> entityKeys) {
        mBrightify = brightify;
        mEntityKeys = entityKeys;
    }

    @Override
    public Map<Key<ENTITY>, Boolean> now() {
        SQLiteDatabase db = mBrightify.getDatabase();

        db.beginTransaction();
        try {
            Map<Key<ENTITY>, Boolean> results = new HashMap<Key<ENTITY>, Boolean>();
            EntityMetadata<ENTITY> metadata = Entities.getMetadata(mEntityKeys.iterator().next().getType());
            for(Key<ENTITY> key : mEntityKeys) {
                int affected = db.delete(metadata.getTableName(), metadata.getIdColumnName() + " = ?", new String[] { String.valueOf(key.getId()) });
                if(affected > 1) {
                    throw new IllegalStateException("Delete command affected more than one row at once!");
                }

                results.put(key, affected == 1);
            }

            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void async(Callback<Map<Key<ENTITY>, Boolean>> callback) {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
