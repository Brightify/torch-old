package com.brightgestures.brightify.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.BrightifyFactory;
import com.brightgestures.brightify.EntityMetadata;
import com.brightgestures.brightify.sql.statement.DropTable;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class MigrationAssistantImpl<ENTITY> implements MigrationAssistant<ENTITY> {

    private final BrightifyFactory brightifyFactory;
    private final EntityMetadata<ENTITY> entityMetadata;

    public MigrationAssistantImpl(BrightifyFactory brightifyFactory, EntityMetadata<ENTITY> entityMetadata) {
        this.brightifyFactory = brightifyFactory;
        this.entityMetadata = entityMetadata;
    }

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
            dropTable.setDatabaseName(brightifyFactory.getDatabaseName());
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
    }

    private SQLiteDatabase getDatabase() {
        return brightifyFactory.getDatabaseEngine().getDatabase();
    }
}
