package com.brightgestures.brightify.action.load;

import android.database.Cursor;
import com.brightgestures.brightify.EntityMetadata;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Property;
import com.brightgestures.brightify.Ref;
import com.brightgestures.brightify.util.Serializer;
import com.brightgestures.brightify.util.TypeUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public class CursorIterator<E> implements Iterator<E> {
    protected final EntityMetadata<E> mEntityMetadata;
    protected final Cursor mCursor;

    public CursorIterator(EntityMetadata<E> entityMetadata, Cursor cursor) {
        mCursor = cursor;
        mEntityMetadata = entityMetadata;
    }

    @Override
    public boolean hasNext() {
        return !mCursor.isLast();
    }

    @Override
    public E next() {
        if(!mCursor.moveToNext()) {
            return null;
        }

        return createFromCursor();
    }

    @SuppressWarnings("unchecked")
    private E createFromCursor() {
        EntityMetadata<E> metadata = mEntityMetadata;
        Cursor cursor = mCursor;

        E entity = TypeUtils.construct(metadata.getEntityClass());

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
            }  else if(TypeUtils.isAssignableFrom(Key.class, type)) {
                throw new UnsupportedOperationException("Not implemented!");
            } else if(TypeUtils.isAssignableFrom(Ref.class, type)) {
                throw new UnsupportedOperationException("Not implemented!");
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

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not implemented and never will be!");
    }
}
