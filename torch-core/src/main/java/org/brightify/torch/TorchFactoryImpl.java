package org.brightify.torch;

import org.brightify.torch.action.relation.RelationResolverImpl;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.filter.ReferenceCollectionProperty;
import org.brightify.torch.filter.ReferenceProperty;
import org.brightify.torch.model.Table;
import org.brightify.torch.model.Table$;
import org.brightify.torch.model.TableDetails;
import org.brightify.torch.model.TableDetails$;
import org.brightify.torch.relation.RelationResolver;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.Validate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TorchFactoryImpl implements TorchFactory {
    private final DatabaseEngine databaseEngine;

    private final Table$ tableMetadata = Table$.create();
    private final TableDetails$ tableDetailsMetadata = TableDetails$.create();
    
    private final EntitiesImpl entities = new EntitiesImpl();
    
    public TorchFactoryImpl(DatabaseEngine databaseEngine) {
        this(databaseEngine, Collections.<EntityDescription<?>>emptySet());
    }

    /**
     * You shouldn't need to initialize the factory yourself, but if you need to do so, remember that the context you
     * pass in will be stored in this instance of the factory. If you want to use the factory through multiple
     * activities (storing it at static level), you should use application context to avoid memory leaks.
     *
     * @param engine        DatabaseEngine which will be used by {@link Torch} instances created by this factory, never
     *                      null.
     * @param descriptions   A set of {@link EntityDescription} to directly register. Cannot be null,
     *                      provide empty set if you have no metadata to register.
     * @throws IllegalArgumentException When any of the parameters is null.
     */
    public TorchFactoryImpl(DatabaseEngine engine, Set<EntityDescription<?>> descriptions) throws IllegalArgumentException {
        Validate.argumentNotNull(engine, "Database engine cannot be null!");
        Validate.argumentNotNull(descriptions, "Set of entity descriptions cannot be null!");

        databaseEngine = engine;
        databaseEngine.setTorchFactory(this);

        registerInternalEntities();

        for (EntityDescription<?> description : descriptions) {
            entities.registerMetadata(description);
        }

        for (EntityDescription<?> description : descriptions) {
            register(description);
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

    private <ENTITY> void register(EntityDescription<ENTITY> description) {
        for (ReferenceProperty<ENTITY, ?> referenceProperty : description.getReferenceProperties()) {
            initializeReferenceProperty(referenceProperty);
        }
        for(ReferenceCollectionProperty<ENTITY, ?> referenceCollectionProperty : description.getReferenceCollectionProperties()) {
            initializeRefCollectionProperty(referenceCollectionProperty);
        }

        Table table = begin().load()
                             .type(Table.class)
                             .filter(Table$.tableName.equalTo(description.getSafeName()))
                             .single();
        MigrationAssistant<ENTITY> migrationAssistant = databaseEngine.getMigrationAssistant(description);
        if (table == null) {
            migrationAssistant.createStore();
            table = new Table();
            table.setTableName(description.getSafeName());
            table.setRevision(description.getRevision());

            begin().save().entity(table);
        } else if (!table.getRevision().equals(description.getRevision())) {
            if (description.getMigrationType() == Entity.MigrationType.DROP_CREATE) {
                migrationAssistant.recreateStore();
            } else {
                try {
                    description.migrate(migrationAssistant, table.getRevision(), description.getRevision());
                } catch (Exception e) {
                    if (description.getMigrationType() == Entity.MigrationType.TRY_MIGRATE) {
                        e.printStackTrace();
                        migrationAssistant.recreateStore();
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            }
            table.setRevision(description.getRevision());

            begin().save().entity(table);
        }
    }

    private <ENTITY, CHILD> void initializeReferenceProperty(ReferenceProperty<ENTITY, CHILD> referenceProperty) {
        EntityDescription<CHILD> referencedEntityDescription = entities.getDescription(
                referenceProperty.getReferencedType());
        referenceProperty.setReferencedEntityDescription(referencedEntityDescription);
    }

    private <ENTITY, CHILD> void initializeRefCollectionProperty(ReferenceCollectionProperty<ENTITY, CHILD> referenceCollectionProperty) {
        EntityDescription<CHILD> referencedEntityDescription = entities.getDescription(referenceCollectionProperty.getReferencedType());
        referenceCollectionProperty.setReferencedEntityDescription(referencedEntityDescription);
    }

    @Override
    public RelationResolver getRelationResolver() {
        return new RelationResolverImpl(begin());
    }

    public static class BasicConfiguration implements TorchConfiguration<BasicConfiguration> {

        private final DatabaseEngine databaseEngine;
        private Set<EntityDescription<?>> descriptions = new HashSet<EntityDescription<?>>();

        public BasicConfiguration(DatabaseEngine databaseEngine) {
            this.databaseEngine = databaseEngine;
        }

        @Override
        public Set<EntityDescription<?>> getRegisteredEntityDescriptions() {
            return descriptions;
        }

        @Override
        public DatabaseEngine getDatabaseEngine() {
            return databaseEngine;
        }

        @Override
        public BasicConfiguration register(Class<?> entityOrEntityDescriptionClass) {
            EntityDescription<?> description = EntitiesImpl.resolveEntityDescription(entityOrEntityDescriptionClass);
            return register(description);
        }

        @Override
        public <ENTITY> BasicConfiguration register(EntityDescription<ENTITY> description) {
            descriptions.add(description);
            return this;
        }

        @Override
        public BasicConfiguration register(Collection<? extends EntityDescription<?>> descriptions) {
            this.descriptions.addAll(descriptions);
            return this;
        }

        @Override
        public BasicConfiguration register(EntityDescription<?>... descriptions) {
            return register(Arrays.asList(descriptions));
        }

        @Override
        public TorchFactory initializeFactory() {
            return new TorchFactoryImpl(databaseEngine, descriptions);
        }
    }
}
