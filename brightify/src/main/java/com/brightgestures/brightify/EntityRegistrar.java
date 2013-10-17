package com.brightgestures.brightify;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface EntityRegistrar {

    /**
     * Registers entity into {@link Entities}.
     * @param entityClass Entity class
     * @param <ENTITY> Type of entity
     * @return
     */
    public <ENTITY> EntityRegistrar register(Class<ENTITY> entityClass);

    public <ENTITY> EntityMetadataCompatibility<ENTITY> unregister(Class<ENTITY> entityClass);

}
