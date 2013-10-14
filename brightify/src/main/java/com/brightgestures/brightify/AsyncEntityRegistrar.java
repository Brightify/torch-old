package com.brightgestures.brightify;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface AsyncEntityRegistrar {
    public <T extends AsyncEntityRegistrar & AsyncSubmit, ENTITY> T register(Class<ENTITY> entityClass);
}
