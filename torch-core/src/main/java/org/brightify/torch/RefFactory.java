package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class RefFactory {

    @SuppressWarnings("unchecked")
    public static <ENTITY> Ref<ENTITY> createRef(ENTITY entity) {
        Class<ENTITY> entityClass = (Class<ENTITY>) entity.getClass();
        EntityMetadata<ENTITY> metadata = TorchService.factory().getEntities().getMetadata(entityClass);
        return new RefImpl<ENTITY>(metadata, entity);
    }

}
