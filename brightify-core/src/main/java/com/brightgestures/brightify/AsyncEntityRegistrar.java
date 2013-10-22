package com.brightgestures.brightify;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface AsyncEntityRegistrar {
    public <ENTITY> AsyncEntityRegistrarSubmit register(Class<ENTITY> entityClass);

    public <ENTITY> AsyncEntityRegistrarSubmit register(EntityMetadata<ENTITY> metadata);
}
