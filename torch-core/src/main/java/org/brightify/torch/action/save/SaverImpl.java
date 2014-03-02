package org.brightify.torch.action.save;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Key;
import org.brightify.torch.KeyFactory;
import org.brightify.torch.Torch;
import org.brightify.torch.util.AsyncRunner;
import org.brightify.torch.util.Callback;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SaverImpl implements Saver, AsyncSaver {

    private final Torch torch;

    public SaverImpl(Torch torch) {
        this.torch = torch;
    }

    @Override
    public AsyncSaver async() {
        return this;
    }

    @Override
    public <ENTITY> Key<ENTITY> entity(ENTITY entity) {
        return entities(Collections.singleton(entity)).keySet().iterator().next();
    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, ENTITY> entities(ENTITY... entities) {
        return entities(Arrays.asList(entities));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <ENTITY> Map<Key<ENTITY>, ENTITY> entities(Iterable<ENTITY> entities) {
        SQLiteDatabase db = torch.getDatabase();
        db.beginTransaction();
        try {
            Map<Key<ENTITY>, ENTITY> results = new HashMap<Key<ENTITY>, ENTITY>();
            Class<ENTITY> entityClass = (Class<ENTITY>) entities.iterator().next().getClass();
            EntityMetadata<ENTITY> metadata = torch.getFactory().getEntities().getMetadata(entityClass);
            if (metadata == null) {
                throw new IllegalStateException("Entity not registered!");
            }

            for (ENTITY entity : entities) {
                ContentValues values;
                try {
                    values = metadata.toContentValues(entity);
                } catch (Exception e) {
                    // FIXME handle the exception better
                    throw new RuntimeException(e);
                }
                long id = db.replaceOrThrow(metadata.getTableName(), null, values);
                if (id == -1) {
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
    public <ENTITY> void entity(Callback<Key<ENTITY>> callback, final ENTITY entity) {
        AsyncRunner.run(callback, new AsyncRunner.Task<Key<ENTITY>>() {
            @Override
            public Key<ENTITY> doWork() throws Exception {
                return entity(entity);
            }
        });
    }

    @Override
    public <ENTITY> void entities(Callback<Map<Key<ENTITY>, ENTITY>> callback, final ENTITY... entities) {
        AsyncRunner.run(callback, new AsyncRunner.Task<Map<Key<ENTITY>, ENTITY>>() {
            @Override
            public Map<Key<ENTITY>, ENTITY> doWork() throws Exception {
                return entities(entities);
            }
        });
    }

    @Override
    public <ENTITY> void entities(Callback<Map<Key<ENTITY>, ENTITY>> callback, final Iterable<ENTITY> entities) {
        AsyncRunner.run(callback, new AsyncRunner.Task<Map<Key<ENTITY>, ENTITY>>() {
            @Override
            public Map<Key<ENTITY>, ENTITY> doWork() throws Exception {
                return entities(entities);
            }
        });
    }
}
