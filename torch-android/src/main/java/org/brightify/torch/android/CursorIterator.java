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
    private final LoadQuery<ENTITY> query;
    private final Cursor cursor;

    public CursorIterator(Torch torch, LoadQuery<ENTITY> query) {
        cursor = query.run(torch);
        if(cursor.isClosed()) {
            throw new IllegalStateException("The cursor is closed!");
        }
        this.torch = torch;
        this.query = query;
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
            entity = query.getEntityMetadata().createFromCursor(torch, cursor, query.getLoadGroups());
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
