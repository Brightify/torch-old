package org.brightify.torch.action.load.sync;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TypedFilterOrderLimitListLoader<ENTITY> extends TypedLoader<ENTITY>, FilterLoader<ENTITY>,
        OrderLoader<ENTITY>, LimitLoader<ENTITY>, ListLoader<ENTITY>, Countable {
}
