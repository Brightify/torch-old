package com.brightgestures.brightify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.model.Table;
import com.brightgestures.brightify.model.TableDetailsMetadata;
import com.brightgestures.brightify.model.TableMetadata;
import com.brightgestures.brightify.util.MigrationAssistantImpl;

import java.util.Collection;
import java.util.Set;

public class BrightifyFactoryImpl implements BrightifyFactory {
    protected final Context context;
    protected final DatabaseEngine databaseEngine;

    protected final TableMetadata tableMetadata = TableMetadata.create();
    protected final TableDetailsMetadata tableDetailsMetadata = TableDetailsMetadata.create();
    protected final EntitiesImpl entities = new EntitiesImpl();

    protected final boolean initialized;

    private String databaseName = Settings.DEFAULT_DATABASE_NAME;

    public BrightifyFactoryImpl(Context context) {
        this(context, null);
    }

    public BrightifyFactoryImpl(Context context, Set<EntityMetadata<?>> metadatas) {
        this(context, metadatas, new BasicFactoryConfiguration());
    }

    /**
     * You shouldn't need to initialize the factory yourself, but if you need to do so, remember that the context you
     * pass in will be stored in this instance of the factory. If you want to use the factory through multiple
     * activities (storing it at static level), you should use application context to avoid memory leaks.
     *
     * @param context       Context which will be used to open database with.
     * @param metadatas     Optional set of {@link com.brightgestures.brightify.EntityMetadata} to directly register.
     * @param configuration
     */
    public BrightifyFactoryImpl(Context context, Set<EntityMetadata<?>> metadatas,
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
        entities.registerMetadata(tableDetailsMetadata);
    }

    private void verifyConfiguration() {
        // TODO When there is more configuration, verify it
    }

    @Override
    public DatabaseEngine getDatabaseEngine() {
        return databaseEngine;
    }

    @Override
    public Brightify begin() {
        return new BrightifyImpl(this);
    }

    @Override
    public void onCreateDatabase(SQLiteDatabase db) {
        Collection<EntityMetadata<?>> metadatas = entities.getAllMetadatas();

        for (EntityMetadata<?> metadata : metadatas) {
            metadata.createTable(db);
        }
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
        // Entities.unregisterAll();

        entities.clear();

        databaseEngine.close();
    }

    @Override
    public Entities getEntities() {
        return entities;
    }

    @Override
    public <ENTITY> BrightifyFactory register(Class<ENTITY> entityClass) {
        EntityMetadata<ENTITY> metadata = EntitiesImpl.findMetadata(entityClass);
        return register(metadata);
    }

    @Override
    public <ENTITY> BrightifyFactory register(EntityMetadata<ENTITY> metadata) {
        entities.registerMetadata(metadata);

        Table table = begin().load().type(Table.class).filter("tableName = ?", metadata.getTableName()).single();
        if (table == null) {
            table = new Table();
            table.setTableName(metadata.getTableName());
            table.setVersion(metadata.getVersion());
        } else if (table.getVersion() != metadata.getVersion()) {
            MigrationAssistantImpl<ENTITY> migrationAssistant = new MigrationAssistantImpl<>(this, metadata);
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
