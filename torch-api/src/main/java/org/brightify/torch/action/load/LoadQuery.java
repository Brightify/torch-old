package org.brightify.torch.action.load;

import org.brightify.torch.EntityMetadata;
import org.brightify.torch.action.load.sync.OrderLoader;
import org.brightify.torch.filter.Column;
import org.brightify.torch.filter.EntityFilter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface LoadQuery<ENTITY> {

    EntityMetadata<ENTITY> getEntityMetadata();

    Set<Class<?>> getLoadGroups();

    List<EntityFilter> getEntityFilters();

    Map<Column<?>, OrderLoader.Direction> getOrderMap();

    Integer getLimit();

    Integer getOffset();
}
