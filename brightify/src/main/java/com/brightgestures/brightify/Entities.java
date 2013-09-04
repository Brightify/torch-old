package com.brightgestures.brightify;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Entities {

    private static Map<Class<?>, EntityMetadata<?>> byClass = new HashMap<Class<?>, EntityMetadata<?>>();
    private static Map<String, EntityMetadata<?>> byKind = new HashMap<String, EntityMetadata<?>>();

    public static <T> Key<T> keyOf(T model) {
        EntityMetadata<T> metadata = getMetadataSafe(model);

        return metadata.getKey(model);
    }

    public static Collection<EntityMetadata<?>> getAllMetadatas() {
        return byClass.values();
    }

    @SuppressWarnings("unchecked")
    public static <T> EntityMetadata<T> getMetadata(T model) {
        return (EntityMetadata<T>) getMetadata(model.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <T> EntityMetadata<T> getMetadata(Class<T> clazz) {
        return (EntityMetadata<T>) byClass.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> EntityMetadata<T> getMetadataSafe(T model) {
        return (EntityMetadata<T>) getMetadataSafe(model.getClass());
    }

    public static <T> EntityMetadata<T> getMetadataSafe(Class<T> clazz) {
        EntityMetadata<T> metadata = getMetadata(clazz);

        if(metadata == null) {
            throw new IllegalStateException(clazz + " has not been registered!");
        } else {
            return metadata;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> EntityMetadata<T> getMetadataByKind(String kind) {
        return (EntityMetadata<T>) byKind.get(kind);
    }

    public static <T> EntityMetadata<T> getMetadataByKindSafe(String kind) {
        EntityMetadata<T> metadata = getMetadataByKind(kind);

        if(metadata == null) {
            throw new IllegalStateException(kind + " has not been registered!");
        } else {
            return metadata;
        }
    }

    public static <T> EntityMetadata<T> getMetadataByKey(Key<T> key) {
        return getMetadataByKind(key.getKind());
    }

    public static <T> EntityMetadata<T> getMetadataByKeySafe(Key<T> key) {
        return getMetadataByKindSafe(key.getKind());
    }

    @SuppressWarnings("unchecked")
    public static <T> Key<T> toKey(Object keyOrEntity) {
        if(keyOrEntity instanceof Key<?>) {
            return (Key<T>) keyOrEntity;
        } else { // TODO add Ref-s
            return keyOf((T) keyOrEntity);
        }
    }

    public static <T> void register(Class<T> clazz, EntityMetadata<T> metadata) {
        byClass.put(clazz, metadata);
        byKind.put(metadata.getKind(), metadata);
    }

    @SuppressWarnings("unchecked")
    public static <T> EntityMetadata<T> unregister(Class<T> entityClass) {
        EntityMetadata<?> metadata = byClass.remove(entityClass);
        byKind.remove(metadata.getKind());
        return (EntityMetadata<T>) metadata;
    }

}
