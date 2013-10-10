package com.brightgestures.brightify;

import com.brightgestures.brightify.annotation.Id;
import com.brightgestures.brightify.annotation.Index;
import com.brightgestures.brightify.annotation.Load;
import com.brightgestures.brightify.annotation.Unindex;
import com.brightgestures.brightify.util.AnnotationMap;
import com.brightgestures.brightify.util.Helper;
import com.brightgestures.brightify.util.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Property {

    private String mColumnName;

    private Field mField;

    private AnnotationMap mAnnotationMap = new AnnotationMap();

    private Class<?>[] loadGroups;
    private Class<?>[] loadUnlessGroups;

    public Property(Field field) {
        mColumnName = field.getName();
        mField = field;

        field.setAccessible(true);

        Annotation[] annotations = field.getAnnotations();
        for(Annotation annotation : annotations) {
            mAnnotationMap.putAnnotation(annotation);
        }

        if(isId() && !TypeUtils.isAssignableFrom(Long.class, getType())) {
            throw new IllegalStateException("Property with @Id cannot be null!");
        }

        if(isIndex() && isUnindex()) {
            throw new IllegalStateException("Cannot have @Indexed and @Unindexed on the same field: " + field);
        }

        Load load = getAnnotation(Load.class);
        if(load != null) {
            loadGroups = load.value();

            if(load.unless().length > 0) {
                loadUnlessGroups = load.unless();
            }
        }

        // TODO add @Index and @Unindex support

        // TODO add @IgnoreSave support
    }

    public String getColumnName() {
        return mColumnName;
    }

    public <A extends Annotation> boolean isAnnotationPresent(Class<A> annotationClass) {
        return getAnnotation(annotationClass) != null;
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return mAnnotationMap.getAnnotation(annotationClass);
    }

    public Field getField() {
        return mField;
    }

    public Class<?> getType() {
        return mField.getType();
    }

    public boolean isId() {
        return getAnnotation(Id.class) != null;
    }

    public boolean isIndex() {
        return getAnnotation(Index.class) != null;
    }

    public boolean isUnindex() {
        return getAnnotation(Unindex.class) != null;
    }

    public boolean shouldLoad(Class<?>[] groups) {
        if(loadGroups == null) {
            return false;
        }
        if (loadGroups.length > 0 && !matches(groups, loadGroups)) {
            return false;
        }
        if (loadUnlessGroups != null && matches(groups, loadUnlessGroups)) {
            return false;
        }

        return true;
    }

    private boolean matches(Class<?>[] groups, Class<?>[] loadGroups) {
        for(Class<?> propertyGroup : loadGroups) {
            for(Class<?> enabledGroup : groups) {
                if(propertyGroup.isAssignableFrom(enabledGroup)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Object get(Object entity) {
        try {
            return mField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Object entity, Object value) {
        try {
            mField.set(entity, value);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
