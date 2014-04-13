package org.brightify.torch.action.load.sync;

import org.brightify.torch.filter.Property;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface OrderLoader<ENTITY> {

    OrderDirectionLimitListLoader<ENTITY> orderBy(Property<?> property);

    public enum Direction {
        ASCENDING, DESCENDING
    }

}
