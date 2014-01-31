package org.brightify.torch.action.load;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface TypedFilterOrderLimitListLoader<ENTITY> extends TypedLoader<ENTITY>, FilterLoader<ENTITY>,
        OrderLoader<ENTITY>, LimitLoader<ENTITY>, ListLoader<ENTITY> {
}
