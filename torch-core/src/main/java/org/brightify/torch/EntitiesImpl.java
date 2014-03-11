package org.brightify.torch;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntitiesImpl implements Entities {

    private Map<Class<?>, EntityMetadata<?>> byClass = new HashMap<Class<?>, EntityMetadata<?>>();
    private Map<String, EntityMetadata<?>> byTableName = new HashMap<String, EntityMetadata<?>>();

    @Override
    public <ENTITY> Key<ENTITY> keyOf(ENTITY entity) {
        Class<ENTITY> entityClass = (Class<ENTITY>) entity.getClass();
        EntityMetadata<ENTITY> metadata = getMetadata(entityClass);

        return KeyFactory.create(entityClass, metadata.getEntityId(entity));
    }

    public void clear() {
        byClass.clear();
        byTableName.clear();
    }

    @Override
    public Collection<EntityMetadata<?>> getAllMetadatas() {
        return byClass.values();
    }

    @Override
    public <ENTITY> EntityMetadata<ENTITY> getMetadata(Class<ENTITY> entityClass) {
        return (EntityMetadata<ENTITY>) byClass.get(entityClass);
    }

    @Override
    public <ENTITY> EntityMetadata<ENTITY> getMetadata(String tableName) {
        return (EntityMetadata<ENTITY>) byTableName.get(tableName);
    }

    public <ENTITY> void registerMetadata(EntityMetadata<ENTITY> metadata) {
        Class<ENTITY> entityClass = metadata.getEntityClass();
        String tableName = metadata.getTableName();
        if (!byClass.containsKey(entityClass)) { // TODO should this be IllegalArgumentException ?
            byClass.put(entityClass, metadata);
            // throw new IllegalStateException("This entity's metadata are already registered! Class: " + entityClass
            // .getName());
        }
        if (!byTableName.containsKey(tableName)) {
            byTableName.put(tableName, metadata);
            // throw new IllegalStateException("This table name is already registered! Table name:" + tableName);
        }

    }

    public static <ENTITY> EntityMetadata<ENTITY> findMetadata(Class<ENTITY> entityClass) {
        // TODO Move "Metadata" to constant which is consistent through torch-compiler (probably Settings class)
        String metadataName = entityClass.getName() + "$";

        try {
            Class<EntityMetadata<ENTITY>> metadataClass = (Class<EntityMetadata<ENTITY>>) Class.forName(metadataName);
            Constructor<EntityMetadata<ENTITY>> constructor = metadataClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Entity metadata '" + metadataName + "' doesn't exist!", e);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Entity metadata '" + metadataName + "' doesn't exist!", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Entity metadata '" + metadataName + "' doesn't exist!", e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Entity metadata '" + metadataName + "' doesn't exist!", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Entity metadata '" + metadataName + "' doesn't exist!", e);
        }
    }

}
