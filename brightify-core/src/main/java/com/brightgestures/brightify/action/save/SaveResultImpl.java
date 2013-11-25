package com.brightgestures.brightify.action.save;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.Entities;
import com.brightgestures.brightify.EntityMetadata;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.KeyFactory;
import com.brightgestures.brightify.util.Callback;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class SaveResultImpl<ENTITY> implements SaveResult<ENTITY> {

    protected final Brightify mBrightify;
    protected final Iterable<ENTITY> mEntities;

    public SaveResultImpl(Brightify brightify, Iterable<ENTITY> entities) {
        mBrightify = brightify;
        mEntities = entities;
    }

    @Override
    public Map<Key<ENTITY>, ENTITY> now() {
        SQLiteDatabase db = mBrightify.getDatabase();
        db.beginTransaction();
        try {
            Map<Key<ENTITY>, ENTITY> results = new HashMap<Key<ENTITY>, ENTITY>();
            Class<ENTITY> entityClass = (Class<ENTITY>) mEntities.iterator().next().getClass();
            EntityMetadata<ENTITY> metadata = mBrightify.getFactory().getEntities().getMetadata(entityClass);
            if(metadata == null) {
                throw new IllegalStateException("Entity not registered!");
            }

            for(ENTITY entity : mEntities) {
                ContentValues values;
                try {
                    values = metadata.toContentValues(entity);
                } catch(Exception e) {
                    // FIXME handle the exception better
                    throw new RuntimeException(e);
                }
                long id = db.replaceOrThrow(metadata.getTableName(), null, values);
                if(id == -1) {
                    throw new IllegalStateException("Error when storing data into database!");
                }

                metadata.setEntityId(entity, id);

                Key<ENTITY> key = KeyFactory.create(entityClass, id);

                results.put(key, entity);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void async(Callback<Map<Key<ENTITY>, ENTITY>> callback) {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
