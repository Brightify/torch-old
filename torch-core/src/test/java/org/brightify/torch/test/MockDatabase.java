package org.brightify.torch.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MockDatabase {

    private Map<String, Map<RawEntity.PrimaryKey, RawEntity>> data =
            new HashMap<String, Map<RawEntity.PrimaryKey, RawEntity>>();
    private Map<String, AtomicLong> idCounter = new HashMap<String, AtomicLong>();

    public void open() {
        data = new HashMap<String, Map<RawEntity.PrimaryKey, RawEntity>>();
        idCounter = new HashMap<String, AtomicLong>();
    }

    public void wipe() {
        close();
        open();
    }

    public void close() {
        data = null;
        idCounter = null;
    }

    public void prepareStore(String entityName) {
        data.put(entityName, new HashMap<RawEntity.PrimaryKey, RawEntity>());
        idCounter.put(entityName, new AtomicLong(1));
    }

    public void deleteStore(String entityName) {
        data.remove(entityName);
        idCounter.remove(entityName);
    }

    public boolean storeExists(String entityName) {
        return data.containsKey(entityName) && idCounter.containsKey(entityName);
    }

    @Deprecated
    public Map<RawEntity.PrimaryKey, RawEntity> getEntityDatabase(String entityName) {
        return data.get(entityName);
    }

    public boolean isOpen() {
        return data != null && idCounter != null;
    }

    public MockTransaction beginTransaction() {
        return new MockTransaction();
    }

    public class MockTransaction {

        private Map<String, Map<RawEntity.PrimaryKey, RawEntity>> transactionData =
                new HashMap<String, Map<RawEntity.PrimaryKey, RawEntity>>();
        private Map<String, AtomicLong> transactionIdCounter = new HashMap<String, AtomicLong>();

        public long acquireNewId(String entityName) {
            if(!transactionIdCounter.containsKey(entityName)) {
                transactionIdCounter.put(entityName, new AtomicLong(idCounter.get(entityName).get()));
            }

            return transactionIdCounter.get(entityName).getAndIncrement();
        }

        public void store(String entityName, RawEntity.PrimaryKey primaryKey, RawEntity entity) {
            if(!transactionData.containsKey(entityName)) {
                transactionData.put(entityName, new HashMap<RawEntity.PrimaryKey, RawEntity>());
            }

            transactionData.get(entityName).put(primaryKey, entity);
        }

        public void commit() {
            for (String safeName : transactionData.keySet()) {
                data.get(safeName).putAll(transactionData.get(safeName));
                idCounter.putAll(transactionIdCounter);
            }
        }

    }

}
