package org.brightify.torch.android;

import android.database.Cursor;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.action.load.LoadQuery;
import org.brightify.torch.filter.ValueProperty;
import org.brightify.torch.util.ReadableRawContainerImpl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
* @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
*/
public class CursorIterator<ENTITY> implements Iterator<ENTITY> {
    private final EntityDescription<ENTITY> description;
    private final Cursor cursor;
    private final ReadableRawContainerImpl rawContainer;

    public CursorIterator(TorchFactory torchFactory, LoadQuery<ENTITY> query, Cursor cursor) {
        this.cursor = cursor;
        if(cursor.isClosed()) {
            throw new IllegalStateException("The cursor is closed!");
        }
        CursorReadableRawEntity cursorRawEntity = new CursorReadableRawEntity(cursor);
        this.rawContainer = new ReadableRawContainerImpl();
        this.rawContainer.setRawEntity(cursorRawEntity);
        this.description = query.getEntityDescription();
        if(!this.cursor.moveToFirst()) {
            this.cursor.close();
        }
    }

    public boolean close() {
        if(cursor.isClosed()) {
            return false;
        } else {
            cursor.close();
            return true;
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

        ENTITY entity = description.createEmpty();
        try {
            for (ValueProperty<ENTITY, ?> valueProperty : description.getValueProperties()) {
                rawContainer.setPropertyName(valueProperty.getSafeName());
                valueProperty.readFromRawContainer(rawContainer, entity);
            }
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
