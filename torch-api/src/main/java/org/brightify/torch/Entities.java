package org.brightify.torch;

import java.util.Collection;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Entities {
    <ENTITY> Key<ENTITY> keyOf(ENTITY entity);

    Collection<EntityMetadata<?>> getAllMetadatas();

    <ENTITY> EntityMetadata<ENTITY> getMetadata(Class<ENTITY> entityClass);

    <ENTITY> EntityMetadata<ENTITY> getMetadata(String tableName);
}
