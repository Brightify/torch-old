package com.brightgestures.brightify.action;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.Brightify;
import com.brightgestures.brightify.Entities;
import com.brightgestures.brightify.EntityMetadata;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Property;
import com.brightgestures.brightify.Result;
import com.brightgestures.brightify.util.Serializer;
import com.brightgestures.brightify.util.TypeUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Loader {

    private Brightify mBrightify;

    protected Set<Class<?>> mLoadGroups;

    public Loader(Brightify brightify) {
        mBrightify = brightify;
        mLoadGroups = Collections.emptySet();
    }

    public Loader group(Class<?>... groups) {
        mLoadGroups.addAll(Arrays.asList(groups));

        return this;
    }

    public <E> LoadType<E> type(Class<E> type) {
        return new LoadType<E>(this, type);
    }

    public <E> LoadResult<E> key(Key<E> key) {
        return new LoadResult<E>(key);
    }

    public <E> LoadResult<E> entity(E entity) {
        return key(Key.create(entity));
    }

    public <E> E now(Key<E> key) {
        return key(key).now();
    }

    public class LoadType<E> {
        private Loader mLoader;
        private Class<E> mType;

        public LoadType(Loader loader, Class<E> type) {
            mLoader = loader;
            mType = type;
        }


        public LoadType<E> filter(String condition, Object value) {
            // TODO implement
            return this;
        }

        public LoadResult<E> id(Long id) {
            return mLoader.key(Key.create(mType, id));
        }

        public Map<Long, E> ids(Long... ids) {
            for(Long id : ids) {

            }
            return null; // TODO implement! https://code.google.com/p/objectify-appengine/source/browse/src/main/java/com/googlecode/objectify/impl/LoadTypeImpl.java
        }
    }

    public class LoadResult<E> implements Result<E> {

        private final Key<E> mKey;

        public LoadResult(Key<E> key) {
            mKey = key;
        }

        @SuppressWarnings("unchecked")
        @Override
        public E now() {
            SQLiteDatabase db = mBrightify.getReadableDatabase();
            Cursor cursor = null;
            try {
                EntityMetadata<E> metadata = Entities.getMetadataByKey(mKey);

                int columnsCount = metadata.getProperties().size();

                String[] columns = new String[columnsCount];

                int i = 0;
                for(Property property : metadata.getProperties()) {
                    columns[i] = property.getColumnName();
                    i++;
                }

                db.

                cursor = db.query(metadata.getTableName(), columns, null, null, null, null, null);

                if(cursor == null || !cursor.moveToFirst()) {
                    return null;
                }

                E entity = (E) TypeUtils.construct(metadata.getEntityClass());

                for(Property property : metadata.getProperties()) {
                    int index = cursor.getColumnIndex(property.getColumnName());
                    Class<?> type = property.getType();
                    Object value = null;
                    if(TypeUtils.isAssignableFrom(Boolean.class, type)) {
                        value = cursor.getInt(index) > 0;
                    } else if(TypeUtils.isAssignableFrom(Byte.class, type)) {
                        value = (byte) cursor.getInt(index);
                    } else if(TypeUtils.isAssignableFrom(Short.class, type)) {
                        value = cursor.getShort(index);
                    } else if(TypeUtils.isAssignableFrom(Integer.class, type)) {
                        value = cursor.getInt(index);
                    } else if(TypeUtils.isAssignableFrom(Long.class, type)) {
                        value = cursor.getLong(index);
                    } else if(TypeUtils.isAssignableFrom(Float.class, type)) {
                        value = cursor.getFloat(index);
                    } else if(TypeUtils.isAssignableFrom(Double.class, type)) {
                        value = cursor.getDouble(index);
                    } else if(TypeUtils.isAssignableFrom(String.class, type)) {
                        value = cursor.getString(index);
                    } else if(TypeUtils.isAssignableFrom(byte[].class, type)) {
                        value = cursor.getBlob(index);
                    } else if(TypeUtils.isAssignableFrom(Serializable.class, type)) {
                        try {
                            value = Serializer.deserialize(cursor.getBlob(index));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        throw new IllegalStateException("Type '" + type.toString() + "' cannot be restored from database!");
                    }

                    property.set(entity, value);
                }

                return entity;
            } finally {
                if(cursor != null) {
                    cursor.close();
                }
                db.close();
            }
        }
    }

}
