package org.brightify.torch.android;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.brightify.torch.android.AndroidSQLiteEngine;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.filter.Column;
import org.brightify.torch.sql.statement.DropTable;
import org.brightify.torch.util.MigrationAssistant;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AndroidSQLiteMigrationAssistant<ENTITY> implements MigrationAssistant<ENTITY> {

    private final AndroidSQLiteEngine databaseEngine;
    private final EntityMetadata<ENTITY> entityMetadata;

    public AndroidSQLiteMigrationAssistant(AndroidSQLiteEngine databaseEngine, EntityMetadata<ENTITY> entityMetadata) {
        this.databaseEngine = databaseEngine;
        this.entityMetadata = entityMetadata;
    }

    @Override
    public void addColumn(Column<?> column) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void changeColumnType(Column<?> column, Class<?> from, Class<?> to) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void renameColumn(String from, String to) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void removeColumn(String name) {
        throw new UnsupportedOperationException("Not implemented!");
    }
/*
    @Override
    public void createTable() {
        if(!tableExists()) {
            entityMetadata.createTable(getDatabase());
        }
    }

    @Override
    public void dropTable() {
        if(tableExists()) {
            DropTable dropTable = new DropTable();
            dropTable.setTableName(entityMetadata.getTableName());
            dropTable.setDatabaseName(databaseEngine.getDatabaseName());
            dropTable.run(getDatabase());
        }
    }

    @Override
    public void dropCreateTable() {
        dropTable();
        createTable();
    }

    @Override
    public boolean tableExists() {
        Cursor cursor = getDatabase().rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = ?",
                                      new String[] { entityMetadata.getTableName() });
        if(cursor != null) {
            if(cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }*/

    private SQLiteDatabase getDatabase() {
        return databaseEngine.getDatabase();
    }
}
