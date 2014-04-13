package org.brightify.torch;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.model.Table;
import org.brightify.torch.model.Table$;
import org.brightify.torch.model.TableDetails;
import org.brightify.torch.model.TableDetails$;
import org.brightify.torch.sql.statement.DropTable;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.Validate;

import java.util.Collections;
import java.util.Set;

public class TorchFactoryImpl implements TorchFactory {
    private final DatabaseEngine databaseEngine;

    private final Table$ tableMetadata = Table$.create();
    private final TableDetails$ tableDetailsMetadata = TableDetails$.create();
    private final EntitiesImpl entities = new EntitiesImpl();

    public TorchFactoryImpl(DatabaseEngine databaseEngine) {
        this(databaseEngine, Collections.<EntityMetadata<?>>emptySet());
    }

    public TorchFactoryImpl(DatabaseEngine databaseEngine, Set<EntityMetadata<?>> metadataSet) {
        this(databaseEngine, metadataSet, new BasicConfiguration());
    }

    /**
     * You shouldn't need to initialize the factory yourself, but if you need to do so, remember that the context you
     * pass in will be stored in this instance of the factory. If you want to use the factory through multiple
     * activities (storing it at static level), you should use application context to avoid memory leaks.
     *
     * @param engine        DatabaseEngine which will be used by {@link Torch} instances created by this factory, never
     *                      null.
     * @param metadataSet   A set of {@link org.brightify.torch.EntityMetadata} to directly register. Cannot be null,
     *                      provide empty set if you have no metadata to register.
     * @param configuration Configuration holder to be used to configure this factory. Cannot be null.
     *
     * @throws IllegalArgumentException When any of the parameters is null.
     */
    public TorchFactoryImpl(DatabaseEngine engine, Set<EntityMetadata<?>> metadataSet, Configuration configuration)
            throws IllegalArgumentException {
        Validate.argumentNotNull(engine, "Database engine cannot be null!");
        Validate.argumentNotNull(metadataSet, "Set of entity metadata cannot be null!");
        Validate.argumentNotNull(configuration, "Factory configuration cannot be null!");

        configuration.configureFactory(this);

        databaseEngine = engine;
        databaseEngine.setTorchFactory(this);

        verifyConfiguration();

        registerInternalEntities();

        for (EntityMetadata<?> metadata : metadataSet) {
            register(metadata);
        }

    }

    private void registerInternalEntities() {
        // We need to register our metadata
        entities.registerMetadata(tableMetadata);
        MigrationAssistant<Table> tableMigrationAssistant = databaseEngine.getMigrationAssistant(tableMetadata);
        tableMigrationAssistant.createTable();
        entities.registerMetadata(tableDetailsMetadata);
        MigrationAssistant<TableDetails> tableDetailsMigrationAssistant =
                databaseEngine.getMigrationAssistant(tableDetailsMetadata);
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
    public void unload() {
        entities.clear();
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

        Table table = begin().load().type(Table.class).filter(Table$.tableName.equalTo(metadata.getTableName()))
                             .single();
        MigrationAssistant<ENTITY> migrationAssistant = databaseEngine.getMigrationAssistant(metadata);
        if (table == null) {
            createTable(metadata);
            table = new Table();
            table.setTableName(metadata.getTableName());
            table.setVersion(metadata.getVersion());
        } else if (!table.getVersion().equals(metadata.getVersion())) {
            if (metadata.getMigrationType() == Entity.MigrationType.DROP_CREATE) {
                dropCreateTable(metadata);
            } else {
                try {
                    metadata.migrate(migrationAssistant, table.getVersion(), metadata.getVersion());
                } catch (Exception e) {
                    if (metadata.getMigrationType() == Entity.MigrationType.TRY_MIGRATE) {
                        e.printStackTrace();
                        dropCreateTable(metadata);
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

    public static class BasicConfiguration implements Configuration {
        public static BasicConfiguration create() {
            return new BasicConfiguration();
        }

        @Override
        public void configureFactory(TorchFactory factory) {
        }
    }

}
