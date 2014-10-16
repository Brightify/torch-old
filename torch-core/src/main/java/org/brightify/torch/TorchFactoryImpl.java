package org.brightify.torch;

import org.brightify.torch.android.action.relation.RelationResolverImpl;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.model.Table;
import org.brightify.torch.model.Table$;
import org.brightify.torch.model.TableDetails;
import org.brightify.torch.model.TableDetails$;
import org.brightify.torch.relation.RelationResolver;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.Validate;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TorchFactoryImpl implements TorchFactory {
    private final DatabaseEngine databaseEngine;

    private final Table$ tableMetadata = Table$.create();
    private final TableDetails$ tableDetailsMetadata = TableDetails$.create();
    
    private final EntitiesImpl entities = new EntitiesImpl();
    
    public TorchFactoryImpl(DatabaseEngine databaseEngine) {
        this(databaseEngine, Collections.<EntityDescription<?>>emptySet());
    }

    public TorchFactoryImpl(DatabaseEngine databaseEngine, Set<EntityDescription<?>> metadataSet) {
        this(databaseEngine, metadataSet, new BasicConfiguration());
    }

    /**
     * You shouldn't need to initialize the factory yourself, but if you need to do so, remember that the context you
     * pass in will be stored in this instance of the factory. If you want to use the factory through multiple
     * activities (storing it at static level), you should use application context to avoid memory leaks.
     *
     * @param engine        DatabaseEngine which will be used by {@link Torch} instances created by this factory, never
     *                      null.
     * @param metadataSet   A set of {@link EntityDescription} to directly register. Cannot be null,
     *                      provide empty set if you have no metadata to register.
     * @param configuration Configuration holder to be used to configure this factory. Cannot be null.
     *
     * @throws IllegalArgumentException When any of the parameters is null.
     */
    public TorchFactoryImpl(DatabaseEngine engine, Set<EntityDescription<?>> metadataSet, Configuration configuration)
            throws IllegalArgumentException {
        Validate.argumentNotNull(engine, "Database engine cannot be null!");
        Validate.argumentNotNull(metadataSet, "Set of entity metadata cannot be null!");
        Validate.argumentNotNull(configuration, "Factory configuration cannot be null!");

        configuration.configureFactory(this);

        databaseEngine = engine;
        databaseEngine.setTorchFactory(this);

        verifyConfiguration();

        registerInternalEntities();

        for (EntityDescription<?> metadata : metadataSet) {
            register(metadata);
        }

    }

    private void registerInternalEntities() {
        // We need to register our metadata
        entities.registerMetadata(tableMetadata);
        MigrationAssistant<Table> tableMigrationAssistant = databaseEngine.getMigrationAssistant(tableMetadata);
        tableMigrationAssistant.createStore();
        entities.registerMetadata(tableDetailsMetadata);
        MigrationAssistant<TableDetails> tableDetailsMigrationAssistant =
                databaseEngine.getMigrationAssistant(tableDetailsMetadata);
        tableDetailsMigrationAssistant.createStore();
    }


//    private boolean tableExists(EntityMetadata<?> entityMetadata) {
//        String tableName = entityMetadata.getSafeName();
//
//        return begin().load().type(SQLiteMaster.class).filter(SQLiteMaster$.tableName.equalTo(tableName)).count() > 0;
//    }

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

    public TorchFactory register(Class<?>... entityClasses) {
        TorchFactory returnFactory = this;
        for (Class<?> entityClass : entityClasses) {
            returnFactory = register(entityClass);
        }
        return returnFactory;
    }

    @Override
    public <ENTITY> TorchFactory register(Class<ENTITY> entityClass) {


        EntityDescription<ENTITY> metadata = EntitiesImpl.findMetadata(entityClass);
        return register(metadata);
    }

    public TorchFactory register(List<EntityDescription<?>> metadataList) throws IllegalArgumentException {
        if(metadataList == null) {
            throw new IllegalArgumentException("List of metadata cannot be null!");
        }
        TorchFactory returnFactory = this;
        for (EntityDescription<?> metadata : metadataList) {
            returnFactory = register(metadata);
        }
        return returnFactory;
    }

    @Override
    public <ENTITY> TorchFactory register(EntityDescription<ENTITY> metadata) {
        entities.registerMetadata(metadata);

        Table table = begin().load().type(Table.class).filter(Table$.tableName.equalTo(metadata.getSafeName()))
                             .single();
        MigrationAssistant<ENTITY> migrationAssistant = databaseEngine.getMigrationAssistant(metadata);
        if (table == null) {
            migrationAssistant.createStore();
            table = new Table();
            table.setTableName(metadata.getSafeName());
            table.setVersion(metadata.getVersion());
        } else if (!table.getVersion().equals(metadata.getVersion())) {
            if (metadata.getMigrationType() == Entity.MigrationType.DROP_CREATE) {
                migrationAssistant.recreateStore();
            } else {
                try {
                    metadata.migrate(migrationAssistant, table.getVersion(), metadata.getVersion());
                } catch (Exception e) {
                    if (metadata.getMigrationType() == Entity.MigrationType.TRY_MIGRATE) {
                        e.printStackTrace();
                        migrationAssistant.recreateStore();
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
    public RelationResolver getRelationResolver() {
        return new RelationResolverImpl(begin());
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
