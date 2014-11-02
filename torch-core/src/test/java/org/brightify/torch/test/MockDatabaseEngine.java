package org.brightify.torch.test;

import org.brightify.torch.DatabaseEngine;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.ReadableRawEntity;
import org.brightify.torch.Torch;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.WritableRawEntity;
import org.brightify.torch.action.load.LoadQuery;
import org.brightify.torch.action.load.OrderLoader;
import org.brightify.torch.filter.Property;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.functional.EditFunction;

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
    private Map<String, Map<Long, RawEntity>> database = new HashMap<String, Map<Long, RawEntity>>();
    private Map<String, Long> idCounter = new HashMap<String, Long>();

    @Override
    public <ENTITY> void each(LoadQuery<ENTITY> loadQuery, EditFunction<ENTITY> function) {
        List<ENTITY> entities = load(loadQuery);
        List<ENTITY> saveEntities = new ArrayList<ENTITY>();
        for (ENTITY entity : entities) {
            if(function.apply(entity)) {
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
        if(iterator.hasNext()) {
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
        Long counter = idCounter.get(description.getSafeName());
        Map<Long, RawEntity> transaction = new HashMap<Long, RawEntity>();

        for (ENTITY entity : entities) {
            RawEntity rawEntity = new RawEntity();

            try {
                description.setEntityId(entity, counter);
                description.toRawEntity(torchFactory, entity, rawEntity);
                results.put(entity, counter);
                transaction.put(counter, rawEntity);
                counter++;
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
            Long id = description.getEntityId(entity);
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

    @Override
    public boolean wipe() {
        database = new HashMap<String, Map<Long, RawEntity>>();
        idCounter = new HashMap<String, Long>();
        return true;
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

        List<ENTITY> result = new ArrayList<ENTITY>();
        for (RawEntity rawEntity : rawEntities) {
            ENTITY entity = description.createEmpty();
            try {
                description.setFromRawEntity(torchFactory, rawEntity, entity, loadQuery.getLoadGroups());
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
            if(!storeExists()) {
                database.put(entityDescription.getSafeName(), new HashMap<Long, RawEntity>());
                idCounter.put(entityDescription.getSafeName(), 0L);
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
