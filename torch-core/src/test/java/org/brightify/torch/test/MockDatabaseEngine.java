package org.brightify.torch.test;

import org.brightify.torch.DatabaseEngine;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.Key;
import org.brightify.torch.ReadableRawEntity;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.WritableRawEntity;
import org.brightify.torch.action.load.LoadQuery;
import org.brightify.torch.action.load.sync.OrderLoader;
import org.brightify.torch.filter.Property;
import org.brightify.torch.util.MigrationAssistant;

import java.util.ArrayList;
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
    private Map<Class<?>, Map<Long, RawEntity>> database = new HashMap<Class<?>, Map<Long, RawEntity>>();
    private Map<Class<?>, Long> idCounter = new HashMap<Class<?>, Long>();

    @Override
    public <ENTITY> Iterator<ENTITY> load(LoadQuery<ENTITY> loadQuery) {
        return loadList(loadQuery).iterator();
    }

    @Override
    public <ENTITY> int count(LoadQuery<ENTITY> loadQuery) {
        return loadList(loadQuery).size();
    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, ENTITY> save(Iterable<ENTITY> entities) {
        Map<Key<ENTITY>, ENTITY> results = new HashMap<Key<ENTITY>, ENTITY>();
        Iterator<ENTITY> iterator = entities.iterator();
        if (!iterator.hasNext()) {
            return results;
        }

        Class<ENTITY> entityClass = (Class<ENTITY>) iterator.next().getClass();
        EntityDescription<ENTITY> description = torchFactory.getEntities().getDescription(entityClass);
        Long counter = idCounter.get(entityClass);
        Map<Long, RawEntity> transaction = new HashMap<Long, RawEntity>();

        for (ENTITY entity : entities) {
            RawEntity rawEntity = new RawEntity();

            try {
                description.setEntityId(entity, counter);
                description.toRawEntity(torchFactory, entity, rawEntity);
                results.put(description.createKey(entity), entity);
                transaction.put(counter, rawEntity);
                counter++;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        database.get(entityClass).putAll(transaction);

        return results;
    }

    @Override
    public <ENTITY> Map<Key<ENTITY>, Boolean> delete(Iterable<Key<ENTITY>> keys) {
        Map<Key<ENTITY>, Boolean> result = new HashMap<Key<ENTITY>, Boolean>();
        Iterator<Key<ENTITY>> iterator = keys.iterator();
        if (!iterator.hasNext()) {
            return result;
        }

        Map<Long, RawEntity> entityDatabase = database.get(iterator.next().getType());

        for (Key<ENTITY> key : keys) {
            RawEntity rawEntity = entityDatabase.remove(key.getId());
            result.put(key, rawEntity != null);
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

    @Override
    public boolean wipe() {
        database = new HashMap<Class<?>, Map<Long, RawEntity>>();
        idCounter = new HashMap<Class<?>, Long>();
        return true;
    }

    private <ENTITY> ArrayList<ENTITY> loadList(final LoadQuery<ENTITY> loadQuery) {
        EntityDescription<ENTITY> description = loadQuery.getEntityDescription();
        Map<Long, RawEntity> entityDatabase = database.get(description.getEntityClass());

        List<RawEntity> rawEntities = new ArrayList<RawEntity>();
        for (RawEntity rawEntity : entityDatabase.values()) {
            if (loadQuery.getFilter() != null && !MockDatabaseFilter.applyFilter(loadQuery.getFilter(), rawEntity)) {
                continue;
            }

            rawEntities.add(rawEntity);
        }

        for (final Map.Entry<Property<?>, OrderLoader.Direction> orderEntry : loadQuery.getOrderMap().entrySet()) {
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

        ArrayList<ENTITY> result = new ArrayList<ENTITY>();
        for (RawEntity rawEntity : rawEntities) {
            ENTITY entity;
            try {
                entity = description.createFromRawEntity(torchFactory, rawEntity, loadQuery.getLoadGroups());
            } catch (Exception e) {
                // FIXME handle the exception better
                throw new RuntimeException(e);
            }
            result.add(entity);

        }
        return result;
    }

    public class MockMigrationAssistant<ENTITY> implements MigrationAssistant<ENTITY> {

        private final EntityDescription<ENTITY> entityDescription;
        private final Class<ENTITY> entityClass;

        public MockMigrationAssistant(EntityDescription<ENTITY> entityDescription) {
            this.entityDescription = entityDescription;
            entityClass = entityDescription.getEntityClass();
        }

        @Override
        public void addProperty(Property<?> property) {

        }

        @Override
        public void changePropertyType(Property<?> property, Class<?> from, Class<?> to) {

        }

        @Override
        public void renameProperty(String from, String to) {
            Map<Long, RawEntity> entityDatabase = database.get(entityClass);
            for (RawEntity rawEntity : entityDatabase.values()) {
                Object value = rawEntity.getValue(from);
                rawEntity.storeValue(from, null);
                rawEntity.storeValue(to, value);
            }
        }

        @Override
        public void removeProperty(String name) {
            Map<Long, RawEntity> entityDatabase = database.get(entityClass);
            for (RawEntity rawEntity : entityDatabase.values()) {
                rawEntity.storeValue(name, null);
            }
        }

        @Override
        public void createStore() {
            database.put(entityClass, new HashMap<Long, RawEntity>());
            idCounter.put(entityClass, 0L);
        }

        @Override
        public void deleteStore() {
            database.remove(entityClass);
            idCounter.remove(entityClass);
        }

        @Override
        public void recreateStore() {
            deleteStore();
            createStore();
        }

        @Override
        public boolean storeExists() {
            return database.containsKey(entityClass) && idCounter.containsKey(entityClass);
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
