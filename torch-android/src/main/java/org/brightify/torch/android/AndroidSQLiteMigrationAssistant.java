package org.brightify.torch.android;

import android.database.sqlite.SQLiteDatabase;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.Torch;
import org.brightify.torch.filter.Property;
import org.brightify.torch.util.MigrationAssistant;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class AndroidSQLiteMigrationAssistant<ENTITY> implements MigrationAssistant<ENTITY> {

    private final AndroidSQLiteEngine databaseEngine;
    private final EntityDescription<ENTITY> entityDescription;

    public AndroidSQLiteMigrationAssistant(AndroidSQLiteEngine databaseEngine,
                                           EntityDescription<ENTITY> entityDescription) {
        this.databaseEngine = databaseEngine;
        this.entityDescription = entityDescription;
    }

    @Override
    public void addProperty(Property<ENTITY, ?> property) {
        databaseEngine.addColumn(entityDescription, property);
    }

    @Override
    public void renameProperty(String from, String to) {
        databaseEngine.renameColumn(entityDescription, from, to);
    }

    @Override
    public void removeProperty(String name) {
        databaseEngine.removeColumn(entityDescription, name);
    }

    @Override
    public void createStore() {
        databaseEngine.createTableIfNotExists(entityDescription);
    }

    @Override
    public void deleteStore() {
        databaseEngine.dropTableIfExists(entityDescription);
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

    @Override
    public Torch torch() {
        return databaseEngine.getTorchFactory().begin();
    }

    private SQLiteDatabase getDatabase() {
        return databaseEngine.getDatabase();
    }
}
