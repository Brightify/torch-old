package org.brightify.torch.action.load.combined;

import org.brightify.torch.action.load.Countable;
import org.brightify.torch.action.load.FilterLoader;
import org.brightify.torch.action.load.LimitLoader;
import org.brightify.torch.action.load.ListLoader;
import org.brightify.torch.action.load.OrderLoader;
import org.brightify.torch.action.load.TypedLoader;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TypedFilterOrderLimitListLoader<ENTITY> extends TypedLoader<ENTITY>, FilterLoader<ENTITY>,
        OrderLoader<ENTITY>, LimitLoader<ENTITY>, ListLoader<ENTITY>, Countable {
}
