package com.brightgestures.brightify.action.delete;

import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.*;
import com.brightgestures.brightify.util.Callback;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class Deleter {

    protected Brightify mBrightify;

    public Deleter(Brightify brightify) {
        mBrightify = brightify;
    }

    public class DeleteResult<ENTITY> implements Result<Map<Key<ENTITY>, Boolean>> {

        private final Iterable<ENTITY> mEntities;

        public DeleteResult(Iterable<ENTITY> entities) {
            mEntities = entities;
        }

        @Override
        public Map<Key<ENTITY>, Boolean> now() {
            SQLiteDatabase db = mBrightify.getDatabase();
            db.beginTransaction();
            try {
                Map<Key<ENTITY>, Boolean> results = new HashMap<Key<ENTITY>, Boolean>();

                for(ENTITY entity : mEntities) {
                    EntityMetadata<ENTITY> metadata = Entities.getMetadata(entity);

                    Long id = metadata.getId(entity);

                    if(id == null) {
                        throw new IllegalArgumentException("Can't delete entity without an ID! Is this entity even stored?");
                    }

                    Key<ENTITY> key = metadata.getKey(entity);

                    int affected = db.delete(metadata.getTableName(), metadata.getIdProperty().getColumnName() + " = ?",
                            new String[] { String.valueOf(id) });

                    if(affected > 1) {
                        throw new IllegalStateException("Delete command affected more than one row!");
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
        public void async(Callback callback) {
            throw new UnsupportedOperationException("Not implemented!");
        }
    }


}
