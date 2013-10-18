package com.brightgestures.brightify.action.delete;

import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.*;
import com.brightgestures.brightify.util.Callback;
import com.brightgestures.brightify.util.ResultWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class Deleter {

    protected Brightify mBrightify;

    public Deleter(Brightify brightify) {
        mBrightify = brightify;
    }

    public <ENTITY> TypedDeleter<ENTITY> type(Class<ENTITY> entityClass) {
        return new TypedDeleter<ENTITY>(entityClass);
    }

    public <ENTITY> Result<Boolean> key(Key<ENTITY> key) {
        Result<Map<Key<ENTITY>, Boolean>> base = keys(Collections.singleton(key));

        return new ResultWrapper<Map<Key<ENTITY>, Boolean>, Boolean>(base) {
            @Override
            protected Boolean wrap(Map<Key<ENTITY>, Boolean> original) {
                return original.values().iterator().next();
            }

            @Override
            public void async(Callback<Boolean> callback) {
                throw new UnsupportedOperationException("Not implemented!");
            }
        };
    }

    public <ENTITY> Result<Map<Key<ENTITY>, Boolean>> keys(Key<ENTITY>... keys) {
        return keys(Arrays.asList(keys));
    }

    public <ENTITY> Result<Map<Key<ENTITY>, Boolean>> keys(Iterable<Key<ENTITY>> keys) {
        return new DeleteKeyResult<ENTITY>(keys);
    }

    public <ENTITY> Result<Boolean> entity(ENTITY entity) {
        Result<Map<ENTITY, Boolean>> base = entities(Collections.singleton(entity));

        return new ResultWrapper<Map<ENTITY, Boolean>, Boolean>(base) {
            @Override
            protected Boolean wrap(Map<ENTITY, Boolean> original) {
                return original.values().iterator().next();
            }

            @Override
            public void async(Callback<Boolean> callback) {
                throw new UnsupportedOperationException("Not implemented!");
            }
        };
    }

    public <ENTITY> Result<Map<ENTITY, Boolean>> entities(ENTITY... entities) {
        return entities(Arrays.asList(entities));
    }

    public <ENTITY> Result<Map<ENTITY, Boolean>> entities(Iterable<ENTITY> entities) {
        return new DeleteEntityResult<ENTITY>(entities);
    }

    public class TypedDeleter<ENTITY> {
        private final Class<ENTITY> mEntityClass;

        public TypedDeleter(Class<ENTITY> entityClass) {
            mEntityClass = entityClass;
        }

        public Result<Boolean> id(long id) {
            return key(Key.create(mEntityClass, id));
        }

        public Result<Map<Key<ENTITY>, Boolean>> ids(Long... ids) {
            return ids(Arrays.asList(ids));
        }

        public Result<Map<Key<ENTITY>, Boolean>> ids(Iterable<Long> ids) {
            List<Key<ENTITY>> keys = new ArrayList<Key<ENTITY>>();
            for(Long id : ids) {
                if(id == null) {
                    throw new IllegalArgumentException("Id to delete cannot be null!");
                }

                keys.add(Key.create(mEntityClass, id));
            }

            return keys(keys);
        }
    }

    public class DeleteKeyResult<ENTITY> implements Result<Map<Key<ENTITY>, Boolean>> {
        private final Iterable<Key<ENTITY>> mKeys;

        public DeleteKeyResult(Iterable<Key<ENTITY>> keys) {
            mKeys = keys;
        }

        @Override
        public Map<Key<ENTITY>, Boolean> now() {
            SQLiteDatabase db = mBrightify.getDatabase();
            db.beginTransaction();
            try {
                Map<Key<ENTITY>, Boolean> results = new HashMap<Key<ENTITY>, Boolean>();

                for(Key<ENTITY> key : mKeys) {
                    EntityMetadataCompatibility<ENTITY> metadata = EntitiesCompatibility.getMetadata(key.getType());
                    long id = key.getId();

                    int affected = delete(db, metadata, id);

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

    public class DeleteEntityResult<ENTITY> implements Result<Map<ENTITY, Boolean>> {

        private final Iterable<ENTITY> mEntities;

        public DeleteEntityResult(Iterable<ENTITY> entities) {
            mEntities = entities;
        }

        @Override
        public Map<ENTITY, Boolean> now() {
            SQLiteDatabase db = mBrightify.getDatabase();
            db.beginTransaction();
            try {
                Map<ENTITY, Boolean> results = new HashMap<ENTITY, Boolean>();

                for(ENTITY entity : mEntities) {
                    EntityMetadataCompatibility<ENTITY> metadata = EntitiesCompatibility.getMetadata(entity);
                    Long id = metadata.getId(entity);

                    int affected = delete(db, metadata, id);

                    results.put(entity, affected == 1);
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

    private static <ENTITY> int delete(SQLiteDatabase db, EntityMetadataCompatibility<ENTITY> metadata, Long id) {
        if(id == null) {
            throw new IllegalArgumentException("Can't delete entity without an ID! Is this entity even stored?");
        }

        int affected = db.delete(metadata.getTableName(), metadata.getIdProperty().getColumnName() + " = ?",
                new String[] { String.valueOf(id) });

        if(affected > 1) {
            throw new IllegalStateException("Delete command affected more than one row!");
        }

        return affected;
    }


}
