package org.brightify.torch.action.load;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface FilterLoader<ENTITY> {

    OrderLimitListLoader<ENTITY> filter(String condition, Object... params);
    OrderLimitListLoader<ENTITY> filter(EntityFilter nestedFilter);

}
