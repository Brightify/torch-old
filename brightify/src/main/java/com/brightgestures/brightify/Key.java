package com.brightgestures.brightify;

import com.brightgestures.brightify.annotation.Entity;

import java.io.*;
import java.util.*;

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

    public static <T> Key<T> keyFromByteArray(byte[] data) {
        List<Key<T>> keys = keysFromByteArray(data);
        if(keys.size() == 0) {
            return null;
        }
        if(keys.size() > 1) {
            throw new IllegalArgumentException("Input value has to contain one id! Containts: " + keys.size());
        }
        return keys.iterator().next();
    }

    public static <T> List<Key<T>> keysFromByteArray(byte[] data) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        DataInputStream inputStream = new DataInputStream(byteArrayInputStream);
        List<Key<T>> keys = new ArrayList<Key<T>>();

        try {
            int keyCount = inputStream.readInt();
            if(keyCount == 0) {
                return keys;
            }

            String className = inputStream.readUTF();
            Class<T> entityClass = (Class<T>) Class.forName(className);
            for(int i = 0; i < keyCount; i++) {
                long id = inputStream.readLong();
                Key<T> key = Key.create(entityClass, id);
                keys.add(key);
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO this has to be changed
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e); // TODO this has to be changed
        }

        return keys;
    }

    public static <T> byte[] keyToByteArray(Key<T> key) {
        return keysToByteArray(Collections.singletonList(key));
    }

    public static <T> byte[] keysToByteArray(List<Key<T>> keys) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            outputStream.writeInt(keys.size());
            if(keys.size() > 0) {
                Key<T> firstKey = keys.iterator().next();
                outputStream.writeUTF(firstKey.getType().getName());

                for(Key<T> key : keys) {
                    outputStream.writeLong(key.getId());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO this has to be changed
        }

        return byteArrayOutputStream.toByteArray();
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
