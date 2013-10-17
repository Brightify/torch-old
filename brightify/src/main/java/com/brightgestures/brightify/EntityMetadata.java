package com.brightgestures.brightify;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public abstract class EntityMetadata<ENTITY> {

    public EntityMetadata() {
    }

    public abstract void createTable(SQLiteDatabase db);

    public abstract ENTITY createFromCursor(Cursor cursor);

    public abstract ContentValues toContentValues(ENTITY entity);

}
