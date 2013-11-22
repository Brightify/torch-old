package com.brightgestures.brightify;

import com.brightgestures.brightify.annotation.Entity;

import java.io.*;
import java.util.*;

public class Key<ENTITY> implements Serializable {
    public static final String KEY_KIND_DELIMITER = "___";
    public static final String KEY_ID_DELIMITER = ",";

    private long mId;
    private String mKind;
    private Class<ENTITY> mType;

    private Key() {}

    private Key(Class<ENTITY> entityClass, long id) {
        mKind = getKind(entityClass);
        mId = id;
        mType = entityClass;
    }

    public long getId() {
        return mId;
    }

    public String getKind() {
        return mKind;
    }

    public Class<ENTITY> getType() {
        return mType;
    }

    public static <ENTITY> Key<ENTITY> create(Class<ENTITY> entityClass, long id) {
        return new Key<ENTITY>(entityClass, id);
    }

    public static <ENTITY> Key<ENTITY> create(ENTITY entity) {
        return null;
        //return EntitiesCompatibility.keyOf(entity);
    }

    public static String getKind(Class<?> clazz) {
        return clazz.getSimpleName();
        /*String kind = getKindRecursive(clazz);
        if(kind == null) {
            throw new IllegalArgumentException("Class hierarchy for " + clazz + " has no @Entity annotation!");
        } else {
            return kind;
        }*/
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
