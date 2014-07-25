package org.brightify.torch.android;

import android.database.Cursor;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.action.load.LoadQuery;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
* @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
*/
public class CursorIterator<ENTITY> implements Iterator<ENTITY> {
    private final TorchFactory torchFactory;
    private final LoadQuery<ENTITY> query;
    private final Cursor cursor;
    private final CursorReadableRawEntity cursorRawEntity;

    public CursorIterator(TorchFactory torchFactory, LoadQuery<ENTITY> query, Cursor cursor) {
        this.cursor = cursor;
        this.cursorRawEntity = new CursorReadableRawEntity(cursor);
        if(cursor.isClosed()) {
            throw new IllegalStateException("The cursor is closed!");
        }
        this.torchFactory = torchFactory;
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
            entity = query.getEntityDescription().createFromRawEntity(torchFactory, cursorRawEntity, query.getLoadGroups());
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
