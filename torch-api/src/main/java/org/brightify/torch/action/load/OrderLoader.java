package org.brightify.torch.action.load;

import org.brightify.torch.action.load.combined.OrderDirectionLimitListLoader;
import org.brightify.torch.filter.Property;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface OrderLoader<ENTITY> {

    OrderDirectionLimitListLoader<ENTITY> orderBy(Property<ENTITY, ?> property);

    enum Direction {
        ASCENDING, DESCENDING
    }

}
