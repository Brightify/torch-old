package org.brightify.torch;

import java.util.Collection;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Entities {
    <ENTITY> Key<ENTITY> keyOf(ENTITY entity);

    Collection<EntityDescription<?>> getAllDescriptions();

    <ENTITY> EntityDescription<ENTITY> getDescription(Class<ENTITY> entityClass);

    <ENTITY> EntityDescription<ENTITY> getDescription(String name);

    // FIXME this should be done differently (and have name registerDescription)
    <ENTITY> void registerMetadata(EntityDescription<ENTITY> metadata);
}
