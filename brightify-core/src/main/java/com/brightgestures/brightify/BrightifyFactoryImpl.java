package com.brightgestures.brightify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.model.Table;
import com.brightgestures.brightify.model.TableDetailsMetadata;
import com.brightgestures.brightify.model.TableMetadata;
import com.brightgestures.brightify.util.MigrationAssistant;
import com.brightgestures.brightify.util.MigrationAssistantImpl;

import java.util.Collection;
import java.util.List;

public class BrightifyFactoryImpl implements BrightifyFactory {

    protected final Context context;
    protected final FactoryConfiguration configuration;
    protected final DatabaseEngine databaseEngine;

    protected final TableMetadata tableMetadata = new TableMetadata();
    protected final TableDetailsMetadata tableDetailsMetadata = new TableDetailsMetadata();
    protected final EntitiesImpl entities = new EntitiesImpl();

    BrightifyFactoryImpl(Context applicationContext) {
        this(applicationContext, null);
    }

    BrightifyFactoryImpl(Context applicationContext, List<EntityMetadata<?>> metadatas) {
        this(applicationContext, metadatas, FactoryConfigurationImpl.create(applicationContext));
    }

    BrightifyFactoryImpl(Context applicationContext, List<EntityMetadata<?>> metadatas,
                         FactoryConfiguration configuration) {
        context = applicationContext;
        this.configuration = configuration;
        // TODO what about the CursorFactory (currently null = default)?
        databaseEngine = new DatabaseEngineImpl(applicationContext, configuration.getDatabaseName(), null);
        databaseEngine.setOnCreateDatabaseListener(this);
        if(configuration.isEnableQueryLogging()) {
            Settings.enableQueryLogging();
        }

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

    @Override
    public FactoryConfiguration getConfiguration() {
        return configuration;
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
        if(table == null) {
            table = new Table();
            table.setTableName(metadata.getTableName());
            table.setVersion(metadata.getVersion());
        } else if(table.getVersion() != metadata.getVersion()) {
            MigrationAssistantImpl<ENTITY> migrationAssistant = new MigrationAssistantImpl<>(this, metadata);
            if(metadata.getMigrationType() == Entity.MigrationType.DROP_CREATE) {
                migrationAssistant.dropCreateTable();
            } else {
                try {
                    metadata.migrate(migrationAssistant, table.getVersion(), metadata.getVersion());
                } catch(Exception e) {
                    if(metadata.getMigrationType() == Entity.MigrationType.TRY_MIGRATE) {
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
}
