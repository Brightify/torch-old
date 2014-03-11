package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface AsyncEntityRegistrar {
    public <ENTITY> AsyncEntityRegistrarSubmit register(Class<ENTITY> entityClass);

    public <ENTITY> AsyncEntityRegistrarSubmit register(EntityMetadata<ENTITY> metadata);
}
