package com.brightgestures.brightify.action.load;

import android.database.Cursor;
import com.brightgestures.brightify.EntityMetadataCompatibility;
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
    protected final EntityMetadataCompatibility<E> mEntityMetadata;
    protected final Cursor mCursor;
    protected boolean mFirst = true;
    protected boolean mHasNext = false;

    public CursorIterator(EntityMetadataCompatibility<E> entityMetadata, Cursor cursor) {
        if(cursor.isClosed()) {
            throw new IllegalStateException("The cursor is closed!");
        }
        mCursor = cursor;
        mEntityMetadata = entityMetadata;
        if(!mCursor.moveToFirst()) {
            mCursor.close();
        }
    }

    @Override
    public boolean hasNext() { // this goes forever when there is no data!
        return !mCursor.isClosed();
    }

    @Override
    public E next() {
        if(mCursor.isClosed()) {
            throw new IllegalStateException("The cursor is closed!");
        }

        E entity = createFromCursor();

        if(!mCursor.moveToNext()) {
            mCursor.close();
        }

        return entity;
    }

    @SuppressWarnings("unchecked")
    private E createFromCursor() {
        EntityMetadataCompatibility<E> metadata = mEntityMetadata;
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
