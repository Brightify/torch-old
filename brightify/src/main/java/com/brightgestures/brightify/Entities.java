package com.brightgestures.brightify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Entities {
    private static Map<Class<?>, EntityMetadata<?>> byClass = new HashMap<Class<?>, EntityMetadata<?>>();
    private static Map<String, EntityMetadata<?>> byKind = new HashMap<String, EntityMetadata<?>>();

    public static <ENTITY> Key<ENTITY> keyOf(ENTITY model) {
        EntityMetadata<ENTITY> metadata = getMetadataSafe(model);

        return metadata.getKey(model);
    }

    public static Collection<EntityMetadata<?>> getAllMetadatas() {
        return byClass.values();
    }

    @SuppressWarnings("unchecked")
    public static <ENTITY> EntityMetadata<ENTITY> getMetadata(ENTITY model) {
        return (EntityMetadata<ENTITY>) getMetadata(model.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <ENTITY> EntityMetadata<ENTITY> getMetadata(Class<ENTITY> clazz) {
        return (EntityMetadata<ENTITY>) byClass.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <ENTITY> EntityMetadata<ENTITY> getMetadataSafe(ENTITY model) {
        return (EntityMetadata<ENTITY>) getMetadataSafe(model.getClass());
    }

    public static <ENTITY> EntityMetadata<ENTITY> getMetadataSafe(Class<ENTITY> clazz) {
        EntityMetadata<ENTITY> metadata = getMetadata(clazz);

        if(metadata == null) {
            throw new IllegalStateException(clazz + " has not been registered!");
        } else {
            return metadata;
        }
    }

    @SuppressWarnings("unchecked")
    public static <ENTITY> EntityMetadata<ENTITY> getMetadataByKind(String kind) {
        return (EntityMetadata<ENTITY>) byKind.get(kind);
    }

    public static <ENTITY> EntityMetadata<ENTITY> getMetadataByKindSafe(String kind) {
        EntityMetadata<ENTITY> metadata = getMetadataByKind(kind);

        if(metadata == null) {
            throw new IllegalStateException(kind + " has not been registered!");
        } else {
            return metadata;
        }
    }

    public static <ENTITY> EntityMetadata<ENTITY> getMetadataByKey(Key<ENTITY> key) {
        return getMetadataByKind(key.getKind());
    }

    public static <ENTITY> EntityMetadata<ENTITY> getMetadataByKeySafe(Key<ENTITY> key) {
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

    static <ENTITY> EntityMetadata<ENTITY> register(Class<ENTITY> entityClass) {
        EntityMetadata<ENTITY> metadata = new EntityMetadata<ENTITY>(entityClass);
        register(entityClass, metadata);
        return metadata;
    }

    static <ENTITY> void register(Class<ENTITY> entityClass, EntityMetadata<ENTITY> metadata) {
        byClass.put(entityClass, metadata);
        byKind.put(metadata.getKind(), metadata);
    }

    @SuppressWarnings("unchecked")
    static <ENTITY> EntityMetadata<ENTITY> unregister(Class<ENTITY> entityClass) {
        EntityMetadata<?> metadata = byClass.remove(entityClass);
        byKind.remove(metadata.getKind());
        return (EntityMetadata<ENTITY>) metadata;
    }

    static void unregisterAll() {
        ArrayList<EntityMetadata<?>> metadatas = new ArrayList<EntityMetadata<?>>(byClass.values());
        for(EntityMetadata<?> metadata : metadatas) {
            unregister(metadata.getEntityClass());
        }
    }

}
