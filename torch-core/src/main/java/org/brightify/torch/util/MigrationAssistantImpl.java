package org.brightify.torch.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.EntityMetadata;
import org.brightify.torch.sql.statement.DropTable;
import org.brightify.torch.util.MigrationAssistant;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class MigrationAssistantImpl<ENTITY> implements MigrationAssistant<ENTITY> {

    private final TorchFactory torchFactory;
    private final EntityMetadata<ENTITY> entityMetadata;

    public MigrationAssistantImpl(TorchFactory torchFactory, EntityMetadata<ENTITY> entityMetadata) {
        this.torchFactory = torchFactory;
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
            dropTable.setDatabaseName(torchFactory.getDatabaseName());
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
        return torchFactory.getDatabaseEngine().getDatabase();
    }
}
