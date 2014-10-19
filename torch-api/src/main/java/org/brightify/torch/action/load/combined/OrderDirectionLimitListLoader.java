package org.brightify.torch.action.load.combined;

import org.brightify.torch.action.load.Countable;
import org.brightify.torch.action.load.DirectionLoader;
import org.brightify.torch.action.load.LimitLoader;
import org.brightify.torch.action.load.ListLoader;
import org.brightify.torch.action.load.OrderLoader;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface OrderDirectionLimitListLoader<ENTITY> extends OrderLoader<ENTITY>, DirectionLoader<ENTITY>,
        LimitLoader<ENTITY>, ListLoader<ENTITY>, Countable {
}
