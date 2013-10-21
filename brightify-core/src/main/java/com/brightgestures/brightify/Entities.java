package com.brightgestures.brightify;

import java.util.Collection;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Entities {

    public static <ENTITY> Key<ENTITY> keyOf(ENTITY entity) {
        return null;
    }

    public static Collection<EntityMetadata<?>> getAllMetadatas() {
        return EntityMetadataMap.byClass.values();
    }

    public static <ENTITY> EntityMetadata<ENTITY> getMetadata(Class<ENTITY> entityClass) {
        return (EntityMetadata<ENTITY>) EntityMetadataMap.byClass.get(entityClass);
    }

}
