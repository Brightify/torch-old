package com.brightgestures.brightify;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.util.MigrationAssistant;

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

    public abstract int getVersion();

    public abstract Entity.MigrationType getMigrationType();

    public abstract Long getEntityId(ENTITY entity);

    public abstract void setEntityId(ENTITY entity, Long id);

    public abstract Class<ENTITY> getEntityClass();

    public abstract Key<ENTITY> createKey(ENTITY entity);

    public abstract void createTable(SQLiteDatabase db);

    public abstract ENTITY createFromCursor(Cursor cursor) throws Exception;

    public abstract ContentValues toContentValues(ENTITY entity) throws Exception;

    public abstract void migrate(MigrationAssistant<ENTITY> assistant, int sourceVersion, int targetVersion) throws Exception;
}
