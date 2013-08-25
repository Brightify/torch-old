package com.brightgestures.droidorm;

import com.brightgestures.droidorm.annotation.Entity;

import java.lang.reflect.Type;

@Deprecated
public class KeyMetadata<T> {

    private Property mIdProperty;
    private Class<T> mModelClass;
    private String mKind;

    public KeyMetadata(Class<T> modelClass) {
        assert modelClass.getAnnotation(Entity.class) != null;
        mModelClass = modelClass;
        mKind = Key.getKind(modelClass);
    }

    public String getKind() {
        return mKind;
    }

    public String getIdFieldName() {
        return mIdProperty.getField().getName();
    }

    public Type getIdFieldType() {
        return mIdProperty.getType();
    }

    public void setLongId(T model, Long id) {
        if(!mModelClass.isAssignableFrom(model.getClass())) {
            throw new IllegalArgumentException("Trying to use metadata for " + mModelClass.getName() + "to set key of " + model.getClass().getName());
        }
        mIdProperty.set(model, id);
    }

    public Key<T> getKey(T model) {
        return Key.create(mModelClass, getId(model));
    }

    private Long getId(T model) {
        return (Long)mIdProperty.get(model);
    }


}
