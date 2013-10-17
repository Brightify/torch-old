package com.brightgestures.brightify;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public abstract class EntityMetadata<ENTITY> {

    public EntityMetadata() {
    }

    public abstract ENTITY createFromCursor(Cursor cursor);

    public abstract ContentValues toContentValues(ENTITY entity);

}
