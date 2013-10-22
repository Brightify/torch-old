package com.brightgestures.brightify;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Entities {

    private static Map<Class<?>, EntityMetadata<?>> byClass = new HashMap<Class<?>, EntityMetadata<?>>();
    private static Map<String, EntityMetadata<?>> byTableName = new HashMap<String, EntityMetadata<?>>();

    public static <ENTITY> Key<ENTITY> keyOf(ENTITY entity) {
        Class<ENTITY> entityClass = (Class<ENTITY>) entity.getClass();
        EntityMetadata<ENTITY> metadata = getMetadata(entityClass);

        return Key.create(entityClass, metadata.getEntityId(entity));
    }

    public static Collection<EntityMetadata<?>> getAllMetadatas() {
        return byClass.values();
    }

    public static <ENTITY> EntityMetadata<ENTITY> getMetadata(Class<ENTITY> entityClass) {
        return (EntityMetadata<ENTITY>) byClass.get(entityClass);
    }

    public static <ENTITY> EntityMetadata<ENTITY> getMetadata(String tableName) {
        return (EntityMetadata<ENTITY>) byTableName.get(tableName);
    }

    public static <ENTITY> void findAndRegisterMetadata(Class<ENTITY> entityClass) {
        String metadataName = entityClass.getName().replaceAll("\\.", "_") + "Metadata";
        String metadataFullName = "com.brightgestures.brightify.metadata." + metadataName;

        try {
            Class.forName(metadataFullName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Entity metadata '" + metadataFullName + "' not found!", e);
        }
    }

    public static <ENTITY> void registerMetadata(EntityMetadata<ENTITY> metadata) {
        Class<ENTITY> entityClass = metadata.getEntityClass();
        String tableName = metadata.getTableName();
        if(byClass.containsKey(entityClass)) { // TODO should this be IllegalArgumentException ?
            throw new IllegalStateException("This entity's metadata are already registered! Class: " + entityClass.getName());
        }
        if(byTableName.containsKey(tableName)) {
            throw new IllegalStateException("This table name is already registered! Table name:" + tableName);
        }
        byClass.put(entityClass, metadata);
        byTableName.put(tableName, metadata);
    }

}
