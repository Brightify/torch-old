package com.brightgestures.brightify.action.delete;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public abstract class DeleterType<ENTITY> {

    public static class NopDeleterType<ENTITY> extends DeleterType<ENTITY> {

    }

    public static class TypedDeleterType<ENTITY> extends DeleterType<ENTITY> {

        protected final Class<ENTITY> mEntityClass;

        public TypedDeleterType(Class<ENTITY> entityClass) {
            mEntityClass = entityClass;
        }
    }

}
