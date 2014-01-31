package org.brightify.torch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.model.Table;
import org.brightify.torch.model.TableDetails;
import org.brightify.torch.model.TableDetailsMetadata;
import org.brightify.torch.model.TableMetadata;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.MigrationAssistantImpl;

import java.util.Set;

public class TorchFactoryImpl implements TorchFactory {
    protected final Context context;
    protected final DatabaseEngine databaseEngine;

    protected final TableMetadata tableMetadata = TableMetadata.create();
    protected final TableDetailsMetadata tableDetailsMetadata = TableDetailsMetadata.create();
    protected final EntitiesImpl entities = new EntitiesImpl();

    protected final boolean initialized;

    private String databaseName = Settings.DEFAULT_DATABASE_NAME;

    public TorchFactoryImpl(Context context) {
        this(context, null);
    }

    public TorchFactoryImpl(Context context, Set<EntityMetadata<?>> metadatas) {
        this(context, metadatas, new BasicFactoryConfiguration());
    }

    /**
     * You shouldn't need to initialize the factory yourself, but if you need to do so, remember that the context you
     * pass in will be stored in this instance of the factory. If you want to use the factory through multiple
     * activities (storing it at static level), you should use application context to avoid memory leaks.
     *
     * @param context       Context which will be used to open database with.
     * @param metadatas     Optional set of {@link org.brightify.torch.EntityMetadata} to directly register.
     * @param configuration
     */
    public TorchFactoryImpl(Context context, Set<EntityMetadata<?>> metadatas,
                            FactoryConfiguration configuration) {
        this.context = context;
        configuration.configureFactory(this);

        // TODO what about the CursorFactory (currently null = default)?
        databaseEngine = new DatabaseEngineImpl(context, getDatabaseName(), null);
        databaseEngine.setOnCreateDatabaseListener(this);

        verifyConfiguration();
        initialized = true;

        registerInternalEntities();

        if (metadatas != null) {
            for (EntityMetadata<?> metadata : metadatas) {
                register(metadata);
            }
        }
    }

    private void registerInternalEntities() {
        // We need to register our metadata
        entities.registerMetadata(tableMetadata);
        MigrationAssistant<Table> tableMigrationAssistant = new MigrationAssistantImpl<Table>(this, tableMetadata);
        tableMigrationAssistant.createTable();
        entities.registerMetadata(tableDetailsMetadata);
        MigrationAssistant<TableDetails> tableDetailsMigrationAssistant =
                new MigrationAssistantImpl<TableDetails>(this, tableDetailsMetadata);
        tableDetailsMigrationAssistant.createTable();
    }

    private void verifyConfiguration() {
        // TODO When there is more configuration, verify it
    }

    @Override
    public DatabaseEngine getDatabaseEngine() {
        return databaseEngine;
    }

    @Override
    public Torch begin() {
        return new TorchImpl(this);
    }

    @Override
    public void onCreateDatabase(SQLiteDatabase db) {

    }

    @Override
    public SQLiteDatabase forceOpenOrCreateDatabase() {
        // This will force create the database
        return databaseEngine.getDatabase();
    }

    @Override
    public boolean deleteDatabase() {
        return databaseEngine.deleteDatabase();
    }

    @Override
    public void unload() {
        entities.clear();

        databaseEngine.close();
    }

    @Override
    public Entities getEntities() {
        return entities;
    }

    @Override
    public <ENTITY> TorchFactory register(Class<ENTITY> entityClass) {
        EntityMetadata<ENTITY> metadata = EntitiesImpl.findMetadata(entityClass);
        return register(metadata);
    }

    @Override
    public <ENTITY> TorchFactory register(EntityMetadata<ENTITY> metadata) {
        entities.registerMetadata(metadata);

        Table table = begin().load().type(Table.class).filter("tableName = ?", metadata.getTableName()).single();
        MigrationAssistantImpl<ENTITY> migrationAssistant = new MigrationAssistantImpl<ENTITY>(this, metadata);
        if (table == null) {
            migrationAssistant.createTable();
            table = new Table();
            table.setTableName(metadata.getTableName());
            table.setVersion(metadata.getVersion());
        } else if (!table.getVersion().equals(metadata.getVersion())) {
            if (metadata.getMigrationType() == Entity.MigrationType.DROP_CREATE) {
                migrationAssistant.dropCreateTable();
            } else {
                try {
                    metadata.migrate(migrationAssistant, table.getVersion(), metadata.getVersion());
                } catch (Exception e) {
                    if (metadata.getMigrationType() == Entity.MigrationType.TRY_MIGRATE) {
                        e.printStackTrace();
                        migrationAssistant.dropCreateTable();
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            }
            table.setVersion(metadata.getVersion());

            begin().save().entity(table);
        }


        return this;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public void setDatabaseName(String databaseName) throws IllegalStateException, IllegalArgumentException {
        if (initialized) {
            throw new IllegalStateException("This factory has already been initialized, cannot change database name!");
        }

        if (databaseName == null || databaseName.equals("")) {
            throw new IllegalArgumentException("Database name cannot be empty or null!");
        }

        this.databaseName = databaseName;
    }
}
