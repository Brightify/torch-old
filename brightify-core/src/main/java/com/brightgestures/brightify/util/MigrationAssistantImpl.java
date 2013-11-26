package com.brightgestures.brightify.util;

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
        entityMetadata.createTable(getDatabase());
    }

    @Override
    public void dropTable() {
        DropTable dropTable = new DropTable();
        dropTable.setTableName(entityMetadata.getTableName());
        dropTable.setDatabaseName(brightifyFactory.getDatabaseName());
        dropTable.run(getDatabase());
    }

    @Override
    public void dropCreateTable() {
        dropTable();
        createTable();
    }

    private SQLiteDatabase getDatabase() {
        return brightifyFactory.getDatabaseEngine().getDatabase();
    }
}
