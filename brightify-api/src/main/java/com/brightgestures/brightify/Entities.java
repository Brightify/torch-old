package com.brightgestures.brightify;

import java.util.Collection;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public interface Entities {
    <ENTITY> Key<ENTITY> keyOf(ENTITY entity);

    Collection<EntityMetadata<?>> getAllMetadatas();

    <ENTITY> EntityMetadata<ENTITY> getMetadata(Class<ENTITY> entityClass);

    <ENTITY> EntityMetadata<ENTITY> getMetadata(String tableName);
}
