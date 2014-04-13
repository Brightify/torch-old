package org.brightify.torch.compile;

import javax.lang.model.element.Element;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface EntityContext {

    Set<Class<?>> getEntityClasses();

    Set<EntityInfo> getEntityInfos();

    void registerEntityInfo(EntityInfo entityInfo);

    void registerEntityClass(Class<?> entityClass);

    boolean containsEntity(String name);

    boolean containsEntity(Element element);

    boolean containsEntity(Class<?> entityClass);

    boolean containsEntity(Property property);
}
