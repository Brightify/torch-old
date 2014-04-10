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
}
