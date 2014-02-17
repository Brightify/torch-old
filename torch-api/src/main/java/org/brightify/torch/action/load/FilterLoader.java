package org.brightify.torch.action.load;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface FilterLoader<ENTITY> {

    OperatorFilterOrderLimitListLoader<ENTITY> filter(String condition, Object... params);
    OperatorFilterOrderLimitListLoader<ENTITY> filter(EntityFilter nestedFilter);

}
