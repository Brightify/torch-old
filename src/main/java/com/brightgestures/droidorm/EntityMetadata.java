package com.brightgestures.droidorm;

import com.brightgestures.droidorm.annotation.Entity;
import com.brightgestures.droidorm.annotation.Index;
import com.brightgestures.droidorm.util.Helper;
import com.brightgestures.droidorm.util.TypeUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class EntityMetadata<T> {

    private final Class<T> mEntityClass;
    private final String mKind;
    private final String mTableName;
    private final Property mIdProperty;
    private final List<Property> mProperties = new ArrayList<Property>();
    // TODO add ClassHashMap for annotations - yes probably

    private boolean mIndex;

    public EntityMetadata(Class<T> entityClass) {
        if(!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Model has to be annotated with @Entity!");
        }

        mEntityClass = entityClass;
        mKind = Key.getKind(entityClass);
        mTableName = Helper.deCamelize(entityClass.getSimpleName());
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

    public final List<Property> getProperties() {
        return mProperties;
    }

    public final Class<?> getEntityClass() {
        return mEntityClass;
    }

    public final String getKind() {
        return mKind;
    }

    public final Key<T> getKey(T entity) {
        return Key.create(mEntityClass, getId(entity));
    }

    public final Long getId(T entity) {
        return (Long) mIdProperty.get(entity);
    }

    public final void setId(T entity, Long id) {
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
