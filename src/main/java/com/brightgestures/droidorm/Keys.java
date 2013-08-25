package com.brightgestures.droidorm;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class Keys {

    private static Map<Class<?>, KeyMetadata<?>> byClass = new HashMap<Class<?>, KeyMetadata<?>>();

    private static Map<String, KeyMetadata<?>> byKind = new HashMap<String, KeyMetadata<?>>();

    public static <T> Key<T> keyOf(T model) {
        KeyMetadata<T> metadata = getMetadataSafe(model);

        return metadata.getKey(model);
    }

    @SuppressWarnings("unchecked")
    public static <T> KeyMetadata<T> getMetadata(T model) {
        return (KeyMetadata<T>) getMetadata(model.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <T> KeyMetadata<T> getMetadata(Class<T> clazz) {
        return (KeyMetadata<T>)byClass.get(clazz);
    }

    public static <T> KeyMetadata<T> getMetadataSafe(Class<T> clazz) {
        KeyMetadata<T> metadata = getMetadata(clazz);
        if(metadata == null) {
            throw new IllegalStateException(clazz + " has not been registered!");
        } else {
            return metadata;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> KeyMetadata<T> getMetadataSafe(T model) {
        return (KeyMetadata<T>) getMetadataSafe(model.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <T> Key<T> toKey(Object keyOrEntity) {
        if(keyOrEntity instanceof Key<?>) {
            return (Key<T>) keyOrEntity;
        } else { // TODO add Ref-s
            return keyOf((T) keyOrEntity);
        }
    }

    public static <T> void register(Class<T> clazz, KeyMetadata<T> metadata) {
        byClass.put(clazz, metadata);
        byKind.put(metadata.getKind(), metadata);
    }
}
