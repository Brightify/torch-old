package com.brightgestures.brightify.action.save;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.Entities;
import com.brightgestures.brightify.EntityMetadata;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Property;
import com.brightgestures.brightify.Ref;
import com.brightgestures.brightify.Result;
import com.brightgestures.brightify.util.Callback;
import com.brightgestures.brightify.util.ResultWrapper;
import com.brightgestures.brightify.util.Serializer;
import com.brightgestures.brightify.util.TypeUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.brightgestures.brightify.BrightifyService.bfy;

public class Saver {

    protected Brightify mBrightify;

    public Saver(Brightify brightify) {
        mBrightify = brightify;
    }

    public <E> Result<Map<Key<E>, E>> entities(E... entities) {
        return entities(Arrays.asList(entities));
    }

    public <E> Result<Key<E>> entity(E entity) {
        Result<Map<Key<E>, E>> base = entities(Collections.singleton(entity));

        return new ResultWrapper<Map<Key<E>, E>, Key<E>>(base) {
            @Override
            protected Key<E> wrap(Map<Key<E>, E> original) {
                return original.keySet().iterator().next();
            }

            @Override
            public void async(Callback<Key<E>> callback) {
                throw new UnsupportedOperationException("Not implemented!");
            }
        };
    }

    public <E> Result<Map<Key<E>, E>> entities(Iterable<E> entities) {

        return new SaveResult<E>(entities);
    }

    public class SaveResult<E> implements Result<Map<Key<E>, E>> {

        private final Iterable<E> mEntities;

        public SaveResult(Iterable<E> entities) {
            mEntities = entities;
        }

        @Override
        public void async(Callback<Map<Key<E>, E>> callback) {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public final Map<Key<E>, E> now() {
            SQLiteDatabase db = mBrightify.getDatabase();
            db.beginTransaction();

            try {
                Map<Key<E>, E> results = new HashMap<Key<E>, E>();

                for(E entity : mEntities) {
                    Key<E> key = addEntityToTransaction(db, entity);

                    results.put(key, entity);

                }

                db.setTransactionSuccessful();
                return results;
            } finally {
                db.endTransaction();
            }
        }

        // public final void async(Callback<E> callback) { }

        private Key<E> addEntityToTransaction(SQLiteDatabase db, E entity) {
            EntityMetadata<E> metadata = Entities.getMetadata(entity);

            ContentValues values = new ContentValues();

            for(Property property : metadata.getProperties()) {
                String key = property.getColumnName();
                Object value = property.get(entity);
                Class<?> type = property.getType();

                if(TypeUtils.isAssignableFrom(Boolean.class, type)) {
                    values.put(key, (Boolean) value);
                } else if(TypeUtils.isAssignableFrom(Byte.class, type)) {
                    values.put(key, (Byte) value);
                } else if(TypeUtils.isAssignableFrom(Short.class, type)) {
                    values.put(key, (Short) value);
                } else if(TypeUtils.isAssignableFrom(Integer.class, type)) {
                    values.put(key, (Integer) value);
                } else if(TypeUtils.isAssignableFrom(Long.class, type)) {
                    values.put(key, (Long) value);
                } else if(TypeUtils.isAssignableFrom(Float.class, type)) {
                    values.put(key, (Float) value);
                } else if(TypeUtils.isAssignableFrom(Double.class, type)) {
                    values.put(key, (Double) value);
                } else if(TypeUtils.isAssignableFrom(String.class, type)) {
                    values.put(key, (String) value);
                } else if(TypeUtils.isAssignableFrom(byte[].class, type)) {
                    values.put(key, (byte[]) value);
                } else if(TypeUtils.isAssignableFrom(Key.class, type)) {
                    throw new UnsupportedOperationException("Not implemented!");
                } else if(TypeUtils.isAssignableFrom(Ref.class, type)) {
                    throw new UnsupportedOperationException("Not implemented!");
                } else if(TypeUtils.isAssignableFrom(Serializable.class, type)) {
                    try {
                        values.put(key, Serializer.serialize(value));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new IllegalStateException("Type '" + type.toString() + "' cannot be stored into database!");
                }
            }

            Long id = db.replaceOrThrow(metadata.getTableName(), null, values);

            if(id == -1) {
                throw new IllegalStateException("Error when inserting into database!");
            }

            metadata.getIdProperty().set(entity, id);

            return metadata.getKey(entity);
        }
    }

}
