package org.brightify.torch.action.load.sync;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface OrderLimitListLoader<ENTITY> extends OrderLoader<ENTITY>, LimitLoader<ENTITY>, ListLoader<ENTITY>, Countable {
}
