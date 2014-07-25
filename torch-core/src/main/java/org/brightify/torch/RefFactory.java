package org.brightify.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class RefFactory {

    @SuppressWarnings("unchecked")
    public static <ENTITY> Ref<ENTITY> createRef(ENTITY entity) {
        Class<ENTITY> entityClass = (Class<ENTITY>) entity.getClass();
        EntityDescription<ENTITY> metadata = TorchService.factory().getEntities().getDescription(entityClass);
        return new RefImpl<ENTITY>(metadata, entity);
    }

}
