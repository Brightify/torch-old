package com.brightgestures.brightify.action.load;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface OrderDirectionLimitListLoader<ENTITY> extends OrderLoader<ENTITY>, DirectionLoader<ENTITY>,
        LimitLoader<ENTITY>, ListLoader<ENTITY> {
}
