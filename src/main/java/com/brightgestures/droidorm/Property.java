package com.brightgestures.droidorm;

import com.brightgestures.droidorm.annotation.Id;
import com.brightgestures.droidorm.annotation.Index;
import com.brightgestures.droidorm.annotation.Unindex;
import com.brightgestures.droidorm.util.ClassHashMap;
import com.brightgestures.droidorm.util.Helper;
import com.brightgestures.droidorm.util.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Property {

    private String mColumnName;

    private Field mField;

    private ClassHashMap<Annotation> mAnnotationMap = new ClassHashMap<Annotation>();

    public Property(Field field) {
        mColumnName = Helper.deCamelize(field.getName());
        mField = field;

        field.setAccessible(true);

        Annotation[] annotations = field.getAnnotations();
        for(Annotation annotation : annotations) {
            mAnnotationMap.putObject(annotation);
        }

        if(isId() && !TypeUtils.isAssignableFrom(Long.class, getType())) {
            throw new IllegalStateException("Property with @Id cannot be null!");
        }

        if(isIndex() && isUnindex()) {
            throw new IllegalStateException("Cannot have @Indexed and @Unindexed on the same field: " + field);
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
        return mAnnotationMap.getObject(annotationClass);
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
