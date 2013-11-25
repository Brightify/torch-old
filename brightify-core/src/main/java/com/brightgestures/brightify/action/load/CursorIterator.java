package com.brightgestures.brightify.action.load;

import android.database.Cursor;
import com.brightgestures.brightify.EntityMetadata;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Property;
import com.brightgestures.brightify.Ref;
import com.brightgestures.brightify.util.Serializer;
import com.brightgestures.brightify.util.TypeUtilsCompatibility;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
* @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
*/
public class CursorIterator<ENTITY> implements Iterator<ENTITY> {
    protected final EntityMetadata<ENTITY> mEntityMetadata;
    protected final Cursor mCursor;
    protected boolean mFirst = true;
    protected boolean mHasNext = false;

    public CursorIterator(EntityMetadata<ENTITY> entityMetadata, Cursor cursor) {
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
    public ENTITY next() {
        if(mCursor.isClosed()) {
            throw new NoSuchElementException("The cursor is closed!");
        }

        ENTITY entity;
        try {
            entity = mEntityMetadata.createFromCursor(mCursor);
        } catch (Exception e) {
            // FIXME handle the exception better
            throw new RuntimeException(e);
        }

        if(!mCursor.moveToNext()) {
            mCursor.close();
        }

        return entity;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not implemented and probably never will be!");
    }
}
