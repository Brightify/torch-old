package com.brightgestures.brightify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class EntitiesCompatibility {
    private static Map<Class<?>, EntityMetadataCompatibility<?>> byClass = new HashMap<Class<?>, EntityMetadataCompatibility<?>>();
    private static Map<String, EntityMetadataCompatibility<?>> byKind = new HashMap<String, EntityMetadataCompatibility<?>>();

    public static <ENTITY> Key<ENTITY> keyOf(ENTITY model) {
        EntityMetadataCompatibility<ENTITY> metadata = getMetadataSafe(model);

        return metadata.getKey(model);
    }

    public static Collection<EntityMetadataCompatibility<?>> getAllMetadatas() {
        return byClass.values();
    }

    @SuppressWarnings("unchecked")
    public static <ENTITY> EntityMetadataCompatibility<ENTITY> getMetadata(ENTITY model) {
        return (EntityMetadataCompatibility<ENTITY>) getMetadata(model.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <ENTITY> EntityMetadataCompatibility<ENTITY> getMetadata(Class<ENTITY> clazz) {
        return (EntityMetadataCompatibility<ENTITY>) byClass.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <ENTITY> EntityMetadataCompatibility<ENTITY> getMetadataSafe(ENTITY model) {
        return (EntityMetadataCompatibility<ENTITY>) getMetadataSafe(model.getClass());
    }

    public static <ENTITY> EntityMetadataCompatibility<ENTITY> getMetadataSafe(Class<ENTITY> clazz) {
        EntityMetadataCompatibility<ENTITY> metadata = getMetadata(clazz);

        if(metadata == null) {
            throw new IllegalStateException(clazz + " has not been registered!");
        } else {
            return metadata;
        }
    }

    @SuppressWarnings("unchecked")
    public static <ENTITY> EntityMetadataCompatibility<ENTITY> getMetadataByKind(String kind) {
        return (EntityMetadataCompatibility<ENTITY>) byKind.get(kind);
    }

    public static <ENTITY> EntityMetadataCompatibility<ENTITY> getMetadataByKindSafe(String kind) {
        EntityMetadataCompatibility<ENTITY> metadata = getMetadataByKind(kind);

        if(metadata == null) {
            throw new IllegalStateException(kind + " has not been registered!");
        } else {
            return metadata;
        }
    }

    public static <ENTITY> EntityMetadataCompatibility<ENTITY> getMetadataByKey(Key<ENTITY> key) {
        return getMetadataByKind(key.getKind());
    }

    public static <ENTITY> EntityMetadataCompatibility<ENTITY> getMetadataByKeySafe(Key<ENTITY> key) {
        return getMetadataByKindSafe(key.getKind());
    }

    @SuppressWarnings("unchecked")
    public static <ENTITY> Key<ENTITY> toKey(Object keyOrEntity) {
        if(keyOrEntity instanceof Key<?>) {
            return (Key<ENTITY>) keyOrEntity;
        } else { // TODO add Ref-s
            return keyOf((ENTITY) keyOrEntity);
        }
    }

    public static int count() {
        return byClass.size();
    }

    static <ENTITY> EntityMetadataCompatibility<ENTITY> register(Class<ENTITY> entityClass) {
        EntityMetadataCompatibility<ENTITY> metadata = new EntityMetadataCompatibility<ENTITY>(entityClass);
        register(entityClass, metadata);
        return metadata;
    }

    static <ENTITY> void register(Class<ENTITY> entityClass, EntityMetadataCompatibility<ENTITY> metadata) {
        byClass.put(entityClass, metadata);
        byKind.put(metadata.getKind(), metadata);
    }

    @SuppressWarnings("unchecked")
    static <ENTITY> EntityMetadataCompatibility<ENTITY> unregister(Class<ENTITY> entityClass) {
        EntityMetadataCompatibility<?> metadata = byClass.remove(entityClass);
        byKind.remove(metadata.getKind());
        return (EntityMetadataCompatibility<ENTITY>) metadata;
    }

    static void unregisterAll() {
        ArrayList<EntityMetadataCompatibility<?>> metadatas = new ArrayList<EntityMetadataCompatibility<?>>(byClass.values());
        for(EntityMetadataCompatibility<?> metadata : metadatas) {
            unregister(metadata.getEntityClass());
        }
    }

}
