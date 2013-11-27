package com.brightgestures.brightify;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.util.MigrationAssistant;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface EntityMetadata<ENTITY> {
    String getIdColumnName();

    String[] getColumns();

    String getTableName();

    String getVersion();

    Entity.MigrationType getMigrationType();

    Long getEntityId(ENTITY entity);

    void setEntityId(ENTITY entity, Long id);

    Class<ENTITY> getEntityClass();

    Key<ENTITY> createKey(ENTITY entity);

    void createTable(SQLiteDatabase db);

    ENTITY createFromCursor(Cursor cursor) throws Exception;

    ContentValues toContentValues(ENTITY entity) throws Exception;

    void migrate(MigrationAssistant<ENTITY> assistant, String sourceVersion, String targetVersion) throws Exception;
}
