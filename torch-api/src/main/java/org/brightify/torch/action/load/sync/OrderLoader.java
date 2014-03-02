package org.brightify.torch.action.load.sync;

import org.brightify.torch.filter.Column;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface OrderLoader<ENTITY> {

    OrderDirectionLimitListLoader<ENTITY> orderBy(Column<?> column);

    public enum Direction {
        ASCENDING, DESCENDING
    }

}
