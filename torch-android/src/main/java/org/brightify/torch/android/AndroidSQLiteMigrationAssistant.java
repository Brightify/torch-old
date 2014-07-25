package org.brightify.torch.android;

import android.database.sqlite.SQLiteDatabase;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.filter.Property;
import org.brightify.torch.sql.statement.DropTable;
import org.brightify.torch.util.MigrationAssistant;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AndroidSQLiteMigrationAssistant<ENTITY> implements MigrationAssistant<ENTITY> {

    private final AndroidSQLiteEngine databaseEngine;
    private final EntityDescription<ENTITY> entityDescription;

    public AndroidSQLiteMigrationAssistant(AndroidSQLiteEngine databaseEngine, EntityDescription<ENTITY> entityDescription) {
        this.databaseEngine = databaseEngine;
        this.entityDescription = entityDescription;
    }

    @Override
    public void addProperty(Property<?> property) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void changePropertyType(Property<?> property, Class<?> from, Class<?> to) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void renameProperty(String from, String to) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void removeProperty(String name) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void createStore() {
        if(!storeExists()) {
            databaseEngine.createTableIfNotExists(entityDescription);
        }
    }

    @Override
    public void deleteStore() {
        databaseEngine.dropTableIfExists(entityDescription);
        DropTable dropTable = new DropTable();
        dropTable.setTableName(entityDescription.getSafeName());
        dropTable.setDatabaseName(databaseEngine.getDatabaseName());
        dropTable.run(getDatabase());

    }

    @Override
    public void recreateStore() {
        deleteStore();
        createStore();
    }

    @Override
    public boolean storeExists() {
        return databaseEngine.tableExists(entityDescription);
    }

    private SQLiteDatabase getDatabase() {
        return databaseEngine.getDatabase();
    }
}
