package org.brightify.torch.action.load;

import org.brightify.torch.EntityDescription;
import org.brightify.torch.filter.BaseFilter;
import org.brightify.torch.filter.Property;

import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface LoadQuery<ENTITY> {

    EntityDescription<ENTITY> getEntityDescription();

    Set<Class<?>> getLoadGroups();

    BaseFilter<ENTITY, ?> getFilter();

    Map<Property<ENTITY, ?>, OrderLoader.Direction> getOrderMap();

    Integer getLimit();

    Integer getOffset();
}
