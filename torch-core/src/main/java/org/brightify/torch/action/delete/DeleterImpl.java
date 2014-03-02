package org.brightify.torch.action.delete;

import android.database.sqlite.SQLiteDatabase;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Key;
import org.brightify.torch.Torch;
import org.brightify.torch.util.AsyncRunner;
import org.brightify.torch.util.Callback;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class DeleterImpl implements Deleter, AsyncDeleter {

    protected final Torch torch;

    public DeleterImpl(Torch torch) {
        this.torch = torch;
    }

    @Override
    public AsyncDeleter async() {
        return this;
    }

    @Override
    public <ENTITY> Boolean entity(ENTITY entity) {
        return entities(Collections.singleton(entity)).values().iterator().next();
    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, Boolean> entities(ENTITY... entities) {
        return entities(Arrays.asList(entities));
    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, Boolean> entities(Iterable<ENTITY> entities) {
        if (entities == null) {
            throw new IllegalArgumentException("Entities cannot be null!");
        }

        EntityMetadata<ENTITY> metadata = null;
        LinkedList<Key<ENTITY>> keys = new LinkedList<Key<ENTITY>>();
        for (ENTITY entity : entities) {
            if (metadata == null) {
                metadata = torch.getFactory().getEntities().getMetadata((Class<ENTITY>) entity.getClass());
            }
            keys.addLast(metadata.createKey(entity));
        }

        return keys(keys);
    }

    @Override
    public <ENTITY> Boolean key(Key<ENTITY> key) {
        return keys(Collections.singleton(key)).values().iterator().next();
    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, Boolean> keys(Key<ENTITY>... keys) {
        return keys(Arrays.asList(keys));
    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, Boolean> keys(Iterable<Key<ENTITY>> keys) {
        if (keys == null) {
            throw new IllegalArgumentException("Keys cannot be null!");
        }

        SQLiteDatabase db = torch.getDatabase();

        db.beginTransaction();
        try {
            Map<Key<ENTITY>, Boolean> results = new HashMap<Key<ENTITY>, Boolean>();
            EntityMetadata<ENTITY> metadata = null;
            for (Key<ENTITY> key : keys) {
                if (metadata == null) {
                    metadata = torch.getFactory().getEntities().getMetadata(key.getType());
                }

                int affected = db.delete(metadata.getTableName(), metadata.getIdColumn().getName() + " = ?",
                                         new String[] { String.valueOf(key.getId()) });
                if (affected > 1) {
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
    public <ENTITY> void entity(Callback<Boolean> callback, final ENTITY entity) {
        AsyncRunner.run(callback, new AsyncRunner.Task<Boolean>() {
            @Override
            public Boolean doWork() throws Exception {
                return entity(entity);
            }
        });
    }

    @Override
    public <ENTITY> void entities(Callback<Map<Key<ENTITY>, Boolean>> callback, final ENTITY... entities) {
        AsyncRunner.run(callback, new AsyncRunner.Task<Map<Key<ENTITY>, Boolean>>() {
            @Override
            public Map<Key<ENTITY>, Boolean> doWork() throws Exception {
                return entities(entities);
            }
        });
    }

    @Override
    public <ENTITY> void entities(Callback<Map<Key<ENTITY>, Boolean>> callback, final Iterable<ENTITY> entities) {
        AsyncRunner.run(callback, new AsyncRunner.Task<Map<Key<ENTITY>, Boolean>>() {
            @Override
            public Map<Key<ENTITY>, Boolean> doWork() throws Exception {
                return entities(entities);
            }
        });
    }

    @Override
    public <ENTITY> void key(Callback<Boolean> callback, final Key<ENTITY> key) {
        AsyncRunner.run(callback, new AsyncRunner.Task<Boolean>() {
            @Override
            public Boolean doWork() throws Exception {
                return key(key);
            }
        });
    }

    @Override
    public <ENTITY> void keys(Callback<Map<Key<ENTITY>, Boolean>> callback, final Key<ENTITY>... keys) {
        AsyncRunner.run(callback, new AsyncRunner.Task<Map<Key<ENTITY>, Boolean>>() {
            @Override
            public Map<Key<ENTITY>, Boolean> doWork() throws Exception {
                return keys(keys);
            }
        });
    }

    @Override
    public <ENTITY> void keys(Callback<Map<Key<ENTITY>, Boolean>> callback, final Iterable<Key<ENTITY>> keys) {
        AsyncRunner.run(callback, new AsyncRunner.Task<Map<Key<ENTITY>, Boolean>>() {
            @Override
            public Map<Key<ENTITY>, Boolean> doWork() throws Exception {
                return keys(keys);
            }
        });
    }
}
