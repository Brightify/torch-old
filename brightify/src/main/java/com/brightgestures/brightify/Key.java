package com.brightgestures.brightify;

import com.brightgestures.brightify.annotation.Entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Key<T> implements Serializable {
    public static final String KEY_KIND_DELIMITER = "___";
    public static final String KEY_ID_DELIMITER = ",";

    private long mId;
    private String mKind;
    private Class<T> mType;

    private Key() {}

    private Key(Class<T> modelClass, long id) {
        mKind = getKind(modelClass);
        mId = id;
        mType = modelClass;
    }

    public long getId() {
        return mId;
    }

    public String getKind() {
        return mKind;
    }

    public Class<T> getType() {
        return mType;
    }

    public static <T> List<Key<T>> keyListFromString(String value) {
        return (List<Key<T>>) Arrays.asList(keysFromString(value));
    }

    public static <T> Key<T> keyFromString(String value) {
        Key<T>[] keys = keysFromString(value);
        if(keys.length != 1) {
            throw new IllegalArgumentException("Input value has to contain one id! Containts: " + keys.length);
        }
        return keys[0];
    }

    public static <T> Key<T>[] keysFromString(String value) {
        String[] splitted = value.split(KEY_KIND_DELIMITER);

        if(splitted.length != 2) {
            throw new IllegalArgumentException("Input value was not key string! Value: " + value);
        }

        String className = splitted[0];
        String[] idStrings = splitted[1].split(KEY_ID_DELIMITER);

        Key<T>[] keys = new Key[idStrings.length];

        int i = 0;
        for(String idString : idStrings) {
            Long id = Long.parseLong(idString);
            try {
                keys[i] = Key.create((Class<T>) Class.forName(className), id);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Input class doesn't exist! Class name: " + className);
            }
            i++;
        }

        return keys;
    }

    public static <T> String keyListToString(List<Key<T>> keys) {
        return keysToString(keys.toArray(new Key[keys.size()]));
    }

    public static <T> String keyToString(Key<T> key) {
        return keysToString(key);
    }

    public static <T> String keysToString(Key<T>... keys) {
        if(keys.length == 0) {
            return null;
        }
        Key<T> firstKey = keys[0];
        StringBuilder builder = new StringBuilder(firstKey.getType().getName())
                .append(KEY_KIND_DELIMITER);

        int i = 0;
        for(Key<T> key : keys) {
            if(i > 0) {
                builder.append(KEY_ID_DELIMITER);
            }
            builder.append(key.getId());
            i++;
        }

        return builder.toString();
    }

    public static <T> Key<T> create(Class<T> modelClass, long id) {
        return new Key<T>(modelClass, id);
    }

    public static <T> Key<T> create(T model) {
        return Entities.keyOf(model);
    }

    public static String getKind(Class<?> clazz) {
        String kind = getKindRecursive(clazz);
        if(kind == null) {
            throw new IllegalArgumentException("Class hierarchy for " + clazz + " has no @Entity annotation!");
        } else {
            return kind;
        }
    }

    private static String getKindRecursive(Class<?> clazz) {
        if(clazz == Object.class) {
            return null;
        }

        String kind = getKindHere(clazz);
        if(kind != null) {
            return kind;
        } else {
            return getKindRecursive(clazz.getSuperclass());
        }
    }

    private static String getKindHere(Class<?> clazz) {
        Entity entity = clazz.getAnnotation(Entity.class);
        if(entity != null) {
            if(entity.name() != null && entity.name().length() !=0) {
                return entity.name();
            } else {
                return clazz.getSimpleName();
            }
        }
        return null;
    }
}
