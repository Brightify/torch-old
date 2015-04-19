package org.brightify.torch.test;

import org.brightify.torch.DatabaseEngine;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.LoadContainer;
import org.brightify.torch.LoadContainerImpl;
import org.brightify.torch.Ref;
import org.brightify.torch.RefImpl;
import org.brightify.torch.Torch;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.action.load.LoadQuery;
import org.brightify.torch.action.load.OrderLoader;
import org.brightify.torch.filter.NumberProperty;
import org.brightify.torch.filter.Property;
import org.brightify.torch.filter.ReferenceCollectionProperty;
import org.brightify.torch.filter.ReferenceProperty;
import org.brightify.torch.filter.ValueProperty;
import org.brightify.torch.impl.filter.IntegerPropertyImpl;
import org.brightify.torch.impl.filter.LongPropertyImpl;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.ReadableRawContainerImpl;
import org.brightify.torch.util.WritableRawContainerImpl;
import org.brightify.torch.util.functional.EditFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MockDatabaseEngine implements DatabaseEngine {
    private TorchFactory torchFactory;
    private MockDatabase database = new MockDatabase();

    @Override
    public boolean open() {
        database.open();
        return true;
    }

    @Override
    public boolean close() {
        database.open();
        return true;
    }


    @Override
    public boolean wipe() {
        database.wipe();
        return true;
    }

    @Override
    public boolean isOpen() {
        return database.isOpen();
    }

    @Override
    public <ENTITY> void each(LoadQuery<ENTITY> loadQuery, EditFunction<ENTITY> function) {
        List<ENTITY> entities = load(loadQuery);
        List<ENTITY> saveEntities = new ArrayList<ENTITY>();
        for (ENTITY entity : entities) {
            if (function.apply(entity)) {
                saveEntities.add(entity);
            }
        }
        save(saveEntities);
    }

    @Override
    public <ENTITY> List<ENTITY> load(LoadQuery<ENTITY> loadQuery) {
        return loadList(loadQuery);
    }

    @Override
    public <ENTITY> ENTITY first(LoadQuery<ENTITY> loadQuery) {
        Iterator<ENTITY> iterator = load(loadQuery).iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    @Override
    public <ENTITY> int count(LoadQuery<ENTITY> loadQuery) {
        return loadList(loadQuery).size();
    }

    @Override
    public <ENTITY> Map<ENTITY, Long> save(Iterable<ENTITY> entities) {
        MockDatabase.MockTransaction transaction = database.beginTransaction();

        Map<ENTITY, Long> result;
        try {
            result = save(entities, transaction);

            transaction.commit();

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <ENTITY> Map<ENTITY, Long> save(Iterable<ENTITY> entities, MockDatabase.MockTransaction transaction) throws Exception {
        Map<ENTITY, Long> results = new HashMap<ENTITY, Long>();
        Iterator<ENTITY> iterator = entities.iterator();
        if (!iterator.hasNext()) {
            return results;
        }

        @SuppressWarnings("unchecked")
        Class<ENTITY> entityClass = (Class<ENTITY>) iterator.next().getClass();
        EntityDescription<ENTITY> description = torchFactory.getEntities().getDescription(entityClass);
        WritableRawContainerImpl rawContainer = new WritableRawContainerImpl();

        for (ReferenceProperty<ENTITY, ?> referenceProperty : description.getReferenceProperties()) {
            List<?> objects = referencedObjects(referenceProperty, entities);
            save(objects, transaction);
        }

        for (ReferenceCollectionProperty<ENTITY, ?> property : description.getReferenceCollectionProperties()) {
            List<?> objects = referencedObjects(property, entities);
            save(objects, transaction);
        }

        for (ENTITY entity : entities) {
            RawEntity rawEntity = new RawEntity();
            rawContainer.setRawEntity(rawEntity);
            Long id = description.getIdProperty().get(entity);
            if (id == null) {
                id = transaction.acquireNewId(description.getSafeName());
                description.getIdProperty().set(entity, id);
            }

            for (ValueProperty<ENTITY, ?> valueProperty : description.getValueProperties()) {
                rawContainer.setPropertyName(valueProperty.getSafeName());
                valueProperty.writeToRawContainer(entity, rawContainer);
            }

            for (ReferenceProperty<ENTITY, ?> referenceProperty : description.getReferenceProperties()) {
                rawContainer.setPropertyName(referenceProperty.getSafeName());
                Ref<?> ref = referenceProperty.get(entity);
                if (ref == null) {
                    rawContainer.putNull();
                } else {
                    Long entityId = ref.getEntityId();
                    if (entityId == null) {
                        rawContainer.putNull();
                    } else {
                        rawContainer.put(entityId);
                    }
                }
            }

            for (ReferenceCollectionProperty<ENTITY, ?> property : description.getReferenceCollectionProperties()) {
                rawContainer.setPropertyName(property.getSafeName());
                Collection<?> children = property.get(entity);
                if(children == null) {
                    rawContainer.putNull();
                } else {
                    int count = children.size();
                    rawContainer.put(count);
                }
            }

            results.put(entity, id);
            transaction.store(description.getSafeName(), RawEntity.PrimaryKey.of(id), rawEntity);
        }

        for (ReferenceCollectionProperty<ENTITY, ?> property : description.getReferenceCollectionProperties()) {
            storeChildren(description, entities, property, transaction);
        }

        return results;
    }

    private <ENTITY, CHILD> void storeChildren(EntityDescription<ENTITY> parentDescription, Iterable<ENTITY> entities,
                                               ReferenceCollectionProperty<ENTITY, CHILD> property,
                                               MockDatabase.MockTransaction transaction) {
        String bindEntityName = getBindEntityName(parentDescription, property);
        EntityDescription<CHILD> childDescription = property.getReferencedEntityDescription();

        for (ENTITY entity : entities) {
            Long parentId = parentDescription.getIdProperty().get(entity);
            Collection<CHILD> children = property.get(entity);
            if(children == null) {
                continue;
            }

            int i = 0;
            for (CHILD child : children) {
                RawEntity rawEntity = new RawEntity();
                Long childId = childDescription.getIdProperty().get(child);
                BindTableDescription.parentId.set(rawEntity, parentId);
                BindTableDescription.childId.set(rawEntity, childId);
                BindTableDescription.position.set(rawEntity, i);
                transaction.store(bindEntityName, RawEntity.PrimaryKey.of(parentId, i), rawEntity);
                i++;
            }
        }
    }

    private <ENTITY, CHILD> List<CHILD> referencedObjects(ReferenceProperty<ENTITY, CHILD> property,
                                                          Iterable<ENTITY> entities) {
        List<CHILD> children = new ArrayList<CHILD>();

        for (ENTITY entity : entities) {
            Ref<CHILD> ref = property.get(entity);
            if (ref == null) {
                continue;
            }
            CHILD child = ref.get();
            if (child == null) {
                continue;
            }
            children.add(child);
        }

        return children;
    }

    private <ENTITY, CHILD> List<CHILD> referencedObjects(ReferenceCollectionProperty<ENTITY, CHILD> property,
                                                          Iterable<ENTITY> entities) {
        List<CHILD> children = new ArrayList<CHILD>();

        for (ENTITY entity : entities) {
            Collection<CHILD> entityChildren = property.get(entity);
            if (entityChildren == null) {
                continue;
            }
            children.addAll(entityChildren);
        }

        return children;
    }

    @Override
    public <ENTITY> Map<ENTITY, Boolean> delete(Iterable<ENTITY> entities) {
        Map<ENTITY, Boolean> result = new HashMap<ENTITY, Boolean>();
        Iterator<ENTITY> iterator = entities.iterator();
        if (!iterator.hasNext()) {
            return result;
        }

        @SuppressWarnings("unchecked")
        Class<ENTITY> entityClass = (Class<ENTITY>) iterator.next().getClass();
        EntityDescription<ENTITY> description = torchFactory.getEntities().getDescription(entityClass);

        Map<RawEntity.PrimaryKey, RawEntity> entityDatabase = database.getEntityDatabase(description.getSafeName());


        for (ENTITY entity : entities) {
            Long id = description.getIdProperty().get(entity);
            RawEntity rawEntity = entityDatabase.remove(RawEntity.PrimaryKey.of(id));
            result.put(entity, rawEntity != null);
        }

        return result;
    }

    @Override
    public <ENTITY> MigrationAssistant<ENTITY> getMigrationAssistant(EntityDescription<ENTITY> description) {
        return new MockMigrationAssistant<ENTITY>(description);
    }

    @Override
    public TorchFactory getTorchFactory() {
        return torchFactory;
    }

    @Override
    public void setTorchFactory(TorchFactory factory) {
        torchFactory = factory;
    }

    private <ENTITY> List<ENTITY> loadList(final LoadQuery<ENTITY> loadQuery) {
        EntityDescription<ENTITY> description = loadQuery.getEntityDescription();
        Map<RawEntity.PrimaryKey, RawEntity> entityDatabase = database.getEntityDatabase(description.getSafeName());

        List<RawEntity> rawEntities = new ArrayList<RawEntity>();
        for (RawEntity rawEntity : entityDatabase.values()) {
            if (loadQuery.getFilter() != null && !MockDatabaseFilter.applyFilter(loadQuery.getFilter(), rawEntity)) {
                continue;
            }

            rawEntities.add(rawEntity);
        }

        for (final Map.Entry<Property<ENTITY, ?>, OrderLoader.Direction> orderEntry : loadQuery.getOrderMap()
                                                                                               .entrySet()) {
            Collections.sort(rawEntities, new Comparator<RawEntity>() {
                @Override
                public int compare(RawEntity rawEntity1, RawEntity rawEntity2) {
                    Comparable<Object> c1 = rawEntity1.getValue(orderEntry.getKey().getSafeName());
                    Comparable<Object> c2 = rawEntity2.getValue(orderEntry.getKey().getSafeName());

                    return orderEntry.getValue() == OrderLoader.Direction.ASCENDING ?
                           c1.compareTo(c2) : c2.compareTo(c1);
                }
            });
        }

        if (loadQuery.getLimit() != null) {
            int start = loadQuery.getOffset() != null ? loadQuery.getOffset() : 0;
            int end = Math.min(start + loadQuery.getLimit(), rawEntities.size() - 1);

            if (end - start > 0) {
                rawEntities = rawEntities.subList(start, end);
            } else {
                rawEntities = Collections.emptyList();
            }
        }

        ReadableRawContainerImpl rawContainer = new ReadableRawContainerImpl();
        LoadContainer loadContainer = new LoadContainerImpl(torchFactory, loadQuery.getLoadGroups());

        Map<ReferenceCollectionProperty<ENTITY, ?>, Map<ENTITY, Integer>> referenceCollectionSizes = new HashMap<ReferenceCollectionProperty<ENTITY, ?>, Map<ENTITY, Integer>>();
        for (ReferenceCollectionProperty<ENTITY, ?> property : description.getReferenceCollectionProperties()) {
            referenceCollectionSizes.put(property, new HashMap<ENTITY, Integer>());
        }

        List<ENTITY> result = new ArrayList<ENTITY>();
        for (RawEntity rawEntity : rawEntities) {
            rawContainer.setRawEntity(rawEntity);
            ENTITY entity = description.createEmpty();
            try {
                for (ValueProperty<ENTITY, ?> valueProperty : description.getValueProperties()) {
                    rawContainer.setPropertyName(valueProperty.getSafeName());
                    valueProperty.readFromRawContainer(rawContainer, entity);
                }

                for (ReferenceProperty<ENTITY, ?> referenceProperty : description.getReferenceProperties()) {
                    rawContainer.setPropertyName(referenceProperty.getSafeName());
                    Long childID = rawContainer.getLong();
                    if (childID != null) {
                        Ref<?> ref = setReference(referenceProperty, childID, torchFactory, entity);
                        loadContainer.addReferenceToQueue(ref);
                    }
                }

                for (ReferenceCollectionProperty<ENTITY, ?> property : description.getReferenceCollectionProperties()) {
                    rawContainer.setPropertyName(property.getSafeName());
                    referenceCollectionSizes.get(property).put(entity, rawContainer.getInteger());
                }
            } catch (Exception e) {
                // FIXME handle the exception better
                throw new RuntimeException(e);
            }
            result.add(entity);
        }

        for (ReferenceCollectionProperty<ENTITY, ?> property : description.getReferenceCollectionProperties()) {
            loadChildren(description, result, property, referenceCollectionSizes.get(property));
        }

        loadReferences(loadContainer);
//        loadReferenceCollections(loadContainer);

        return result;
    }

    private <ENTITY, CHILD> void loadChildren(EntityDescription<ENTITY> parentDescription, Iterable<ENTITY> entities,
                                              ReferenceCollectionProperty<ENTITY, CHILD> property,
                                              Map<ENTITY, Integer> referenceCollectionSizes) {
        String bindEntityName = getBindEntityName(parentDescription, property);
        EntityDescription<CHILD> childDescription = property.getReferencedEntityDescription();

        for (ENTITY entity : entities) {
            Long parentId = parentDescription.getIdProperty().get(entity);

            Integer size = referenceCollectionSizes.get(entity);
            if(size == null) {
                continue;
            } else if (size == 0) {
                property.set(entity, new ArrayList<CHILD>());
                continue;
            }

            Long[] childrenIds = new Long[size];

            Map<RawEntity.PrimaryKey, RawEntity> bindTable = database.getEntityDatabase(bindEntityName);
            for (RawEntity bindEntity : bindTable.values()) {
                if(!BindTableDescription.parentId.get(bindEntity).equals(parentId)) {
                    continue;
                }
                Integer position = BindTableDescription.position.get(bindEntity);
                Long childId = BindTableDescription.childId.get(bindEntity);

                childrenIds[position] = childId;
            }

            Collection<CHILD> children = torchFactory.begin()
                                                     .load()
                                                     .type(childDescription.getEntityClass())
                                                     .ids(childrenIds);

            property.set(entity, children);
        }
    }

    private <ENTITY, CHILD> Ref<CHILD> setReference(ReferenceProperty<ENTITY, CHILD> referenceProperty, Long childID,
                                                    TorchFactory torchFactory, ENTITY entity) {
        Ref<CHILD> reference = RefImpl.of(referenceProperty, torchFactory, childID);
        referenceProperty.set(entity, reference);
        return reference;
    }

    private void loadReferences(LoadContainer loadContainer) {
        Iterator<Ref<?>> iterator = loadContainer.getReferenceQueue().iterator();
        while (iterator.hasNext()) {
            Ref<?> reference = iterator.next();

            // #get() will load it.
            // FIXME this has to be changed for better performance
            reference.get();

            iterator.remove();
        }
    }

    private String getBindEntityName(EntityDescription<?> parentDescription, ReferenceCollectionProperty<?, ?> property) {
        return "parent__" + parentDescription.getSafeName() +
               "#" + property.getSafeName() +
               "__child__" + property.getReferencedEntityDescription().getSafeName();
    }

/*
    private <T> void loadReferenceCollections(LoadContainer loadContainer) {
        LoadContainer localLoadContainer = new LoadContainerImpl(loadContainer);
        Iterator<RefCollection<?>> iterator = loadContainer.getReferenceCollectionQueue().iterator();
        while (iterator.hasNext()) {
            // The "T" will be erased and we use it only so we don't need to care about wildcards.
            RefCollection<T> referenceCollection = (RefCollection<T>) iterator.next();
            String bindTableName = getBindEntityName(referenceCollection);

            Long parentId = referenceCollection.getParentId();
            BaseFilter<?, ?> parentIdFilter = BindTableDescription.parentId.equalTo(parentId);

            Map<Long, RawEntity> bindTableDatabase = database.get(bindTableName);
            List<RawEntity> rawEntities = new ArrayList<RawEntity>();

            for (RawEntity rawEntity : bindTableDatabase.values()) {
                if (parentIdFilter != null && !MockDatabaseFilter.applyFilter(parentIdFilter, rawEntity)) {
                    continue;
                }

                rawEntities.add(rawEntity);
            }

            ReadableRawContainerImpl rawContainer = new ReadableRawContainerImpl();
            EntityDescription<T> childDescription = referenceCollection.getChildDescription();
            Map<Long, RawEntity> childDatabase = database.get(childDescription.getSafeName());
            for (RawEntity rawEntity : rawEntities) {
                Long childId = BindTableDescription.childId.get(rawEntity);

                RawEntity rawChild = childDatabase.get(childId);
                rawContainer.setRawEntity(rawChild);
                T child = childDescription.createEmpty();
                try {
                    for (ValueProperty<T, ?> valueProperty : childDescription.getValueProperties()) {
                        rawContainer.setPropertyName(valueProperty.getSafeName());
                        valueProperty.readFromRawContainer(rawContainer, child);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                referenceCollection.set(childId, child);
            }

            iterator.remove();
        }

    }
*/
    private static class BindTableDescription {
        public static NumberProperty<RawEntity, Long> parentId =
                new LongPropertyImpl<RawEntity>(RawEntity.class, "parentId", "torch_parentId") {
                    @Override
                    public Long get(RawEntity entity) {
                        return entity.getLong(getSafeName());
                    }

                    @Override
                    public void set(RawEntity entity, Long value) {
                        entity.put(getSafeName(), value);
                    }
                };
        public static NumberProperty<RawEntity, Long> childId =
                new LongPropertyImpl<RawEntity>(RawEntity.class, "childId", "torch_childId") {
                    @Override
                    public Long get(RawEntity entity) {
                        return entity.getLong(getSafeName());
                    }

                    @Override
                    public void set(RawEntity entity, Long value) {
                        entity.put(getSafeName(), value);
                    }
                };
        public static NumberProperty<RawEntity, Integer> position =
                new IntegerPropertyImpl<RawEntity>(RawEntity.class, "position", "torch_position") {
                    @Override
                    public Integer get(RawEntity entity) {
                        return entity.getInteger(getSafeName());
                    }

                    @Override
                    public void set(RawEntity entity, Integer value) {
                        entity.put(getSafeName(), value);
                    }
                };
    }

    public class MockMigrationAssistant<ENTITY> implements MigrationAssistant<ENTITY> {

        private final EntityDescription<ENTITY> entityDescription;
        private final Class<ENTITY> entityClass;

        public MockMigrationAssistant(EntityDescription<ENTITY> entityDescription) {
            this.entityDescription = entityDescription;
            entityClass = entityDescription.getEntityClass();
        }

        @Override
        public void addProperty(Property<ENTITY, ?> property) {

        }

        @Override
        public void renameProperty(String from, String to) {
            Map<?, RawEntity> entityDatabase = database.getEntityDatabase(entityDescription.getSafeName());
            for (RawEntity rawEntity : entityDatabase.values()) {
                Object value = rawEntity.getValue(from);
                rawEntity.storeValue(from, null);
                rawEntity.storeValue(to, value);
            }
        }

        @Override
        public void removeProperty(String name) {
            Map<?, RawEntity> entityDatabase = database.getEntityDatabase(entityDescription.getSafeName());
            for (RawEntity rawEntity : entityDatabase.values()) {
                rawEntity.storeValue(name, null);
            }
        }

        @Override
        public void createStore() {
            if (!storeExists()) {
                database.prepareStore(entityDescription.getSafeName());
                for (ReferenceCollectionProperty<ENTITY, ?> property :
                        entityDescription.getReferenceCollectionProperties()) {
                    database.prepareStore(getBindEntityName(entityDescription, property));
                }
            }
        }

        @Override
        public void deleteStore() {
            database.deleteStore(entityDescription.getSafeName());
            for (ReferenceCollectionProperty<ENTITY, ?> property :
                    entityDescription.getReferenceCollectionProperties()) {
                database.deleteStore(getBindEntityName(entityDescription, property));
            }
        }

        @Override
        public void recreateStore() {
            deleteStore();
            createStore();
        }

        @Override
        public boolean storeExists() {
            return database.storeExists(entityDescription.getSafeName());
        }

        @Override
        public Torch torch() {
            return getTorchFactory().begin();
        }
    }

}
