package com.brightgestures.brightify;

import javax.annotation.Nonnull;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class KeyFactory {

    /**
     *
     *
     * @param entityClass
     * @param id
     * @param <ENTITY>
     * @return
     */
    public static <ENTITY> Key<ENTITY> create(Class<ENTITY> entityClass, Long id) {
        if(id == null) {
            throw new IllegalArgumentException("Cannot create Key with null ID!");
        }

        return new Key<>(entityClass, id);
    }

    public static <ENTITY> Key<ENTITY> create(ENTITY entity) {
        Class<ENTITY> entityClass = (Class<ENTITY>) entity.getClass();
        EntityMetadata<ENTITY> metadata = Entities.getMetadata(entityClass);



        Long id = metadata.getEntityId(entity);

        return create(entityClass, id);
    }

}
