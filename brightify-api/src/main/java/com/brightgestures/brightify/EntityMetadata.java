package com.brightgestures.brightify;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public abstract class EntityMetadata<ENTITY> {

    public EntityMetadata() {
    }

    public abstract String getIdColumnName();

    public abstract String[] getColumns();

    public abstract String getTableName();

    public abstract Long getEntityId(ENTITY entity);

    public abstract void setEntityId(ENTITY entity, Long id);

    public abstract Class<ENTITY> getEntityClass();

    public Key<ENTITY> createKey(ENTITY entity) {
        return Key.create(getEntityClass(), getEntityId(entity));
    }

    public abstract void createTable(SQLiteDatabase db);

    public abstract ENTITY createFromCursor(Cursor cursor) throws Exception;

    public abstract ContentValues toContentValues(ENTITY entity) throws Exception;
}
