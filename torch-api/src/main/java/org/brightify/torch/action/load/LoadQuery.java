package org.brightify.torch.action.load;

import org.brightify.torch.EntityDescription;
import org.brightify.torch.action.load.sync.OrderLoader;
import org.brightify.torch.filter.Property;
import org.brightify.torch.filter.EntityFilter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface LoadQuery<ENTITY> {

    EntityDescription<ENTITY> getEntityDescription();

    Set<Class<?>> getLoadGroups();

    List<EntityFilter> getEntityFilters();

    Map<Property<?>, OrderLoader.Direction> getOrderMap();

    Integer getLimit();

    Integer getOffset();
}
