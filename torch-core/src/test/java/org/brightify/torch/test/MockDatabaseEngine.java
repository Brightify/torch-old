package org.brightify.torch.test;

import org.brightify.torch.DatabaseEngine;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.LoadContainer;
import org.brightify.torch.LoadContainerImpl;
import org.brightify.torch.ReadableRawEntity;
import org.brightify.torch.Ref;
import org.brightify.torch.RefCollection;
import org.brightify.torch.SaveContainer;
import org.brightify.torch.SaveContainerImpl;
import org.brightify.torch.Torch;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.WritableRawEntity;
import org.brightify.torch.action.load.LoadQuery;
import org.brightify.torch.action.load.OrderLoader;
import org.brightify.torch.filter.EqualToFilter;
import org.brightify.torch.filter.NumberProperty;
import org.brightify.torch.filter.Property;
import org.brightify.torch.filter.ValueProperty;
import org.brightify.torch.impl.filter.LongPropertyImpl;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.ReadableRawContainerImpl;
import org.brightify.torch.util.WritableRawContainerImpl;
import org.brightify.torch.util.functional.EditFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MockDatabaseEngine implements DatabaseEngine {
    private TorchFactory torchFactory;
    private Map<String, Map<Long, RawEntity>> database = new HashMap<String, Map<Long, RawEntity>>();
    private Map<String, AtomicLong> idCounter = new HashMap<String, AtomicLong>();

    @Override
    public boolean open() {
        return wipe();
    }

    @Override
    public boolean close() {
        database = null;
        idCounter = null;
        return true;
    }


    @Override
    public boolean wipe() {
        database = new HashMap<String, Map<Long, RawEntity>>();
        idCounter = new HashMap<String, AtomicLong>();
        return true;
    }

    @Override
    public boolean isOpen() {
        return database != null && idCounter != null;
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
        Map<ENTITY, Long> results = new HashMap<ENTITY, Long>();
        Iterator<ENTITY> iterator = entities.iterator();
        if (!iterator.hasNext()) {
            return results;
        }

        @SuppressWarnings("unchecked")
        Class<ENTITY> entityClass = (Class<ENTITY>) iterator.next().getClass();
        EntityDescription<ENTITY> description = torchFactory.getEntities().getDescription(entityClass);
        AtomicLong counter = idCounter.get(description.getSafeName());
        Map<Long, RawEntity> transaction = new HashMap<Long, RawEntity>();

        WritableRawContainerImpl rawContainer = new WritableRawContainerImpl();
        SaveContainer saveContainer = new SaveContainerImpl(torchFactory, Collections.<Class<?>>emptySet());
        for (ENTITY entity : entities) {
            RawEntity rawEntity = new RawEntity();
            rawContainer.setRawEntity(rawEntity);
            try {
                Long id = description.getIdProperty().get(entity);
                if(id == null) {
                    id = counter.getAndIncrement();
                    description.getIdProperty().set(entity, id);
                }

                for (ValueProperty<ENTITY, ?> valueProperty : description.getValueProperties()) {
                    rawContainer.setPropertyName(valueProperty.getSafeName());
                    valueProperty.writeToRawContainer(entity, rawContainer);
                }

                results.put(entity, id);
                transaction.put(id, rawEntity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        database.get(description.getSafeName()).putAll(transaction);

        return results;
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

        Map<Long, RawEntity> entityDatabase = database.get(description.getSafeName());

        for (ENTITY entity : entities) {
            Long id = description.getIdProperty().get(entity);
            RawEntity rawEntity = entityDatabase.remove(id);
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
        Map<Long, RawEntity> entityDatabase = database.get(description.getSafeName());

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
        List<ENTITY> result = new ArrayList<ENTITY>();
        for (RawEntity rawEntity : rawEntities) {
            rawContainer.setRawEntity(rawEntity);
            ENTITY entity = description.createEmpty();
            try {
                for (ValueProperty<ENTITY, ?> valueProperty : description.getValueProperties()) {
                    rawContainer.setPropertyName(valueProperty.getSafeName());
                    valueProperty.readFromRawContainer(rawContainer, entity);
                }
            } catch (Exception e) {
                // FIXME handle the exception better
                throw new RuntimeException(e);
            }
            result.add(entity);
        }

        loadReferences(loadContainer);
        loadReferenceCollections(loadContainer);

        return result;
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

    private <T> void loadReferenceCollections(LoadContainer loadContainer) {
        LoadContainer localLoadContainer = new LoadContainerImpl(loadContainer);
        Iterator<RefCollection<?>> iterator = loadContainer.getReferenceCollectionQueue().iterator();
        while (iterator.hasNext()) {
            // The "T" will be erased and we use it only so we don't need to care about wildcards.
            RefCollection<T> referenceCollection = (RefCollection<T>) iterator.next();
            String bindTableName = getBindTableName(referenceCollection);

            Long parentId = referenceCollection.getParentId();
            EqualToFilter<?, ?> parentIdFilter = BindTableDescription.parentId.equalTo(parentId);

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

    private String getBindTableName(RefCollection<?> referenceCollection) {
        return "parent__" + referenceCollection.getParentDescription().getSafeName() +
               "#" + referenceCollection.getParentProperty().getSafeName() +
               "__child__" + referenceCollection.getChildDescription().getSafeName();
    }

    private static class BindTableDescription {
        public static NumberProperty<RawEntity, Long> id =
                new LongPropertyImpl<RawEntity>(RawEntity.class, "id", "torch_id") {
                    @Override
                    public Long get(RawEntity entity) {
                        return entity.getLong(getSafeName());
                    }

                    @Override
                    public void set(RawEntity entity, Long value) {
                        entity.put(getSafeName(), value);
                    }
                };
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
            Map<Long, RawEntity> entityDatabase = database.get(entityDescription.getSafeName());
            for (RawEntity rawEntity : entityDatabase.values()) {
                Object value = rawEntity.getValue(from);
                rawEntity.storeValue(from, null);
                rawEntity.storeValue(to, value);
            }
        }

        @Override
        public void removeProperty(String name) {
            Map<Long, RawEntity> entityDatabase = database.get(entityDescription.getSafeName());
            for (RawEntity rawEntity : entityDatabase.values()) {
                rawEntity.storeValue(name, null);
            }
        }

        @Override
        public void createStore() {
            if (!storeExists()) {
                database.put(entityDescription.getSafeName(), new HashMap<Long, RawEntity>());
                idCounter.put(entityDescription.getSafeName(), new AtomicLong());
            }
        }

        @Override
        public void deleteStore() {
            database.remove(entityDescription.getSafeName());
            idCounter.remove(entityDescription.getSafeName());
        }

        @Override
        public void recreateStore() {
            deleteStore();
            createStore();
        }

        @Override
        public boolean storeExists() {
            return database.containsKey(entityDescription.getSafeName()) &&
                   idCounter.containsKey(entityDescription.getSafeName());
        }

        @Override
        public Torch torch() {
            return getTorchFactory().begin();
        }
    }

    public class RawEntity implements ReadableRawEntity, WritableRawEntity {

        private Map<String, Object> values = new HashMap<String, Object>();

        @Override
        public boolean isNull(String propertyName) {
            return values.containsKey(propertyName);
        }

        @Override
        public byte[] getBlob(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public Boolean getBoolean(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public boolean getBooleanPrimitive(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public Byte getByte(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public Short getShort(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public short getShortPrimitive(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public Integer getInteger(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public int getIntegerPrimitive(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public Long getLong(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public long getLongPrimitive(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public Double getDouble(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public double getDoublePrimitive(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public Float getFloat(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public float getFloatPrimitive(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public String getString(String propertyName) {
            return getValue(propertyName);
        }

        @Override
        public void put(String propertyName, String value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, Byte value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, byte value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, Short value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, short value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, Integer value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, int value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, Long value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, long value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, Float value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, float value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, Double value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, double value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, Boolean value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, boolean value) {
            storeValue(propertyName, value);
        }

        @Override
        public void put(String propertyName, byte[] value) {
            storeValue(propertyName, value);
        }

        @Override
        public void putNull(String propertyName) {
            storeValue(propertyName, null);
        }

        @SuppressWarnings("unchecked")
        protected <T> T getValue(String propertyName) {
            return (T) values.get(propertyName);
        }

        protected <T> void storeValue(String propertyName, T value) {
            values.put(propertyName, value);
        }
    }
}
