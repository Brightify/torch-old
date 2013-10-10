package com.brightgestures.brightify.action.load;

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
import java.util.*;

public abstract class BaseLoader<E> implements Loader, ChildLoader {
    protected final Brightify mBrightify;
    protected final BaseLoader<E> mParentLoader;

    protected BaseLoader(BaseLoader<E> parentLoader) {
        this(parentLoader.mBrightify, parentLoader);
    }

    protected BaseLoader(Brightify brightify, BaseLoader<E> parentLoader) {
        mBrightify = brightify;
        mParentLoader = parentLoader;
    }

    @Override
    public BaseLoader<E> getParentLoader() {
        return mParentLoader;
    }

    public abstract void prepareQuery(LoadQuery<E> query);

    // TODO implement! https://code.google.com/p/objectify-appengine/source/browse/src/main/java/com/googlecode/objectify/impl/LoadTypeImpl.java

    public class LoadResult<E> implements Result<List<E>> {

        private final Key<E> mKey;

        public LoadResult(Key<E> key) {
            mKey = key;
        }

        @Override
        public List<E> now() {
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

                //db.query(distinct, table from type, columns, selectionstring, )

                cursor = db.query(metadata.getTableName(), columns, null, null, null, null, null);

                ArrayList<E> entities = new ArrayList<E>();

                if(cursor == null || !cursor.moveToFirst()) {
                    return entities;
                }

                do {
                    E entity = createFromCursor(metadata, cursor);

                    entities.add(entity);
                } while (cursor.moveToNext());

                return entities;
            } finally {
                if(cursor != null) {
                    cursor.close();
                }
                db.close();
            }
        }

        @SuppressWarnings("unchecked")
        private E createFromCursor(EntityMetadata<E> metadata, Cursor cursor) {
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
        }
    }

}
