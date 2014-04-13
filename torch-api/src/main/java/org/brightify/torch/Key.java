package org.brightify.torch;

import java.io.Serializable;

/**
 * Key is unique definition of an entity and should be used instead of using plain entity id.
 *
 * @param <ENTITY>
 */
public class Key<ENTITY> implements Serializable {

    private final long id;
    private final Class<ENTITY> entityClass;

    Key(Class<ENTITY> entityClass, long id) {
        this.id = id;
        this.entityClass = entityClass;
    }

    public long getId() {
        return id;
    }

    public Class<ENTITY> getType() {
        return entityClass;
    }

    public static <ENTITY> Key<ENTITY> create(Class<ENTITY> entityClass, long id) {
        return new Key<ENTITY>(entityClass, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Key<?> key = (Key<?>) o;

        if (id != key.id) {
            return false;
        }
        if (entityClass != null ? !entityClass.equals(key.entityClass) : key.entityClass != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (entityClass != null ? entityClass.hashCode() : 0);
        return result;
    }
}
