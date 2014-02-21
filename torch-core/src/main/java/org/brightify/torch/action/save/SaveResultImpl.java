package org.brightify.torch.action.save;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import org.brightify.torch.Torch;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Key;
import org.brightify.torch.KeyFactory;
import org.brightify.torch.util.AsyncRunner;
import org.brightify.torch.util.Callback;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class SaveResultImpl<ENTITY> implements SaveResult<ENTITY> {

    private final Torch torch;
    private final Iterable<ENTITY> entities;

    public SaveResultImpl(Torch torch, Iterable<ENTITY> entities) {
        this.torch = torch;
        this.entities = entities;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Key<ENTITY>, ENTITY> now() {
        SQLiteDatabase db = torch.getDatabase();
        db.beginTransaction();
        try {
            Map<Key<ENTITY>, ENTITY> results = new HashMap<Key<ENTITY>, ENTITY>();
            Class<ENTITY> entityClass = (Class<ENTITY>) entities.iterator().next().getClass();
            EntityMetadata<ENTITY> metadata = torch.getFactory().getEntities().getMetadata(entityClass);
            if(metadata == null) {
                throw new IllegalStateException("Entity not registered!");
            }

            for(ENTITY entity : entities) {
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
        AsyncRunner.run(new AsyncRunner.Task<Map<Key<ENTITY>, ENTITY>>() {
            @Override
            public Map<Key<ENTITY>, ENTITY> doWork() throws Exception {
                return now();
            }
        }, callback);
    }
}
