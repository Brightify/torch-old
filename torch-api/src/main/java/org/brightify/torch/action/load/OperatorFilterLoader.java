package org.brightify.torch.action.load;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface OperatorFilterLoader<ENTITY> {

    OperatorFilterOrderLimitListLoader<ENTITY> or(String condition, Object... params);
    OperatorFilterOrderLimitListLoader<ENTITY> or(EntityFilter filter);
    OperatorFilterOrderLimitListLoader<ENTITY> and(String condition, Object... params);
    OperatorFilterOrderLimitListLoader<ENTITY> and(EntityFilter filter);

}
