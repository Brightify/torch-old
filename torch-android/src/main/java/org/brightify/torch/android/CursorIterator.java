package org.brightify.torch.android;

import android.database.Cursor;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.Torch;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
* @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
*/
public class CursorIterator<ENTITY> implements Iterator<ENTITY> {
    private final Torch torch;
    protected final EntityMetadata<ENTITY> entityMetadata;
    protected final Cursor cursor;
    protected boolean mFirst = true;
    protected boolean mHasNext = false;

    public CursorIterator(Torch torch, EntityMetadata<ENTITY> entityMetadata, Cursor cursor) {
        if(cursor.isClosed()) {
            throw new IllegalStateException("The cursor is closed!");
        }
        this.torch = torch;
        this.cursor = cursor;
        this.entityMetadata = entityMetadata;
        if(!this.cursor.moveToFirst()) {
            this.cursor.close();
        }
    }

    @Override
    public boolean hasNext() { // this goes forever when there is no data!
        return !cursor.isClosed();
    }

    @Override
    public ENTITY next() {
        if(cursor.isClosed()) {
            throw new NoSuchElementException("The cursor is closed!");
        }

        ENTITY entity;
        try {
            entity = entityMetadata.createFromCursor(torch, cursor);
        } catch (Exception e) {
            // FIXME handle the exception better
            throw new RuntimeException(e);
        }

        if(!cursor.moveToNext()) {
            cursor.close();
        }

        return entity;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not implemented and probably never will be!");
    }
}
