package com.brightgestures.brightify;

import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.annotation.Index;
import com.brightgestures.brightify.util.AnnotationMap;
import com.brightgestures.brightify.util.Helper;
import com.brightgestures.brightify.util.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class EntityMetadata<ENTITY> {

    private final Class<ENTITY> mEntityClass;
    private final String mKind;
    private final String mTableName;
    private final Property mIdProperty;
    private final List<Property> mProperties = new ArrayList<Property>();
    private final AnnotationMap mAnnotationMap = new AnnotationMap();

    private boolean mIndex;

    public EntityMetadata(Class<ENTITY> entityClass) {
        if(!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Model has to be annotated with @Entity!");
        }

        mEntityClass = entityClass;
        mKind = Key.getKind(entityClass);
        mTableName = Helper.tableNameFromClass(entityClass, false);
        mIndex = entityClass.isAnnotationPresent(Index.class);

        Property idProperty = null;
        Field[] fields = entityClass.getDeclaredFields();
        for(Field field : fields) {
            if(!TypeUtils.isOfInterest(field)) {
                continue;
            }

            Property property = new Property(field);

            if(property.isId()) {
                if(idProperty != null) {
                    throw new IllegalStateException("Entity can only have one field with @Id annotation!");
                }
                idProperty = property;
            }

            mProperties.add(property);
        }

        if(idProperty == null) {
            throw new IllegalStateException("Entity has to have exactly one property with @Id annotation!");
        }

        mIdProperty = idProperty;
    }

    public <A extends Annotation> boolean isAnnotationPresent(Class<A> annotationClass) {
        return getAnnotation(annotationClass) != null;
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return mAnnotationMap.getAnnotation(annotationClass);
    }

    public final List<Property> getProperties() {
        return mProperties;
    }

    public final Class<ENTITY> getEntityClass() {
        return mEntityClass;
    }

    public final String getKind() {
        return mKind;
    }

    public final Key<ENTITY> getKey(ENTITY entity) {
        return Key.create(mEntityClass, getId(entity));
    }

    public final Long getId(ENTITY entity) {
        return (Long) mIdProperty.get(entity);
    }

    public final void setId(ENTITY entity, Long id) {
        mIdProperty.set(entity, id);
    }

    public final Property getIdProperty() {
        return mIdProperty;
    }

    public final String getTableName() {
        return mTableName;
    }

    public final boolean isIndex() {
        return mIndex;
    }
}
