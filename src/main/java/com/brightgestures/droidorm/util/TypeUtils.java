package com.brightgestures.droidorm.util;

import com.brightgestures.droidorm.DatabaseFactory;
import com.brightgestures.droidorm.Property;
import com.brightgestures.droidorm.sql.TypeName;
import com.brightgestures.droidorm.sql.affinity.*;
import com.brightgestures.droidorm.annotation.Ignore;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class TypeUtils {

    static final int NOT_SAVEABLE_MODIFIERS = Modifier.FINAL | Modifier.STATIC;

    static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap<Class<?>, Class<?>>();
    static final Map<Class<?>, Class<? extends TypeName>> TYPE_TO_AFFINITY = new HashMap<Class<?>, Class<? extends TypeName>>();

    static {
        PRIMITIVE_TO_WRAPPER.put(boolean.class, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(byte.class, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(short.class, Short.class);
        PRIMITIVE_TO_WRAPPER.put(int.class, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(long.class, Long.class);
        PRIMITIVE_TO_WRAPPER.put(float.class, Float.class);
        PRIMITIVE_TO_WRAPPER.put(double.class, Double.class);
        PRIMITIVE_TO_WRAPPER.put(char.class, Character.class);

        TYPE_TO_AFFINITY.put(Byte.class, IntegerAffinity.class);
        TYPE_TO_AFFINITY.put(Short.class, IntegerAffinity.class);
        TYPE_TO_AFFINITY.put(Integer.class, IntegerAffinity.class);
        TYPE_TO_AFFINITY.put(Long.class, IntegerAffinity.class);
        TYPE_TO_AFFINITY.put(Boolean.class, IntegerAffinity.class);

        TYPE_TO_AFFINITY.put(CharSequence.class, TextAffinity.class);
        TYPE_TO_AFFINITY.put(String.class, TextAffinity.class);
        // TODO should we add StringBuilder?

        TYPE_TO_AFFINITY.put(Serializable.class, NoneAffinity.class);

        TYPE_TO_AFFINITY.put(Float.class, RealAffinity.class);
        TYPE_TO_AFFINITY.put(Double.class, RealAffinity.class);
        // TODO add more later
    }

    public static <T> Constructor<T> getNoArgConstructor(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            if(clazz.isMemberClass() || clazz.isAnonymousClass() || clazz.isLocalClass()) {
                throw new IllegalStateException(clazz.getName() + " must be static and must have a no-arg constructor", e);
            } else {
                throw new IllegalStateException(clazz.getName() + " must have a no-arg constructor", e);
            }
        }
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... args) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(args);
            constructor.setAccessible(true);
            return constructor;

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(clazz.getName() + " has no constructor with args " + Arrays.toString(args), e);
        }
    }


    public static <T> T construct(Class<T> type) {
        Constructor<T> constructor = TypeUtils.getNoArgConstructor(type);
        return TypeUtils.newInstance(constructor);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Collection<?>> T constructCollection(Class<T> type, int size) {
        if((Class<?>) type == List.class || (Class<?>) type == Collection.class) {
            return (T) new ArrayList<Object>(size);
        } else if((Class<?>) type == Set.class) {
            return (T) new HashSet<Object>((int)(size * 1.5));
        } else if((Class<?>) type == SortedSet.class) {
            return (T) new TreeSet<Object>();
        } else {
            return construct(type);
        }
    }

    public static boolean isOfInterest(Field field) {
        return !field.isAnnotationPresent(Ignore.class)
                && ((field.getModifiers() & NOT_SAVEABLE_MODIFIERS) == 0)
                && !field.isSynthetic();
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Constructor<T> constructor, Object... params) {
        try {
            return constructor.newInstance(params);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object field_get(Field field, Object obj) {
        try {
            return field.get(obj);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void field_set(Field field, Object obj, Object value) {
        try {
            field.set(obj, value);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Property> getProperties(DatabaseFactory factory, Class<?> clazz) {
        List<Property> properties = new ArrayList<Property>();
        getProperties(factory, clazz, properties, clazz);
        return properties;
    }

    private static void getProperties(DatabaseFactory factory, Class<?> clazz, List<Property> properties, Class<?> topClass) {
        if(clazz == null || clazz == Object.class) {
            return;
        }

        getProperties(factory, clazz.getSuperclass(), properties, topClass);

        for(Field field : clazz.getDeclaredFields()) {
            if(isOfInterest(field)) {
                properties.add(new Property(field));
            }
        }

        // TODO maybe add methods later
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch(NoSuchFieldException e) {
            if(clazz.getSuperclass() == Object.class) {
                throw e;
            } else {
                return getDeclaredField(clazz.getSuperclass(), fieldName);
            }
        }
    }

    public static Class<? extends TypeName> affinityFromClass(Class<?> type) {
        if(type.isPrimitive()) {
            type = PRIMITIVE_TO_WRAPPER.get(type);
        }

        if(TYPE_TO_AFFINITY.containsKey(type)) {
            return TYPE_TO_AFFINITY.get(type);
        } else if(Serializable.class.isAssignableFrom(type)) {
            return TYPE_TO_AFFINITY.get(Serializable.class);
        } else {
            throw new IllegalArgumentException("Type \"" + type.getName() + "\" isn't supported! " +
                    "You can store custom objects if they implements Serializable interface.");
        }
    }

    public static boolean isAssignableFrom(Class<?> to, Class<?> from) {
        Class<?> notPrimitiveTo = to.isPrimitive() ? PRIMITIVE_TO_WRAPPER.get(to) : to;
        Class<?> notPrimitiveFrom = from.isPrimitive() ? PRIMITIVE_TO_WRAPPER.get(from) : from;

        return notPrimitiveTo.isAssignableFrom(notPrimitiveFrom);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A getAnnotation(Class<A> annotationClass, Annotation[] annotations) {
        for(Annotation annotation : annotations) {
            if(annotationClass.isAssignableFrom(annotation.getClass())) {
                return (A)annotation;
            }
        }
        return null;
    }

    public static <A extends Annotation> A getAnnotation(Class<A> annotationClass, Property property, Class<?> onClass) {
        A annotation = property.getAnnotation(annotationClass);
        if(annotation == null) {
            return onClass.getAnnotation(annotationClass);
        } else {
            return annotation;
        }
    }
}
